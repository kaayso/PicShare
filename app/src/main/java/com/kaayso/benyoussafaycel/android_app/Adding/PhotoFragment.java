package com.kaayso.benyoussafaycel.android_app.Adding;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.kaayso.benyoussafaycel.android_app.Group.GroupActivity;
import com.kaayso.benyoussafaycel.android_app.Home.HomeActivity;
import com.kaayso.benyoussafaycel.android_app.Profile.SettingsActivity;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.Permissions;

import java.util.ArrayList;

/**
 * Created by BenyoussaFaycel on 16/03/2018.
 */

public class PhotoFragment extends Fragment {
    private static final String TAG = "PhotoFragment";
    private static final int CAMERA_REQUEST_CODE = 8;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos,container,false);
        Log.d(TAG, "onCreateView: Started." );
        Button mswitchCamera = (Button) view.findViewById(R.id.switchCamera);
        mswitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ShareActivity) getActivity()).getCurrentFragment() == 1){
                    Log.d(TAG, "onCreateView: Camera launched");
                    if (((ShareActivity) getActivity()).checkPermissions(Permissions.CAMERA_PERMISSIONS[0])){
                        Log.d(TAG, "onCreateView: starting camera");
                        Intent i =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(i, CAMERA_REQUEST_CODE);
                    }else{
                        Intent i = new Intent(getActivity(), ShareActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: done taking a photo");
            //navigate to the final share screen to publish photo
            Bitmap bm = (Bitmap) data.getExtras().get("data");
            // from profile settings
            if(((ShareActivity)getActivity()).getTask() != 0){
                if(((ShareActivity)getActivity()).getTask() == Intent.FLAG_ACTIVITY_MULTIPLE_TASK){
                    //for group photo
                    try {
                        ShareActivity shareActivity = (ShareActivity) getActivity();
                        ArrayList<String> myDataFromActivity = shareActivity.getGroupInfo();
                        Log.d(TAG, "onClick: group info received in gallery fragment : " +myDataFromActivity.get(0) +"/"+myDataFromActivity.get(1));
                        Log.d(TAG, "onActivityResult: reveive new bitmap from camera"+bm);
                        Intent i = new Intent(getActivity(), HomeActivity.class);
                        i.putExtra("Selected Bitmap", bm);
                        i.putExtra("group_id", myDataFromActivity.get(0));
                        i.putExtra("group_name", myDataFromActivity.get(1));
                        startActivity(i);
                        getActivity().finish();
                    }catch (NullPointerException e){
                        Log.d(TAG, "onActivityResult: NullPointerException "+ e.getMessage());
                    }
                }else {
                    // for profile photo
                    try {
                        Log.d(TAG, "onActivityResult: reveive new bitmap from camera"+bm);
                        Intent i = new Intent(getActivity(), SettingsActivity.class);
                        i.putExtra("Selected Bitmap", bm);
                        i.putExtra("to fragment", "EditionFragment");
                        startActivity(i);
                        getActivity().finish();
                    }catch (NullPointerException e){
                        Log.d(TAG, "onActivityResult: NullPointerException "+ e.getMessage());
                    }
                }

            }
            //from root
            else{
                try {
                    // for publishing
                    ShareActivity shareActivity = (ShareActivity) getActivity();
                    //in to group
                    if(shareActivity.getPublishingGroupId()!=null){
                        Log.d(TAG, "onActivityResult: reveive new bitmap from camera"+bm);
                        String PublishingGroupId = shareActivity.getPublishingGroupId();
                        Intent i = new Intent(getActivity(), SharingActivity.class);
                        i.putExtra("PublishingGroupId",PublishingGroupId);
                        i.putExtra("Selected Bitmap", bm);
                        startActivity(i);
                    }
                    //in to home
                    else {
                        Log.d(TAG, "onActivityResult: reveive new bitmap from camera"+bm);
                        Intent i = new Intent(getActivity(), SharingActivity.class);
                        i.putExtra("Selected Bitmap", bm);
                        startActivity(i);
                    }
                }catch (NullPointerException e){
                    Log.d(TAG, "onActivityResult: NullPointerException "+ e.getMessage());
                }
            }
        }
    }
}
