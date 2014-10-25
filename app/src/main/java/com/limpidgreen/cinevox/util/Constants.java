/**
 * Constants.java
 *
 * 9.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.util;

/**
 * Application constants.
 *
 * @author MajaDobnik
 *
 */
public class Constants {
    /** The tag used to log to adb console. */
    public static final String TAG = "CineVox";

    /** Google Project Number. */
    public static final String GOOGLE_PROJ_ID = "1078274153050";

    /**
     * User Data Bundle.
     */
    public static final String USER_DATA_GCM_REG_ID = "com.limpidgreen.cinevox.userdata.gcmregid";

    /**
     * Account type string.
     */
    public static final String ACCOUNT_TYPE = "com.limpidgreen.cinevox";

    /** Extras strings. */
    public static final String API_TOKEN_TYPE = "com.limpidgreen.cinevox.api.token";

    /** The Intent extra to store username. */
    public static final String PARAM_USERNAME = "username";

    /** The Intent extra to store friend list. */
    public static final String PARAM_FRIEND_LIST = "friend_list";

    /** Return codes */
    public static final int ACCOUNT_LOG_IN_REQUEST_CODE = 0;

    public static final int EVENT_FRIEND_SELECT_REQUEST_CODE = 1;

}
