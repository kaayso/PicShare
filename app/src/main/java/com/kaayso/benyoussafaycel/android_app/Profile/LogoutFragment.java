package com.kaayso.benyoussafaycel.android_app.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.kaayso.benyoussafaycel.android_app.Authentication.StartActivity;
import com.kaayso.benyoussafaycel.android_app.R;

/**
 * Created by BenyoussaFaycel on 17/03/2018.
 */

public class LogoutFragment extends Fragment {

    private static final String TAG = "LogoutFragment";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ProgressBar mprogressBar;
    private TextView mwainting;
    private TextView tvsignout;
    private Button mconfirm_signout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_logout,container,false);
        mprogressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        tvsignout = (TextView) view.findViewById(R.id.tvconfirm_signout);
        mwainting = (TextView) view.findViewById(R.id.tvwaiting);
        mconfirm_signout = (Button) view.findViewById(R.id.btnconfirm_signout_);

        mprogressBar.setVisibility(View.GONE);
        mwainting.setVisibility(View.GONE);
        setupAuth();
        mconfirm_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mprogressBar.setVisibility(View.VISIBLE);
                mwainting.setVisibility(View.VISIBLE);
                mAuth.signOut();
                getActivity().finish();
            }
        });
        return view;
    }

    /*
        Authentification on firebase
     */
    public void setupAuth(){
        Log.d(TAG, "setupAuth: started.");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: singed in: " + user.getUid());
                }else {
                    Log.d(TAG, "onAuthStateChanged: singed out");
                    Log.d(TAG, "onAuthStateChanged: Navigating to login in screen.");
                    Intent i = new Intent(getActivity(), StartActivity.class);
                    Toast.makeText(getContext() , "A bientot! ",Toast.LENGTH_SHORT);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}
