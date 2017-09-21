package com.common.utils;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 模拟浏览器工具类
 * Created by Bruce on 2017/9/15.
 */
public class WebClientUtils {

    private static final Log log = LogFactory.getLog(WebClientUtils.class);

    private static final int ERRORNUM = 2;

    public static WebClient getWebClient() {
        return getWebClient(false);
    }

    /**
     * 未加载JS，需要加载JS可以得到webclient对象后重新设置，通过配置文件判断是否需要使用代理
     *
     * @param proxyFlag client是否需要代理
     * @return
     */
    public static WebClient getWebClient(boolean proxyFlag) {
        WebClient webClient = new WebClient(
                BrowserVersion.FIREFOX_10);
        WebClientOptions option = webClient.getOptions();
        option.setJavaScriptEnabled(true);
        option.setCssEnabled(false);
        option.setRedirectEnabled(true);
        option.setActiveXNative(false);
        option.setTimeout(100000);          //如果设置JavaScriptEnabled为TRUE时需要设置超时时间，否则会出现内存溢出的情况，默认JavaScriptEnabled为TRUE
        option.setThrowExceptionOnScriptError(false);
        option.setUseInsecureSSL(true);
        webClient.setJavaScriptTimeout(15000);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getCache().setMaxSize(25);     //设置最大缓存数为0 默认为25--控制CPU使用率
        if (proxyFlag) {
            ProxyConfig proxyConfig = new ProxyConfig("123.190.46.20", 8080);
            option.setProxyConfig(proxyConfig);
        }
        return webClient;
    }

    /**
     * 使用webclient方式获取页面对象：返回结果可能为null
     *
     * @param url
     * @param wc
     * @return
     */
    public static HtmlPage getHtmlPage(String url, WebClient wc) {
        int errorRun = 0;
        HtmlPage htmlPage = null;
        try {
            while (true) {
                try {
                    htmlPage = wc.getPage(url);
                    break;
                } catch (Exception e) {
                    errorRun++;
                    System.out.println("====================: 第 " + errorRun + " 次，网络连接异常,访问URL:" + url);
                    if (errorRun == ERRORNUM) {
                        System.out.println("============: 网络连接异常,访问URL:" + url + "，已终止访问！");
                        break;
                    }
                    Thread.sleep(Math.round(Math.random() * 14 + 1) * 1000);    //随机休眠2~7秒
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return htmlPage;
    }


    /**
     * 访问页面，可重复次数
     *
     * @param request
     * @param client
     * @return
     * @throws Exception
     */
    public static Page getWebRequestPage(WebRequest request, WebClient client) throws Exception {
        Page page = null;
        int count = 1;
        while (page == null
                && count <= ERRORNUM) {
            try {
                page = client.getPage(request);
                Thread.sleep(2000);
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {

                }
            } finally {
                count++;
            }
        }
        return page;
    }


}
