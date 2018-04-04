package com.kaayso.benyoussafaycel.android_app.Tools;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kaayso.benyoussafaycel.android_app.Models.Comment;
import com.kaayso.benyoussafaycel.android_app.Models.Like;
import com.kaayso.benyoussafaycel.android_app.Models.Photo;
import com.kaayso.benyoussafaycel.android_app.Models.User;
import com.kaayso.benyoussafaycel.android_app.Models.UserAccountSettings;
import com.kaayso.benyoussafaycel.android_app.Profile.ProfileFragment;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.BotNavView;
import com.kaayso.benyoussafaycel.android_app.Tools.GridImageAdapter;
import com.kaayso.benyoussafaycel.android_app.Tools.SquareImageView;
import com.kaayso.benyoussafaycel.android_app.Tools.UnivImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BenyoussaFaycel on 31/03/2018.
 */

public class PostFragment extends Fragment {
    private static final String TAG = "PostFragment";
    public interface OnCommentSelectedListener{
        void OnCommentSelectedListener(Photo photo);
    }

    private OnCommentSelectedListener monCommentSelectedListener;
    private Bundle bundle;
    private int mActivityNumber=0;
    private Photo mphoto;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private SquareImageView mImageView;
    private TextView mcomments, mcaption, musername, matUsername, mDate , mLikes;
    private ImageView mBack_arrow, mMenu, mheart, mheartRed, mComment;
    private CircleImageView mProfilPhoto;
    private Heart mlike;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAutListener;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;

    private String mUsername="";
    private String mPhotoProfileUrl="";
    private Boolean mPhotoLikedByCurrentUser;
    private UserAccountSettings mUserAccountSettings;
    private GestureDetector mgestureDetector;
    private StringBuilder mUsers;
    private String mLikesDisplay;
    private User mCurrentUser;

    public PostFragment(){
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        mImageView = (SquareImageView) view.findViewById(R.id.post_img);
        bottomNavigationViewEx = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mBack_arrow =(ImageView) view.findViewById(R.id.back_Arrow);
        mMenu =(ImageView) view.findViewById(R.id.ivMenu_post);
        mheart =(ImageView) view.findViewById(R.id.iv_heart);
        mheartRed =(ImageView) view.findViewById(R.id.iv_heart_red);
        mComment =(ImageView) view.findViewById(R.id.iv_comment);
        mProfilPhoto =(CircleImageView) view.findViewById(R.id.profile_photo);
        mcaption = (TextView) view.findViewById(R.id.image_caption);
        musername = (TextView) view.findViewById(R.id.username);
        matUsername = (TextView) view.findViewById(R.id.Atusername);
        mLikes = (TextView) view.findViewById(R.id.image_likes);
        mDate = (TextView) view.findViewById(R.id.tvDate);
        mcomments = (TextView) view.findViewById(R.id.image_comments);
        mgestureDetector = new GestureDetector(getActivity(), new GestureListener());

        mlike = new Heart(mheart,mheartRed);

        bundle = this.getArguments();

        setupBottomNavigationView();
        setupAuth();
        return view;


    }

    public Photo buildPhoto (Map<String,Object> map){
        Log.d(TAG, "buildPhoto: building photo");
        Photo photo = new Photo();
        photo.setCaption(map.get("caption").toString());
        photo.setDate_created(map.get("date_created").toString());
        photo.setImage_path(map.get("image_path").toString());
        photo.setPhoto_id(map.get("photo_id").toString());
        photo.setTags(map.get("tags").toString());
        photo.setUser_id(map.get("user_id").toString());
        photo.setVisibility(map.get("visibility").toString());
        photo.setGroup_id(map.get("group_id").toString());

        return photo;
    }

