package com.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间日期操作工具类
 * Created by Bruce on 2017/9/15.
 */
public class DateUtils {

    private static final Log logger = LogFactory.getLog(DateUtils.class);

    /**
     * 获取当前日期的字符串格式
     * @param format:返回时间的格式
     * @return
     */
    public static String getCurrentDateString(String format) {
        SimpleDateFormat df1 = new SimpleDateFormat(format);
        Date date = new Date();
        return df1.format(date);
    }

    /**
     * 获取小时
     * @return
     */
    public static int getCurrentHour() {
        SimpleDateFormat df = new SimpleDateFormat("HH");

        String str = df.format(new Date());
        if (str.startsWith("0"))
            str = str.substring(1, str.length());

        return Integer.parseInt(str);
    }

    /**
     * 设置ETC/GMT-8时区
     */
    private static void setTimeZone() {
        TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");
        TimeZone.setDefault(tz);
    }

    /**
     * 得到二个日期间的间隔天数 sj1>sj2 返回正数
     * @param sj1
     * @param sj2
     * @param format 传入的时间格式
     * @return
     */
    public static String getTwoDay(String sj1, String sj2, String format) {
        setTimeZone();
        SimpleDateFormat myFormatter = new SimpleDateFormat(format);
        long day = 0;
        try {
            java.util.Date date = myFormatter.parse(sj1);
            java.util.Date mydate = myFormatter.parse(sj2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }


    /**
     * 比较两个日期传入的大小
     * @param d1
     * @param d2
     * @return
     */
    public static int compareDate(Date d1,Date d2){
        if (d1.getTime() > d2.getTime()) {
            return 1;
        } else if (d1.getTime() < d2.getTime()) {
            return -1;
        } else {//相等
            return 0;
        }
    }
    /**
     * @param startTime
     * @param endTime
     * @param type      1:计算差多少天  2:计算差多少天 3:计算差多少分钟 4:计算差多少秒
     * @return 获取两个日期之间的差
     */
    public static int dateDiff(Date startTime, Date endTime, int type) {
        try {
            long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
            long nh = 1000 * 60 * 60;//一小时的毫秒数
            long nm = 1000 * 60;//一分钟的毫秒数
            long ns = 1000;//一秒钟的毫秒数
            long diff = endTime.getTime() - startTime.getTime();
            if (type == 1) {
                long day = diff / nd;
                return Integer.parseInt(day + "");
            } else if (type == 2) {
                long hour = diff % nd / nh;
                return Integer.parseInt(hour + "");
            } else if (type == 3) {
                long min = diff % nd % nh / nm;
                return Integer.parseInt(min + "");
            } else {
                long sec = diff % nd % nh % nm / ns;
                return Integer.parseInt(sec + "");
            }
        } catch (Exception e) {
            return 0;
        }
    }


}
