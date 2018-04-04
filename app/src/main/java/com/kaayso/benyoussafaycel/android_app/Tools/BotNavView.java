package com.kaayso.benyoussafaycel.android_app.Tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kaayso.benyoussafaycel.android_app.Home.HomeActivity;
import com.kaayso.benyoussafaycel.android_app.Notifs.NotifsActivity;
import com.kaayso.benyoussafaycel.android_app.Profile.ProfileActivity;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Search.SearchActivity;
import com.kaayso.benyoussafaycel.android_app.Adding.ShareActivity;

/**
 * Created by BenyoussaFaycel on 16/03/2018.
 */

public class BotNavView {
    private static final String TAG = "BottomNavigationViewHel";


    /*
    * Setting Bottom navigation view
     */
    public static void setupBottomNavigationView ( BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }


    /*
    * Navigate between Activities
     */

    public static void enableNavigation(final Context context, final Activity activity, BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_house:
                        Intent intent1  = new Intent(context, HomeActivity.class);//NUM_ACTIVITY = 0
                        context.startActivity(intent1);
                        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_search:
                        Intent intent2  = new Intent(context, SearchActivity.class);//NUM_ACTIVITY = 1
                        context.startActivity(intent2);
                        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_add:
                        Intent intent3  = new Intent(context, ShareActivity.class);//NUM_ACTIVITY = 2
                        context.startActivity(intent3);
                        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_alert:
                        Intent intent4  = new Intent(context, NotifsActivity.class);//NUM_ACTIVITY = 3
                        context.startActivity(intent4);
                        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_profil:
                        Intent intent5  = new Intent(context, ProfileActivity.class);//NUM_ACTIVITY = 4
                        context.startActivity(intent5);
                        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                }
                return false;
            }
        });
    }
}
