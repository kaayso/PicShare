package com.kaayso.benyoussafaycel.android_app.Tools;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.kaayso.benyoussafaycel.android_app.Models.UserAccountSettings;
import com.kaayso.benyoussafaycel.android_app.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BenyoussaFaycel on 02/04/2018.
 */

public class UserListAdapter extends ArrayAdapter<User> {
    private static final String TAG = "UserListAdapter";

    private LayoutInflater layoutInflater;
    private  List<User> mUsers = null;
    private int layoutRessource;
    private Context mcontext;
    private Group mCurrentGroup;

    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects, Group gp) {
        super(context, resource, objects);
        mcontext =context;
        layoutRessource =resource;
        mUsers = objects;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCurrentGroup = gp;
    }

    //Make same job as recyclerView
    private static class ViewHolder{
        public TextView email, username;
        public CircleImageView profilePhoto;
        public ImageView add, added;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        //ViewHolder build pattern
            convertView = layoutInflater.inflate(layoutRessource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.username = (TextView) convertView.findViewById(R.id.searchUsername);
            viewHolder.email = (TextView) convertView.findViewById(R.id.searchEmail);
            viewHolder.profilePhoto = (CircleImageView) convertView.findViewById(R.id.search_profilePhoto);
            viewHolder.add = (ImageView) convertView.findViewById(R.id.iv_add);
            viewHolder.added = (ImageView) convertView.findViewById(R.id.iv_added);

            convertView.setTag(viewHolder);
        //set add/add button
        if(mCurrentGroup != null){
            //come from GroupFragment
            Log.d(TAG, "getView: group received: "+mCurrentGroup);
            if (isInList(mCurrentGroup.getUsers(), getItem(position).getUser_id())){
                viewHolder.add.setVisibility(View.INVISIBLE);
                viewHolder.added.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.add.setVisibility(View.VISIBLE);
                viewHolder.added.setVisibility(View.INVISIBLE);
            }

        }else {
            //come from root
            viewHolder.add.setVisibility(View.INVISIBLE);
        }


        //set username
        viewHolder.username.setText(getItem(position).getUsername());

        //set email
        viewHolder.email.setText(getItem(position).getEmail());

        //set profile photo
        Log.d(TAG, "getView: position ="+position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("user_account_settings")
                .orderByChild("user_id")
                .equalTo(getItem(position).getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user "+ ds.getValue(UserAccountSettings.class).getUsername()+"/"+
                            ds.getValue(UserAccountSettings.class).getProfile_photo());

                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(ds.getValue(UserAccountSettings.class).getProfile_photo(), viewHolder.profilePhoto);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.add.setVisibility(View.INVISIBLE);
                viewHolder.added.setVisibility(View.VISIBLE);

                DatabaseMethods databaseMethods = new DatabaseMethods(mcontext);
                databaseMethods.addUserToGroup(mCurrentGroup.getGroup_id(), String.valueOf(getItem(position).getUser_id()));
                Toast.makeText(mcontext,"Utilisateur ajouté.",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mcontext, HomeActivity.class);
                mcontext.startActivity(i);
            }
        });


        return convertView;
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
