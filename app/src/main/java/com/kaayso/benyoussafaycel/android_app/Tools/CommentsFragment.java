package com.kaayso.benyoussafaycel.android_app.Tools;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kaayso.benyoussafaycel.android_app.Home.HomeActivity;
import com.kaayso.benyoussafaycel.android_app.Models.Comment;
import com.kaayso.benyoussafaycel.android_app.Models.Like;
import com.kaayso.benyoussafaycel.android_app.Models.Photo;
import com.kaayso.benyoussafaycel.android_app.Models.User;
import com.kaayso.benyoussafaycel.android_app.Models.UserAccountSettings;
import com.kaayso.benyoussafaycel.android_app.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BenyoussaFaycel on 31/03/2018.
 */

public class CommentsFragment extends Fragment {
    private static final String TAG = "CommentsFragment";
    private Photo mphoto;
    private ImageView mBackArrow, mSend;
    private EditText mComment;
    private ArrayList<Comment> mcomments;
    private Bundle bundle;
    private ListView mListView;
    private Context mcontext;

    public CommentsFragment() {
        super();
        setArguments(new Bundle());
    }

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAutListener;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;
    private DatabaseMethods databaseMethods;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        bundle = this.getArguments();
        mSend = (ImageView) view.findViewById(R.id.ivPostComment);
        mBackArrow = (ImageView) view.findViewById(R.id.back_Arrow);
        mComment = (EditText) view.findViewById(R.id.comment);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mcomments = new ArrayList<>();
        mcontext =getActivity();


        try {
            mphoto = getPhoto();

        }catch (NullPointerException e){
            Log.d(TAG, "onCreateView: NullPointerException : photo is not in the bundle: "+ e.getMessage());
        }
        setupAuth();
        setupWidgets();


        return view;
    }


    private String getCallingActivity(){
        Log.d(TAG, "getPhoto: Getting activity from bundle: "+ getArguments());

        if (bundle != null)return bundle.getString("HomeActivity");
        return null;
    }

    private Photo getPhoto(){
        Log.d(TAG, "getPhoto: Getting photo from bundle: "+ getArguments());

        if (bundle != null)return bundle.getParcelable("photo");
        return null;
    }

    private void setupWidgets(){
        Log.d(TAG, "setupWidgets: setup comment widgets");


        CommentListAdapter commentListAdapter = new CommentListAdapter(mcontext,
                R.layout.layout_comment, mcomments);
        mListView.setAdapter(commentListAdapter);


        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Back arrow pressed");
                if (getActivity()!=null){
                    try{if (getCallingActivity().equals("HomeActivity")){
                        getActivity().getSupportFragmentManager().popBackStack();
                        ((HomeActivity)getActivity()).showLayout();
                    }
                    else {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }}
                    catch (NullPointerException e){
                        Log.d(TAG, "onClick: NullPointerException"+e.getMessage());
                    }
                }
                else {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Back arrow pressed");
                String user_comment = mComment.getText().toString();

                if(!user_comment.equals("")){
                    Log.d(TAG, "onClick: Adding new comment : "+ user_comment);
                    addNewComment(user_comment);
                    mComment.setText("");
                    closeKeyBoard();

                }else {
                    Toast.makeText(mcontext, "Vous ne pouvez pas envoyer un commentaire vide.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void addNewComment(String newComment){
        Log.d(TAG, "addNewComment: adding ");

        String commentID = mdatabaseReference.push().getKey();

        Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setDate_posted(getTimeStamp());
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // insert in photos node
        mdatabaseReference.child("photos").child(mphoto.getPhoto_id())
                .child("comments").child(commentID).setValue(comment);

        // insert in user photos node
        mdatabaseReference.child("user_photos").child(mphoto.getUser_id())
                .child(mphoto.getPhoto_id())
                .child("comments").child(commentID).setValue(comment);
    }

    private String getTimeStamp() {
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy' 'HH:mm:ss' '", Locale.FRANCE);
        date.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        return date.format(new Date());

    }

    private void closeKeyBoard(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager inputMethodManager = (InputMethodManager) mcontext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
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

            if(mcomments.size() == 0){
                mcomments.clear();
                Comment head = new Comment();
                head.setComment(mphoto.getCaption());
                head.setDate_posted(mphoto.getDate_created());
                head.setUser_id(mphoto.getUser_id());
                mcomments.add(head);
                mphoto.setComments(mcomments);
                setupWidgets();
            }

            //Add listener on the node for updating in real time
            mdatabaseReference.child("photos")
                .child(mphoto.getPhoto_id())
                .child("comments").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Query query = mdatabaseReference.child("photos")
                        .orderByChild("photo_id").equalTo(mphoto.getPhoto_id());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: query get photos user from DB.");
                        for (DataSnapshot snap: dataSnapshot.getChildren()){
                            Map<String, Object> map = (HashMap<String,Object>) snap.getValue();

                            Photo photo = buildPhoto(map);
                            mcomments.clear();
                            Comment head = new Comment();

                            head.setComment(mphoto.getCaption());
                            head.setDate_posted(mphoto.getDate_created());
                            head.setUser_id(mphoto.getUser_id());
                            mcomments.add(head);

                            for(DataSnapshot ds : snap.child("comments").getChildren()){
                                Comment comment = new Comment();
                                comment.setUser_id(ds.getValue(Comment.class).getUser_id());
                                comment.setDate_posted(ds.getValue(Comment.class).getDate_posted());
                                comment.setComment(ds.getValue(Comment.class).getComment());
                                mcomments.add(comment);
                            }

                            photo.setComments(mcomments);
                            mphoto = photo;
                            setupWidgets();

                    /*List<Like> likes = new ArrayList<>();
                    for(DataSnapshot ds : snap.child("likes").getChildren()){
                        Like like = new Like();
                        like.setUser_id(ds.getValue(Like.class).getUser_id());
                        likes.add(like);
                    }*/

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: query canceled");
                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
