package com.kaayso.benyoussafaycel.android_app.Group;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.kaayso.benyoussafaycel.android_app.Adding.ShareActivity;
import com.kaayso.benyoussafaycel.android_app.Home.HomeActivity;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.DatabaseMethods;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BenyoussaFaycel on 19/04/2018.
 */

public class NewGroupFragment extends Fragment {
    private static final String TAG = "NewGroupFragment";
    Context mContext;
    CircleImageView mGroupPhoto;
    TextView mGroupName, mCaption;
    CheckBox mPrivateCheckBox;
    Button mSubmit;

    private DatabaseMethods mdatabaseMethods;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_group,container,false);
        Log.d(TAG, "onCreateView: started");
        mContext = getActivity();
        mGroupPhoto = (CircleImageView) view.findViewById(R.id.group_photo);
        mGroupName = (TextView) view.findViewById(R.id.groupName_edit);
        mCaption = (TextView) view.findViewById(R.id.description_edit);
        mPrivateCheckBox = (CheckBox) view.findViewById(R.id.checkbox_private);
        mSubmit = (Button) view.findViewById(R.id.submit);
        mdatabaseMethods = new DatabaseMethods(getActivity());

        init();
        return view;
    }


    private void init(){
        Log.d(TAG, "init: verify all informations to create a new group");
        final  DatabaseReference mdatabaseReference = FirebaseDatabase.getInstance().getReference();
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on submit button");
                if (mGroupName.getText().toString().equals("") || mCaption.getText().toString().equals("")){
                    Log.d(TAG, "onClick: Can't create a new group, complete all fields");
                    Toast.makeText(mContext,"Veuillez remplir tous les champs SVP",  Toast.LENGTH_SHORT).show();
                }
                else {
                    //1st step : make verifications on group name

                    Query query = mdatabaseReference.child("groups")
                            .orderByChild("name")
                            .equalTo(mGroupName.getText().toString());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()){
                                Log.d(TAG, "Group name already exists.");
                                Toast.makeText(mContext,"Le nom de ce groupe existe déjà...",  Toast.LENGTH_SHORT).show();
                            }
                            //2nd step : if no exist then create this group
                            if(!dataSnapshot.exists()){
                                //The group name don't exists on database
                                Toast.makeText(getActivity(), "Groupe enregistré", Toast.LENGTH_SHORT).show();
                                String visibility = "public";
                                if(mPrivateCheckBox.isChecked()){
                                    visibility = "private";
                                }
                                mdatabaseMethods.addNewGroup(mGroupName.getText().toString() ,mCaption.getText().toString() ,visibility);
                                Intent i  = new Intent(getActivity() , HomeActivity.class);
                                startActivity(i);
                                getActivity().finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }
        });


    }




}
