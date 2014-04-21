/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import ge.drivers.automobiles.lib.MyAlert;
import ge.drivers.automobiles.lib.MyResource;
import ge.drivers.automobiles.modules.AboutInfo;

/**
 *
 * @author alexx
 */
public class AboutFragment extends Fragment {

    Context context = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        context = container.getContext();
        try {
            // Inflate the layout for this fragment
            final LinearLayout aboutLayout = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "about"), container, false);

            AsyncTask loader = new AsyncTask<String, Void, Boolean>() {

                private String error = null;
                private AboutInfo about = null;

                @Override
                protected Boolean doInBackground(String... args) {

                    try {
                        about = new AboutInfo(context);

                        return true;
                    } catch (Exception e) {
                        error = e.toString();
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean result) {

                    if (error != null) {
                        MyAlert.alertWin(context, error);
                    } else {
                        try {
                            TextView txt = (TextView) aboutLayout.getChildAt(0);
                            txt.setText(context.getString(MyResource.getString(context, "total_makes")) + ": " + about.getTotalMakes());
                            
                            txt = (TextView) aboutLayout.getChildAt(1);
                            txt.setText(context.getString(MyResource.getString(context, "total_models")) + ": " + about.getTotalModels());
                            
                            txt = (TextView) aboutLayout.getChildAt(2);
                            txt.setText(context.getString(MyResource.getString(context, "total_cars")) + ": " + about.getTotalCars());
                        } catch (Exception e) {
                            MyAlert.alertWin(context, e.toString());
                        }
                    }
                }
            };
            loader.execute(new String[]{});

            return aboutLayout;
        } catch (Exception e) {
            MyAlert.alertWin(context, e.toString());
            return null;
        }
    }
}