    private void init(){
        try {
            //mphoto = getPhoto();

            mActivityNumber = getActivityNumber();
            UnivImageLoader.setImage(getPhoto().getImage_path(), mImageView, null, "");
            String photo_id = getPhoto().getPhoto_id();

            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            final Query query = myRef.child("photos").orderByChild("photo_id").equalTo(photo_id);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap: dataSnapshot.getChildren()) {
                        Map<String, Object> map = (HashMap<String, Object>) snap.getValue();
                        Photo photo = buildPhoto(map);

                        ArrayList<Comment> mcomments = new ArrayList<>();

                        for (DataSnapshot ds : snap.child("comments").getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(ds.getValue(Comment.class).getUser_id());
                            comment.setDate_posted(ds.getValue(Comment.class).getDate_posted());
                            comment.setComment(ds.getValue(Comment.class).getComment());
                            mcomments.add(comment);
                        }
                        photo.setComments(mcomments);
                        mphoto = photo;
                        getPhotoParams();
                        getCurrentUser();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query canceled");
                }
            });
        }catch (NullPointerException e){
            Log.d(TAG, "onCreateView: NullPointerException : photo is not in the bundle: "+ e.getMessage());
        }
    }

    //if fragment is attached to activity then call init()
    @Override
    public void onResume() {
        super.onResume();
        if(isAdded())init();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            monCommentSelectedListener = (OnCommentSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.d(TAG, "onAttach: ClassCastException: "+e.getMessage());
        }
    }

    private void getPhotoParams(){
        final ArrayList<Photo> photos_user = new ArrayList<>();
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        //Querry : User id of current photo owner
        final Query query = myRef.child("user_account_settings").orderByChild("user_id")
                .equalTo(mphoto.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: query get photos user from DB.");
                for (DataSnapshot snap: dataSnapshot.getChildren()){
                    mUserAccountSettings = snap.getValue(UserAccountSettings.class);
                }
                Log.d(TAG, "URL photo: "+ mUserAccountSettings.getProfile_photo());

                //setupWidgets();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query canceled");
            }
        });
    }

    private void setupWidgets(){
        UnivImageLoader.setImage(mUserAccountSettings.getProfile_photo(), mProfilPhoto, null,"");
        musername.setText(mUserAccountSettings.getUsername());
        matUsername.setText("@"+mUserAccountSettings.getUsername());
        mDate.setText(mphoto.getDate_created());
        mcaption.setText(mphoto.getCaption());
        mLikes.setText(mLikesDisplay);

        if(mphoto.getComments().size() > 1){
            mcomments.setText("Voir les "+ (mphoto.getComments().size()) + " commentaires...");
        }else if(mphoto.getComments().size() == 1){
            mcomments.setText("Voir le commentaire");
        }
        else if(mphoto.getComments().size() == 0){
            mcomments.setText("");
        }

        mcomments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to comment fragment.");
                monCommentSelectedListener.OnCommentSelectedListener(mphoto);
            }
        });

        mBack_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to back");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to back");
                monCommentSelectedListener.OnCommentSelectedListener(mphoto);
            }
        });

        if(mPhotoLikedByCurrentUser){
            mheart.setVisibility(View.GONE);
            mheartRed.setVisibility(View.VISIBLE);
            mheartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: red heart touched");
                    return mgestureDetector.onTouchEvent(event);
                }
            });
        }
        else {
            mheart.setVisibility(View.VISIBLE);
            mheartRed.setVisibility(View.GONE);
            mheart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: white heart touched");
                    return mgestureDetector.onTouchEvent(event);
                }
            });
        }

    }
    private Photo getPhoto(){
        Log.d(TAG, "getPhoto: Getting photo from bundle: "+ getArguments());

        if (bundle != null)return bundle.getParcelable("photo");
        return null;
    }

    private int getActivityNumber(){
        Log.d(TAG, "getPhoto: Getting activity number from bundle: "+ getArguments());

        if (bundle != null) return bundle.getInt("acivity number");
        return 0;
    }


    private void getLikes(){
        Log.d(TAG, "getLikes: getting likes string");
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        //Query: Getting all likes
        final Query query = myRef.child("photos").child(mphoto.getPhoto_id())
                .child("likes");

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers = new StringBuilder();
                //Query : getting users id who liked the current photo
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                    final Query query = myRef.child("users").orderByChild("user_id")
                            .equalTo(snap.getValue(Like.class).getUser_id());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snap : dataSnapshot.getChildren()){
                                Log.d(TAG, "onDataChange: found like: "+ snap.getValue(User.class).getUsername());

                                mUsers.append(snap.getValue(User.class).getUsername());
                                mUsers.append(",");

                            }

                            String[] splitUsers = mUsers.toString().split(",");
                            if (mUsers.toString().contains(mCurrentUser.getUsername()+",")){
                                mPhotoLikedByCurrentUser= true;
                            }
                            else {
                                mPhotoLikedByCurrentUser = false;
                            }

                            if (splitUsers.length == 1){
                                mLikesDisplay = "Likée par " + splitUsers[0] + ".";
                            }else if (splitUsers.length == 2){
                                mLikesDisplay = "Likée par " + splitUsers[0] + " et " + splitUsers[1] + ".";
                            }
                            else if (splitUsers.length ==3){
                                mLikesDisplay = "Likée par " + splitUsers[0] + ", " + splitUsers[1] + " et " + splitUsers[2] + ".";

                            }
                            else if (splitUsers.length  > 3){
                                mLikesDisplay = "Likée par  " + splitUsers[0] + ", " + splitUsers[1] + ", " + splitUsers[2]
                                        + " et " + (splitUsers.length - 3) + " autres personnes...";
                            }
                            setupWidgets();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                // NO LIKES
                if (! dataSnapshot.exists()){
                    mLikesDisplay ="";
                    mPhotoLikedByCurrentUser = false;
                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCurrentUser(){
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        final Query query = myRef.child("users").orderByChild("user_id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: the user is founded.");
                for (DataSnapshot snap: dataSnapshot.getChildren()){
                    mCurrentUser = snap.getValue(User.class);
                }
                getLikes();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query canceled");
            }
        });
    }



    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp: onSingleTapUp detected");
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            //Query : User id of current photo owner
            final Query query = myRef.child("photos").child(mphoto.getPhoto_id())
                    .child("likes");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Browse all likes of current photo
                    for (DataSnapshot snap : dataSnapshot.getChildren()){
                        String keyId = snap.getKey();
                        // Case 1 : The user has already liked this photo
                        if (mPhotoLikedByCurrentUser && snap.getValue(Like.class).getUser_id()
                                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            myRef.child("photos").child(mphoto.getPhoto_id())
                                    .child("likes").child(keyId).removeValue();

                            myRef.child("user_photos")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mphoto.getPhoto_id())
                                    .child("likes").child(keyId).removeValue();

                            mlike.toggleLike();
                            getLikes();
                        }

                        // Case 2 : The user hasn't liked this photo
                        else if(!mPhotoLikedByCurrentUser){
                            //Add a new like
                            addNewLike();
                            break;
                        }

                    }
                    if (!dataSnapshot.exists()){
                        //Add a new like
                        addNewLike();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }

    private void addNewLike(){
        Log.d(TAG, "addNewLike: adding new like");
        String newID = mdatabaseReference.push().getKey();
        Like myLike = new Like(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mdatabaseReference.child("photos").child(mphoto.getPhoto_id())
                .child("likes").child(newID).setValue(myLike);

        mdatabaseReference.child("user_photos")
                .child(mphoto.getUser_id())
                .child(mphoto.getPhoto_id())
                .child("likes").child(newID).setValue(myLike);

        mlike.toggleLike();
        getLikes();

    }

    /**
    * BottomNavigationView setup
   */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setUpBottomNavigationView: setting up BottomNavigationView");
        BotNavView.setupBottomNavigationView(bottomNavigationViewEx);
        BotNavView.enableNavigation(getActivity(),getActivity(), bottomNavigationViewEx);
        // When activity is visited set check on item
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);
        menuItem.setChecked(true);
    }

    /*
       Authentification on firebase
    */
    public void setupAuth(){
        Log.d(TAG, "setupAuth: started.");
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

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
