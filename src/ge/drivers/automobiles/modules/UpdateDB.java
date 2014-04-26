/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles.modules;

import android.content.Context;
import android.os.AsyncTask;
import ge.drivers.automobiles.lib.BitmapCache;
import ge.drivers.automobiles.lib.MyAlert;
import ge.drivers.automobiles.lib.MyResource;

/**
 *
 * @author alexx
 */
public class UpdateDB extends AsyncTask<String, Void, Integer> {
    
    Context context;

    public UpdateDB(Context context) {
        
        this.context = context;
    }

    @Override
    protected Integer doInBackground(String... urls) {

        BitmapCache.getInstance().clearCache(context);
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        
        String title = context.getString(MyResource.getString(context, "update_db_title"));
        String success_mess = context.getString(MyResource.getString(context, "update_db_success"));
        MyAlert.alertSuccessWin(context, title, success_mess);
    }
}
