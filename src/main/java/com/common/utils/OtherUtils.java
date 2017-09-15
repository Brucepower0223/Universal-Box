package com.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

/**
 * 其他工具类
 * Created by Bruce on 2017/9/15.
 */
public class OtherUtils {

    private static final Log logger = LogFactory.getLog(OtherUtils.class);


    /**
     * 获取一个i位的随机数字  i为正整数
     * @param i
     * @return
     */
    public static String getRandom(int i)
    {
        Random jjj = new Random();

        if (i == 0)
            return "";
        String jj = "";
        for (int k = 0; k < i; k++)
        {
            jj = jj + jjj.nextInt(9);
        }
        return jj;
    }
}
