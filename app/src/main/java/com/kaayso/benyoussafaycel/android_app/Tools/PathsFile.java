package com.kaayso.benyoussafaycel.android_app.Tools;

import android.os.Environment;

/**
 * Created by BenyoussaFaycel on 29/03/2018.
 */

public class PathsFile {
    public String PATH_DIRECTORY = Environment.getExternalStorageDirectory().getPath();
    public String CAMERA = PATH_DIRECTORY + "/DCIM/Camera";
    public String PIC = PATH_DIRECTORY + "/Pictures";
    public String FIREBASE_IMG_STORAGE = "photos/users/";
}
