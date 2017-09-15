package com.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * 其他工具类
 * Created by Bruce on 2017/9/15.
 */
public class OtherUtils {

    private static final Log logger = LogFactory.getLog(OtherUtils.class);


    /**
     * 获取一个i位的随机数字  i为正整数
     *
     * @param i
     * @return
     */
    public static String getRandom(int i) {
        Random jjj = new Random();

        if (i == 0)
            return "";
        String jj = "";
        for (int k = 0; k < i; k++) {
            jj = jj + jjj.nextInt(9);
        }
        return jj;
    }

    /**
     * MD5加密算法
     *
     * @param str
     * @return
     */
    public static String md5DigestByString(String str) {
        StringBuffer hexValue = new StringBuffer();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            char[] chr = str.toCharArray();
            byte[] byt = new byte[chr.length];
            for (int i = 0; i < chr.length; i++) {
                byt[i] = ((byte) chr[i]);
            }
            byte[] digest = md5.digest(byt);
            for (int i = 0; i < digest.length; i++) {
                int val = digest[i] & 0xFF;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            System.out.println(Thread.currentThread().getName() + ":密码加密完成     " + hexValue.toString());
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return hexValue.toString().toUpperCase();
    }
}
