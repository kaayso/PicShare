package com.kaayso.benyoussafaycel.android_app.Home;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaayso.benyoussafaycel.android_app.R;

/**
 * Created by BenyoussaFaycel on 16/03/2018.
 */

public class GroupsFragment extends Fragment {
    private static final String TAG = "GroupsFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups,container,false);
        return view;
    }
}
