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
    @SerializedName("starting")
    STARTING(0, "starting"),

    @SerializedName("knockout_match")
    KNOCKOUT_MATCH(1, "knockout_match");

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
                return STARTING;
            case 1:
                return KNOCKOUT_MATCH;
        }
        return null;
    }
}
