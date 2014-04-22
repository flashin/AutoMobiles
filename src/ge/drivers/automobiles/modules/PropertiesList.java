/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles.modules;

import android.content.Context;
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

/**
 *
 * @author alexx
 */
public class PropertiesList extends ArrayAdapter<String> {

    private Context context;
    private DatabaseManager dbManager = null;
    private int car_id = 0;
    private String car_title = null;
    private int cnt = 0;
    private String[] keys = null;
    private String[] values = null;
    private String[] images = null;
    private View[] views = null;

    public PropertiesList(Context context, int res, int car_id, String car_title) {

        super(context, res);
        this.context = context;
        this.car_id = car_id;
        this.car_title = car_title;
    }

    public void loadPropertiesList() {

        final ArrayAdapter<String> self = this;
        AsyncTask loader = new AsyncTask<String, Void, Integer>() {

            private String error = null;

            @Override
            protected Integer doInBackground(String... args) {

                try {
                    //open sqlite database
                    dbManager = new SQLiteDBManager(context);
                    dbManager.openDatabase();
                    int starting_views = 1;

                    //Images
                    String query = "select t.id, t.name from Images t, Cars c where t.generation_id = c.generation_id and c.id = " + args[0] + " order by t.order_no asc";
                    Cursor cursor = dbManager.qin(query);
                    int cnt = cursor.getCount();

                    if (cnt > 0) {
                        starting_views++;
                        images = new String[cnt];
                        int i = 0;
                        while (cursor.moveToNext()) {
                            images[i++] = cursor.getString(1);
                        }
                    }

                    //Properties
                    query = "select p.name as prop_key, c.value as prop_value from Properties p, Prop_to_car c where p.id = c.property_id and c.car_id = " + args[0] + " order by p.order_no asc";
                    cursor = dbManager.qin(query);
                    cnt = cursor.getCount();
                    if (cnt > 0) {
                        keys = new String[cnt];
                        values = new String[cnt];
                        views = new View[cnt + starting_views];
                        for (int j = 0; j < starting_views; j++) {
                            views[j] = null;
                        }

                        int i = 0;
                        while (cursor.moveToNext()) {
                            views[i + starting_views] = null;
                            keys[i] = cursor.getString(0);
                            values[i] = cursor.getString(1);
                            i++;
                        }
                    }

                    return cnt;
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
                        self.add(car_title);
                        if (images != null) {
                            self.add(car_title);
                        }
                        if (keys != null) {
                            for (int i = 0; i < keys.length; i++) {
                                self.add(keys[i]);
                            }
                        }
                    } catch (Exception e) {
                        MyAlert.alertWin(context, e.toString());
                    }
                }
            }
        };
        loader.execute(new String[]{car_id + ""});
    }
    
    //Change car favorite option
    public boolean changeFavoriteOption(int is_favorite){
    
        int new_favorite = is_favorite == 1 ? 0 : 1;
        
        if (dbManager != null && car_id > 0){
            String query = "update Cars set is_favorite = " + new_favorite + " where id = " + car_id;
            dbManager.qout(query);
            return true;
        }
        
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            if (views[position] == null) {
                //How many rows on top of the properties, depends on images
                int n = images == null ? 1 : 2;
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (position == 0) {
                    LinearLayout v = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "car_title"), null);

                    //title
                    TextView cartitle = (TextView) v.getChildAt(0);
                    cartitle.setText(car_title);
                    views[position] = v;
                } else if (n == 2 && images != null && position == 1) {
                    LinearLayout v = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "car_images"), null);
                    if (images.length > 0) {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(9, 0, 0, 0);
                        
                        LinearLayout imgs = (LinearLayout) ((HorizontalScrollView) v.getChildAt(0)).getChildAt(0);
                        //Thumbnail
                        for (int i = 0; i < images.length; i++) {
                            ImageView IMG = new ImageView(context);
                            if (i > 0){
                                IMG.setLayoutParams(lp);
                            }
                            ServerConn.loadUrlInImageView(IMG, images[i], 320);
                            imgs.addView(IMG);
                        }
                    }
                    views[position] = v;
                } else if (position >= n) {
                    LinearLayout v = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "property"), null);

                    //key
                    TextView txt = (TextView) v.getChildAt(0);
                    txt.setText(keys[position - n]);

                    //value
                    txt = (TextView) v.getChildAt(1);
                    txt.setText(values[position - n]);

                    views[position] = v;
                }
            }
        } catch (Exception e) {
            MyAlert.alertWin(context, e.toString());
            TextView txt = new TextView(context);
            txt.setText(e.toString());
            return txt;
        }

        return views[position];
    }
}
