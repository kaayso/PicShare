package com.kaayso.benyoussafaycel.android_app.Tools;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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

    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        mcontext =context;
        layoutRessource =resource;
        mUsers = objects;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    //Make same job as recyclerView
    private static class ViewHolder{
        public TextView email, username;
        public CircleImageView profilePhoto;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        //ViewHolder build pattern
            convertView = layoutInflater.inflate(layoutRessource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.username = (TextView) convertView.findViewById(R.id.searchUsername);
            viewHolder.email = (TextView) convertView.findViewById(R.id.searchEmail);
            viewHolder.profilePhoto = (CircleImageView) convertView.findViewById(R.id.search_profilePhoto);

            convertView.setTag(viewHolder);


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


        return convertView;
    }
}
