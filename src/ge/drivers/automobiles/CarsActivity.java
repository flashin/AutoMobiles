/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import ge.drivers.automobiles.lib.MyAlert;
import ge.drivers.automobiles.modules.CarsList;

/**
 *
 * @author alexx
 */
public class CarsActivity extends ActionBarActivity {

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
            String make_name = this.getIntent().getStringExtra("make_name");
            String model_name = this.getIntent().getStringExtra("model_name");
            String title = make_name + " " + model_name;
            actionBar.setTitle(title);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.show();

            setContentView(R.layout.main);
            ListView lv = (ListView) findViewById(R.id.browse_list);

            CarsList carsList = new CarsList(this, R.layout.car_title, getIntent());
            lv.setAdapter(carsList);
            carsList.loadCarsList();
            lv.setOnItemClickListener(carsList.getClickListener());
        } catch (Exception e) {
            MyAlert.alertWin(this, e.toString());
        }
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        Intent parentIntent = getIntent();
        int make_id = parentIntent.getIntExtra("make_id", 0);
        String make_name = parentIntent.getStringExtra("make_name");

        Intent newIntent = new Intent(this, ModelsActivity.class);
        newIntent.putExtra("make_id", make_id);
        newIntent.putExtra("title", make_name);
        
        return newIntent;
    }
}
