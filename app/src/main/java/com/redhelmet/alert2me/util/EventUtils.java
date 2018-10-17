package com.redhelmet.alert2me.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.redhelmet.alert2me.data.model.Area;
import com.redhelmet.alert2me.data.model.Event;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class EventUtils {

    private EventUtils() {
    }

    public static MarkerOptions eventToMarker(Event event, Area area) {
        return new MarkerOptions().position(new LatLng(area.getLatitude(), area.getLongitude())).title(event.getType()).zIndex(event.getSeverity());
    }

    public static String getTimeAgo(Date eventDate) {

        Calendar calendar = Calendar.getInstance();
        Calendar current = Calendar.getInstance();

        calendar.setTime(eventDate);

        long diffInMilliSec = current.getTimeInMillis() - eventDate.getTime();
        long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMilliSec);
        if (diffSeconds < 60) {
            return "now";
        }
        long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliSec);
        if (diffMinutes < 60) {
            if (diffMinutes == 1) {
                return String.format("%s min ago", diffMinutes);
            }
            return String.format("%s mins ago", diffMinutes);
        }
        long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMilliSec);
        if (diffInHours < 24) {
            if (diffInHours == 1) {
                return String.format("%s hr ago", diffInHours);
            }
            return String.format("%s hrs ago", diffInHours);
        }
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMilliSec);
        if (diffInDays < 30) {
            return String.format("%sd ago", diffInDays);
        }
        long diffInMonths = Math.round(diffInDays / 30);
        SimpleDateFormat date = new SimpleDateFormat("MMM");
        String monthName = date.format(calendar.getTime());
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String day = decimalFormat.format(calendar.get(Calendar.DAY_OF_MONTH));
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        if (diffInMonths < 12) {
            return String.format("%s %s", day, monthName);
        }
        return String.format("%s %s %s", day, monthName, year);
    }

    public static String getDetailTimeAgo(Date eventDate) {

        Calendar calendar = Calendar.getInstance();
        Calendar current = Calendar.getInstance();

        calendar.setTime(eventDate);


        SimpleDateFormat simpleHourFormat = new SimpleDateFormat("h:mm a");
        simpleHourFormat.setTimeZone(calendar.getTimeZone());
        String localStringHour = simpleHourFormat.format(eventDate);


        long diffInMilliSec = current.getTime().getTime() - eventDate.getTime();
        long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMilliSec);
        if (diffSeconds < 60) {
            return String.format("Now @ %s", localStringHour);
        }
        long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliSec);
        if (diffMinutes < 60) {
            if (diffMinutes == 1) {
                return String.format("%s minute ago @ %s", diffMinutes, localStringHour);
            }
            return String.format("%s minutes ago @ %s", diffMinutes, localStringHour);
        }
        long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMilliSec);
        if (diffInHours < 24) {
            if (diffInHours == 1) {
                return String.format("%s hour ago @ %s", diffInHours, localStringHour);
            }
            return String.format("%s hours ago @ %s", diffInHours, localStringHour);
        }
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMilliSec);
        if (diffInDays < 30) {
            if (diffInDays == 1) {
                return String.format("%s day ago @ %s", diffInDays, localStringHour);
            }
            return String.format("%s days ago @ %s", diffInDays, localStringHour);
        }
        long diffInMonths = Math.round(diffInDays / 30);
        SimpleDateFormat date = new SimpleDateFormat("MMMM");
        String monthName = date.format(calendar.getTime());
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String day = decimalFormat.format(calendar.get(Calendar.DAY_OF_MONTH));
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        if (diffInMonths < 12) {
            return String.format("%s %s @ %s", day, monthName, localStringHour);
        }
        return String.format("%s %s %s @ %s", day, monthName, year, localStringHour);
    }
}


