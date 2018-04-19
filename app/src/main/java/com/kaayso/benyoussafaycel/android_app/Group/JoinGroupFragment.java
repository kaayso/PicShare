package com.kaayso.benyoussafaycel.android_app.Group;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaayso.benyoussafaycel.android_app.R;

/**
 * Created by BenyoussaFaycel on 19/04/2018.
 */

public class JoinGroupFragment extends Fragment {
    private static final String TAG = "JoinGroupFragment";
    Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_group,container,false);
        Log.d(TAG, "onCreateView: started");
        mContext = getActivity();

        return view;
    }


}
