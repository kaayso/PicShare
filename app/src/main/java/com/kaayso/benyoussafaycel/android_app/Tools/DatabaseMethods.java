package com.kaayso.benyoussafaycel.android_app.Tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaayso.benyoussafaycel.android_app.Home.HomeActivity;
import com.kaayso.benyoussafaycel.android_app.Models.Group;
import com.kaayso.benyoussafaycel.android_app.Models.Photo;
import com.kaayso.benyoussafaycel.android_app.Models.User;
import com.kaayso.benyoussafaycel.android_app.Models.UserAccountSettings;
import com.kaayso.benyoussafaycel.android_app.Models.UserGlobalSettings;
import com.kaayso.benyoussafaycel.android_app.Profile.SettingsActivity;
import com.kaayso.benyoussafaycel.android_app.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by BenyoussaFaycel on 18/03/2018.
 */

public class DatabaseMethods {
    private final String TAG = "DatabaseMethods";

    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAutListener;
    private String muserId;

    private Context mcontext;
    private StorageReference mStorageRef;
    private double mPhotoUploadProgress = 0;

    public DatabaseMethods(Context mcontext) {
        mAuth = FirebaseAuth.getInstance();
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        this.mcontext = mcontext;

        if(mAuth.getCurrentUser()!=null) muserId = mAuth.getCurrentUser().getUid();
    }

    /*public boolean isUsernameExist(String username, DataSnapshot dataSnapshot){
        Log.d(TAG, "isUsernameExist: checking "+username+" already exists.");
        User user =  new User();

        for(DataSnapshot dataS : dataSnapshot.child(muserId).getChildren()){
            Log.d(TAG, "isUsernameExist: "+ dataS.toString());
            user.setUsername(dataS.getValue(User.class).getUsername());
            if (user.getUsername().equals(username)){
                Log.d(TAG, "isUsernameExist: Found a Match: "+ user.getUsername());
                return true;
            }

        }
        return false;
    }*/

    public void updateUsername (String username){
        Log.d(TAG, "updateUsername: Updating username to : "+ username);
        mdatabaseReference.child("users").child(muserId).child("username").setValue(username);
        mdatabaseReference.child("user_account_settings").child(muserId).child("username").setValue(username);
    }
    public void updateEmail (String email){
        Log.d(TAG, "updateEmail: Updating email to : "+ email);
        mdatabaseReference.child("users").child(muserId).child("email").setValue(email);
    }

