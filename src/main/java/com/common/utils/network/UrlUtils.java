package com.common.utils.network;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * url 操作工具类
 * Created by Bruce on 2017/9/15.
 */
public class UrlUtils {

    private static final Log logger = LogFactory.getLog(UrlUtils.class);

    /**
     * 组装url异常
     *
     * @param href
     * @return
     */
    public static String getUrl(String href) {
        try {
            String regEx = "^(https|http)?://.*";                         // 通过正则匹配
            Pattern p = Pattern.compile(regEx);
            Matcher matcher = p.matcher(href);
            boolean ismat = matcher.matches();
            if (!ismat) {                                               // 非法url，需要进行处理
                if (href != null && !href.contains("//")) {             // 判断是否有域名，不存在域名。需要添加http://域名
                    href = "http://" + href;
                } else if (href != null && !href.contains("http:")) {
                    href = "http:" + href;                              // 判断是否存在http:，不存在则需添加
                }
            }
        } catch (Exception e) {
            logger.error("组装url异常", e);
        }
        return href;
    }

}
