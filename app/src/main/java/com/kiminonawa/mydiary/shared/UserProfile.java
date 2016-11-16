package com.kiminonawa.mydiary.shared;

import android.net.Uri;

/**
 * Created by daxia on 2016/11/16.
 */

public class UserProfile {

    /**
     *
     */
    private String name;
    private String email;
    private String personId;
    private Uri photo;

    /**
     * Singleton
     */
    private static UserProfile instance = null;

    private UserProfile() {
    }

    public static UserProfile getInstance() {
        if (instance == null) {
            synchronized (UserProfile.class) {
                if (instance == null) {
                    instance = new UserProfile();
                }
            }
        }
        return instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

    public static void setInstance(UserProfile instance) {
        UserProfile.instance = instance;
    }
}
