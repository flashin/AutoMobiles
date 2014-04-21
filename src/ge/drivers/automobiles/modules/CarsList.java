/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles.modules;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ge.drivers.automobiles.db.DatabaseManager;
import ge.drivers.automobiles.db.SQLiteDBManager;
import ge.drivers.automobiles.lib.MyAlert;
import ge.drivers.automobiles.lib.MyResource;
import ge.drivers.automobiles.lib.ServerConn;
import ge.drivers.automobiles.PropertiesActivity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alexx
 */
public class CarsList extends ArrayAdapter<String> {

    private class CarItem {

        public int type = 1; //1 - title; 2 - images; 3 - car
        public String title;
        public String[] images;
        public int generation_id;
        public int car_id;
        public int make_id;
        public int model_id;
        public String make_name;
        public String model_name;
        public String short_name;
        public String type_name;
        public int is_favorite;
        public String engine;
        public String doors;
        public String years;
    };
    private Context context;
    private DatabaseManager dbManager = null;
    private Intent intent;
    private int model_id = 0;
    private List<CarItem> carItems = null;
    private List<View> views = null;
    private String keyword = null;

    public CarsList(Context context, int res, Intent intent) {

        super(context, res);
        this.context = context;
        this.intent = intent;
        if (this.intent != null) {
            this.model_id = intent.getIntExtra("model_id", 0);
        }
        carItems = new ArrayList<CarItem>();
        views = new ArrayList<View>();
    }

