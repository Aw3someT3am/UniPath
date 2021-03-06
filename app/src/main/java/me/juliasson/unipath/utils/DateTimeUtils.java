package me.juliasson.unipath.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    public static String parseInputFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
    public static String parseOutputFormat = "MMMM d, yyyy";

    public static String parseDateTime(String dateString, String originalFormat, String outputFormat){

        SimpleDateFormat formatter = new SimpleDateFormat(originalFormat, Locale.US);
        Date date = null;
        try {
            date = formatter.parse(dateString);

            SimpleDateFormat dateFormat=new SimpleDateFormat(outputFormat, new Locale("US"));

            return dateFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
