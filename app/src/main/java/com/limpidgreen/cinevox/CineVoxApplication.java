/**
 * CineVoxApplication.java
 *
 * 10.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox;

import android.app.Application;

import com.facebook.model.GraphUser;

/**
 * CineVox Application.
 *
 * @author MajaDobnik
 *
 */
public class CineVoxApplication extends Application {
    private String APIToken;
    private GraphUser user;
    private String facebookAccessToken;

    public String getAPIToken() {
        return APIToken;
    }

    public void setAPIToken(String APIToken) {
        this.APIToken = APIToken;
    }

    @Override
    public void onCreate() {
    }
}
