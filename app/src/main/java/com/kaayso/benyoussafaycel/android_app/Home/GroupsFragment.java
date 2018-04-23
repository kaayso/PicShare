package com.kaayso.benyoussafaycel.android_app.Home;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kaayso.benyoussafaycel.android_app.Adding.ShareActivity;
import com.kaayso.benyoussafaycel.android_app.Models.Comment;
import com.kaayso.benyoussafaycel.android_app.Models.Group;
import com.kaayso.benyoussafaycel.android_app.Models.Photo;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Search.SearchActivity;
import com.kaayso.benyoussafaycel.android_app.Tools.GroupListAdapter;
import com.kaayso.benyoussafaycel.android_app.Tools.PublishingListAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BenyoussaFaycel on 16/03/2018.
 */

public class GroupsFragment extends Fragment {
    private static final String TAG = "GroupsFragment";
    private Group mCurrentGroup;
    private TextView mMessage;
    private ListView mListView;

    private ArrayList<Photo> mphotos;
    private ArrayList<Photo> mPaginatedPhotos;
    private PublishingListAdapter publishingListAdapter;
    private Context mContext;
    private int mresults;


    private TextView name, description, users_number, tv_addPost;
    private CircleImageView groupPhoto;
    private ImageView add, added, menu, crown, addUser, iv_visibilityPivate, iv_visibilityPublic;
    private RelativeLayout relativeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups,container,false);
        mMessage = (TextView) view.findViewById(R.id.tv_group);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mphotos = new ArrayList<>();
        mContext = getActivity();

        name = (TextView) view.findViewById(R.id.group_name);
        description = (TextView) view.findViewById(R.id.description);
        users_number = (TextView) view.findViewById(R.id.tv_nb_users);
        tv_addPost = (TextView) view.findViewById(R.id.tv_addPost);
        groupPhoto = (CircleImageView) view.findViewById(R.id.group_photo);
        add = (ImageView) view.findViewById(R.id.add_group);
        iv_visibilityPivate = (ImageView) view.findViewById(R.id.iv_visibilityPivate);
        iv_visibilityPublic = (ImageView) view.findViewById(R.id.iv_visibilityPublic);
        added = (ImageView) view.findViewById(R.id.checked_group);
        crown = (ImageView) view.findViewById(R.id.iv_crown);
        addUser = (ImageView) view.findViewById(R.id.iv_addUser);
        menu = (ImageView) view.findViewById(R.id.iv_menu);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relLayout1);


        try {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            if(homeActivity.getIntent().hasExtra("calling fragment")){
                if(homeActivity.getIntent().getStringExtra("calling fragment").equals("GroupsFragment")){
                    // display group content
                    mCurrentGroup = homeActivity.getCurrentGroup();
                    setupWidgets();
                    getGroupPhoto();
                }
            }else{
                // no display group content
                relativeLayout.setVisibility(View.INVISIBLE);
            }
        }catch (NullPointerException e){
            Log.d(TAG, "onCreateView: NullPointerException: "+e.getMessage());
        }

        return view;

    }

    private void setupWidgets(){
        Log.d(TAG, "setupWidgets: setup group widgets.");
        // set visibility of content
        mMessage.setVisibility(View.INVISIBLE);
        mListView.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);
        added.setVisibility(View.INVISIBLE);
        menu.setVisibility(View.INVISIBLE);
        tv_addPost.setVisibility(View.VISIBLE);
        addUser.setVisibility(View.VISIBLE);

        //set visibility icon
        if(mCurrentGroup.getVisibility().equals("private")){
            iv_visibilityPublic.setVisibility(View.INVISIBLE);
            iv_visibilityPivate.setVisibility(View.VISIBLE);
        }


        //set crown
        if(mCurrentGroup.getOwner_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            crown.setVisibility(View.VISIBLE);
        }

        // set name
        name.setText(mCurrentGroup.getName());

        //set description
        description.setText(mCurrentGroup.getDescription());

        //set user number
        users_number.setText(String.valueOf(mCurrentGroup.getUsers().size()));

        //set image
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mCurrentGroup.getGroup_photo(), groupPhoto);

        groupPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing group photo");
                if(mCurrentGroup.getOwner_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Intent i = new Intent(mContext, ShareActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    i.putExtra("group_id", mCurrentGroup.getGroup_id());
                    i.putExtra("group_name", mCurrentGroup.getName());
                    mContext.startActivity(i);
                }
                else {
                    Toast.makeText(mContext,"Impossible de changer la photo du groupe car vous n'êtes pas le propriétaire.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: publishing a post in : "+ mCurrentGroup.getName());
                Intent i = new Intent(mContext, ShareActivity.class);
                i.putExtra("PublishingGroupId", mCurrentGroup.getGroup_id());
                mContext.startActivity(i);
            }
        });

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: add new user in the current group: "+mCurrentGroup.toString());
                Intent i = new Intent(mContext, SearchActivity.class);
                i.putExtra("fragment","GroupsFragment");
                i.putExtra("group",mCurrentGroup);
                mContext.startActivity(i);

            }
        });


    }
    
    private void getGroupPhoto(){
        Log.d(TAG, "getGroupPhoto: getting group photos");
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        Query query = myRef.child("photos")
                .orderByChild("group_id")
                .equalTo(mCurrentGroup.getGroup_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Log.d(TAG, "getGroupPhotos: photo founded.");
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

                    if(!isInList(mphotos,photo)){
                        mphotos.add(photo);
                    }
                }
                displayPhotos();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private boolean isInList(ArrayList<Photo> photos , Photo photo) {
        for (int i = 0; i < photos.size(); i++) {
            if (photos.get(i).getPhoto_id().equals(photo.getPhoto_id())) {
                return true;
            }
        }
        return false;
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