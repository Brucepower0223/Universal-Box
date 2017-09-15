package com.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.regex.Pattern;

/**
 * Created by admin on 2017/9/15.
 */
public class IntegerUtils {

    private static final Log logger = LogFactory.getLog(IntegerUtils.class);


    /**
     * 15 或 18位身份证验证
     * @param param
     * @return
     */
    public static boolean isIDCard(String param) {
        Pattern p15 = Pattern.compile("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$");
        Pattern p18 = Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$");
        if (p18.matcher(param).matches() || p15.matcher(param).matches()) {
            return true;
        }
        return false;
    }



}
