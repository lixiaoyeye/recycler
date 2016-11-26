package health.rubbish.recycler.util;

import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lenovo on 2016/11/20.
 */

public class DateUtil {
    public static final DateFormat format_date = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat format_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat format_nyrsf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final DateFormat format_ny = new SimpleDateFormat("yyyy-MM");
    public static final DateFormat format_sfm = new SimpleDateFormat("HH:mm:ss");
    public static final DateFormat format_sf = new SimpleDateFormat("HH:mm");

    public static String getTimeString()
    {
        return getDateString(format_time);
    }

    public static String getDateString()
    {
        return getDateString(format_date);
    }


    public static String getTimeString(Date date)
    {
        return getDateString(date,format_time);
    }

    public static String getDateString(Date date)
    {
        return getDateString(date,format_date);
    }

    /**
     * @return yyyy-MM-dd
     */
    public static String getDateString(DateFormat format) {
        return getDateString(new Date(),format) ;
    }
    /**
     * @return yyyy-MM
     */
    public static String getDateString(Date date,DateFormat format) {
        String datestr ="";
        try {
            datestr = format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datestr;
    }

    public static boolean equals(String datestr1,String datestr2) {
        return getDateString(datestr1).equals(getDateString(datestr2));
    }

    public static boolean equals(Date datestr1,Date datestr2) {
        return getDateString(datestr1).equals(getDateString(datestr2));
    }

    //2016-05-16
    public static Date parseDate(String datestr) {
        Date date = null;

        try {
            if (datestr.length() == 10) {//2016-05-16
                date = format_date.parse(datestr);
            } else if (datestr.length() == 16)//2016-05-16 12:20
            {
                date = format_nyrsf.parse(datestr);

            } else if (datestr.length() == 19)//2016-05-16 12:20:20
            {
                date = format_time.parse(datestr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    //2016-05-16
    public static String parseWeek(String datestr) {
        Date date = parseDate(datestr);
        if (date == null)
            return "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return getWeekString(calendar.get(Calendar.DAY_OF_WEEK));
    }

    //获取每年的最后一天
    public static String getLastDHMOfYear(String datestr) {
        String result = getDateSubstring(datestr, 4);
        if (!TextUtils.isEmpty(result)) {
            return result + "-12-31";
        }
        return "";
    }

    public static String getWeekString(int week) {
        String w = "星期";
        switch (week) {
            case 1:
                w = w + "日";
                break;
            case 2:
                w = w + "一";
                break;
            case 3:
                w = w + "二";
                break;
            case 4:
                w = w + "三";
                break;
            case 5:
                w = w + "四";
                break;
            case 6:
                w = w + "五";
                break;
            case 7:
                w = w + "六";
                break;
            default:
                w = "";
                break;
        }

        return w;
    }

    //2016-05-16
    public static String getDateString(String olddate) {
        return getDateSubstring(olddate, 10);
    }

    //2016-05-16 12:02:01
    public static String getTimeString(String olddate) {
        return getDateSubstring(olddate, 19);
    }

    public static String getDateSubstring(String olddate, int length) {
        String result = "";
        if (!TextUtils.isEmpty(olddate) && olddate.length() > length) {
            result = olddate.substring(0, length);
        } else {
            result = olddate;
        }
        return result;
    }

    public static String getDateString(String olddate, DateFormat format) {
        if (format.equals(format_date))
        {
            return getDateSubstring(olddate, 10);
        }
        else if (format.equals(format_date))
        {
            return getDateSubstring(olddate, 16);
        }
        else if (format.equals(format_time))
        {
            return getDateSubstring(olddate, 19);
        }
        return olddate;
    }


    //2016-05-16 12:02
    public static String getMinute(String olddate) {
        String result = "";
        //2016-02-01
        if (!TextUtils.isEmpty(olddate) && olddate.length() > 16) {
            result = olddate.substring(11, 16);
        } else {
            result = olddate;
        }
        return result;
    }


    public static Calendar getFirstDayOfWeek(Calendar c) {
        Calendar calendar = (Calendar) c.clone();
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        if (day_of_week == 0) day_of_week = 7;
        calendar.add(Calendar.DATE, 1 - day_of_week);
        return calendar;
    }

    public static Calendar getLastDayOfWeek(Calendar c) {
        Calendar calendar = (Calendar) c.clone();
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        if (day_of_week == 0) day_of_week = 7;
        calendar.add(Calendar.DATE, 7 - day_of_week);
        return calendar;
    }

    public static Calendar getFirstDayOfMonth(Calendar c) {
        Calendar calendar = (Calendar) c.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar;
    }

    public static Calendar getLastDayOfMonth(Calendar c) {
        Calendar calendar = (Calendar) c.clone();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar;
    }
}
