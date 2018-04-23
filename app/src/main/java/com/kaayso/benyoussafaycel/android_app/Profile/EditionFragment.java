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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kaayso.benyoussafaycel.android_app.Adding.ShareActivity;
import com.kaayso.benyoussafaycel.android_app.Models.User;
import com.kaayso.benyoussafaycel.android_app.Models.UserAccountSettings;
import com.kaayso.benyoussafaycel.android_app.Models.UserGlobalSettings;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.ConfirmPwdDialog;
import com.kaayso.benyoussafaycel.android_app.Tools.DatabaseMethods;
import com.kaayso.benyoussafaycel.android_app.Tools.UnivImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BenyoussaFaycel on 17/03/2018.
 */

public class EditionFragment extends Fragment  implements ConfirmPwdDialog.OnConfirmPasswordListener{
    
    private static final String TAG = "EditionFragment";
    private CircleImageView mprofileImage;
    private EditText mUsername, mDescription, mEmail, mPassword, mPassword2;
    private TextView mEditProfilePhoto;
    private  ImageView msavechanges;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAutListener;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;
    private DatabaseMethods mdatabaseMethods;

    private UserGlobalSettings muserGlobalSettings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edition,container,false);
        mprofileImage = (CircleImageView) view.findViewById(R.id.profile_photo);
        mUsername = (EditText) view.findViewById(R.id.username_edit);
        mDescription = (EditText) view.findViewById(R.id.description_edit);
        mEmail = (EditText) view.findViewById(R.id.email_edit);
        mPassword = (EditText) view.findViewById(R.id.password_edit);
        mPassword2 = (EditText) view.findViewById(R.id.password_edit2);
        mEditProfilePhoto = (TextView) view.findViewById(R.id.text_changephoto);
        mdatabaseMethods =new DatabaseMethods(getActivity());
        msavechanges = (ImageView) view.findViewById(R.id.savechanges);

        setupAuth();

        // Save changes
        msavechanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileSettings();
            }
        });

        //Back arrow to profile activity
        ImageView backArrow = (ImageView) view.findViewById(R.id.back_Arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigation to profile activity.");
                getActivity().finish();
            }
        });


        return view;
    }

    private void saveProfileSettings (){
        Log.d(TAG, "saveProfileSettings: saving profil settigns.");
        final String username = mUsername.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        final String password2 = mPassword2.getText().toString();

         // username changed
         if(!muserGlobalSettings.getUser().getUsername().equals(username)){
                   checkIfUsernameExists(username.toLowerCase());

         }
        // description changed
        if(!muserGlobalSettings.getUserAccountSettings().getDescription().equals(description)){
            Toast.makeText(getActivity(), "Description enregistrée", Toast.LENGTH_SHORT).show();
            mdatabaseMethods.updateDescription(description);
        }
         // Email changed
         if(!muserGlobalSettings.getUser().getEmail().equals(email) ) {
                   ConfirmPwdDialog confirmPwdDialog = new ConfirmPwdDialog();
                   confirmPwdDialog.show(getFragmentManager(), "ConfirmPwdDialog");
                   confirmPwdDialog.setTargetFragment(EditionFragment.this,1);

         }
        // password changed
        if(!password.equals("")){
            // verify 2 inputs passwords
            if (!password.equals(password2)){
                Toast.makeText(getActivity(), "Les deux mot de passe sont différents...", Toast.LENGTH_SHORT).show();
            }
            // if ok then update
            else{
                mdatabaseMethods.updatePassword(password);
                Toast.makeText(getActivity(), "Nouveau mot de passe enregistré", Toast.LENGTH_SHORT).show();
            }


        }

    }
    /*
        Check if current username already exists on database
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: checking if " + username +" exists already");
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        Query query = dataRef.child("users").orderByChild("username").equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    //The username don't exists on database
                    Toast.makeText(getActivity(), "Pseudo enregistré", Toast.LENGTH_SHORT).show();
                    mdatabaseMethods.updateUsername(username);
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.exists()){
                        Log.d(TAG, "checkIfUsernameExists: Username exists: " + ds.getValue(User.class).toString());
                        Toast.makeText(getActivity(), "Ce pseudo existe déjà...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupProfileWidgets (UserGlobalSettings usersettings){
        Log.d(TAG, "setupProfileWidgets: setting widgets with data retrieving from Firebase: " + usersettings.toString());
        muserGlobalSettings = usersettings;
        UserAccountSettings userAccountSettings = usersettings.getUserAccountSettings();
        UnivImageLoader.setImage(userAccountSettings.getProfile_photo(), mprofileImage, null,"");
        mUsername.setText(userAccountSettings.getUsername());
        mDescription.setText(userAccountSettings.getDescription());
        mEmail.setText(usersettings.getUser().getEmail());
        mEditProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent i = new Intent(getActivity(), ShareActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //# of flag = 268435456
                getActivity().startActivity(i);
                getActivity().finish();
            }
        });
    }
    /*
      Authentification on firebase
   */
    public void setupAuth(){
        Log.d(TAG, "setupAuth: started.");
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        mAutListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: singed in: " + user.getUid());
                }else {
                    Log.d(TAG, "onAuthStateChanged: singed out");
                }
            }
        };
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // user informations
                setupProfileWidgets(mdatabaseMethods.getAccountSettings(dataSnapshot));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAutListener);
    }

    public void onStop() {
        super.onStop();
        if (mAutListener != null){
            mAuth.removeAuthStateListener(mAutListener);
        }
    }

    @Override
    public void onConfirmPassword(String pwd) {
        Log.d(TAG, "onConfirmPassword: getting password : "+ pwd);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), pwd);

        // Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete:  re-authenticated successful.");

                            //check if email is not already present in the database
                             mAuth.fetchProvidersForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                 @Override
                                 public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                     if(task.isSuccessful()){

                                         try {
                                             if(task.getResult().getProviders().size() == 1){
                                                 Log.d(TAG, "onComplete: That email exists already.");
                                                 Toast.makeText(getActivity(), "Cet email est déjà utilisé", Toast.LENGTH_SHORT).show();
                                             }
                                             else {
                                                 Log.d(TAG, "onComplete: That email is available.");

                                                 mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                 if (task.isSuccessful()) {
                                                                     Log.d(TAG, "User email address updated.");
                                                                     Toast.makeText(getActivity(), "Email enregistré", Toast.LENGTH_SHORT).show();
                                                                     mdatabaseMethods.updateEmail(mEmail.getText().toString());
                                                                 }
                                                             }
                                                         });
                                             }
                                         }catch (NullPointerException e){
                                             Log.d(TAG, "onComplete: NullPointerException: "+e.getMessage());
                                         }

                                     }
                                 }
                             });
                        }

                        else {
                           Log.d(TAG, "onComplete: re-authenticated failed.");

                        }

                    }
                });

    }
}
