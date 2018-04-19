package com.kaayso.benyoussafaycel.android_app.Group;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kaayso.benyoussafaycel.android_app.Adding.GalleryFragment;
import com.kaayso.benyoussafaycel.android_app.Adding.PhotoFragment;
import com.kaayso.benyoussafaycel.android_app.Home.FriendsFragment;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.BotNavView;
import com.kaayso.benyoussafaycel.android_app.Tools.SectionsPagerAdapter;

/**
 * Created by BenyoussaFaycel on 16/03/2018.
 */

public class GroupActivity extends AppCompatActivity {
    private static final String TAG = "GroupActivity";
    private Context mContext = GroupActivity.this;
    private static final int NUM_ACTIVITY =3;
    private ViewPager mviewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Log.d(TAG, "onCreate: started.");

        mviewPager = (ViewPager) findViewById(R.id.viewpager_container);

        setupBottomNavigationView();
        setupViewPager();
    }

    private  void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NewGroupFragment());
        adapter.addFragment(new JoinGroupFragment());

        mviewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mviewPager);

        tabLayout.getTabAt(0).setText("Créer un groupe");
        tabLayout.getTabAt(1).setText("Rejoindre un groupe");
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
