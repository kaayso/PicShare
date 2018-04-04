package com.kaayso.benyoussafaycel.android_app.Home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kaayso.benyoussafaycel.android_app.Authentication.StartActivity;
import com.kaayso.benyoussafaycel.android_app.Models.Photo;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.BotNavView;
import com.kaayso.benyoussafaycel.android_app.Tools.CommentsFragment;
import com.kaayso.benyoussafaycel.android_app.Tools.PublishingListAdapter;
import com.kaayso.benyoussafaycel.android_app.Tools.SectionsPagerAdapter;
import com.kaayso.benyoussafaycel.android_app.Tools.UnivImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeActivity extends AppCompatActivity implements PublishingListAdapter.OnLoadMoreItemsListener{
    private static final String TAG = "HomeActivity";
    private Context mContext = HomeActivity.this;
    private static final int NUM_ACTIVITY = 2;
    private static final int HOME_FRAG = 1;

    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener mAutListener;

    private ViewPager viewPager;
    private FrameLayout frameLayout;
    private RelativeLayout relativeLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");
        viewPager = (ViewPager) findViewById(R.id.viewpager_container);
        frameLayout =(FrameLayout) findViewById(R.id.container);
        relativeLayout =(RelativeLayout) findViewById(R.id.relLayout_home);

        setupAuth();
        initImageLoader();
        setupBottomNavigationView();
        setupViewPager();
        //mAuth.signOut();
    }


    /*
        Authentification on firebase
     */
    public void setupAuth(){
        Log.d(TAG, "setupAuth: started.");
        mAuth = FirebaseAuth.getInstance();
        mAutListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                checkUser(user);
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: singed in: " + user.getUid());
                }else {
                    Log.d(TAG, "onAuthStateChanged: singed out");
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAutListener);
        checkUser(mAuth.getCurrentUser());
        viewPager.setCurrentItem(HOME_FRAG);
    }

    public void onStop() {
        super.onStop();
        if (mAutListener != null){
            mAuth.removeAuthStateListener(mAutListener);
        }
    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hidding layout");
        relativeLayout.setVisibility(View.GONE);
        frameLayout.setVisibility(View.VISIBLE);
    }


    public void showLayout(){
        Log.d(TAG, "hideLayout: hidding layout");
        relativeLayout.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.GONE);
    }


    /*
    * View Pager setup, adding the 3 tabs : Camera, Home, Groups
     */
    private void setupViewPager (){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FriendsFragment()); // index = 0
        adapter.addFragment(new HomeFragment());// index = 1
        adapter.addFragment(new GroupsFragment());// index = 2
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_friends);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_main);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_group);

    }

    public void onCommentSelected(Photo photo, String callingActivity){
        Log.d(TAG, "onCommentSelected: Selected comments");

        CommentsFragment fragment = new CommentsFragment();
        Bundle bundle= new Bundle();
        bundle.putParcelable("photo", photo);
        bundle.putString("HomeActivity", callingActivity);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack("CommentsFragment");
        transaction.commit();
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




    private void initImageLoader(){
        UnivImageLoader univImageLoader = new UnivImageLoader(mContext);
        ImageLoader.getInstance().init(univImageLoader.getConfig());

    }

    private void checkUser( FirebaseUser user ){
        Log.d(TAG, "checkUser: Checking state of current user");
        if (user == null){
            Intent i = new Intent(mContext, StartActivity.class);
            startActivity(i);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(frameLayout.getVisibility() == View.VISIBLE){
            showLayout();

        }else {

        }
    }

    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");

        HomeFragment fragment = (HomeFragment)getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.viewpager_container + ":"+ viewPager.getCurrentItem());
        if (fragment != null){
            fragment.displayMorePhotos();
        }

    }
}
