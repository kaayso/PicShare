package com.kaayso.benyoussafaycel.android_app.Models;

import java.util.List;

/**
 * Created by BenyoussaFaycel on 01/04/2018.
 */

public class Comment {
    private String comment;
    private String user_id;
    private List<Like> likes;
    private String date_posted;

    public Comment(String comment, String user_id, List<Like> likes, String date_posted) {
        this.comment = comment;
        this.user_id = user_id;
        this.likes = likes;
        this.date_posted = date_posted;
    }

    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public String getDate_posted() {
        return date_posted;
    }

    public void setDate_posted(String date_posted) {
        this.date_posted = date_posted;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", user_id='" + user_id + '\'' +
                ", likes=" + likes +
                ", date_posted='" + date_posted + '\'' +
                '}';
    }

}
