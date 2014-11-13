/**
 * RatingPhase.java
 *
 * 13.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.model;

import com.google.gson.annotations.SerializedName;

/**
 * Event Rating Phase enum.
 *
 * @author MajaDobnik
 *
 */
public enum RatingPhase {
    @SerializedName("wait_users")
    WAIT_USERS(0, "wait_users"),

    @SerializedName("starting")
    STARTING(1, "starting"),

    @SerializedName("knockout_match")
    KNOCKOUT_MATCH(2, "knockout_match"),

    @SerializedName("done")
    DONE(3, "done");

    private String type;
    private Integer id;

    /**
     * Constructor.
     *
     * @param type
     */
    private RatingPhase(Integer id, String type) {
        this.id = id;
        this.type = type;
    } // end RatingPhase()

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return type;
    } // end toString()

    public static RatingPhase fromInteger(int x) {
        switch(x) {
            case 0:
                return WAIT_USERS;
            case 1:
                return STARTING;
            case 2:
                return KNOCKOUT_MATCH;
            case 3:
                return DONE;
        }
        return null;
    }
}
