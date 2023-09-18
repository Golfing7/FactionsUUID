package com.massivecraft.factions.util;

import java.io.Serializable;

public class StrikeInfo implements Serializable {

    private long givenAt;

    private String strikeReason;

    public StrikeInfo(long givenAt, String strikeReason){
        this.givenAt = givenAt;
        this.strikeReason = strikeReason;
    }

    public long getGivenAt() {
        return givenAt;
    }

    public String getStrikeReason() {
        return strikeReason;
    }
}
