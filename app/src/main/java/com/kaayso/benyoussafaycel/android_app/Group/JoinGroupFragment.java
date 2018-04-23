package com.kaayso.benyoussafaycel.android_app.Group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kaayso.benyoussafaycel.android_app.Home.HomeActivity;
import com.kaayso.benyoussafaycel.android_app.Models.Group;
import com.kaayso.benyoussafaycel.android_app.Models.User;
import com.kaayso.benyoussafaycel.android_app.Profile.ProfileActivity;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.GroupListAdapter;
import com.kaayso.benyoussafaycel.android_app.Tools.UserListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by BenyoussaFaycel on 19/04/2018.
 */

public class JoinGroupFragment extends Fragment {
    private static final String TAG = "JoinGroupFragment";
    private Context mContext;
    private List<Group> mgroups;
    private GroupListAdapter mgroupListAdapter;
    private ListView mListview;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_group,container,false);
        Log.d(TAG, "onCreateView: JoinGroupFragment started");
        mContext = getActivity();
        mgroups =new ArrayList<>();
        mListview =(ListView) view.findViewById(R.id.list_view);
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
        Query query = databaseReference.child("groups");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren() ){
                    Log.d(TAG, "onDataChange: getting groups : "+ds.child("name").toString());

                    Map<String, Object> map = (HashMap<String,Object>) ds.getValue();

                    Group group = buildGroup(map);
                    ArrayList<String> musers =new ArrayList<>();
                    for(DataSnapshot snap : ds.child("users").getChildren()){
                        Log.d(TAG, "onDataChange: gettings all members oh the current group : " +snap.child("user_id").getValue());
                        musers.add((String)snap.child("user_id").getValue());
                    }
                    group.setUsers(musers);
                    mgroups.add(group);
                }
                updateListGroups();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public Group buildGroup (Map<String,Object> map){
        Log.d(TAG, "buildPhoto: building group");
        Group group = new Group();

        group.setName(map.get("name").toString());
        group.setGroup_id(map.get("group_id").toString());
        group.setOwner_id(map.get("owner_id").toString());
        group.setVisibility(map.get("visibility").toString());
        group.setDescription(map.get("description").toString());
        group.setGroup_photo(map.get("group_photo").toString());

        return group;
    }

    private void updateListGroups(){
        Log.d(TAG, "updateUsersList: updating groups list");
        mgroupListAdapter = new GroupListAdapter(mContext, R.layout.layout_group, mgroups );
        mListview.setAdapter(mgroupListAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected user "+ mgroups.get(position).toString());
                //navigate to GroupHome activity if the current is in this group
                if (isInList(mgroups.get(position).getUsers(), FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Intent i = new Intent(mContext, HomeActivity.class);
                    i.putExtra("calling fragment", "GroupsFragment");
                    i.putExtra("group", mgroups.get(position));
                    startActivity(i);

                }
                else {
                    Toast.makeText(mContext,"Accés impossible, vous n'appartenez pas à ce groupe.",Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
    private boolean isInList(List<String> users , String userid) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).equals(userid)) {
                return true;
            }
        }
        return false;
    }


}
