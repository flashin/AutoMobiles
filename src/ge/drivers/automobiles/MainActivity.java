package ge.drivers.automobiles;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import ge.drivers.automobiles.fragments.AboutFragment;
import ge.drivers.automobiles.fragments.BrowseFragment;
import ge.drivers.automobiles.lib.MyAlert;

public class MainActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ActionBar actionBar = this.getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayShowTitleEnabled(false);

            Tab[] tabs = new Tab[3];
            tabs[0] = actionBar.newTab().setText(R.string.app_browse).setTabListener(new TabListener<BrowseFragment>(
                    this, "browse", BrowseFragment.class));            
            tabs[1] = actionBar.newTab().setText(R.string.app_favorites).setTabListener(new TabListener<BrowseFragment>(
                    this, "favorites", BrowseFragment.class));            
            tabs[2] = actionBar.newTab().setText(R.string.app_about).setTabListener(new TabListener<AboutFragment>(
                    this, "about", AboutFragment.class));
            for (int i = 0; i < tabs.length; i++){
                actionBar.addTab(tabs[i]);
            }

            actionBar.show();
        } catch (Exception e) {
            //alert exception
            MyAlert.alertWin(this, e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {

        private Fragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;

        public TabListener(Activity activity, String tag, Class<T> clz) {
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
}
