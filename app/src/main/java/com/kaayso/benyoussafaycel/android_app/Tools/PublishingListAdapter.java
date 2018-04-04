package com.kaayso.benyoussafaycel.android_app.Tools;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kaayso.benyoussafaycel.android_app.Home.HomeActivity;
import com.kaayso.benyoussafaycel.android_app.Models.Comment;
import com.kaayso.benyoussafaycel.android_app.Models.Like;
import com.kaayso.benyoussafaycel.android_app.Models.Photo;
import com.kaayso.benyoussafaycel.android_app.Models.User;
import com.kaayso.benyoussafaycel.android_app.Models.UserAccountSettings;
import com.kaayso.benyoussafaycel.android_app.Profile.ProfileActivity;
import com.kaayso.benyoussafaycel.android_app.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BenyoussaFaycel on 02/04/2018.
 */

public class PublishingListAdapter extends ArrayAdapter<Photo> {
    public interface OnLoadMoreItemsListener{
        void onLoadMoreItems();
    }
    public OnLoadMoreItemsListener onLoadMoreItemsListener;
    private static final String TAG = "PublishingListAdapter";
    private LayoutInflater layoutInflater;
    private  List<Photo> mphotos = null;
    private int layoutRessource;
    private Context mcontext;
    private DatabaseReference mreference;
    private String currentUser ="";


    public PublishingListAdapter(@NonNull Context context, int resource, @NonNull List<Photo> objects) {
        super(context, resource, objects);
        mcontext = context;
        layoutRessource = resource;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mreference = FirebaseDatabase.getInstance().getReference();

    }


    //Make same job as recyclerView
    private static class ViewHolder{
        public TextView mdate, musername, mcaption, matusername, mlikes, mcomments;
        public CircleImageView mprofilePhoto;
        ImageView redHeart, whiteHeart, comment, share, menu, date;
        String LikesString;
        SquareImageView image;

        UserAccountSettings userAccountSettings = new UserAccountSettings();
        User user =new User();
        StringBuilder users;
        String mLikesString;
        boolean likeByCurrentUser;
        Heart heart;
        GestureDetector detector;
        Photo photo;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        final ViewHolder viewHolder;

