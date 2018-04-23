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
import com.kaayso.benyoussafaycel.android_app.Adding.ShareActivity;
import com.kaayso.benyoussafaycel.android_app.Home.HomeActivity;
import com.kaayso.benyoussafaycel.android_app.Models.Group;
import com.kaayso.benyoussafaycel.android_app.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BenyoussaFaycel on 02/04/2018.
 */

public class GroupListAdapter extends ArrayAdapter<Group> {
    private static final String TAG = "GroupListAdapter";

    private LayoutInflater layoutInflater;
    private  List<Group> mGroups = null;
    private int layoutRessource;
    private Context mcontext;

    public GroupListAdapter(@NonNull Context context, int resource, @NonNull List<Group> objects) {
        super(context, resource, objects);
        mcontext =context;
        layoutRessource =resource;
        mGroups = objects;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    //Make same job as recyclerView
    private static class ViewHolder{
        public TextView name, description, users_number;
        public CircleImageView groupPhoto;
        public ImageView add, added, visiblity_public, visibility_private, crown;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //ViewHolder build pattern
            convertView = layoutInflater.inflate(layoutRessource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.name = (TextView) convertView.findViewById(R.id.group_name);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.users_number = (TextView) convertView.findViewById(R.id.tv_nb_users);
            viewHolder.groupPhoto = (CircleImageView) convertView.findViewById(R.id.group_photo);
            viewHolder.add = (ImageView) convertView.findViewById(R.id.add_group);
            viewHolder.added = (ImageView) convertView.findViewById(R.id.checked_group);
            viewHolder.crown = (ImageView) convertView.findViewById(R.id.iv_crown);
            viewHolder.visibility_private = (ImageView) convertView.findViewById(R.id.iv_visibilityPivate);
            viewHolder.visiblity_public = (ImageView) convertView.findViewById(R.id.iv_visibilityPublic);


            convertView.setTag(viewHolder);

        //set crown
        if(getItem(position).getOwner_id().equals(user_id)) {
            viewHolder.crown.setVisibility(View.VISIBLE);
        }

        //set add/added icon
        if (isInList(getItem(position).getUsers(), user_id)){
            viewHolder.add.setVisibility(View.INVISIBLE);
            viewHolder.added.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.add.setVisibility(View.VISIBLE);
            viewHolder.added.setVisibility(View.INVISIBLE);
        }

        //set visibility icon
        if (getItem(position).getVisibility().equals("private")){
            viewHolder.visiblity_public.setVisibility(View.INVISIBLE);
            viewHolder.visibility_private.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.visiblity_public.setVisibility(View.VISIBLE);
            viewHolder.visibility_private.setVisibility(View.INVISIBLE);
        }

        //set users number
        viewHolder.users_number.setText(String.valueOf(getItem(position).getUsers().size()));

        //set name
        viewHolder.name.setText(getItem(position).getName());

        //set description
        viewHolder.description.setText(getItem(position).getDescription());

        //set group photo
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("groups")
                .orderByChild("group_id")
                .equalTo(getItem(position).getGroup_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found group ");
                    Map<String, Object> map = (HashMap<String,Object>) ds.getValue();

                    Group group = buildGroup(map);
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(group.getGroup_photo(), viewHolder.groupPhoto);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If current user is not the group owner then he can left the current group
                if(!getItem(position).getOwner_id().equals(user_id)) {
                    if(!getItem(position).getVisibility().equals("private")){
                        viewHolder.add.setVisibility(View.INVISIBLE);
                        viewHolder.added.setVisibility(View.VISIBLE);

                        DatabaseMethods databaseMethods = new DatabaseMethods(mcontext);
                        databaseMethods.addUserToGroup(getItem(position).getGroup_id(), user_id);
                        Toast.makeText(mcontext,"Groupe ajouté.",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(mcontext, HomeActivity.class);
                        mcontext.startActivity(i);
                    }
                    else {
                        Toast.makeText(mcontext,"Impossible d'ajouter un groupe privé, l'ajout est possible uniquement " +
                                "sur invitation.",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        viewHolder.added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If current user is not the group owner then he can left the current group
                if(!getItem(position).getOwner_id().equals(user_id)){
                    viewHolder.add.setVisibility(View.VISIBLE);
                    viewHolder.added.setVisibility(View.INVISIBLE);
                    DatabaseMethods databaseMethods = new DatabaseMethods(mcontext);
                    databaseMethods.removeUserFromGroup(getItem(position).getGroup_id(), user_id);
                    Toast.makeText(mcontext,"Groupe retiré.",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(mcontext, HomeActivity.class);
                    mcontext.startActivity(i);
                }else {
                    Toast.makeText(mcontext,"Impossible de quitter le groupe car vous êtes le propriétaire.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewHolder.groupPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing group photo");
                if(getItem(position).getOwner_id().equals(user_id)) {
                    Intent i = new Intent(mcontext, ShareActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    i.putExtra("group_id", getItem(position).getGroup_id());
                    i.putExtra("group_name", getItem(position).getName());
                    mcontext.startActivity(i);
                }
                else {
                    Toast.makeText(mcontext,"Impossible de changer la photo du groupe car vous n'êtes pas le propriétaire.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
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

    private boolean isInList(List<String> users , String userid) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).equals(userid)) {
                return true;
            }
        }
        return false;
    }
}
