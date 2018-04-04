package com.kaayso.benyoussafaycel.android_app.Tools;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kaayso.benyoussafaycel.android_app.R;


/**
 * Created by BenyoussaFaycel on 29/03/2018.
 */

public class ConfirmPwdDialog extends DialogFragment {
    private static final String TAG = "ConfirmPwdDialog";
    OnConfirmPasswordListener onConfirmPasswordListener;
    TextView mCancel, mConfirm;
    EditText mPassword;
    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container , @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.dialog_confirmation_tochange_email, container, false);
        Log.d(TAG, "onCreateView: Started.");

        mCancel =(TextView) view.findViewById(R.id.dialog_cancel);
        mConfirm =(TextView) view.findViewById(R.id.dialog_confim);
        mPassword =(EditText) view.findViewById(R.id.confirm_password);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Closing dialog box.");
                getDialog().dismiss();
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: confirming dialog box.");
                if(!mPassword.getText().toString().equals("")){
                    onConfirmPasswordListener.onConfirmPassword(mPassword.getText().toString());
                    getDialog().dismiss();
                }
                else {
                    Toast.makeText(getActivity(), "Entrez votre mot de passe svp", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
    public  interface  OnConfirmPasswordListener{
        public void onConfirmPassword(String pwd);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onConfirmPasswordListener = (OnConfirmPasswordListener) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException" + e.getMessage() );
        }
    }
}
