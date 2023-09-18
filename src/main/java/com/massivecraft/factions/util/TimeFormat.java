package com.massivecraft.factions.util;

public class TimeFormat {
    public static String timeFormattedNoSecondsExtended(int duration, boolean dayFirst){
        int minutes = (duration) % 60;
        int hours = ((duration) / 60) % 24;
        int days = ((duration) / 60) / 24;
        if(minutes == 0 && hours == 0 && days == 0)return "0m";

        String minuteAppend = minutes != 0 || hours != 0 || days != 0 ? minutes + " minutes " : "";

        String hourAppend = hours != 0 || days != 0 ? hours + " hours " : "";

        String dayAppend = days != 0 ? days + " days " : "";

        return dayFirst ? (dayAppend + hourAppend + minuteAppend).trim() : (minuteAppend + hourAppend + dayAppend).trim();
    }
}
