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
import ge.drivers.automobiles.modules.PropertiesList;

/**
 *
 * @author alexx
 */
public class PropertiesActivity extends Activity {

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
            String car_title = this.getIntent().getStringExtra("car_title");
            actionBar.setTitle(title);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.show();
            
            setContentView(R.layout.main);
            ListView lv = (ListView)findViewById(R.id.browse_list);
            
            int car_id = this.getIntent().getIntExtra("car_id", 0);
            PropertiesList propList = new PropertiesList(this, R.layout.car_title, car_id, car_title);
            lv.setAdapter(propList);
            propList.loadPropertiesList();
        }
        catch (Exception e){
            MyAlert.alertWin(this, e.toString());
        }
    }
}
