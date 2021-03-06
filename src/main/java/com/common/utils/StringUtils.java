package com.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具类
 * Created by Bruce on 2017/9/15.
 */

public class StringUtils {
    private static final Log logger = LogFactory.getLog(StringUtils.class);

    public static void main(String[] args) {
        String cert = "421083199309100036";
        cert = cert.substring(cert.length() - 6, cert.length());
        System.out.println(cert);

    }

    /**
     * 判断字符串的编码
     *
     * @param str
     * @return
     */
    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s = encode;
                return s;
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s2 = encode;
                return s2;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s3 = encode;
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";
    }


    /**
     * 根据json对象获取字符串，不存在对应key返回空字符串，防止抛出异常
     *
     * @param json
     * @param key
     * @return
     */
    public static String getStringByJsonWhereHave(JSONObject json, String key) {
        try {
            if (json != null && json.containsKey(key)) {
                return json.getString(key);
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 清除&nbsp,并去掉空格
     *
     * @param tdElements
     * @param index
     * @return
     */
    public static String clearNBSP(Elements tdElements, int index) {
        try {
            return Jsoup.parse(tdElements.get(index).toString().replace("&nbsp;", "")).text().trim();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 验证是否为空
     *
     * @param str
     * @return
     */
    public static boolean isBlankOrNull(String str) {
        if (null == str) {
            return true;
        }
        return str.length() == 0;
    }

    /**
     * 判断字符串是否纯数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 替换字符串对象中的空格&nbsp，回车\n br,tab键
     *
     * @param content
     * @return
     */
    public static String replaceKeyTab(String content) {
        content = content.replaceAll("<br/>", "");
        content = content.replaceAll("<br>", "");
        content = content.replaceAll("\\n", "");
        content = content.replaceAll("\n", "");
        content = content.replaceAll("\\r", "");
        content = content.replaceAll("\r", "");
        content = content.replaceAll("\\t", "");
        content = content.replaceAll("\\u000a", "");
        content = content.replaceAll("&nbsp;", "");
        content = content.replaceAll("&nbsp", "");
        content = content.replaceAll(" ", "");       //去掉&nbsp引起的空格
        return content;
    }

    /**
     * 将list转换为如下形式 &key1=value1&key1=value2
     *
     * @param lst
     * @param key
     * @return
     */
    public static String lstToValueStr(List<Object> lst, String key) {
        String str = "";
        if (lst == null) {
            return null;
        }
        for (int i = 0; i < lst.size(); i++) {
            str = str + "&" + key + "=" + (String) lst.get(i);
        }
        return str;
    }

    /**
     * 反转字符串
     *
     * @param str
     * @return
     */
    public static String covertStr(String str) {
        StringBuffer sb = new StringBuffer(str);
        StringBuffer sb1 = new StringBuffer();
        int len = sb.length();
        for (int i = len - 1; i >= 0; i--) {
            sb1.append(sb.charAt(i));
        }
        return sb1.toString();
    }

    /**
     * 获取字符在源串中的出现次数
     *
     * @param src
     * @param c
     * @return
     */
    public static int getRepeatCount(String src, char c) {
        int count = 0;
        for (int i = 0; i < src.length(); i++) {
            if (src.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    /**
     * 是否包含特殊字符
     *
     * @param str
     * @return
     */
    public static boolean isContainSpecialCharacter(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(regEx);
        return matcher.find();
    }

    /**
     * 通過字符串轉換成KB流量
     *
     * @param str
     * @return
     */
    public static Float getKBByStringGMK(String str) {
        String strNumber = "";//截取到的数字字符串
        String strOther = str;//存放剩下的字符串,初始值
        Float count = 0.00f;
        try {
            if ("".equals(str) || null == str || (!str.contains("K") && !str.contains("M") && !str.contains("G"))) {
                try {
                    return Float.parseFloat(str);
                } catch (NumberFormatException e) {
                    logger.info("转换异常了，返回0.00f" + str);
                    return count;
                }
            }

            strOther = strOther.replaceAll("B", "").replaceAll("\\(", "").replaceAll("\\)", "");
            if (strOther.indexOf("G") > -1) {
                strNumber = str.substring(0, strOther.indexOf("G"));
                strOther = str.substring(strOther.indexOf("G") + 1, strOther.length());
                count = Float.parseFloat(strNumber) * 1024 * 1024; // 1 小时等于3600秒
            }
            if (strOther.indexOf("M") > -1) {
                strNumber = strOther.substring(0, strOther.indexOf("M"));
                strOther = strOther.substring(strOther.indexOf("M") + 1, strOther.length());
                count += Float.parseFloat(strNumber) * 1024;
            }
            if (strOther.indexOf("K") > -1) {
                strNumber = strOther.replaceAll("K", "");
                count += Float.parseFloat(strNumber);
            }
        } catch (NumberFormatException e) {
            logger.error("流量转换失败", e);
        }
        return count;
    }

    /**
     * 字符串转换为流
     *
     * @param input
     * @return
     */
    public static InputStream toInputStream(String input) {
        byte[] bytes = input.getBytes();
        return new ByteArrayInputStream(bytes);
    }

}
