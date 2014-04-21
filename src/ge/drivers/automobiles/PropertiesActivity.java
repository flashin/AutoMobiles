/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import ge.drivers.automobiles.lib.MyAlert;
import ge.drivers.automobiles.modules.PropertiesList;

/**
 *
 * @author alexx
 */
public class PropertiesActivity extends ActionBarActivity {

    private int is_favorite = 0;
    private PropertiesList propList = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // ToDo add your GUI initialization code here
        try {
            ActionBar actionBar = this.getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            is_favorite = this.getIntent().getIntExtra("is_favorite", 0);
            String make_name = this.getIntent().getStringExtra("make_name");
            String model_name = this.getIntent().getStringExtra("model_name");
            String title = make_name + " " + model_name;
            String car_title = this.getIntent().getStringExtra("car_title");
            actionBar.setTitle(title);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.show();

            setContentView(R.layout.main);
            ListView lv = (ListView) findViewById(R.id.browse_list);

            int car_id = this.getIntent().getIntExtra("car_id", 0);
            propList = new PropertiesList(this, R.layout.car_title, car_id, car_title);
            lv.setAdapter(propList);
            propList.loadPropertiesList();
        } catch (Exception e) {
            MyAlert.alertWin(this, e.toString());
        }
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        Intent parentIntent = getIntent();
        int make_id = parentIntent.getIntExtra("make_id", 0);
        int model_id = parentIntent.getIntExtra("model_id", 0);
        String make_name = parentIntent.getStringExtra("make_name");
        String model_name = parentIntent.getStringExtra("model_name");

        Intent newIntent = null;
        if (parentIntent.getIntExtra("from_favorites", 0) == 1) {
            newIntent = new Intent(this, MainActivity.class);
            newIntent.putExtra("active_tab", 1);
        }
        else if (parentIntent.getStringExtra("keyword") != null){
            newIntent = new Intent(this, SearchActivity.class);
            newIntent.putExtra("keyword", parentIntent.getStringExtra("keyword"));
        }
        else {
            newIntent = new Intent(this, CarsActivity.class);
            newIntent.putExtra("make_id", make_id);
            newIntent.putExtra("model_id", model_id);
            newIntent.putExtra("make_name", make_name);
            newIntent.putExtra("model_name", model_name);
        }

        return newIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorite_actions, menu);
        if (is_favorite == 1) {
            MenuItem item = menu.getItem(0);
            item.setIcon(R.drawable.star_full);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_favorite:
                changeFavoriteOption(item);
                return true;
            default:
                return false;
        }
    }

    public void changeFavoriteOption(final MenuItem item) {

        final ActionBarActivity self = this;
        AsyncTask loader = new AsyncTask<String, Void, Integer>() {

            private String error = null;

            @Override
            protected Integer doInBackground(String... args) {

                try {
                    if (propList.changeFavoriteOption(is_favorite)) {
                        return 1;
                    };
                    return 0;
                } catch (Exception e) {
                    error = e.toString();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Integer result) {

                if (error != null) {
                    MyAlert.alertWin(self, error);
                } else if (result == 1) {
                    try {
                        int resource = is_favorite == 1 ? R.drawable.star_empty : R.drawable.star_full;
                        is_favorite = is_favorite == 1 ? 0 : 1;
                        item.setIcon(resource);
                    } catch (Exception e) {
                        MyAlert.alertWin(self, e.toString());
                    }
                }
            }
        };
        loader.execute(new String[]{});
    }
}
