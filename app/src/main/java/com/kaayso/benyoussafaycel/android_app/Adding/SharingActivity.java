package com.kaayso.benyoussafaycel.android_app.Adding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaayso.benyoussafaycel.android_app.Models.Group;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.DatabaseMethods;
import com.kaayso.benyoussafaycel.android_app.Tools.UnivImageLoader;

/**
 * Created by BenyoussaFaycel on 30/03/2018.
 */

public class SharingActivity extends AppCompatActivity{
    private static final String TAG = "SharingActivity";
    private ImageView mBackArrow;
    private TextView mShare, tv_private;
    private static final String APPEND = "file://";
    private int countImg = 0;
    private EditText mDescription;
    private String mImgUrl;
    private CheckBox checkBox;
    private ProgressBar mprogressBar;
    private Bitmap bitmap;



    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAutListener;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;
    private DatabaseMethods mdatabaseMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);
        Log.d(TAG, "onCreate: started got : " + getIntent().getStringExtra("Selected Image"));

        mBackArrow = (ImageView) findViewById(R.id.ivbackArrow);
        mShare = (TextView) findViewById(R.id.tvShare);
        tv_private = (TextView) findViewById(R.id.tv_private);
        final Context mcontext = SharingActivity.this;
        mdatabaseMethods = new DatabaseMethods(mcontext);
        mDescription = (EditText) findViewById(R.id.description);
        mImgUrl = getIntent().getStringExtra("Selected Image");
        mprogressBar = (ProgressBar) findViewById(R.id.progressBar);
        mprogressBar.setVisibility(View.GONE);
        checkBox = (CheckBox) findViewById(R.id.checkbox);

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Back to gallery fragment.");
                finish();
            }
        });

        if(getIntent().hasExtra("PublishingGroupId")) {
            checkBox.setVisibility(View.INVISIBLE);
            tv_private.setVisibility(View.INVISIBLE);
        }
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate to final share screen.");
                //Upload image to Firebase
                String description = mDescription.getText().toString();
                String visibility = "public";

                // publishing for a GroupFragment
                if(getIntent().hasExtra("PublishingGroupId")){
                    visibility = "protected";
                    //came from gallery
                    if(getIntent().hasExtra("Selected Image")) {
                        if (!description.equals("")) {
                            Toast.makeText(mcontext , "Chargement de la photo...",Toast.LENGTH_SHORT ).show();
                            mprogressBar.setVisibility(View.VISIBLE);
                            mdatabaseMethods.uploadGroupPhoto("new_photo", getIntent().getStringExtra("PublishingGroupId")
                                    , description, visibility, countImg, null, mImgUrl);
                        } else {
                            Toast.makeText(mcontext, "Complétez le champ description", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else if(getIntent().hasExtra("Selected Bitmap")){
                        bitmap = (Bitmap) getIntent().getParcelableExtra("Selected Bitmap");
                        if (!description.equals("")) {
                            Toast.makeText(mcontext , "Chargement de la photo...",Toast.LENGTH_SHORT ).show();
                            mprogressBar.setVisibility(View.VISIBLE);
                            mdatabaseMethods.uploadGroupPhoto("new_photo", getIntent().getStringExtra("PublishingGroupId")
                                    , description, visibility, countImg, bitmap, null);
                        } else {
                            Toast.makeText(mcontext, "Complétez le champ description", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                // publishing for HomeFragment
                else if(!getIntent().hasExtra("PublishingGroupId")){
                    //came from gallery
                    if(getIntent().hasExtra("Selected Image")){
                        if (checkBox.isChecked() && !description.equals("")){
                            Toast.makeText(mcontext , "Chargement de la photo...",Toast.LENGTH_SHORT ).show();
                            visibility = "private";
                            mprogressBar.setVisibility(View.VISIBLE);
                            mdatabaseMethods.uploadPhoto("new_photo", description, visibility, countImg, mImgUrl,"",null);
                        }else if(!checkBox.isChecked() && !description.equals("")){
                            Toast.makeText(mcontext , "Chargement de la photo...",Toast.LENGTH_SHORT ).show();
                            mprogressBar.setVisibility(View.VISIBLE);
                            mdatabaseMethods.uploadPhoto("new_photo", description, visibility, countImg, mImgUrl, "",null);
                        }
                        else {
                            Toast.makeText(mcontext,"Complétez le champ description",Toast.LENGTH_SHORT).show();
                        }
                    }
                    //came from camera
                    else if(getIntent().hasExtra("Selected Bitmap")){
                        bitmap = (Bitmap) getIntent().getParcelableExtra("Selected Bitmap");
                        if (checkBox.isChecked() && !description.equals("")){
                            Toast.makeText(mcontext , "Chargement de la photo...",Toast.LENGTH_SHORT ).show();
                            visibility = "private";
                            mprogressBar.setVisibility(View.VISIBLE);
                            mdatabaseMethods.uploadPhoto("new_photo", description, visibility, countImg, null,"",bitmap);
                        }else if(!checkBox.isChecked() && !description.equals("")){
                            Toast.makeText(mcontext , "Chargement de la photo...",Toast.LENGTH_SHORT ).show();
                            mprogressBar.setVisibility(View.VISIBLE);
                            mdatabaseMethods.uploadPhoto("new_photo", description, visibility, countImg, null, "",bitmap);
                        }else {
                            Toast.makeText(mcontext,"Complétez le champ description",Toast.LENGTH_SHORT).show();
                        }

                    }
                }




            }
        });


        setupAuth();
        setImage();
    }

    /*
        get image url from incoming intent and displays the chosen image
     */
    private void setImage(){
        ImageView photo = (ImageView) findViewById(R.id.imgShare);

        //came from gallery
        if(getIntent().hasExtra("Selected Image")){
            UnivImageLoader.setImage(getIntent().getStringExtra("Selected Image"), photo , null, APPEND);
            Log.d(TAG, "setImage: new image url : "+mImgUrl);
        }
        //came from camera
        else if(getIntent().hasExtra("Selected Bitmap")){
            bitmap = (Bitmap) getIntent().getParcelableExtra("Selected Bitmap");
            Log.d(TAG, "setImage: new image bitmap: "+bitmap);
            photo.setImageDrawable(new BitmapDrawable(SharingActivity.this.getResources(), bitmap));        }
    }




    /*
       Authentification on firebase
    */
    public void setupAuth(){
        Log.d(TAG, "setupAuth: started.");
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "onDataChange: image count: "+countImg);

        mAutListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: singed in: " + user.getUid());
                }else {
                    Log.d(TAG, "onAuthStateChanged: singed out");
                }
            }
        };
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                countImg = mdatabaseMethods.getImagesCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: "+countImg);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAutListener);
    }

    public void onStop() {
        super.onStop();
        if (mAutListener != null){
            mAuth.removeAuthStateListener(mAutListener);
        }
    }
}
