package com.kaayso.benyoussafaycel.android_app.Tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.kaayso.benyoussafaycel.android_app.Profile.ProfileActivity;
import com.kaayso.benyoussafaycel.android_app.Profile.ProfileFragment;
import com.kaayso.benyoussafaycel.android_app.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by BenyoussaFaycel on 17/03/2018.
 */

public class UnivImageLoader {
    private static final String TAG = "UnivImageLoader";
    private static final int defaultImage = R.drawable.ic_profil;

    private Context mctx;
    private static ImageLoaderConfiguration myConfig;

    public UnivImageLoader(Context ctx) {
        this.mctx = ctx;
    }

    public  ImageLoaderConfiguration getConfig(){
        DisplayImageOptions defaultOptions = new  DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(400)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mctx)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(150 * 1024 *1024).build();
        myConfig = config;

        return config;

    }

    /*
      * This method can be used to set img that are static, it can't be used of the images are being changed in the Fragment/Activity
      *  OR if they are being set in a list or gridview
      * Universal URL with 'prefix pref'
     */
    public static  void setImage (String imgUrl, ImageView imageView, final ProgressBar progressBar , String pref ){

        ImageLoader imageLoader = ImageLoader.getInstance();
        try {
            imageLoader.init(myConfig);
        }catch (NullPointerException e){
            Log.d(TAG, "setImage: NullPointerException: "+e.getMessage());
        }
        imageLoader.displayImage(pref + imgUrl, imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (progressBar != null){
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }
}
