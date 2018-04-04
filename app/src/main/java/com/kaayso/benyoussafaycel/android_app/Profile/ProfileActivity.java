package com.kaayso.benyoussafaycel.android_app.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.kaayso.benyoussafaycel.android_app.Models.Photo;
import com.kaayso.benyoussafaycel.android_app.Models.User;
import com.kaayso.benyoussafaycel.android_app.Tools.CommentsFragment;
import com.kaayso.benyoussafaycel.android_app.Tools.PostFragment;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.ViewProfileFragment;

/**
 * Created by BenyoussaFaycel on 16/03/2018.
 */

public class ProfileActivity extends AppCompatActivity implements  ProfileFragment.OnSelectedImageListener,
        ViewProfileFragment.OnSelectedImageListener,PostFragment.OnCommentSelectedListener{
    private static final String TAG = "ProfileActivity";
    private Context mContext = ProfileActivity.this;
    private static final int NUM_ACTIVITY =4;
    private static final int NUM_COLS = 3;
    private ProgressBar mpg;
    private ImageView mprofilePhoto;


    @Override
    public void OnCommentSelectedListener(Photo photo) {
        Log.d(TAG, "OnCommentSelectedListener: selected a comment");
        CommentsFragment commentsFragment = new CommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("photo", photo);
        commentsFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, commentsFragment);
        transaction.addToBackStack("CommentsFragment");
        transaction.commit();
    }

    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image from gridView: "+ photo.toString());

        PostFragment postFragment =new PostFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("photo", photo);
        bundle.putInt("acivity number",activityNumber);
        postFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, postFragment);
        transaction.addToBackStack("PostFragment");
        transaction.commit();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started.");


        init();
       /* setupBottomNavigationView();
        setupWidgets();
        setupToolbar();
        setProfileImage();
        imgsGridsetup();*/
    }

    private void init(){
        Log.d(TAG, "init: profile_fragment");
        Intent intent = getIntent();
        if(intent.hasExtra("calling activity")){
            Log.d(TAG, "init: searching for user object attached as intent extra");

            if(intent.hasExtra("user")){
                Log.d(TAG, "init: navigating to profile user");
                User user = intent.getParcelableExtra("user");
                if(!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    ViewProfileFragment viewProfileFragment = new ViewProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", intent.getParcelableExtra("user"));
                    viewProfileFragment.setArguments(bundle);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, viewProfileFragment);
                    transaction.addToBackStack("ViewProfileFragment");
                    transaction.commit();
                }else {
                    Log.d(TAG, "init: navigating to own profile");
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, profileFragment);
                    transaction.addToBackStack("profile_fragment");
                    transaction.commit();
                }

            }else
            {
                Toast.makeText(mContext,"Une erreur s'est produite...",Toast.LENGTH_SHORT).show();
            }
            
        }else {
            Log.d(TAG, "init: navigating to own profile");
            ProfileFragment profileFragment = new ProfileFragment();
            FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, profileFragment);
            transaction.addToBackStack("profile_fragment");
            transaction.commit();
        }
        
        
    }



}
