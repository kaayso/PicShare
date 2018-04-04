package com.kaayso.benyoussafaycel.android_app.Tools;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by BenyoussaFaycel on 29/03/2018.
 */

public class SearchFile {


    public static ArrayList<String> getDirectoriesName(ArrayList<String> directories){
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0 ; i < directories.size() ; i++){
            int index = directories.get(i).lastIndexOf("/");
            String name = directories.get(i).substring(index+1);
            names.add(name);
        }
        return names;
    }


    /*
        Return list of paths of directories inside directory
     */
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File [] files = file.listFiles();

        for(int i=0 ; i<files.length ; i++){
            if(files[i].isDirectory()) pathArray.add(files[i].getAbsolutePath());
        }
        return pathArray;
    }

    /*
        Return list of paths of files inside directory

     */
    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File [] files = file.listFiles();

        for(int i=0 ; i<files.length ; i++){
            if(files[i].isFile()) pathArray.add(files[i].getAbsolutePath());
        }
        return pathArray;
    }


}