            convertView = layoutInflater.inflate(layoutRessource, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.musername = (TextView) convertView.findViewById(R.id.username);
            viewHolder.matusername = (TextView) convertView.findViewById(R.id.Atusername);
            viewHolder.mdate = (TextView) convertView.findViewById(R.id.tvDate);
            viewHolder.mcaption = (TextView) convertView.findViewById(R.id.image_caption);
            viewHolder.mlikes = (TextView) convertView.findViewById(R.id.image_likes);
            viewHolder.mcomments = (TextView) convertView.findViewById(R.id.image_comments);
            viewHolder.mprofilePhoto = (CircleImageView) convertView.findViewById(R.id.profile_photo);
            viewHolder.image = (SquareImageView) convertView.findViewById(R.id.post_img);
            viewHolder.redHeart = (ImageView) convertView.findViewById(R.id.iv_heart_red);
            viewHolder.whiteHeart = (ImageView) convertView.findViewById(R.id.iv_heart);
            viewHolder.comment = (ImageView) convertView.findViewById(R.id.iv_comment);
            viewHolder.share = (ImageView) convertView.findViewById(R.id.iv_share);
            viewHolder.menu = (ImageView) convertView.findViewById(R.id.ivMenu_post);
            viewHolder.date = (ImageView) convertView.findViewById(R.id.ivdate);

            viewHolder.heart = new Heart(viewHolder.whiteHeart,viewHolder.redHeart);
            viewHolder.photo = getItem(position);
            viewHolder.detector = new GestureDetector(mcontext, new GestureListener(viewHolder));
            viewHolder.users = new StringBuilder();

            convertView.setTag(viewHolder);


        Log.d(TAG, "liked : "+ viewHolder.likeByCurrentUser);

        //get the current user username (need for cheching likes string)
        getCurrentUsername();

        //get likes string
        getLikes(viewHolder);


        //set the comment
        List<Comment> comments = getItem(position).getComments();
        Log.d(TAG, "getView: cette photo a :" + comments.size() +" commentaires");

        viewHolder.mcomments.setText("Voir les commentaires...");

        viewHolder.mcomments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigation to comments for : "+ getItem(position).getPhoto_id());
                ((HomeActivity)mcontext).onCommentSelected(getItem(position),"HomeActivity");
                // hide viewpager layout
                ((HomeActivity)mcontext).hideLayout();

            }
        });
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigation to comments for : "+ getItem(position).getPhoto_id());
                ((HomeActivity)mcontext).onCommentSelected(getItem(position),"HomeActivity");
                // hide viewpager layout
                ((HomeActivity)mcontext).hideLayout();

            }
        });

        Log.d(TAG, "getView: photo : " + getItem(position).getCaption());
        //set image
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(getItem(position).getImage_path(), viewHolder.image);

        //set date
        viewHolder.mdate.setText(getItem(position).getDate_created());

        //set caption
        viewHolder.mcaption.setText(getItem(position).getCaption());

        //set profile photo and username/@username
        final Query query = mreference.child("user_account_settings")
                .orderByChild("user_id")
                .equalTo(getItem(position).getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot snap : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found the user of the current post: " +
                            snap.getValue(UserAccountSettings.class).toString());

                    viewHolder.musername.setText(snap.getValue(UserAccountSettings.class).getUsername());
                    viewHolder.matusername.setText("@"+snap.getValue(UserAccountSettings.class).getUsername());

                    imageLoader.displayImage(snap.getValue(UserAccountSettings.class).getProfile_photo(), viewHolder.mprofilePhoto);

                    viewHolder.musername.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile user : " + viewHolder.user.getUsername());

                            Intent intent =new Intent(mcontext, ProfileActivity.class);
                            intent.putExtra("calling activity", "HomeActivity");
                            intent.putExtra("user", viewHolder.user);
                            mcontext.startActivity(intent);
                        }
                    });

                    viewHolder.mprofilePhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile user : " + viewHolder.user.getUsername());

                            Intent intent =new Intent(mcontext, ProfileActivity.class);
                            intent.putExtra("calling activity", "HomeActivity");
                            intent.putExtra("user", viewHolder.user);
                            mcontext.startActivity(intent);
                        }
                    });
                    viewHolder.userAccountSettings = snap.getValue(UserAccountSettings.class);

                    viewHolder.comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to comments fragment : ");
                            ((HomeActivity)mcontext).onCommentSelected(getItem(position),"HomeActivity");
                            // hide viewpager layout
                            ((HomeActivity)mcontext).hideLayout();
                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get the user object
        final Query query2 = mreference.child("users")
                .orderByChild("user_id")
                .equalTo(getItem(position).getUser_id());

        query2.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found the user (target): "+
                            snap.getValue(User.class).getUsername());
                    viewHolder.user = snap.getValue(User.class);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (isEndOfList(position)){
            Log.d(TAG, "getView: end of list");
           loadMoreData();
        }

        return convertView;
    }

    private boolean isEndOfList(int position){
        return position == getCount() -1;
    }

    private void loadMoreData(){
        try {
            onLoadMoreItemsListener = (OnLoadMoreItemsListener) getContext();
        }catch (ClassCastException e){
            Log.d(TAG, "loadMoreData: ClassCastException" + e.getMessage());
        }

        try {
            onLoadMoreItemsListener.onLoadMoreItems();
        }catch (NullPointerException e){
            Log.d(TAG, "loadMoreData: NullPointerException" + e.getMessage());
        }
    }



    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        ViewHolder mviewHolder;
        public GestureListener(ViewHolder viewHolder) {
            mviewHolder = viewHolder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp: onSingleTapUp detected");

            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            //Query : User id of current photo owner
            final Query query = myRef.child("photos").child(mviewHolder.photo.getPhoto_id())
                    .child("likes");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()){
                        String keyId = snap.getKey();
                        // Case 1 : The user has already liked this photo
                        if (mviewHolder.likeByCurrentUser && snap.getValue(Like.class).getUser_id()
                                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            Log.d(TAG, "onDataChange: removing");
                            mreference.child("photos").child(mviewHolder.photo.getPhoto_id())
                                    .child("likes").child(keyId).removeValue();

                            mreference.child("user_photos")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mviewHolder.photo.getPhoto_id())
                                    .child("likes").child(keyId).removeValue();

                            mviewHolder.heart.toggleLike();
                            getLikes(mviewHolder);
                        }

                        // Case 2 : The user hasn't liked this photo
                        else if(!mviewHolder.likeByCurrentUser){
                            //Add a new like
                            addNewLike(mviewHolder);
                            break;
                        }

                    }
                    if (!dataSnapshot.exists()){
                        //Add a new like
                        addNewLike(mviewHolder);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }

    private void getCurrentUsername (){
        Log.d(TAG, "getCurrentUsername: retreiving current useraccountsettings : ");

        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        final Query query = myRef.child("user_account_settings")// USER node?
                .orderByChild("user_id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: currentUser : " +snap.getValue(UserAccountSettings.class).getUsername());
                    currentUser = snap.getValue(UserAccountSettings.class).getUsername();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getLikes(final ViewHolder viewHolder){
        Log.d(TAG, "getLikes: getting likes string");
        viewHolder.LikesString="";
        try {
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

            //Query: Getting all likes
            Log.d(TAG, "getLikes: photo : " +viewHolder.photo.getCaption());
            final Query query = myRef.child("photos").child(viewHolder.photo.getPhoto_id())
                    .child("likes");

            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    viewHolder.users = new StringBuilder();
                    //Query : getting users id who liked the current photo
                    for (DataSnapshot snap : dataSnapshot.getChildren()){
                        final Query query = mreference.child("users").orderByChild("user_id")
                                .equalTo(snap.getValue(Like.class).getUser_id());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snap : dataSnapshot.getChildren()){
                                    Log.d(TAG, "onDataChange: found like: "+ snap.getValue(User.class).getUsername());

                                    viewHolder.users .append(snap.getValue(User.class).getUsername());
                                    viewHolder.users .append(",");

                                }
                                String[] splitUsers = viewHolder.users .toString().split(",");
                                Log.d(TAG, "onDataChange: current username "+currentUser);
                                if (viewHolder.users .toString().contains(currentUser+",")){
                                    viewHolder.likeByCurrentUser= true;
                                }
                                else {
                                    viewHolder.likeByCurrentUser= false;
                                }
                                int len =splitUsers.length;
                                if (len == 1){
                                    viewHolder.LikesString = "Likée par " + splitUsers[0] + ".";
                                }else if (len == 2){
                                    viewHolder.LikesString = "Likée par " + splitUsers[0] + " et " + splitUsers[1] + ".";
                                }
                                else if (len ==3){
                                    viewHolder.LikesString = "Likée par " + splitUsers[0] + ", " + splitUsers[1] + " et " + splitUsers[2] + ".";

                                }
                                else if (len  > 3){
                                    viewHolder.LikesString = "Likée par  " + splitUsers[0] + ", " + splitUsers[1] + ", " + splitUsers[2]
                                            + " et " + (splitUsers.length - 3) + " autres personnes...";
                                }
                                //setupWidgets();
                                Log.d(TAG, "onDataChange: likes string: " + viewHolder.LikesString);
                                setupLikesString(viewHolder, viewHolder.LikesString);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    // NO LIKES
                    if (! dataSnapshot.exists()){
                        Log.d(TAG, "onDataChange: No data for likes...");
                        viewHolder.LikesString ="";
                        viewHolder.likeByCurrentUser = false;
                        //setupWidgets();
                        setupLikesString(viewHolder, viewHolder.LikesString);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){
            Log.d(TAG, "getLikes: NullPointerException " + e.getMessage() );
            viewHolder.LikesString ="";
            viewHolder.likeByCurrentUser = false;
            //setup likes string
            setupLikesString(viewHolder, viewHolder.LikesString);
        }



    }


    private void addNewLike(ViewHolder viewHolder){
        Log.d(TAG, "addNewLike: adding new like");
        String newID = mreference.push().getKey();
        Like myLike = new Like(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mreference.child("photos").child(viewHolder.photo.getPhoto_id())
                .child("likes").child(newID).setValue(myLike);

        mreference.child("user_photos")
                .child(viewHolder.photo.getUser_id())
                .child(viewHolder.photo.getPhoto_id())
                .child("likes").child(newID).setValue(myLike);

        viewHolder.heart.toggleLike();
        getLikes(viewHolder);

    }


    private void setupLikesString(final ViewHolder holder, String likesStrings){
        Log.d(TAG, "setupLikesString: likes string: "+ holder.LikesString);

        if(holder.likeByCurrentUser){
            Log.d(TAG, "setupLikesString: photo is liked by current user: " + holder.user.getUsername());
            holder.whiteHeart.setVisibility(View.GONE);
            holder.redHeart.setVisibility(View.VISIBLE);
            holder.redHeart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    return holder.detector.onTouchEvent(event);
                }
            });


        }else {
            Log.d(TAG, "setupLikesString: photo isn't liked by current user: " + currentUser);
            holder.whiteHeart.setVisibility(View.VISIBLE);
            holder.redHeart.setVisibility(View.GONE);
            holder.whiteHeart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    return holder.detector.onTouchEvent(event);
                }
            });
        }

        holder.mlikes.setText(likesStrings);
    }


}
