package com.kaayso.benyoussafaycel.android_app.Home;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kaayso.benyoussafaycel.android_app.Models.Comment;
import com.kaayso.benyoussafaycel.android_app.Models.Like;
import com.kaayso.benyoussafaycel.android_app.Models.Photo;
import com.kaayso.benyoussafaycel.android_app.Models.User;
import com.kaayso.benyoussafaycel.android_app.Models.UserAccountSettings;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.PublishingListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by BenyoussaFaycel on 16/03/2018.
 */

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private ArrayList<Photo> mphotos;
    private ArrayList<Photo> mphotos2;
    private ArrayList<String> mfollowing;
    private ArrayList<String> mFriends;
    private ArrayList<String> mFriends2;
    private ArrayList<Photo> mPaginatedPhotos;
    private int mresults;
    private ListView mListView;
    private PublishingListAdapter publishingListAdapter;
    private Context mContext;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mphotos = new ArrayList<>();
        mphotos2 = new ArrayList<>();
        mfollowing = new ArrayList<>();
        mFriends = new ArrayList<>();
        mFriends2 = new ArrayList<>();
        mContext = getActivity();

        try {
            mfollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
            getFriends ();
            getFollowing ();
        }catch (NullPointerException e){
            Log.d(TAG, "onCreateView: NullPointerException: "+ e.getMessage());
        }





        Log.d(TAG, "onDataChange: my friend  = "+ mFriends.toString());

        return view;
    }

    //all users that the current user follows
    private void getFollowing (){
        Log.d(TAG, "getFollowing: getting following");

        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        final Query query = myRef.child("following")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found following users for current user. "+ snap.child("user_id").getValue());

                    if (!mfollowing.contains(snap.child("user_id").getValue().toString())){
                        mfollowing.add(snap.child("user_id").getValue().toString());
                    }

                }

                //get photos
                getPhotosOfFollowing();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    private void getPhotosOfFollowing(){
        Log.d(TAG, "getPhotos of following users: ");

        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        for(int i=0; i < mfollowing.size(); i++ ){
            final Query query = myRef.child("user_photos")
                    .child(mfollowing.get(i))
                    .orderByChild("user_id")
                    .equalTo(mfollowing.get(i));

            final int size = i;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()){
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
                        if(photo.getVisibility().equals("public")){
                            mphotos.add(photo);
                        }else if(photo.getVisibility().equals("private")
                                && photo.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            mphotos.add(photo);
                        }

                    }
                    if (size >= mfollowing.size()-1 ){
                        //display photos
                        displayPhotos();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    //all users who are friend with this current user
    private void getFriends (){
        Log.d(TAG, "getFriends: getting Friends");
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("friends")
                .child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren() ){
                    Log.d(TAG, "onDataChange: match founded step 1: "+ds.child("user_id").getValue().toString());

                    final String keyID = ds.child("user_id").getValue().toString();
                    Query query = databaseReference.child("friends")
                            .child(keyID).orderByChild("user_id")
                            .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: match founded step 2 with : "+ keyID);
                            for (DataSnapshot ds: dataSnapshot.getChildren() ){
                                Log.d(TAG, "onDataChange  data retreived : "+ ds.getValue().toString());

                                if (!mFriends2.contains(keyID)){
                                    mFriends2.add(keyID);
                                }
                            }

                            try {
                                Log.d(TAG, "searchForMatch: useridList len: "+ mFriends2.size());
                                for (int i = 0; i< mFriends2.size() ; i++){
                                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                                    Query query2 = myRef.child("users").orderByChild("user_id").equalTo(mFriends2.get(i));

                                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds: dataSnapshot.getChildren() ){
                                                Log.d(TAG, "onDataChange: building user object: "+ds.getValue(User.class).getUsername());

                                                if (!mFriends.contains(ds.getValue(User.class).getUser_id())){
                                                    mFriends.add(ds.getValue(User.class).getUser_id());
                                                }
                                            }
                                            // they are friends, make something
                                            getPhotosOfFriends();                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }catch (NullPointerException e){
                                Log.d(TAG, "searchForMatch: "+e.getMessage());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }




    private void getPhotosOfFriends(){
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        for(int i=0; i < mFriends.size(); i++ ){
            final Query query = myRef.child("user_photos")
                    .child(mFriends.get(i))
                    .orderByChild("user_id")
                    .equalTo(mFriends.get(i));

            final int size = i;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snap : dataSnapshot.getChildren()){
                        Log.d(TAG, "getPhotosOfFriends: number of friends founded: "+ mFriends.size());
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
                        if(photo.getVisibility().equals("private") && !isInList(mphotos,photo)){
                            mphotos.add(photo);
                        }

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    private boolean isInList(ArrayList<Photo> photos , Photo photo) {
        for (int i = 0; i < photos.size(); i++) {
            if (photos.get(i).getPhoto_id().equals(photo.getPhoto_id())) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<String> removeDuplication(ArrayList<String> friends, ArrayList<String> following){
        for (int i= 0 ; i< friends.size(); i++){
            if(following.contains(friends.get(i))){
                friends.remove(i);
            }
        }

        return friends;
    }



    private void displayPhotos(){
        mPaginatedPhotos =new ArrayList<>();

        if(mphotos !=null){
            try {
                Collections.sort(mphotos, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });
                int iterations = mphotos.size();

                //Number of photos received to display
                if (mphotos.size() > 5){
                    iterations = 5;
                }

                mresults = 5;
                for(int i = 0; i<iterations ; i++){
                    mPaginatedPhotos.add(mphotos.get(i));
                }

                publishingListAdapter = new PublishingListAdapter(getActivity(), R.layout.layout_publishing_listitem, mPaginatedPhotos);
                mListView.setAdapter(publishingListAdapter);

            }catch (NullPointerException e){
                Log.d(TAG, "displayPhotos: NullPointerException: "+e.getMessage());
            }catch (IndexOutOfBoundsException e){
                Log.d(TAG, "displayPhotos: IndexOutOfBoundsException: "+e.getMessage());
            }
        }

    }

    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try {

            if(mphotos.size() > mresults){
                int iterations;

                if(mphotos.size() >= mresults+ 5){
                    Log.d(TAG, "displayMorePhotos: more than 5 photos");
                    iterations = 5;


                }else {
                    Log.d(TAG, "displayMorePhotos: less than 5 photos");
                    iterations = mphotos.size() - mresults;
                }
                for(int i = mresults ; i< iterations + mresults ; i++){
                    mPaginatedPhotos.add(mphotos.get(i));

                }
                mresults = mresults +iterations;
                publishingListAdapter.notifyDataSetChanged();
            }



        }catch (NullPointerException e){
            Log.d(TAG, "displayMorePhotos: NullPointerException"+ e.getMessage());
        }
        catch (IndexOutOfBoundsException e){
            Log.d(TAG, "displayPhotos: IndexOutOfBoundsException: "+e.getMessage());
        }
    }


}
