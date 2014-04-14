/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles.modules;

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
        public String engine;
        public String doors;
        public String years;
    };
    private Context context;
    private DatabaseManager dbManager = null;
    private int model_id = 0;
    private String make_name = null;
    private String model_name = null;
    private List<CarItem> carItems = null;
    private List<View> views = null;

    public CarsList(Context context, int res, int model_id, String make_name, String model_name) {

        super(context, res);
        this.context = context;
        this.model_id = model_id;
        this.make_name = make_name;
        this.model_name = model_name;
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
                            query = "select t.id, t.engine, t.doors, t.from_year, t.to_year from Cars t where t.generation_id = " + item2.generation_id + " order by t.order_no asc";
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (views.get(position) == null) {
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
                    //Thumbnail
                    ImageView IMG = new ImageView(context);
                    ServerConn.loadUrlInImageView(IMG, item.images[0]);
                    v.addView(IMG);
                }
                views.add(position, v);
            } else if (item.type == 3) {
                LinearLayout v = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "car_short_desc"), null);

                //engine
                TextView txt = (TextView) v.getChildAt(0);
                txt.setText(item.engine);

                //doors
                txt = (TextView) v.getChildAt(1);
                txt.setText(context.getString(MyResource.getString(context, "label_doors")) + ": " + item.doors);

                //yeas
                txt = (TextView) v.getChildAt(2);
                txt.setText(context.getString(MyResource.getString(context, "label_years")) + ": " + item.years);

                views.add(position, v);
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
                    if (carItems.get(position).type == 3) {
                        Intent intent = new Intent(context, PropertiesActivity.class);
                        intent.putExtra("make_name", make_name);
                        intent.putExtra("model_name", model_name);
                        intent.putExtra("car_id", carItems.get(position).car_id);
                        intent.putExtra("car_title", carItems.get(position).title + " " + carItems.get(position).engine);
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    MyAlert.alertWin(context, e.toString());
                }
            }
        };
        return clickListener;
    }
}
