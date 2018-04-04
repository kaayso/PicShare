package com.kaayso.benyoussafaycel.android_app.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.BotNavView;
import com.kaayso.benyoussafaycel.android_app.Tools.DatabaseMethods;
import com.kaayso.benyoussafaycel.android_app.Tools.SectionsStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by BenyoussaFaycel on 16/03/2018.
 */

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private Context mcontext;
    public SectionsStatePagerAdapter mpagerAdapter;
    private ViewPager mviewPager;
    private RelativeLayout mrelativeLayout;
    private static final int NUM_ACTIVITY =4;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settigns);
        mcontext = SettingsActivity.this;


        Log.d(TAG, "onCreate: started.");

        ImageView backArrow = (ImageView) findViewById(R.id.back_Arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigation back to profile.");
                finish();
            }
        });

        mviewPager  = (ViewPager) findViewById(R.id.viewpager_container);
        mrelativeLayout = (RelativeLayout) findViewById(R.id.relLayout);

        setupListSettigns();
        setupFragments();
        setupBottomNavigationView();
        getExtrasIntent();
    }


    public void setViewPager( int fragNum){
        mrelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: Navigation fragement #: "+ fragNum);
        mviewPager.setAdapter(mpagerAdapter);
        mviewPager.setCurrentItem(fragNum);
    }

    private void setupFragments(){
        mpagerAdapter =  new SectionsStatePagerAdapter(getSupportFragmentManager());
        mpagerAdapter.addFragment(new EditionFragment(), "Modifier le profil");
        mpagerAdapter.addFragment(new AboutFragment(), "A propose");
        mpagerAdapter.addFragment(new LogoutFragment(), "Se deconnecter");

    }


    private void setupListSettigns(){
        Log.d(TAG, "setupSettigns: setupSettings account.");
        ListView listView = (ListView) findViewById(R.id.list_settings);

        ArrayList<String> options = new ArrayList<>();
        options.add("Modifier le profil");
        options.add("A propos");
        options.add("Se déconnecter");
        ArrayAdapter arrayAdapter =  new ArrayAdapter(mcontext, android.R.layout.simple_list_item_1 , options);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent , View view, int position, long id){
                Log.d(TAG, "onItemLongClick: navigating to fragment #: " + position);
                setViewPager(position);
            }
        });

    }

    /*
      * BottomNavigationView setup
    */

    private void setupBottomNavigationView(){
        Log.d(TAG, "setUpBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BotNavView.setupBottomNavigationView(bottomNavigationViewEx);
        BotNavView.enableNavigation(SettingsActivity.this,this, bottomNavigationViewEx);
        // When activity is visited set check on item
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(NUM_ACTIVITY);
        menuItem.setChecked(true);
    }

    private void getExtrasIntent ( ){
        Intent i = getIntent();
        Log.d(TAG, "getExtrasIntent: New image URL");
        if(i.hasExtra("Selected Image") || i.hasExtra("Selected Bitmap")) {
            if (i.getStringExtra("to fragment").equals("EditionFragment")) {

                if (i.hasExtra("Selected Image")) {
                    //set a new profile picture
                    DatabaseMethods databaseMethods = new DatabaseMethods(mcontext);
                    databaseMethods.uploadPhoto("profile_photo", null, null, 0
                            , i.getStringExtra("Selected Image"), null, null);
                } else if (i.hasExtra("Selected Bitmap")) {
                    //set a new profile picture
                    DatabaseMethods databaseMethods = new DatabaseMethods(mcontext);
                    databaseMethods.uploadPhoto("profile_photo", null, null, 0
                            , i.getStringExtra("Selected Image"), null, (Bitmap) i.getParcelableExtra("Selected Bitmap"));
                }
            }
        }

        if (i.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "getExtrasIntent: received calling activity from: "+ R.string.profile_activity);
            setViewPager(mpagerAdapter.getFragmentNumber("Modifier le profil"));

        }
    }


}
