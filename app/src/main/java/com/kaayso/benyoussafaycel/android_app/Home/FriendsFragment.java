package com.kaayso.benyoussafaycel.android_app.Home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kaayso.benyoussafaycel.android_app.Models.User;
import com.kaayso.benyoussafaycel.android_app.Profile.ProfileActivity;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.UserListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BenyoussaFaycel on 16/03/2018.
 */

public class FriendsFragment extends Fragment {
    private static final String TAG = "FriendsFragment";

    private ListView mListview;
    private List<User> mUsers;
    private List<String> mUsersID;
    private UserListAdapter userListAdapter;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends,container,false);

        Log.d(TAG, "onCreate: started.");
        mListview = (ListView) view.findViewById(R.id.list_view);
        mContext = getActivity();
        mUsers = new ArrayList<>();
        mUsersID = new ArrayList<>();
        try {
            searchForMatch();
        }catch (NullPointerException e){
            Log.d(TAG, "onCreateView: NullPointerException: "+e.getMessage());
        }

        return view;
    }

    private void searchForMatch(){
        Log.d(TAG, "searchForMatch: searching for a match: ");
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("friends")
                .child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid());


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren() ){
                    Log.d(TAG, "onDataChange: getting friends idstep 1: "+ds.child("user_id").getValue().toString());

                    final String keyID = ds.child("user_id").getValue().toString();

                    Query query2 = databaseReference.child("friends")
                            .child(keyID)
                            .orderByChild("user_id")
                            .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    //for each user verify if current user is friend with him
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: match founded step 2 with : "+ keyID);
                            for (DataSnapshot ds: dataSnapshot.getChildren() ){
                                Log.d(TAG, "onDataChange  data retreived : "+ ds.getValue().toString());
                                mUsersID.add(keyID);
                            }
                            try {
                                Log.d(TAG, "searchForMatch: useridList len: "+ mUsersID.size());
                                for (int i = 0; i< mUsersID.size() ; i++){
                                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                                    Query query3 = myRef.child("users").orderByChild("user_id").equalTo(mUsersID.get(i));

                                    query3.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds: dataSnapshot.getChildren() ){
                                                Log.d(TAG, "onDataChange: this user is a friend: "+ds.getValue(User.class).getUsername());
                                               if(!isMyfFriend(mUsers,ds.getValue(User.class))) {
                                                   mUsers.add(ds.getValue(User.class));
                                               }

                                            }
                                            updateListUsers();
                                        }

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

    private boolean isMyfFriend(List<User> friends , User user) {
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getUser_id().equals(user.getUser_id())) {
                return true;
            }
        }
        return false;
    }

    private void updateListUsers(){

        Log.d(TAG, "updateUsersList: updating users list");
        userListAdapter = new UserListAdapter(mContext, R.layout.layout_user_search, mUsers );
        mListview.setAdapter(userListAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected user "+ mUsers.get(position).toString());
                //navigate to profile activity
                Intent i = new Intent(mContext, ProfileActivity.class);
                i.putExtra("calling activity", "SearchActivity");
                i.putExtra("user", mUsers.get(position));
                startActivity(i);
            }
        });

    }





}
