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
import ge.drivers.automobiles.modules.ModelsList;

/**
 *
 * @author alexx
 */
public class ModelsActivity extends Activity {

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
            String title = this.getIntent().getStringExtra("title");
            actionBar.setTitle(title);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.show();
            
            setContentView(R.layout.main);
            ListView lv = (ListView)findViewById(R.id.browse_list);
            
            int make_id = this.getIntent().getIntExtra("make_id", 0);
            ModelsList modelsList = new ModelsList(this, R.layout.make_item, make_id, title);
            lv.setAdapter(modelsList);
            modelsList.loadModelsList();
            lv.setOnItemClickListener(modelsList.getClickListener());
        }
        catch (Exception e){
            MyAlert.alertWin(this, e.toString());
        }
    }
}
