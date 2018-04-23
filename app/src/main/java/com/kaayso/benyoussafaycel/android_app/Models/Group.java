package com.kaayso.benyoussafaycel.android_app.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BenyoussaFaycel on 30/03/2018.
 */

public class Group implements Parcelable{
    private String group_id;
    private String name;
    private String description;
    private String visibility;
    private String owner_id;
    private String group_photo;
    private List <String> users;

    public Group(String group_id, String name, String description,
                 String visibility, String owner_id, String group_photo, List<String> users) {
        this.group_id = group_id;
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.owner_id = owner_id;
        this.group_photo = group_photo;
        this.users = users;
    }

    public Group() {
    }


    protected Group(Parcel in) {
        group_id = in.readString();
        name = in.readString();
        description = in.readString();
        visibility = in.readString();
        owner_id = in.readString();
        group_photo = in.readString();
        users = in.createStringArrayList();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getGroup_photo() {
        return group_photo;
    }

    public void setGroup_photo(String group_photo) {
        this.group_photo = group_photo;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Group{" +
                "group_id='" + group_id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", visibility='" + visibility + '\'' +
                ", owner_id='" + owner_id + '\'' +
                ", group_photo='" + group_photo + '\'' +
                ", users=" + users +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(group_id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(visibility);
        dest.writeString(owner_id);
        dest.writeString(group_photo);
        dest.writeStringList(users);
    }
}
