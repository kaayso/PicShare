package com.kaayso.benyoussafaycel.android_app.Adding;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.BotNavView;
import com.kaayso.benyoussafaycel.android_app.Tools.Permissions;
import com.kaayso.benyoussafaycel.android_app.Tools.SectionsPagerAdapter;
import com.kaayso.benyoussafaycel.android_app.Tools.SectionsStatePagerAdapter;

/**
 * Created by BenyoussaFaycel on 16/03/2018.
 */

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";
    private Context mContext = ShareActivity.this;
    private static final int NUM_ACTIVITY =0;
    private static final int MY_PERMISSIONS_REQUEST =1;

    private ViewPager mviewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.d(TAG, "onCreate: started.");
        if (checkPermissionsArray(Permissions.PERMISSIONS)){
            setupViewPager();
        }else
        {
            verifyPermissions(Permissions.PERMISSIONS);
        }

        setupBottomNavigationView();
    }
    public int getTask() {
        int task = getIntent().getFlags();
        Log.d(TAG, "getTask: Task : " + task);
        return task;
    }
    private  void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new  PhotoFragment());

        mviewPager =(ViewPager) findViewById(R.id.viewpager_container);
        mviewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabstop_share);
        tabLayout.setupWithViewPager(mviewPager);

        tabLayout.getTabAt(0).setText("Gallerie");
        tabLayout.getTabAt(1).setText("Photos");

    }
    /*
        return fragment #
        Gallerie = 0
        Photo = 1
     */
    public int getCurrentFragment(){
        return mviewPager.getCurrentItem();
    }
    /*
            Check array of permissions
         */
    private boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");
        for(int i=0 ; i< permissions.length ; i++){
            if (!checkPermissions(permissions[i])){
                return false;
            }
        }
        return true;
    }

    private void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions");
        ActivityCompat.requestPermissions(this,permissions,MY_PERMISSIONS_REQUEST);
    }

    /*
        Check a single permission is it has been veirified
     */
    public boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission.");
        int permiRequest = ActivityCompat.checkSelfPermission(this,permission);
        if (permiRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: Permission was not garanted for "+ permission);
            return false;
        }
        Log.d(TAG, "checkPermissions: Permission was garanted for "+ permission);
        return true;
    }



    /*
   * BottomNavigationView setup
   */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setUpBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BotNavView.setupBottomNavigationView(bottomNavigationViewEx);
        BotNavView.enableNavigation(mContext,this, bottomNavigationViewEx);
        // When activity is visited set check on item
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(NUM_ACTIVITY);
        menuItem.setChecked(true);
    }



}