    public void loadCarsList() {

        final ArrayAdapter<String> self = this;
        AsyncTask loader = new AsyncTask<String, Void, Integer>() {

            private String error = null;

            @Override
            protected Integer doInBackground(String... args) {

                try {
                    //open sqlite database
                    dbManager = new SQLiteDBManager(context);
                    dbManager.openDatabase();

                    String query = "select t.id, t.name from Generations t where t.model_id = " + args[0] + " order by t.order_no asc";

                    Cursor cursor = dbManager.qin(query);
                    int cnt = cursor.getCount();

                    CarItem item;
                    CarItem item2;
                    if (cnt > 0) {
                        while (cursor.moveToNext()) {
                            //Title row
                            views.add(null);
                            item = new CarItem();
                            item.type = 1;
                            item.generation_id = cursor.getInt(0);
                            item.title = cursor.getString(1);

                            //Get generation images
                            query = "select t.id, t.name from Images t where t.generation_id = " + item.generation_id + " order by t.order_no asc";
                            Cursor cursor2 = dbManager.qin(query);
                            cnt = cursor2.getCount();
                            if (cnt > 0) {
                                item.images = new String[cnt];
                                int i = 0;
                                while (cursor2.moveToNext()) {
                                    item.images[i++] = cursor2.getString(1);
                                }
                            }
                            carItems.add(item);

                            //Images row
                            views.add(null);
                            item2 = new CarItem();
                            item2.type = 2;
                            item2.generation_id = item.generation_id;
                            item2.title = item.title;
                            item2.images = item.images;
                            carItems.add(item2);

                            //Cars
                            query = "select t.id, t.engine, t.doors, t.from_year, t.to_year, t.is_favorite from Cars t where t.generation_id = " + item2.generation_id + " order by t.order_no asc";
                            cursor2 = dbManager.qin(query);
                            cnt = cursor2.getCount();
                            if (cnt > 0) {
                                while (cursor2.moveToNext()) {
                                    views.add(null);
                                    item = new CarItem();
                                    item.type = 3;
                                    item.car_id = cursor2.getInt(0);
                                    item.generation_id = item2.generation_id;
                                    item.title = item2.title;
                                    item.engine = cursor2.getString(1);
                                    item.doors = cursor2.getString(2);
                                    item.years = cursor2.getString(3).concat(" - ");
                                    String to_year = cursor2.getString(4);
                                    if (!to_year.equals("-")) {
                                        item.years.concat(to_year);
                                    }
                                    item.is_favorite = cursor2.getInt(5);
                                    carItems.add(item);
                                }
                            }
                        }
                    }

                    return 1;
                } catch (Exception e) {
                    error = e.toString();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Integer result) {

                if (error != null) {
                    MyAlert.alertWin(context, error);
                } else {
                    for (int i = 0; i < carItems.size(); i++) {
                        self.add(carItems.get(i).title);
                    }
                }
            }
        };
        loader.execute(new String[]{model_id + ""});
    }

    //get cars for search or favorites
    public void loadSpecificCars(Intent intent) {

        final ArrayAdapter<String> self = this;

        String query = null;
        if (intent != null && Intent.ACTION_SEARCH.equals(intent.getAction())) {
            keyword = intent.getStringExtra(SearchManager.QUERY);
        } else if (intent != null && intent.getStringExtra("keyword") != null) {
            keyword = intent.getStringExtra("keyword");
        }
        if (keyword != null) {
            keyword = keyword.replace("'", "");
            query = "select c.id, m1.name || ' ' || g.name as short_name, g.name as generation_name, c.generation_id, c.engine, c.doors, c.type, c.from_year, c.to_year, c.is_favorite, m1.id as make_id, m2.id as model_id, m1.name as make_name, m2.id as model_name "
                    + " from Makes m1, Models m2, Generations g, Cars c "
                    + " where m1.id = m2.make_id and m2.id = g.model_id and g.id = c.generation_id "
                    + " and m1.name || ' ' || g.name like '%" + keyword + "%'"
                    + " order by short_name asc, g.order_no asc"
                    + " limit 0, 150";
        } else {
            query = "select c.id, m1.name || ' ' || g.name as short_name, g.name as generation_name, c.generation_id, c.engine, c.doors, c.type, c.from_year, c.to_year, c.is_favorite, m1.id as make_id, m2.id as model_id, m1.name as make_name, m2.id as model_name "
                    + " from Makes m1, Models m2, Generations g, Cars c "
                    + " where m1.id = m2.make_id and m2.id = g.model_id and g.id = c.generation_id and c.is_favorite = 1 "
                    + " order by short_name asc, g.order_no asc";
        }
        AsyncTask loader = new AsyncTask<String, Void, Integer>() {

            private String error = null;

            @Override
            protected Integer doInBackground(String... args) {

                try {
                    //open sqlite database
                    dbManager = new SQLiteDBManager(context);
                    dbManager.openDatabase();

                    Cursor cursor = dbManager.qin(args[0]);
                    int cnt = cursor.getCount();

                    CarItem item;
                    if (cnt > 0) {
                        while (cursor.moveToNext()) {
                            item = new CarItem();
                            item.type = 4;
                            item.car_id = cursor.getInt(0);
                            item.short_name = cursor.getString(1);
                            item.title = cursor.getString(2);
                            item.generation_id = cursor.getInt(3);
                            item.engine = cursor.getString(4);
                            item.doors = cursor.getString(5);
                            item.type_name = cursor.getString(6);
                            item.years = cursor.getString(7).concat(" - ");
                            String to_year = cursor.getString(8);
                            if (!to_year.equals("-")) {
                                item.years.concat(to_year);
                            }
                            item.is_favorite = cursor.getInt(9);
                            item.make_id = cursor.getInt(10);
                            item.model_id = cursor.getInt(11);
                            item.make_name = cursor.getString(12);
                            item.model_name = cursor.getString(13);

                            //Get generation images
                            String query = "select t.id, t.name from Images t where t.generation_id = " + item.generation_id + " order by t.order_no asc limit 0, 1";
                            Cursor cursor2 = dbManager.qin(query);
                            cnt = cursor2.getCount();
                            if (cnt > 0) {
                                item.images = new String[cnt];
                                int i = 0;
                                while (cursor2.moveToNext()) {
                                    item.images[i++] = cursor2.getString(1);
                                }
                            }
                            carItems.add(item);
                        }
                    }

                    return 1;
                } catch (Exception e) {
                    error = e.toString();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Integer result) {

                if (error != null) {
                    MyAlert.alertWin(context, error);
                } else {
                    try {
                        for (int i = 0; i < carItems.size(); i++) {
                            views.add(null);
                            self.add(carItems.get(i).short_name);
                        }
                    } catch (Exception e) {
                        MyAlert.alertWin(context, e.toString());
                    }
                }
            }
        };
        loader.execute(new String[]{query});
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (views.get(position) == null) {
            try {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                CarItem item = carItems.get(position);
                if (item.type == 1) {
                    LinearLayout v = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "car_title"), null);

                    //title
                    TextView cartitle = (TextView) v.getChildAt(0);
                    cartitle.setText(item.title);
                    views.add(position, v);
                } else if (item.type == 2) {
                    LinearLayout v = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "car_images"), null);
                    if (item.images.length > 0) {
                        LinearLayout imgs = (LinearLayout) ((HorizontalScrollView) v.getChildAt(0)).getChildAt(0);
                        //Thumbnail
                        for (int i = 0; i < item.images.length; i++) {
                            ImageView IMG = new ImageView(context);
                            ServerConn.loadUrlInImageView(IMG, item.images[i]);
                            imgs.addView(IMG);
                        }
                    }
                    views.add(position, v);
                } else if (item.type == 3) {
                    LinearLayout v = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "car_short_desc"), null);

