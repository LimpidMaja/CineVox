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
    FINISHED(6, "finished"),

    @SerializedName("declined")
    DECLINED(7, "declined"),

    @SerializedName("failed")
    FAILED(8, "failed"),

    @SerializedName("start_without_all")
    START_WITHOUT_ALL(9, "start_without_all"),

    @SerializedName("continue_without_all")
    CONTINUE_WITHOUT_ALL(10, "continue_without_all");

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
            case 7:
                return DECLINED;
            case 8:
                return FAILED;
            case 9:
                return START_WITHOUT_ALL;
            case 10:
                return CONTINUE_WITHOUT_ALL;
        }
        return null;
    }
}
