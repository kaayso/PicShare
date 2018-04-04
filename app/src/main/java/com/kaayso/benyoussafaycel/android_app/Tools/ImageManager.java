package com.kaayso.benyoussafaycel.android_app.Tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by BenyoussaFaycel on 30/03/2018.
 */

public class ImageManager {
    private static final String TAG = "ImageManager";

    public static Bitmap getBitmap(String imgUrl){
        File imageFile = new File(imgUrl);
        FileInputStream fileInputStream = null;
        Bitmap bitmap = null;
        try {
            fileInputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        }catch (FileNotFoundException e){
            Log.d(TAG, "getBitmap:FileNotFoundException "+e.getMessage());
        }
        finally {
            try {
                fileInputStream.close();
            }catch (IOException e){
                Log.d(TAG, "getBitmap:IOException "+e.getMessage());
            }
        }
        return bitmap;
    }

    /**
     * return byte array from a bitmap
     * quality is between 0 and 100%
     */
    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }
}
