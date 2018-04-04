package com.kaayso.benyoussafaycel.android_app.Tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.kaayso.benyoussafaycel.android_app.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BenyoussaFaycel on 17/03/2018.
 */

public class GridImageAdapter extends ArrayAdapter<String>{

    private Context mcontext;
    private LayoutInflater mInflater;
    private int layoutRes;
    private String mAppend;
    private ArrayList<String> urls;

    public GridImageAdapter(@NonNull Context mcontext, int layoutRes, String mAppend, ArrayList<String> urls) {
        super(mcontext, layoutRes, urls);
        this.mInflater = (LayoutInflater) mcontext.getSystemService(mcontext.LAYOUT_INFLATER_SERVICE);
        this.mcontext = mcontext;
        this.layoutRes = layoutRes;
        this.mAppend = mAppend;
        this.urls = urls;
    }



    private  static  class  ViewHolder{
        SquareImageView imageView;
        ProgressBar mprogressBar;

    }

    @NonNull
    @Override
    public View getView (int position, @NonNull View convertView, @NonNull ViewGroup viewGroup){

        /*
        Viewholder build pattern ( like recyclerView )
         */
        final ViewHolder viewHolder;
        if(convertView == null){
            convertView = mInflater.inflate(layoutRes,viewGroup,false);
            viewHolder = new  ViewHolder();
            viewHolder.mprogressBar = (ProgressBar) convertView.findViewById(R.id.gridImgprogressBar);
            viewHolder.imageView = (SquareImageView) convertView.findViewById(R.id.gridImgView);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String URLs = getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mAppend + URLs, viewHolder.imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (viewHolder.mprogressBar != null){
                    viewHolder.mprogressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (viewHolder.mprogressBar != null){
                    viewHolder.mprogressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (viewHolder.mprogressBar != null){
                    viewHolder.mprogressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (viewHolder.mprogressBar != null){
                    viewHolder.mprogressBar.setVisibility(View.GONE);
                }
            }
        });
        return convertView;
    }
}
