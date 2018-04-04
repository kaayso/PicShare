package com.kaayso.benyoussafaycel.android_app.Authentication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.jackandphantom.circularimageview.CircleImage;
import com.kaayso.benyoussafaycel.android_app.Home.HomeActivity;
import com.kaayso.benyoussafaycel.android_app.R;

/**
 * Created by BenyoussaFaycel on 17/03/2018.
 */

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";

    private Context mcontex;
    private EditText memail, mpassword;
    private TextView mnewAccount , mwaiting;
    private ProgressBar mprogressBar;
    private Button msingin;
    private CircleImage mlogo;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAutListener;



    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Log.d(TAG, "onCreate: started.");
        mprogressBar = (ProgressBar) findViewById(R.id.progressBar);
        mwaiting = (TextView) findViewById(R.id.waiting);
        memail = (EditText) findViewById(R.id.input_email);
        mpassword = (EditText) findViewById(R.id.input_pwd);
        mcontex = StartActivity.this;
        mlogo = (CircleImage) findViewById(R.id.logo);
        mlogo.setBorderWidth(5);
        mlogo.setAddShadow(true);
        mlogo.setShadowRadius(20);
        mlogo.setShadowColor(Color.BLACK);

        mwaiting.setVisibility(View.GONE);
        mprogressBar.setVisibility(View.GONE);
        setupBase();
        setupAuth();

    }


    @Override
    public void onBackPressed(){
        Log.d(TAG, "onBackPressed: back pressed.");
        final AlertDialog.Builder builder = new AlertDialog.Builder(mcontex);
        builder.setMessage("Voulez-vous vraiment quitter?");
        builder.setCancelable(true);
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: Quitting");
                finish();
            }
        });
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*
       Setup firebase
    */
    public void setupAuth(){
        Log.d(TAG, "setupAuth: started.");
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

    private void setupBase(){
        // Sing in Button
        msingin = (Button) findViewById(R.id.SingIn_btn);
        msingin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = memail.getText().toString();
                String pwd = mpassword.getText().toString();

                if (email.equals("") || pwd.equals("")){
                    Toast.makeText(mcontex, "Veuillez remplir tous les champs SVP", Toast.LENGTH_SHORT).show();
                }
                else {
                    mprogressBar.setVisibility(View.VISIBLE);
                    mwaiting.setVisibility(View.VISIBLE);
                    Toast.makeText(mcontex, "Connexion...", Toast.LENGTH_SHORT).show();

                    mAuth.signInWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "onComplete: "+ task.isSuccessful());
                                    FirebaseUser user = mAuth.getCurrentUser();


                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        try {
                                            if(user.isEmailVerified()){
                                                Log.d(TAG, "onComplete: isEmailVerified: " + user.isEmailVerified());
                                                Toast.makeText(mcontex, "Bienvenue parmi nous.", Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(StartActivity.this , HomeActivity.class);
                                                startActivity(i);
                                            }
                                            else{
                                                Toast.makeText(mcontex, "Email non vérifié...",Toast.LENGTH_SHORT).show();
                                                mprogressBar.setVisibility(View.GONE);
                                                mwaiting.setVisibility(View.GONE);
                                                mAuth.signOut();
                                            }
                                        }catch (NullPointerException e){
                                            Log.d(TAG, "onComplete: NullPointerException: "+ e.getMessage());
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(mcontex, "Echec de l'authentification.",
                                                Toast.LENGTH_SHORT).show();
                                        mprogressBar.setVisibility(View.GONE);
                                        mwaiting.setVisibility(View.GONE);
                                    }
                                }
                            });




                }
            }
        });

        mnewAccount = (TextView) findViewById(R.id.link_signup);
        mnewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Go to register activity.");
                Intent i = new Intent(mcontex, RegisterActivity.class);
                startActivity(i);
            }
        });
        if (mAuth.getInstance().getCurrentUser()!= null){
            Intent i = new Intent(mcontex, HomeActivity.class);
            startActivity(i);
            finish();
        }
    }

}
