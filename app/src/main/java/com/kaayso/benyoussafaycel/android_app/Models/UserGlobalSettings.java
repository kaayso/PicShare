package com.kaayso.benyoussafaycel.android_app.Models;

/**
 * Created by BenyoussaFaycel on 18/03/2018.
 */

public class UserGlobalSettings {
    private User user;
    private UserAccountSettings userAccountSettings;

    public UserGlobalSettings(User user, UserAccountSettings userAccountSettings) {
        this.user = user;
        this.userAccountSettings = userAccountSettings;
    }

    public UserGlobalSettings() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAccountSettings getUserAccountSettings() {
        return userAccountSettings;
    }

    public void setUserAccountSettings(UserAccountSettings userAccountSettings) {
        this.userAccountSettings = userAccountSettings;
    }

    @Override
    public String toString() {
        return "UserGlobalSettings{" +
                "user=" + user +
                ", userAccountSettings=" + userAccountSettings +
                '}';
    }
}