    public void updateDescription(String description) {
        Log.d(TAG, "updateDescription: Updating description to : "+ description);
        mdatabaseReference.child("user_account_settings").child(muserId).child("description").setValue(description);
    }
    public void updatePassword(String password) {
        Log.d(TAG, "updatePassword: Updating password.");
        mAuth.getCurrentUser().updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password is updated.");
                        }else {
                            Log.d(TAG, "User password is NOT updated.");
                        }
                    }
                });

    }


    public void registerNewEmail(final  String email , String password , final String usernmae){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "registerNewEmail onComplete: " +task.isSuccessful());
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            muserId = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "createUserWithEmail:success");
                            verificationEmail();
                            Toast.makeText(mcontext, "Inscription réussie.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            String message = task.getException().toString();

                            if (message.contains("email address is already in use")){
                                Toast.makeText(mcontext, "Echec de l'inscription, cette addresse email est déjà utilisée." ,
                                        Toast.LENGTH_LONG).show();
                            }
                            else if (message.contains("should be at least 6 characters")){
                                Toast.makeText(mcontext, "Echec de l'inscription, le mot de passe doit contenir 6 caractères minimum.",
                                        Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(mcontext, "Inscription échouée, ",
                                        Toast.LENGTH_LONG).show();
                            }


                        }
                    }
                });
    }

    /**
        Add user data to database
     */
    public void addNewUser (String email , String username, String description, String profile_photo){

        User user = new User(muserId, email , username );
        UserAccountSettings userAccountSettings = new UserAccountSettings(description, username, 0, 0,
                0, 0, profile_photo,muserId);

        //First node: users
        mdatabaseReference.child(mcontext.getString(R.string.db_users)).child(muserId).setValue(user);

        //Second node: user_account_settings
        mdatabaseReference.child(mcontext.getString(R.string.user_account_settings)).child(muserId).setValue(userAccountSettings);


    }

    /**
        Sendin email verification to user
     */
    public void verificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "verificationEmail: started.");
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: Sending verification email: " + task.isSuccessful() );

                    }else {
                        Log.d(TAG, "onComplete: Sending verification email: " + task.isSuccessful() );
                        Toast.makeText(mcontext , "Verification d'email non envoyée.",Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    /**
        Retrieves the account settings about the current user
     */
    public UserGlobalSettings getAccountSettings(DataSnapshot dataSnapshot){
        Log.d(TAG, "getAccountSettings: retrieving user account settings from firebase.");

        UserAccountSettings userAccountSettings = new UserAccountSettings();
        User user = new User();

        // Loop to get data from firebase

        for (DataSnapshot snap : dataSnapshot.getChildren()) {

            //user_account_settings node

            if(snap.getKey().equals(mcontext.getString(R.string.user_account_settings))){
                Log.d(TAG, "getAccountSettings: datasnapshot: "+ snap);
                try{

                    //Username
                    userAccountSettings.setUsername(snap.child(muserId)
                            .getValue(UserAccountSettings.class)
                            .getUsername());

                    //Description
                    userAccountSettings.setDescription(snap.child(muserId)
                            .getValue(UserAccountSettings.class)
                            .getDescription());

                    //Followers
                    userAccountSettings.setFollowers(snap.child(muserId)
                            .getValue(UserAccountSettings.class)
                            .getFollowers());

                    //Following
                    userAccountSettings.setFollowing(snap.child(muserId)
                            .getValue(UserAccountSettings.class)
                            .getFollowing());

                    //Posts
                    userAccountSettings.setPosts(snap.child(muserId)
                            .getValue(UserAccountSettings.class)
                            .getPosts());

                    //Groups
                    userAccountSettings.setGroups(snap.child(muserId)
                            .getValue(UserAccountSettings.class)
                            .getGroups());

                    //Profile photo
                    userAccountSettings.setProfile_photo(snap.child(muserId)
                            .getValue(UserAccountSettings.class)
                            .getProfile_photo());
                    Log.d(TAG, "getAccountSettings: retrieved user_account_settings: "+ userAccountSettings.toString());
                }catch (NullPointerException e){
                    Log.d(TAG, "getAccountSettings: NullPointerException: " + e.getMessage());
                }

            }

            // user node
            if(snap.getKey().equals(mcontext.getString(R.string.db_users))) {
                Log.d(TAG, "getAccountSettings: datasnapshot: " + snap);

                try{

                    //Username
                    user.setUsername(snap.child(muserId)
                            .getValue(User.class)
                            .getUsername());

                    //Email
                    user.setEmail(snap.child(muserId)
                            .getValue(User.class)
                            .getEmail());

                    //UserID
                    user.setUser_id(snap.child(muserId)
                            .getValue(User.class)
                            .getUser_id());
                    Log.d(TAG, "getAccountSettings: retrieved user: "+ user.toString());

                }catch (NullPointerException e){
                    Log.d(TAG, "getAccountSettings: NullPointerException: " + e.getMessage());
                }
            }

        }

        return new UserGlobalSettings(user , userAccountSettings);
    }


    public int getImagesCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for (DataSnapshot snap : dataSnapshot.child("user_photos").child(muserId).getChildren()){
            count ++;
        }
        return count;
    }


    public void uploadPhoto(String typePhoto, final String description, final String visibility, int countImg, final String mImgUrl
            , final String group_id, Bitmap bitmap) {
        Log.d(TAG, "uploadPhoto: uploading new photo: " +mImgUrl);
        PathsFile pathsFile = new PathsFile();

        /**
         *  Case NEW photo
         */
        if(typePhoto.equals("new_photo")){
            Log.d(TAG, "uploadPhoto: new photo");

            StorageReference storageReference = mStorageRef.child(pathsFile.FIREBASE_IMG_STORAGE + "/"
                    + FirebaseAuth.getInstance().getCurrentUser().getUid()+"/photo_"+(countImg+1));

            //convert url to bitmap
            if (bitmap == null)bitmap = ImageManager.getBitmap(mImgUrl);
            byte[] data = ImageManager.getBytesFromBitmap(bitmap,100);
            UploadTask uploadTask =storageReference.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mcontext, "Transfert de photo réussi", Toast.LENGTH_SHORT).show();

                    // add the new photo to : users_photo node et photos node
                    addPhotoToDb(description ,visibility, firebaseUrl.toString(), group_id);

                    // navigate to home activity
                    Intent i = new Intent(mcontext, HomeActivity.class);
                    mcontext.startActivity(i);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: upload failed.");
                    Toast.makeText(mcontext, "Transfert de photo échoué", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()) ;

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mcontext, "Transfert de photo en cours: "+ String.format("%.0f",progress )+ "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: "+ progress + "% done");
                }
            });


        }

        /**
         *  Case PROFILE photo
         */
        else if(typePhoto.equals("profile_photo")){
            Log.d(TAG, "uploadPhoto: new profile photo");
            StorageReference storageReference = mStorageRef.child(pathsFile.FIREBASE_IMG_STORAGE + "/"
                    + FirebaseAuth.getInstance().getCurrentUser().getUid()+"/profile_photo");

            //convert url to bitmap
            if (bitmap == null)bitmap = ImageManager.getBitmap(mImgUrl);
            byte[] data = ImageManager.getBytesFromBitmap(bitmap,100);
            UploadTask uploadTask =storageReference.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mcontext, "Transfert de photo réussi", Toast.LENGTH_SHORT).show();

                    // add the new photo to : users_settings node
                    setProfilePhoto(firebaseUrl.toString());

                    ((SettingsActivity)mcontext).setViewPager(
                            ((SettingsActivity)mcontext).mpagerAdapter.getFragmentNumber("Modifier le profil")
                    );

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: upload failed.");
                    Toast.makeText(mcontext, "Transfert de photo échoué", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()) ;

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mcontext, "Transfert de photo en cours: "+ String.format("%.0f",progress )+ "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: "+ progress + "% done");
                }
            });
        }
    }

    private void setProfilePhoto(String imgUrl){
        Log.d(TAG, "setProfilePhoto: setting profile image : "+ imgUrl);
        mdatabaseReference.child("user_account_settings")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("profile_photo").setValue(imgUrl);

    }

    private void addPhotoToDb(String description, String visibility, String imgUrl , String group_id){
        Log.d(TAG, "addPhotoToDb: Adding photo to database");
        String tags = extractTags(description);
        //New photo id
        String id_photo = mdatabaseReference.child("photos").push().getKey();

        Photo photo = new Photo();
        photo.setCaption(description);
        photo.setDate_created(getTimeStamp());
        photo.setVisibility(visibility);
        photo.setImage_path(imgUrl);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(id_photo);
        photo.setGroup_id(group_id);

        //insert to database
        mdatabaseReference.child("user_photos").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(id_photo).setValue(photo);
        mdatabaseReference.child("photos").child(id_photo).setValue(photo);

    }

    private String getTimeStamp() {
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy' 'HH:mm:ss' '", Locale.FRANCE);
        date.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        return date.format(new Date());

    }

    private String extractTags(String desc){
        if (desc.indexOf('#')>0){
            StringBuilder stringBuilder = new StringBuilder();
            char[] charArray = desc.toCharArray();
            boolean foundword = false;
            for( char c : charArray){
                if(c == ' ' ){
                    foundword = false;
                }
                if(c == '#'){
                    foundword = true;
                    stringBuilder.append(c);
                }else{
                    if(foundword){
                        stringBuilder.append(c);
                    }
                }

            }
            String s = stringBuilder.toString().replace("#", ",#");
            return s.substring(1, s.length());
        }
        return "";

    }


}
