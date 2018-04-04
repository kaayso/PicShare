package com.kaayso.benyoussafaycel.android_app.Models;

/**
 * Created by BenyoussaFaycel on 30/03/2018.
 */

public class Group {
    private String groupId;
    private String groupName;
    private String owner_id;

    public Group() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }
}
