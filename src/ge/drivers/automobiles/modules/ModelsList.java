/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles.modules;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ge.drivers.automobiles.CarsActivity;
import ge.drivers.automobiles.db.DatabaseManager;
import ge.drivers.automobiles.db.SQLiteDBManager;
import ge.drivers.automobiles.lib.MyAlert;
import ge.drivers.automobiles.lib.MyResource;
import ge.drivers.automobiles.lib.ServerConn;

/**
 *
 * @author alexx
 */
public class ModelsList extends ArrayAdapter<String> {

    private Context context;
    private DatabaseManager dbManager = null;
    private int make_id = 0;
    private String make_title = null;
    private int cnt = 0;
    private int[] ids = null;
    private String[] names = null;
    private String[] images = null;
    private View[] views = null;

    public ModelsList(Context context, int res, int make_id, String make_title) {

        super(context, res);
        this.context = context;
        this.make_id = make_id;
        this.make_title = make_title;
    }

    public void loadModelsList() {

        final ArrayAdapter<String> self = this;
        AsyncTask loader = new AsyncTask<String, Void, Integer>() {

            private String error = null;

            @Override
            protected Integer doInBackground(String... args) {

                try {
                    //open sqlite database
                    dbManager = new SQLiteDBManager(context);
                    dbManager.openDatabase();

                    String query = "select t.id, t.name, t.image from Models t where t.make_id = " + args[0] + " order by t.name asc";

                    Cursor cursor = dbManager.qin(query);
                    cnt = cursor.getCount();

                    if (cnt > 0) {
                        ids = new int[cnt];
                        names = new String[cnt];
                        images = new String[cnt];
                        views = new View[cnt];
                        int i = 0;
                        while (cursor.moveToNext()) {
                            ids[i] = cursor.getInt(0);
                            names[i] = cursor.getString(1);
                            images[i] = cursor.getString(2);
                            views[i] = null;
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
                    for (int i = 0; i < cnt; i++) {
                        self.add(names[i]);
                    }
                }
            }
        };
        loader.execute(new String[]{make_id + ""});
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (views[position] == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            views[position] = inflater.inflate(MyResource.getLayout(context, "make_item"), null);

            //Thumbnail
            ImageView IMG = new ImageView(context);
            ServerConn.loadUrlInImageView(IMG, images[position]);
            LinearLayout thumbnail = (LinearLayout) ((LinearLayout) views[position]).getChildAt(0);
            thumbnail.addView(IMG);

            //Name
            TextView maketitle = (TextView) ((LinearLayout) views[position]).getChildAt(1);
            maketitle.setText(names[position]);
        }

        return views[position];
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
                    Intent intent = new Intent(context, CarsActivity.class);
                    intent.putExtra("model_id", ids[position]);
                    intent.putExtra("make_name", make_title);
                    intent.putExtra("model_name",  names[position]);
                    context.startActivity(intent);
                } catch (Exception e) {
                    MyAlert.alertWin(context, e.toString());
                }
            }
        };
        return clickListener;
    }
}
