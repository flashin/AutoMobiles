package ge.drivers.automobiles;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import ge.drivers.automobiles.fragments.AboutFragment;
import ge.drivers.automobiles.fragments.BrowseFragment;
import ge.drivers.automobiles.fragments.FavoritesFragment;
import ge.drivers.automobiles.lib.BitmapCache;
import ge.drivers.automobiles.lib.MyAlert;
import ge.drivers.automobiles.modules.UpdateDB;

public class MainActivity extends ActionBarActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //init image cache
            BitmapCache.getInstance().initCache(this);

            createTabs(getIntent());
        } catch (Exception e) {
            //alert exception
            MyAlert.alertWin(this, e.toString());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        try {
            ActionBar actionBar = this.getActionBar();
            if (actionBar.getTabCount() > 0) {
                actionBar.removeAllTabs();
            }
            createTabs(intent);
        } catch (Exception e) {
            //alert exception
            MyAlert.alertWin(this, e.toString());
        }
    }

    private void createTabs(Intent intent) {

        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        Tab[] tabs = new Tab[3];
        tabs[0] = actionBar.newTab().setText(R.string.app_browse).setTabListener(new TabListener<BrowseFragment>(
                this, "browse", BrowseFragment.class));
        tabs[1] = actionBar.newTab().setText(R.string.app_favorites).setTabListener(new TabListener<FavoritesFragment>(
                this, "favorites", FavoritesFragment.class));
        tabs[2] = actionBar.newTab().setText(R.string.app_about).setTabListener(new TabListener<AboutFragment>(
                this, "about", AboutFragment.class));

        int active_tab = intent.getIntExtra("active_tab", 0);
        for (int i = 0; i < tabs.length; i++) {
            if (active_tab == i) {
                actionBar.addTab(tabs[i], true);
            } else {
                actionBar.addTab(tabs[i]);
            }
        }

        actionBar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {

        private Fragment mFragment;
        private final ActionBarActivity mActivity;
        private final String mTag;
        private final Class<T> mClass;

        public TabListener(ActionBarActivity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            // Check if the fragment is already initialized
            if (mFragment == null) {
                // If not, instantiate and add it to the activity
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                // If it exists, simply attach it in order to show it
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                // Detach the fragment, because another one is being attached
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_update_db:
                updateDatabase();
                return true;
            default:
                return false;
        }
    }

    public void updateDatabase() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(this.getString(R.string.update_db_title));
        dialog.setMessage(this.getString(R.string.update_db_question));

        String ok_but = "Yes";
        String no_but = "No";
        final Context context = this;
        dialog.setPositiveButton(ok_but, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                UpdateDB UpdateDB = new UpdateDB(context);
                UpdateDB.execute(new String[]{});
            }
        });
        dialog.setNegativeButton(no_but, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
