package com.kaayso.benyoussafaycel.android_app.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kaayso.benyoussafaycel.android_app.Models.Group;
import com.kaayso.benyoussafaycel.android_app.Models.User;
import com.kaayso.benyoussafaycel.android_app.Models.UserAccountSettings;
import com.kaayso.benyoussafaycel.android_app.Profile.ProfileActivity;
import com.kaayso.benyoussafaycel.android_app.R;
import com.kaayso.benyoussafaycel.android_app.Tools.BotNavView;
import com.kaayso.benyoussafaycel.android_app.Tools.UserListAdapter;
import com.kaayso.benyoussafaycel.android_app.Tools.ViewProfileFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by BenyoussaFaycel on 16/03/2018.
 */

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private Context mContext = SearchActivity.this;
    private static final int NUM_ACTIVITY =1;


    private ListView mListview;
    private EditText mInputSearch;
    private List<User> mUsers;
    private UserListAdapter userListAdapter;
    private Group mCurrentGroup = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: started.");
        mInputSearch = (EditText) findViewById(R.id.seach_input);
        mListview = (ListView) findViewById(R.id.list_view);
        mContext = SearchActivity.this;

        getExtras();
        closeKeyBoard();
        setupBottomNavigationView();
        initTextListener();
    }

    private void getExtras(){
        Log.d(TAG, "getExtra: getting extras.");
        if(getIntent().hasExtra("fragment")){
            if(getIntent().hasExtra("group")){
                mCurrentGroup = getIntent().getParcelableExtra("group");
            }
        }
    }
    private void closeKeyBoard(){
        if(getCurrentFocus() != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    private void searchForMatch(String word){
        Log.d(TAG, "searchForMatch: searching for a match: "+ word);
        mUsers.clear();

        //update users list
        if(word.length() != 0){
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            Query query = databaseReference.child("users").orderByChild("username").equalTo(word);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for ( DataSnapshot ds : dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: found users" + ds.getValue(User.class).toString());
                        mUsers.add(ds.getValue(User.class));

                        // Update listView
                        updateUsersList();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {

        }
    }

    private void initTextListener(){
        Log.d(TAG, "initTextListener: initializing");

        mUsers = new ArrayList<>();

        mInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                String input = mInputSearch.getText().toString().toLowerCase(Locale.getDefault());
                searchForMatch(input);
                Log.d(TAG, "afterTextChanged: LOCALE GETDEFAULT() : "+ input);

            }
        });
    }

    private void updateUsersList(){
        Log.d(TAG, "updateUsersList: updating users list");

        userListAdapter = new UserListAdapter(mContext, R.layout.layout_user_search, mUsers,mCurrentGroup);
        mListview.setAdapter(userListAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected user "+ mUsers.get(position).toString());
                //navigate to profile activity
                Intent i = new Intent(mContext, ProfileActivity.class);
                i.putExtra("calling activity", "SearchActivity");
                i.putExtra("user", mUsers.get(position));
                startActivity(i);
            }
        });

    }


    /*
   * BottomNavigationView setup
   */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setUpBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BotNavView.setupBottomNavigationView(bottomNavigationViewEx);
        BotNavView.enableNavigation(mContext,this, bottomNavigationViewEx);
        // When activity is visited set check on item
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(NUM_ACTIVITY);
        menuItem.setChecked(true);
    }



}
