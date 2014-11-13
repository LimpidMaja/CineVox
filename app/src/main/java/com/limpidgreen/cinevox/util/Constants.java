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

    /** The Intent extra to store the voting setting use voting. */
    public static final String PARAM_USE_VOTING = "use_voting";

    /** The Intent extra to store the voting setting time limit. */
    public static final String PARAM_TIME_LIMIT = "time_limit";

    /** The Intent extra to store the voting setting voting percent. */
    public static final String PARAM_VOTING_PERCENT = "voting_percent";

    /** The Intent extra to store the voting setting rating system. */
    public static final String PARAM_RATING_SYSTEM = "rating_system";

    /** The Intent extra to store the voting setting voting range. */
    public static final String PARAM_VOTING_RANGE = "voting_range";

    /** The Intent extra to store the voting setting votes per user. */
    public static final String PARAM_USE_VOTES_PER_USER = "votes_per_user";

    /** The Intent extra to store the voting setting tie knockout. */
    public static final String PARAM_TIE_KNOCKOUT = "tie_knockout";

    /** The Intent extra to store the voting setting knockout rounds. */
    public static final String PARAM_KNOCKOUT_ROUNDS = "knockout_rounds";

    /** Return codes */
    public static final int ACCOUNT_LOG_IN_REQUEST_CODE = 0;

    public static final int EVENT_FRIEND_SELECT_REQUEST_CODE = 1;

    public static final int EVENT_MOVIE_SELECT_REQUEST_CODE = 2;

    public static final int EVENT_VOTE_MOVIE_REQUEST_CODE = 3;

    public static final int EVENT_KNOCKOUT_MOVIE_REQUEST_CODE = 4;

    public static final int EVENT_VOTING_EDIT_REQUEST_CODE = 5;

    public static final int SETTINGS_REQUEST_CODE = 6;

    public static final int LOG_OUT_RESPONSE_CODE = 1;

    public static final String ACTION_JOIN_EVENT = "com.limpidgreen.cinevox.action.JOIN_EVENT";
    public static final String ACTION_DECLINE_EVENT = "com.limpidgreen.cinevox.action.DECLINE_EVENT";

}
