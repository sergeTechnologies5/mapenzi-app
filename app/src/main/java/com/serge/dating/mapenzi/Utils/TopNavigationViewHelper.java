package com.serge.dating.mapenzi.Utils;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.serge.dating.mapenzi.Main.MainActivity;
import com.serge.dating.mapenzi.Matched.Matched_Activity;
import com.serge.dating.mapenzi.Profile.Profile_Activity;
import com.serge.dating.mapenzi.R;


public class TopNavigationViewHelper {

    private static final String TAG = "TopNavigationViewHelper";

    public static void setupTopNavigationView(BottomNavigationViewEx tv) {
        Log.d(TAG, "setupTopNavigationView: setting up navigationview");


    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_profile:
                        Intent intent2 = new Intent(context, Profile_Activity.class);
                        context.startActivity(intent2);
                        break;

                    case R.id.ic_main:
                        Intent intent1 = new Intent(context, MainActivity.class);
                        context.startActivity(intent1);
                        break;

                    case R.id.ic_matched:
                        Intent intent3 = new Intent(context, Matched_Activity.class);
                        context.startActivity(intent3);

                        break;
                }

                return false;
            }
        });
    }
}
