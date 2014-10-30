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

    /** The Intent extra to store movie list. */
    public static final String PARAM_MOVIE_LIST = "movie_list";

    /** The Intent extra to store the event id. */
    public static final String PARAM_EVENT_ID = "event_id";

    /** The Intent extra to store the event confirm status. */
    public static final String PARAM_EVENT_CONFIRM_STATUS = "event_confirm_status";

    /** The Intent extra to store the accept event boolean. */
    public static final String PARAM_ACCEPT = "accept";

    /** The Intent extra to store the notification id. */
    public static final String PARAM_NOTIFICATION_ID = "notification_id";

    /** Return codes */
    public static final int ACCOUNT_LOG_IN_REQUEST_CODE = 0;

    public static final int EVENT_FRIEND_SELECT_REQUEST_CODE = 1;

    public static final int EVENT_MOVIE_SELECT_REQUEST_CODE = 2;

    public static final int EVENT_VOTE_MOVIE_REQUEST_CODE = 3;

    public static final String ACTION_JOIN_EVENT = "com.limpidgreen.cinevox.action.JOIN_EVENT";
    public static final String ACTION_DECLINE_EVENT = "com.limpidgreen.cinevox.action.DECLINE_EVENT";

}
