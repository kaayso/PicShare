package com.kaayso.benyoussafaycel.android_app.Authentication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.DatabaseMethods;

/**
 * Created by BenyoussaFaycel on 17/03/2018.
 */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAutListener;

    private Context mcontext;
    private String email, password, username;
    private EditText mEmail, mUsername , mPassword;
    private TextView mwaiting;
    private Button mBtnRegister;
    private ProgressBar mprogressBar;
    private DatabaseMethods databaseMethods;

    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;


    private String adding= "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        Log.d(TAG, "onCreate: started.");
        mcontext = RegisterActivity.this;
        databaseMethods = new DatabaseMethods(mcontext);
        initWidget();
        setupAuth();
        init();
    }


    /*
        Initialisation widgets
     */
    private void initWidget(){
        Log.d(TAG, "initWidget: initializing widgets");
        mprogressBar = (ProgressBar) findViewById(R.id.progressBar);
        mwaiting = (TextView) findViewById(R.id.waiting);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_pwd);
        mUsername = (EditText) findViewById(R.id.input_username);
        mBtnRegister = (Button) findViewById(R.id.register_btn);
        mcontext = RegisterActivity.this;

        mwaiting.setVisibility(View.GONE);
        mprogressBar.setVisibility(View.GONE);
    }


    private void init(){

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();

                if (checkInputs(email, username , password)){
                    mprogressBar.setVisibility(View.VISIBLE);
                    mwaiting.setVisibility(View.VISIBLE);
                    username=username.toLowerCase();
                    databaseMethods.registerNewEmail(email, password, username);
                }
            }
        });
    }

    private boolean checkInputs(String email, String username, String password){
        Log.d(TAG, "checkInputs: Checking inputs values.");
        if (email.equals("") ||username.equals("")||password.equals("")){
            Toast.makeText(mcontext,"Veuillez remplir tous les champs SVP.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /*
       Setup firebase
    */
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
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.exists()){
                        adding = mdatabaseReference.push().getKey().substring(2,10);
                        Log.d(TAG, "onDataChange: username is already exits, add rand String"+ adding);                    }
                }
                String usrname ="";
                usrname = username + adding;

                //add new user to database
                databaseMethods.addNewUser(email , usrname , "", "");
                Toast.makeText(mcontext, "Vous êtes enregistré! Vérfication de l'email envoyée."
                        ,Toast.LENGTH_SHORT).show();

                mAuth.signOut();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void setupAuth(){
        Log.d(TAG, "setupAuth: started.");
        mAuth = FirebaseAuth.getInstance();
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference();

        mAutListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: singed in: " + user.getUid());
                    mdatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            checkIfUsernameExists(username);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    finish();

                }else {
                    Log.d(TAG, "onAuthStateChanged: singed out");
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAutListener);
    }

    public void onStop() {
        super.onStop();
        if (mAutListener != null){
            mAuth.removeAuthStateListener(mAutListener);
        }
    }
}
