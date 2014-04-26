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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import ge.drivers.automobiles.lib.MyAlert;
import ge.drivers.automobiles.modules.ModelsList;

/**
 *
 * @author alexx
 */
public class ModelsActivity extends ActionBarActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // ToDo add your GUI initialization code here  
        try {
            createModels(this.getIntent());
        } catch (Exception e) {
            MyAlert.alertWin(this, e.toString());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        try {
            createModels(intent);
        } catch (Exception e) {
            MyAlert.alertWin(this, e.toString());
        }
    }

    private void createModels(Intent intent) {

        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        String title = intent.getStringExtra("title");
        actionBar.setTitle(title);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.show();

        setContentView(R.layout.main);
        ListView lv = (ListView) findViewById(R.id.browse_list);

        int make_id = intent.getIntExtra("make_id", 0);
        ModelsList modelsList = new ModelsList(this, R.layout.make_item, make_id, title);
        lv.setAdapter(modelsList);
        modelsList.loadModelsList();
        lv.setOnItemClickListener(modelsList.getClickListener());

        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = (AdView) findViewById(R.id.ad_view);
        adView.loadAd(adRequest);
    }
}
