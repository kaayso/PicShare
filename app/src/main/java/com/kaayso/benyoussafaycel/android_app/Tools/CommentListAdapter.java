package com.kaayso.benyoussafaycel.android_app.Tools;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kaayso.benyoussafaycel.android_app.Models.Comment;
import com.kaayso.benyoussafaycel.android_app.Models.User;
import com.kaayso.benyoussafaycel.android_app.Models.UserAccountSettings;
import com.kaayso.benyoussafaycel.android_app.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BenyoussaFaycel on 01/04/2018.
 */

public class CommentListAdapter extends ArrayAdapter<Comment>{
    private static final String TAG = "CommentListAdapter";

    private LayoutInflater minflater;
    private int layout;
    private Context mcontext;
    private UserAccountSettings mUserAccountSettings;

    public CommentListAdapter(@NonNull Context context, int resource, @NonNull List<Comment> objects) {
        super(context, resource, objects);
        minflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        mcontext = context;
        layout = resource;

    }

    //Make same job as recyclerView
    private static class ViewHolder{
        public TextView comment, username, atusername, date, likes, reply;
        public CircleImageView profilePhoto;
        public ImageView heart;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null){
                convertView = minflater.inflate(layout,parent,false);
                viewHolder = new ViewHolder();

                viewHolder.comment = (TextView) convertView.findViewById(R.id.tvComment);
                viewHolder.username = (TextView) convertView.findViewById(R.id.commentUsername);
                viewHolder.atusername = (TextView) convertView.findViewById(R.id.commentAtUsername);
                viewHolder.date = (TextView) convertView.findViewById(R.id.commentDatePosted);
                viewHolder.likes = (TextView) convertView.findViewById(R.id.commentLikes);
                viewHolder.reply = (TextView) convertView.findViewById(R.id.commentReply);
                viewHolder.profilePhoto = (CircleImageView) convertView.findViewById(R.id.comment_profilePhoto);
                viewHolder.heart = (ImageView) convertView.findViewById(R.id.commentHeart);

                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //set the comment
            viewHolder.comment.setText(getItem(position).getComment());

            //set date
            viewHolder.date.setText(getItem(position).getDate_posted());

            //set user informations
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

            //Querry : User id of current comment owner
            final Query query = myRef.child("user_account_settings").orderByChild("user_id")
                    .equalTo(getItem(position).getUser_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap: dataSnapshot.getChildren()){
                        mUserAccountSettings = snap.getValue(UserAccountSettings.class);
                        viewHolder.username.setText(mUserAccountSettings.getUsername());
                        viewHolder.atusername.setText("@"+mUserAccountSettings.getUsername());

                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.displayImage(mUserAccountSettings.getProfile_photo(),
                                viewHolder.profilePhoto);

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query canceled");
                }
            });

            try {
                if(position == 0){
                    viewHolder.likes.setVisibility(View.GONE);
                    viewHolder.reply.setVisibility(View.GONE);
                    viewHolder.heart.setVisibility(View.GONE);
                }
            }catch (NullPointerException e){
                Log.d(TAG, "getView: NullPointerException" + e.getMessage());
            }

            return  convertView;

    }
}
