/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.automobiles.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import ge.drivers.automobiles.lib.MyAlert;
import ge.drivers.automobiles.lib.MyResource;
import ge.drivers.automobiles.modules.CarsList;

/**
 *
 * @author alexx
 */
public class FavoritesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = container.getContext();
        try {
            LinearLayout mainLayout = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "main"), container, false);

            ListView lv = (ListView) mainLayout.findViewById(MyResource.getResource(context, "browse_list"));
            CarsList carsList = new CarsList(context, MyResource.getResource(context, "car_spec_desc"), null);
            lv.setAdapter(carsList);
            carsList.loadSpecificCars(null);
            lv.setOnItemClickListener(carsList.getClickListener());

            AdRequest adRequest = new AdRequest.Builder().build();
            AdView adView = (AdView) mainLayout.findViewById(MyResource.getResource(context, "ad_view"));
            adView.loadAd(adRequest);

            return mainLayout;
        } catch (Exception e) {
            MyAlert.alertWin(context, e.toString());
            return null;
        }
    }
}
