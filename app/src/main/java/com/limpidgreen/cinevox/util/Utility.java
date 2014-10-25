/**
 * Utility.java
 *
 * 14.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Application utility methods.
 *
 * @author MajaDobnik
 *
 */
public class Utility {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static Pattern pattern;
    private static Matcher matcher;
    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Validate Email with regular expression
     *
     * @param email
     * @return true for Valid Email and false for Invalid Email
     */
    public static boolean validate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean checkPlayServices(Activity activity, Context context) {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        context,
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();

            }
            return false;
        } else {
            /*Toast.makeText(
                    context,
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();*/
        }
        return true;
    }
}
