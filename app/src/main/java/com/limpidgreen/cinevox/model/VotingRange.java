/**
 * VotingRange.java
 *
 * 13.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.model;

import com.google.gson.annotations.SerializedName;

/**
 * Event Voting Range enum.
 *
 * @author MajaDobnik
 *
 */
public enum VotingRange {
    @SerializedName("one_to_five")
    ONE_TO_FIVE("one_to_five"),

    @SerializedName("one_to_ten")
    ONE_TO_TEN("one_to_ten"),

    @SerializedName("up_down")
    UP_DOWN("up_down");

    private String type;

    /**
     * Constructor.
     *
     * @param type
     */
    private VotingRange(String type) {
        this.type = type;
    } // end VotingRange()

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return type;
    } // end toString()

    public static VotingRange fromInteger(int x) {
        switch(x) {
            case 0:
                return ONE_TO_FIVE;
            case 1:
                return ONE_TO_TEN;
            case 2:
                return UP_DOWN;
        }
        return null;
    }
}
