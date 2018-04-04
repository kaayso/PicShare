package com.kaayso.benyoussafaycel.android_app.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaayso.benyoussafaycel.android_app.R;

/**
 * Created by BenyoussaFaycel on 17/03/2018.
 */

public class AboutFragment extends Fragment {
    private static final String TAG = "AboutFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_about,container,false);
        return view;
    }
}
