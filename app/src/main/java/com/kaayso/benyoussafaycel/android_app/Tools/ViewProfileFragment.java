package com.kaayso.benyoussafaycel.android_app.Tools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kaayso.benyoussafaycel.android_app.Models.Comment;
import com.kaayso.benyoussafaycel.android_app.Models.Like;
import com.kaayso.benyoussafaycel.android_app.Models.Photo;
import com.kaayso.benyoussafaycel.android_app.Models.User;
import com.kaayso.benyoussafaycel.android_app.Models.UserAccountSettings;
import com.kaayso.benyoussafaycel.android_app.Models.UserGlobalSettings;
import com.kaayso.benyoussafaycel.android_app.Profile.ProfileActivity;
import com.kaayso.benyoussafaycel.android_app.Profile.SettingsActivity;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Search.SearchActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BenyoussaFaycel on 18/03/2018.
 */

public class ViewProfileFragment extends Fragment {

    public interface  OnSelectedImageListener{
        void onGridImageSelected(Photo photo, int activityNumber);
    }
    private List<User> mUsers;
    private List<String> mUsersID;

    OnSelectedImageListener onSelectedImageListener;

    private static final int NUM_ACTIVITY =4;
    public Context mContext;

    private static final String TAG = "ProfileFragment";
    private CircleImageView mProfile_photo;
    private GridView mGridView;
    private TextView mPosts, mFollowers, mFollowing, mUsername, mDiscription, mGroups,mtextEditProfile;
    private ProgressBar mprogressBar;
    private ImageView profileMenu, mBackButton;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private int NUM_COLS = 3;
    private User mUser;
    private TextView mFollow, mUnfollow, mAddFriend, mRemoveFriend, mwaitingAdd;
    private int followersCount = 0;
    private int followingCount = 0;
    private int postsCount = 0;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAutListener;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_visited, container, false);
        Log.d(TAG, "onCreateView: started.");
        mProfile_photo = (CircleImageView) view.findViewById(R.id.profile_photo);
        mUsername = (TextView) view.findViewById(R.id.profileName);
        mDiscription = (TextView) view.findViewById(R.id.description);
        mFollowing = (TextView) view.findViewById(R.id.tvFollowing);
        mFollowers = (TextView) view.findViewById(R.id.tvFollowers);
        mPosts = (TextView) view.findViewById(R.id.tvPosts);
        mGroups = (TextView) view.findViewById(R.id.tvGroups);
        mprogressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        mGridView = (GridView) view.findViewById(R.id.gridView);
        bottomNavigationViewEx = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        profileMenu = (ImageView) view.findViewById(R.id.profileMenu);
        mBackButton = (ImageView) view.findViewById(R.id.back_Arrow);
        mtextEditProfile = (TextView) view.findViewById(R.id.textEditProfile);
        mContext = getActivity();
        mUsers = new ArrayList<>();
        mUsersID = new ArrayList<>();

        //On visit (widgets)
        mFollow = (TextView) view.findViewById(R.id.follow);
        mUnfollow = (TextView) view.findViewById(R.id.unfollow);
        mAddFriend = (TextView) view.findViewById(R.id.addFriend);
        mRemoveFriend = (TextView) view.findViewById(R.id.removeFriend);
        mwaitingAdd = (TextView) view.findViewById(R.id.waitingAdd);

        try {
            mUser = getUserFromBundle();
            //searchForMatch();
            init();
            Log.d(TAG, "onCreateView: user getting :" + mUser.toString());
        }catch (ClassCastException e){
            Log.d(TAG, "onCreateView: ClassCastException "+ e.getMessage());
            Toast.makeText(mContext, "Une érreur s'est produite.",Toast.LENGTH_SHORT ).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }
        setupBottomNavigationView();
        setupAuth();
        isFollowing();
        isRequestFriendwaiting();
        getFollowers();
        getFollowing();
        getPost();

        mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: follow");

                FirebaseDatabase.getInstance().getReference()
                        .child("following")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUser_id())
                        .child("user_id").setValue(mUser.getUser_id());


                FirebaseDatabase.getInstance().getReference()
                        .child("followers")
                        .child(mUser.getUser_id())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("user_id").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                setFollowbtn();
            }
        });

        mUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: unfollow");
                FirebaseDatabase.getInstance().getReference()
                        .child("following")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUser_id()).removeValue();


                FirebaseDatabase.getInstance().getReference()
                        .child("followers")
                        .child(mUser.getUser_id())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

                setUnFollowbtn();


            }
        });

        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: add to firend list");

                FirebaseDatabase.getInstance().getReference()
                        .child("friends")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUser_id())
                        .child("user_id").setValue(mUser.getUser_id());

                setAddFriendbtn();
            }
        });

        mRemoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: remove to firend list");

                FirebaseDatabase.getInstance().getReference()
                        .child("friends")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUser_id()).removeValue();

                FirebaseDatabase.getInstance().getReference()
                        .child("friends")
                        .child(mUser.getUser_id())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

                setmRemoveFriendbtn();
            }
        });

        mwaitingAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: remove to firend list");

                FirebaseDatabase.getInstance().getReference()
                        .child("friends")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUser_id()).removeValue();

                setmRemoveFriendbtn();
            }
        });

        mtextEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate to: edit profil fragment");
                Intent i = new Intent(mContext, SettingsActivity.class);
                // key/value (type of operation / where it comes from)
                i.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

            }
        });


        return view;
    }

    private void setFollowbtn(){
        Log.d(TAG, "setFollowbtn: udating following for " + FirebaseAuth.getInstance().getCurrentUser().toString());
        mFollow.setVisibility(View.GONE);
        mUnfollow.setVisibility(View.VISIBLE);
        mtextEditProfile.setVisibility(View.GONE);
    }

    private void setUnFollowbtn(){
        Log.d(TAG, "setFollowbtn: udating following for " + FirebaseAuth.getInstance().getCurrentUser().toString());
        mFollow.setVisibility(View.VISIBLE);
        mUnfollow.setVisibility(View.GONE);
        mtextEditProfile.setVisibility(View.GONE);
    }

    private void setAddFriendbtn(){
        Log.d(TAG, "setFollowbtn: udating following for " + FirebaseAuth.getInstance().getCurrentUser().toString());
        mAddFriend.setVisibility(View.GONE);
        mwaitingAdd.setVisibility(View.VISIBLE);
        mRemoveFriend.setVisibility(View.GONE);
        mtextEditProfile.setVisibility(View.GONE);
    }

    private void setWaitongFriendbtn(){
        Log.d(TAG, "setFollowbtn: udating following for " + FirebaseAuth.getInstance().getCurrentUser().toString());
        mAddFriend.setVisibility(View.GONE);
        mwaitingAdd.setVisibility(View.GONE);
        mRemoveFriend.setVisibility(View.VISIBLE);
        mtextEditProfile.setVisibility(View.GONE);
    }

    private void setmRemoveFriendbtn(){
        Log.d(TAG, "setFollowbtn: udating following for " + FirebaseAuth.getInstance().getCurrentUser().toString());
        mAddFriend.setVisibility(View.VISIBLE);
        mRemoveFriend.setVisibility(View.GONE);
        mwaitingAdd.setVisibility(View.GONE);
        mtextEditProfile.setVisibility(View.GONE);
    }

    private void setCurrentUserProfile(){
        Log.d(TAG, "setFollowbtn: udating following for " + FirebaseAuth.getInstance().getCurrentUser().toString());
        mFollow.setVisibility(View.GONE);
        mUnfollow.setVisibility(View.GONE);
        mtextEditProfile.setVisibility(View.VISIBLE);
    }

    private User getUserFromBundle(){
        Log.d(TAG, "getUserFromBundle: arguments: "+ getArguments());

        Bundle bundle = this.getArguments();

        if(bundle != null){
            return bundle.getParcelable("user");
        }else
        {
            return null;
        }

    }

    private void isFollowing(){
        Log.d(TAG, "isFollowing: look if the current user follows the target");

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("following")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild("user_id").equalTo(mUser.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found following" );
                    setFollowbtn();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void isRequestFriendwaiting(){
        Log.d(TAG, "isFollowing: look if the current user follows the target");

        final boolean[] q1 = {false};
        final boolean[] q2 = {false};

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("friends")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild("user_id").equalTo(mUser.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found match 1" );
                    setAddFriendbtn();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query query2 = databaseReference.child("friends")
                .child(mUser.getUser_id())
                .orderByChild("user_id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found match 2" );
                    if (mwaitingAdd.getVisibility() == View.VISIBLE){
                        setWaitongFriendbtn();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getFollowers(){
        Log.d(TAG, "getFollowers: count followers");
        followersCount = 0;
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("followers")
                .child(mUser.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found follower" );
                    followersCount++;
                }

                mFollowers.setText(String.valueOf(followersCount));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getFollowing(){
        Log.d(TAG, "getFollowers: count following");
        followingCount = 0;
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("following")
                .child(mUser.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found following" );
                    followingCount++;
                }

                mFollowing.setText(String.valueOf(followingCount));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getPost(){
        Log.d(TAG, "getFollowers: count posts");
        postsCount = 0;
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("user_photos")
                .child(mUser.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found post" );
                    postsCount++;
                }

                mPosts.setText(String.valueOf(postsCount));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void init(){

        //set profile widgets
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("user_account_settings")
                .orderByChild("user_id").equalTo(mUser.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found users" + ds.getValue(UserAccountSettings.class).toString());

                    UserGlobalSettings userGlobalSettings = new UserGlobalSettings();

                    userGlobalSettings.setUser(mUser);
                    userGlobalSettings.setUserAccountSettings(ds.getValue(UserAccountSettings.class));
                    setupProfileWidgets(userGlobalSettings);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set user's photos

        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        final Query query2 = myRef.child("user_photos").child(mUser.getUser_id());

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: query get photos user from DB.");
                ArrayList<Photo> photos_user = new ArrayList<>();
                for (DataSnapshot snap: dataSnapshot.getChildren()){
                    Map<String, Object> map = (HashMap<String,Object>) snap.getValue();
                    Photo photo = buildPhoto(map);

                    ArrayList<Comment> mcomments =new ArrayList<>();

                    for(DataSnapshot ds : snap.child("comments").getChildren()){
                        Comment comment = new Comment();
                        comment.setUser_id(ds.getValue(Comment.class).getUser_id());
                        comment.setDate_posted(ds.getValue(Comment.class).getDate_posted());
                        comment.setComment(ds.getValue(Comment.class).getComment());
                        mcomments.add(comment);
                    }
                    photo.setComments(mcomments);

                    List<Like> likes = new ArrayList<>();
                    for(DataSnapshot ds : snap.child("likes").getChildren()){
                        Like like = new Like();
                        like.setUser_id(ds.getValue(Like.class).getUser_id());
                        likes.add(like);
                    }
                    photo.setLikes(likes);

                    Log.d(TAG, "onDataChange: friends list size: "+ mUsers.size());

                    if(!photo.getVisibility().equals("private")){
                        photos_user.add(photo);
                    }

                }


                // Once getting all photos -> setup gridView
                setupAllImageGrid(photos_user);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query canceled");
            }
        });

    }



    private void setupAllImageGrid(final ArrayList<Photo> photos_user){
        int griWidth = getResources().getDisplayMetrics().widthPixels;
        int imgWidth = griWidth/NUM_COLS;
        mGridView.setColumnWidth(imgWidth);

        ArrayList<String> urlsPhotos = new ArrayList<>();

        for (int i = 0 ; i < photos_user.size() ; i++){
            urlsPhotos.add(photos_user.get(i).getImage_path());
        }
        GridImageAdapter gridImageAdapter = new GridImageAdapter(mContext, R.layout.layout_grid_imgview,"" ,urlsPhotos);
        mGridView.setAdapter(gridImageAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSelectedImageListener.onGridImageSelected(photos_user.get(position), NUM_ACTIVITY);

            }
        });
    }
    @Override
    public void onAttach(Context context) {

        try{
            onSelectedImageListener = (OnSelectedImageListener) getActivity();
        }catch (ClassCastException e){
            Log.d(TAG, "onAttach: ClassCastException: "+ e.getMessage());
        }
        super.onAttach(context);
    }

    private void setupProfileWidgets (UserGlobalSettings usersettings){
        Log.d(TAG, "setupProfileWidgets: setting widgets with data retrieving from Firebase: " + usersettings.toString());
        User use = usersettings.getUser();
        UserAccountSettings userAccountSettings = usersettings.getUserAccountSettings();
        mprogressBar.setVisibility(View.GONE);
        UnivImageLoader.setImage(userAccountSettings.getProfile_photo(), mProfile_photo, null,"");
        mUsername.setText(userAccountSettings.getUsername());
        mDiscription.setText(userAccountSettings.getDescription());
        mFollowing.setText(String.valueOf(userAccountSettings.getFollowing()));
        mFollowers.setText(String.valueOf(userAccountSettings.getFollowers()));
        mPosts.setText(String.valueOf(userAccountSettings.getPosts()));
        mGroups.setText(String.valueOf(userAccountSettings.getGroups()));

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to back");
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();
            }
        });
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




     /*
   * BottomNavigationView setup
   */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setUpBottomNavigationView: setting up BottomNavigationView");
        BotNavView.setupBottomNavigationView(bottomNavigationViewEx);
        BotNavView.enableNavigation(mContext,getActivity(), bottomNavigationViewEx);
        // When activity is visited set check on item
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(NUM_ACTIVITY);
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
