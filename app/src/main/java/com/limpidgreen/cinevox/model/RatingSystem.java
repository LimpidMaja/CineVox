/**
 * RatingSystem.java
 *
 * 13.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.model;

import com.google.gson.annotations.SerializedName;

/**
 * Event Rating System enum.
 *
 * @author MajaDobnik
 *
 */
public enum RatingSystem {
    @SerializedName("voting")
    VOTING("voting"),

    @SerializedName("knockout")
    KNOCKOUT("knockout"),

    @SerializedName("tags")
    TAGS("tags"),

    @SerializedName("random")
    RANDOM("random");

    private String type;

    /**
     * Constructor.
     *
     * @param type
     */
    private RatingSystem(String type) {
        this.type = type;
    } // end RatingSystem()

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return type;
    } // end toString()

    public static RatingSystem fromInteger(int x) {
        switch(x) {
            case 0:
                return VOTING;
            case 1:
                return KNOCKOUT;
            case 2:
                return TAGS;
            case 3:
                return RANDOM;
        }
        return null;
    }
}
