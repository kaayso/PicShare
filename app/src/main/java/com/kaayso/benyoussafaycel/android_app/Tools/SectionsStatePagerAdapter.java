package com.kaayso.benyoussafaycel.android_app.Tools;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by BenyoussaFaycel on 17/03/2018.
 */

public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final HashMap<String, Integer> mFragNum = new HashMap<>();
    private final HashMap<Fragment , Integer> mFrag = new HashMap<>();
    private final HashMap<Integer , String> mFragName = new HashMap<>();


    public SectionsStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }


    /*
        * To add fragments to a list
        * And HashMap
     */

    public void  addFragment ( Fragment fragment , String fragName ){
        mFragmentList.add(fragment);
        mFrag.put(fragment,mFragmentList.indexOf(fragment));
        mFragName.put(mFragmentList.indexOf(fragment), fragName);
        mFragNum.put(fragName , mFragmentList.indexOf(fragment));

    }


    /*
    * Returns fragment number from name
     */
    public Integer getFragmentNumber(String fragName){
        if (mFragNum.containsKey(fragName)){
            return mFragNum.get(fragName);
        }else {
            return null;
        }

    }

    /*
    * Returns fragment name from number
     */

    public String getFragmentName(Integer fragNumber){
        if(mFragName.containsKey(fragNumber)){
            return mFragName.get(fragNumber);
        }else {
            return null;
        }
    }


    /*
    * Returns fragment number from fragment object
     */
    public Integer getFragmentNumber ( Fragment fragment){
        if ( mFrag.containsKey(fragment)){
            return mFrag.get(fragment);
        }
        else {
            return null;
        }
    }
}
