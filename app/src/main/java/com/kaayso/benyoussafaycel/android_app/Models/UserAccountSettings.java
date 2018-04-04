package com.kaayso.benyoussafaycel.android_app.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by BenyoussaFaycel on 18/03/2018.
 */

public class UserAccountSettings implements Parcelable {

    private String description;
    private String username;
    private long followers;
    private long following;
    private long groups;
    private long posts;
    private String profile_photo;
    private String user_id;


    public UserAccountSettings(String description, String username, long followers,
                               long following, long groups, long posts, String profile_photo, String user_id) {
        this.description = description;
        this.username = username;
        this.followers = followers;
        this.following = following;
        this.groups = groups;
        this.posts = posts;
        this.profile_photo = profile_photo;
        this.user_id = user_id;
    }

    public UserAccountSettings() {
    }

    protected UserAccountSettings(Parcel in) {
        description = in.readString();
        username = in.readString();
        followers = in.readLong();
        following = in.readLong();
        groups = in.readLong();
        posts = in.readLong();
        profile_photo = in.readString();
        user_id = in.readString();
    }

    public static final Creator<UserAccountSettings> CREATOR = new Creator<UserAccountSettings>() {
        @Override
        public UserAccountSettings createFromParcel(Parcel in) {
            return new UserAccountSettings(in);
        }

        @Override
        public UserAccountSettings[] newArray(int size) {
            return new UserAccountSettings[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }

    public long getFollowers() {
        return followers;
    }

    public long getFollowing() {
        return following;
    }

    public long getGroups() {
        return groups;
    }

    public long getPosts() {
        return posts;
    }

    public String getProfile_photo() {
        return profile_photo;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public void setGroups(long groups) {
        this.groups = groups;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }


    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "description='" + description + '\'' +
                ", username='" + username + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", groups=" + groups +
                ", posts=" + posts +
                ", profile_photo='" + profile_photo + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(username);
        dest.writeLong(followers);
        dest.writeLong(following);
        dest.writeLong(groups);
        dest.writeLong(posts);
        dest.writeString(profile_photo);
        dest.writeString(user_id);
    }
}
