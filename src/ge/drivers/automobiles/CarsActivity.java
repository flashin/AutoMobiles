/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import ge.drivers.automobiles.lib.MyAlert;
import ge.drivers.automobiles.modules.CarsList;

/**
 *
 * @author alexx
 */
public class CarsActivity extends Activity {

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
            ListView lv = (ListView)findViewById(R.id.browse_list);
            
            int model_id = this.getIntent().getIntExtra("model_id", 0);
            CarsList carsList = new CarsList(this, R.layout.car_title, model_id, make_name, model_name);
            lv.setAdapter(carsList);
            carsList.loadCarsList();
            lv.setOnItemClickListener(carsList.getClickListener());
        }
        catch (Exception e){
            MyAlert.alertWin(this, e.toString());
        }
    }
}
