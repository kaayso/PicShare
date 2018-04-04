package com.kaayso.benyoussafaycel.android_app.Tools;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by BenyoussaFaycel on 31/03/2018.
 */

public class Heart {
    private static final String TAG = "Heart";

    public ImageView wHeart, rHeart;
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR =new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR =new AccelerateInterpolator();
    public Heart(ImageView wHeart, ImageView rHeart) {
        this.wHeart = wHeart;
        this.rHeart = rHeart;
    }

    public void toggleLike(){
        Log.d(TAG, "toggleLike: toggling heart.");

        // toggle animation
        AnimatorSet animationSet = new AnimatorSet();

        if(rHeart.getVisibility() == View.VISIBLE){
            Log.d(TAG, "toggleLike: toggle red heart OFF.");
            rHeart.setScaleX(0.1f);
            rHeart.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(rHeart, "scaleY", 1f, 0f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(rHeart, "scaleX", 1f, 0f);
            scaleDownY.setDuration(300);
            scaleDownX.setInterpolator(ACCELERATE_INTERPOLATOR);

            rHeart.setVisibility(View.GONE);
            wHeart.setVisibility(View.VISIBLE);

            animationSet.playTogether(scaleDownX,scaleDownY);
        }

        else if(rHeart.getVisibility() == View.GONE){
            Log.d(TAG, "toggleLike: toggle red heart ON.");
            rHeart.setScaleX(0.1f);
            rHeart.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(rHeart, "scaleY", 0.1f, 1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(rHeart, "scaleX", 0.1f, 1f);
            scaleDownY.setDuration(300);
            scaleDownX.setInterpolator(DECELERATE_INTERPOLATOR);

            rHeart.setVisibility(View.VISIBLE);
            wHeart.setVisibility(View.GONE);

            animationSet.playTogether(scaleDownX,scaleDownY);
        }

        animationSet.start();
    }
}
