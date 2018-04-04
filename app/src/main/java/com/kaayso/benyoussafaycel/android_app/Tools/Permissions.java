package com.kaayso.benyoussafaycel.android_app.Tools;


import android.Manifest;

/**
 * Created by BenyoussaFaycel on 29/03/2018.
 */

public class Permissions {

    public static final String[] PERMISSIONS ={Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,

            Manifest.permission.CAMERA};
    public static final String[] CAMERA_PERMISSIONS ={Manifest.permission.CAMERA};
    public static final String[] WRITE_STORAGE_PERMISSIONS ={ Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String[] READ_STORAGE_PERMISSIONS ={ Manifest.permission.READ_EXTERNAL_STORAGE};
}
