package au.edu.federation.capstoneprototype;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static Date string_date_full(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);

            return myDate;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return myDate;

    }

    public static String string_time(String date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        String myTime = null;
        try {
            Date myDate = timeFormat.parse(date);
            return formatter.format(myDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return myTime;
    }


    public static boolean compareTwoDates(Date startDate, Date endDate) {
        Date sDate = getZeroTimeDate(startDate);
        Date eDate = getZeroTimeDate(endDate);
        if (sDate.before(eDate)) {
            return false;
        }
        if (sDate.after(eDate)) {
            return false;
        }
        return true;
    }

    private static Date getZeroTimeDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();
        return date;
    }
}