                    LinearLayout cardesc = (LinearLayout) v.getChildAt(0);
                    //engine
                    TextView txt = (TextView) cardesc.getChildAt(0);
                    txt.setText(item.engine);

                    //doors
                    txt = (TextView) cardesc.getChildAt(1);
                    txt.setText(context.getString(MyResource.getString(context, "label_doors")) + ": " + item.doors);

                    //yeas
                    txt = (TextView) cardesc.getChildAt(2);
                    txt.setText(context.getString(MyResource.getString(context, "label_years")) + ": " + item.years);

                    //If is favorite show favorite image
                    if (item.is_favorite == 1) {
                        ImageView favImg = new ImageView(context);
                        favImg.setImageResource(MyResource.getDrawable(context, "star_full"));

                        LinearLayout favlayout = (LinearLayout) v.getChildAt(1);
                        favlayout.addView(favImg);
                    }

                    views.add(position, v);
                } else if (item.type == 4) {
                    LinearLayout v = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "car_spec_desc"), null);

                    LinearLayout iconlayout = (LinearLayout) v.getChildAt(0);
                    ImageView iconImg = new ImageView(context);
                    if (item.images != null && item.images.length > 0) {
                        ServerConn.loadUrlInImageView(iconImg, item.images[0], 90);
                    } else {
                        iconImg.setImageResource(MyResource.getDrawable(context, "noimage"));
                    }
                    iconlayout.addView(iconImg);

                    LinearLayout cardesc = (LinearLayout) v.getChildAt(1);
                    //short name
                    TextView txt = (TextView) cardesc.getChildAt(0);
                    txt.setText(item.short_name);

                    //engine
                    txt = (TextView) cardesc.getChildAt(1);
                    txt.setText(item.engine);

                    //type
                    txt = (TextView) cardesc.getChildAt(2);
                    txt.setText(context.getString(MyResource.getString(context, "label_car_type")) + ": " + item.type_name);

                    //doors
                    txt = (TextView) cardesc.getChildAt(3);
                    txt.setText(context.getString(MyResource.getString(context, "label_doors")) + ": " + item.doors);

                    //years
                    txt = (TextView) cardesc.getChildAt(4);
                    txt.setText(context.getString(MyResource.getString(context, "label_years")) + ": " + item.years);

                    //If is favorite show favorite image
                    if (item.is_favorite == 1) {
                        ImageView favImg = new ImageView(context);
                        favImg.setImageResource(MyResource.getDrawable(context, "star_full"));

                        LinearLayout favlayout = (LinearLayout) v.getChildAt(2);
                        favlayout.addView(favImg);
                    }

                    views.add(position, v);
                }
            } catch (Exception e) {
                TextView tview = new TextView(context);
                tview.setText(e.toString());
                views.add(position, tview);
            }
        }

        return views.get(position);
    }

    /**
     * Click event of the menu item
     */
    public AdapterView.OnItemClickListener getClickListener() {
        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {

            /**
             * Opens Pop up window with law description
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    int item_type = carItems.get(position).type;
                    if (item_type == 3 || item_type == 4) {
                        Intent newIntent = new Intent(context, PropertiesActivity.class);
                        newIntent.putExtra("car_id", carItems.get(position).car_id);
                        newIntent.putExtra("is_favorite", carItems.get(position).is_favorite);
                        newIntent.putExtra("car_title", carItems.get(position).title + " " + carItems.get(position).engine);

                        if (item_type == 3) {
                            newIntent.putExtra("make_id", intent.getIntExtra("make_id", 0));
                            newIntent.putExtra("model_id", intent.getIntExtra("model_id", 0));
                            newIntent.putExtra("make_name", intent.getStringExtra("make_name"));
                            newIntent.putExtra("model_name", intent.getStringExtra("model_name"));
                        } else {
                            newIntent.putExtra("make_id", carItems.get(position).make_id);
                            newIntent.putExtra("model_id", carItems.get(position).model_id);
                            newIntent.putExtra("make_name", carItems.get(position).make_name);
                            newIntent.putExtra("model_name", carItems.get(position).model_name);
                            if (keyword != null) {
                                newIntent.putExtra("keyword", keyword);
                            } else {
                                newIntent.putExtra("from_favorites", 1);
                            }
                        }
                        context.startActivity(newIntent);
                    }
                } catch (Exception e) {
                    MyAlert.alertWin(context, e.toString());
                }
            }
        };
        return clickListener;
    }
}
