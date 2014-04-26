/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.widget.SearchView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import ge.drivers.automobiles.lib.MyAlert;
import ge.drivers.automobiles.modules.CarsList;

/**
 *
 * @author alexx
 */
public class SearchActivity extends ActionBarActivity {

    ListView lv = null;

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
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.show();

            setContentView(R.layout.main);
            lv = (ListView) findViewById(R.id.browse_list);

            searchResult(getIntent());

            AdRequest adRequest = new AdRequest.Builder().build();
            AdView adView = (AdView) findViewById(R.id.ad_view);
            adView.loadAd(adRequest);
        } catch (Exception e) {
            MyAlert.alertWin(this, e.toString());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        try {
            searchResult(intent);
        } catch (Exception e) {
            MyAlert.alertWin(this, e.toString());
        }
    }

    private void searchResult(Intent intent) {

        CarsList carsList = new CarsList(this, R.layout.car_spec_desc, null);
        lv.setAdapter(carsList);
        carsList.loadSpecificCars(intent);
        lv.setOnItemClickListener(carsList.getClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_activity_actions, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        //Update keyword
        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            String keyword = getIntent().getStringExtra(SearchManager.QUERY);
            searchView.setIconified(false);
            searchView.setQuery(keyword, false);
        }

        return super.onCreateOptionsMenu(menu);
    }
}
