package com.redhelmet.alert2me.domain.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.redhelmet.alert2me.model.Area;
import com.redhelmet.alert2me.model.Event;


public class EventUtils {

    public MarkerOptions eventToMarker(Event event, Area area) {

        return new MarkerOptions().position(new LatLng(area.getLatitude(), area.getLongitude())).title(event.getType()).zIndex(event.getSeverity());
    }


    public String getTimeAgo(Date eventDate) throws ParseException {

        Calendar calendar = Calendar.getInstance();
        Calendar current = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        //simpleDateFormat.setTimeZone(calendar.getTimeZone());
        String localStringDate = simpleDateFormat.format(eventDate);
        Date localDate = simpleDateFormat.parse(localStringDate);
        calendar.setTime(localDate);

        long diffInMilliSec = current.getTime().getTime() - localDate.getTime();
        long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMilliSec);
        if (diffSeconds < 60) {
            return "now";
        }
        long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliSec);
        if (diffMinutes < 60) {
            if (diffMinutes == 1) {
                return String.format("%s min", diffMinutes);
            }
            return String.format("%s mins", diffMinutes);
        }
        long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMilliSec);
        if (diffInHours < 24) {
            if (diffInHours == 1) {
                return String.format("%s hr", diffInHours);
            }
            return String.format("%s hrs", diffInHours);
        }
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMilliSec);
        if (diffInDays < 30) {
            if (diffInDays == 1) {
                return String.format("%s day", diffInDays);
            }
            return String.format("%s days", diffInDays);
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

    public String getDetailTimeAgo(Date eventDate) throws ParseException {

        Calendar calendar = Calendar.getInstance();
        Calendar current = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        //simpleDateFormat.setTimeZone(calendar.getTimeZone());
        String localStringDate = simpleDateFormat.format(eventDate);
        Date localDate = simpleDateFormat.parse(localStringDate);
        calendar.setTime(localDate);


        SimpleDateFormat simpleHourFormat = new SimpleDateFormat("h:mm a");
        simpleHourFormat.setTimeZone(calendar.getTimeZone());
        String localStringHour = simpleHourFormat.format(eventDate);


        long diffInMilliSec = current.getTime().getTime() - localDate.getTime();
        long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMilliSec);
        if (diffSeconds < 60) {
            return String.format("Updated now at %s", localStringHour);
        }
        long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliSec);
        if (diffMinutes < 60) {
            if (diffMinutes == 1) {
                return String.format("Updated %s minute ago at %s", diffMinutes, localStringHour);
            }
            return String.format("Updated %s minutes ago at %s", diffMinutes, localStringHour);
        }
        long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMilliSec);
        if (diffInHours < 24) {
            if (diffInHours == 1) {
                return String.format("Updated %s hour ago at %s", diffInHours, localStringHour);
            }
            return String.format("Updated %s hours ago at %s", diffInHours, localStringHour);
        }
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMilliSec);
        if (diffInDays < 30) {
            if (diffInDays == 1) {
                return String.format("Updated %s day ago at %s", diffInDays, localStringHour);
            }
            return String.format("Updated %s days ago at %s", diffInDays, localStringHour);
        }
        long diffInMonths = Math.round(diffInDays / 30);
        SimpleDateFormat date = new SimpleDateFormat("MMMM");
        String monthName = date.format(calendar.getTime());
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String day = decimalFormat.format(calendar.get(Calendar.DAY_OF_MONTH));
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        if (diffInMonths < 12) {
            return String.format("Updated %s %s at %s", day, monthName, localStringHour);
        }
        return String.format("Updated %s %s %s at %s", day, monthName, year, localStringHour);
    }

    public static String readJsonFile(File yourFile) {
        BufferedReader streamReader = null;

        if(yourFile.exists()) {
            try {
                streamReader = new BufferedReader(new InputStreamReader(new FileInputStream(yourFile), "UTF-8"));

                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                return responseStrBuilder.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "";
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }else{
            return "";
        }
    }


    // colors CODING

    public static String getPrimaryColor(){

        return "";
    }
}


