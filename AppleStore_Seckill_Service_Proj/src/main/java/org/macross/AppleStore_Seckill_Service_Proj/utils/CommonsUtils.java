package org.macross.AppleStore_Seckill_Service_Proj.utils;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CommonsUtils {

    /**
     * MD5加密工具类
     * @param data
     * @return
     */
    public static String MD5(String data)  {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }

            return sb.toString().toUpperCase();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;

    }

    /**
     * 获取指定时区的当前时间字符串
     * @param timezone +8:00
     * @return
     */
    public static Date getDateString(String timezone){
        //设置时区，如：+8:00 或者取时区id(GMT)
        TimeZone time = TimeZone.getTimeZone(timezone);
        //格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 设置时区
        TimeZone.setDefault(time);
        // 获取实例
        Calendar calendar = Calendar.getInstance();
        //获取Date对象
        Date date = calendar.getTime();
        return date;
    }

}
