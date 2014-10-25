/**
 * CineVoxApplication.java
 *
 * 10.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox;

import android.accounts.Account;
import android.app.Application;

import com.facebook.model.GraphUser;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * CineVox Application.
 *
 * @author MajaDobnik
 *
 */
public class CineVoxApplication extends Application {
    private String APIToken;
    private Account mAccount;
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
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).build();
        ImageLoader.getInstance().init(config);
    }

    public Account getmAccount() {
        return mAccount;
    }

    public void setmAccount(Account mAccount) {
        this.mAccount = mAccount;
    }
}
