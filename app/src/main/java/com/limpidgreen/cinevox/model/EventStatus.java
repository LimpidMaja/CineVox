/**
 * EventStatus.java
 *
 * 28.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.model;

import com.google.gson.annotations.SerializedName;

/**
 * Event Event Status enum.
 *
 * @author MajaDobnik
 *
 */
public enum EventStatus {
    @SerializedName("waiting_others")
    WAITING_OTHERS(0, "waiting_others"),

    @SerializedName("confirm")
    CONFIRM(1, "confirm"),

    @SerializedName("add_movies")
    ADD_MOVIES(2, "add_movies"),

    @SerializedName("vote")
    VOTE(3, "vote"),

    @SerializedName("knockout_choose")
    KNOCKOUT_CHOOSE(4, "knockout_choose"),

    @SerializedName("winner")
    WINNER(5, "winner"),

    @SerializedName("finished")
    FINISHED(6, "finished");

    private String type;
    private Integer id;

    /**
     * Constructor.
     *
     * @param type
     */
    private EventStatus(Integer id, String type) {
        this.id = id;
        this.type = type;
    } // end EventStatus()

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return type;
    } // end toString()

    public static EventStatus fromInteger(int x) {
        switch(x) {
            case 0:
                return WAITING_OTHERS;
            case 1:
                return CONFIRM;
            case 2:
                return ADD_MOVIES;
            case 3:
                return VOTE;
            case 4:
                return KNOCKOUT_CHOOSE;
            case 5:
                return WINNER;
            case 6:
                return FINISHED;
        }
        return null;
    }
}
