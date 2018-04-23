package com.kaayso.benyoussafaycel.android_app.Adding;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.kaayso.benyoussafaycel.android_app.Group.GroupActivity;
import com.kaayso.benyoussafaycel.android_app.Home.HomeActivity;
import com.kaayso.benyoussafaycel.android_app.Profile.SettingsActivity;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.GridImageAdapter;
import com.kaayso.benyoussafaycel.android_app.Tools.PathsFile;
import com.kaayso.benyoussafaycel.android_app.Tools.SearchFile;
import com.kaayso.benyoussafaycel.android_app.Tools.UnivImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by BenyoussaFaycel on 16/03/2018.
 */

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";
    private ImageView mGallery, mclose;
    private GridView mgridView;
    private ProgressBar mprogressBar;
    private Spinner mspinner;
    private static final int COL_GRID = 3;

    private static final String APPEND = "file://";
    private ArrayList<String> directories;
    private String mImageSelected;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery,container,false);
        Log.d(TAG, "onCreateView: Started.");

        mGallery = (ImageView) view.findViewById(R.id.gallery_imgView);
        mgridView =(GridView) view.findViewById(R.id.gridView);
        mprogressBar =(ProgressBar) view.findViewById(R.id.progressBar);
        mspinner = (Spinner) view.findViewById(R.id.spinner);
        mclose = (ImageView) view.findViewById(R.id.ivCloseshare);
        mprogressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();

        mclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing gallery fragment.");
                getActivity().finish();
            }
        });

        TextView mNext = (TextView) view.findViewById(R.id.tvNext);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate to final share screen.");

                //See if the photo selected is for profile photo, for group photo or for publishing photo
                //profile photo has intent with flag comes from ShareActivity
                if(((ShareActivity)getActivity()).getTask() != 0){
                    if(((ShareActivity)getActivity()).getTask() == Intent.FLAG_ACTIVITY_MULTIPLE_TASK){
                        ShareActivity shareActivity = (ShareActivity) getActivity();
                        ArrayList<String> myDataFromActivity = shareActivity.getGroupInfo();
                        Log.d(TAG, "onClick: group info received in gallery fragment : " +myDataFromActivity.get(0) +"/"+myDataFromActivity.get(1));
                        //for group photo
                        Intent i = new Intent(getActivity(), HomeActivity.class);
                        i.putExtra("Selected Image", mImageSelected);
                        i.putExtra("group_id", myDataFromActivity.get(0));
                        i.putExtra("group_name", myDataFromActivity.get(1));
                        startActivity(i);
                        getActivity().finish();
                    }else {
                        // for profile photo
                        Intent i = new Intent(getActivity(), SettingsActivity.class);
                        i.putExtra("Selected Image", mImageSelected);
                        i.putExtra("to fragment", "EditionFragment");
                        startActivity(i);
                        getActivity().finish();
                    }

                }else{
                    // for publishing
                    ShareActivity shareActivity = (ShareActivity) getActivity();
                    //in to group
                    if(shareActivity.getPublishingGroupId()!=null){
                        Log.d(TAG, "onClick: publishing for group");
                        String PublishingGroupId = shareActivity.getPublishingGroupId();
                        Intent i = new Intent(getActivity(), SharingActivity.class);
                        i.putExtra("PublishingGroupId",PublishingGroupId);
                        i.putExtra("Selected Image", mImageSelected);
                        startActivity(i);
                    }
                    //in to home
                    else {
                        Intent i = new Intent(getActivity(), SharingActivity.class);
                        i.putExtra("Selected Image", mImageSelected);
                        startActivity(i);
                    }
                    
                }


            }
        });

        init();
        return view;
    }

    /*

     */
    private void init(){
        PathsFile pathsFile= new PathsFile();
        if (SearchFile.getDirectoryPaths(pathsFile.PIC)!=null){
            directories = SearchFile.getDirectoryPaths(pathsFile.PIC);
        }
        directories.add(pathsFile.CAMERA);
        ArrayList<String> names = SearchFile.getDirectoriesName(directories);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,names);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspinner.setAdapter(arrayAdapter);

        //On selected directory -> getting all urls files of current directory
        mspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: selected "+directories.get(position));
                try {
                    setupGrid(directories.get(position));
                }catch (NullPointerException e){
                    Log.d(TAG, "onItemSelected: NullPointerException: "+e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    /*
        Getting paths of images of directory specified
     */
    private void setupGrid(String myDirectory){
        Log.d(TAG, "setupGrid: Directory chosen : "+ myDirectory);
        final ArrayList<String> urls = SearchFile.getFilePaths(myDirectory);
        if (urls.size()!=0){
            // set the grid column width
            int widthWindow = getResources().getDisplayMetrics().widthPixels;
            int widthImg = widthWindow/COL_GRID;
            mgridView.setColumnWidth(widthImg);

            // use the grid adapter to adapter img to gridview
            GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imgview,APPEND,urls);
            mgridView.setAdapter(adapter);

            // display first photo when activity  fragment view is inflated
            try{
                setImage(urls.get(0), mGallery, APPEND);
                mImageSelected = urls.get(0);
            }catch (NullPointerException e){
                Log.d(TAG, "setupGrid: Failed : no photo to display "+ e.getMessage());
            }


            mgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemClick: changing display image to: " + urls.get(position));
                    setImage(urls.get(position), mGallery, APPEND);
                    mImageSelected = urls.get(position);
                }
            });
        }
    }

    private void setImage(String url, ImageView imageView, String append){
        Log.d(TAG, "setImage: setting image.");

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(new UnivImageLoader(getActivity()).getConfig());

        imageLoader.displayImage(append + url, imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mprogressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mprogressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mprogressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mprogressBar.setVisibility(View.INVISIBLE);

            }
        });
    }
}
