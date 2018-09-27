package top.kongk.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;

/**
 * 描述：时间转换工具类
 *
 * @author kk
 * @date 2018/9/26 17:08
 */
public class DateTimeUtil {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String dateToString(Date date, String format) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }

    public static String dateToString(Date date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }

    public static Date strToDate(String strDate, String format) {
        //根据format创建模板
        org.joda.time.format.DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
        //用模板生成dateTime
        DateTime dateTime = dateTimeFormatter.parseDateTime(strDate);
        //dateTime 生成 date
        return dateTime.toDate();
    }

    public static Date strToDate(String strDate) {
        //根据format创建模板
        org.joda.time.format.DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        //用模板生成dateTime
        DateTime dateTime = dateTimeFormatter.parseDateTime(strDate);
        //dateTime 生成 date
        return dateTime.toDate();
    }


    public static void main(String[] args) {
        System.out.println(new Date());
        System.out.println(dateToString(new Date()));

        String time = "2020-09-26 17:22:43";
        System.out.println(strToDate(time));

    }
}
