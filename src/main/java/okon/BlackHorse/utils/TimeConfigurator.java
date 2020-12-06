package okon.BlackHorse.utils;

import java.time.Duration;

public class TimeConfigurator {
    public static String toProperUnitOfMeasure(Duration time) {
        String result = "";
        long days = time.toDays();
        long hours = time.minusDays(time.toDays()).toHours();
        long minutes = time.minusHours(time.toHours()).toMinutes();
        long seconds = time.minusMinutes(time.toMinutes()).getSeconds();
        long milliseconds = time.minusSeconds(time.getSeconds()).toMillis();
        if (days != 0L) {
            if (days == 1L) {
                result += days + " day";
            } else {
                result += days + " days";
            }
        } else if (hours != 0) {
            if (hours == 1L) {
                result += hours + " hour";
            } else {
                result += hours + " hours";
            }
        } else if (minutes != 0L) {
            if (minutes == 1L) {
                result += minutes + " minute";
            } else {
                result += minutes + " minutes";
            }
        } else if (seconds != 0L) {
            if (seconds == 1L) {
                result += seconds + " second";
            } else {
                result += seconds + " seconds";
            }
        } else if (milliseconds != 0L) {
            if (milliseconds == 1L) {
                result += milliseconds + " millisecond";
            } else {
                result += milliseconds + " milliseconds";
            }
        }
        return result;
    }
}