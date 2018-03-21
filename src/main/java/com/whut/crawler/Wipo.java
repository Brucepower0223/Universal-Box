package com.whut.crawler;

import com.common.utils.WebClientUtils;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.Cookie;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.net.URL;

/**
 * @author fangjin
 * @date 2018/3/21
 */
public class Wipo {

    public static WebClient webClient = null;

    static {

    }

    public static void main(String[] args) throws Exception {
        sendRequest();

    }


    public static String formatData(String start_num) {
        String params = "{'p':{'start':" + start_num + ",'type':'brand','la':'en','qi':'0-4M6Wmmq8Sre6albK3KF2yOhCm/48opQ+8be17/Jyi9c=','queue':1}";
        return params;


    }


    public static void sendRequest() throws Exception {
        String url = "http://www.wipo.int/branddb/jsp/select.jsp";

        webClient = WebClientUtils.getWebClient();
        webClient.getCookieManager().addCookie(new Cookie(".wipo.int", "JSESSIONID", "3BD905B5F808F5DBE5FA7B04A3739D96"));
        webClient.getCookieManager().addCookie(new Cookie(".wipo.int", "BSWA", "balancer.bswa1"));
        webClient.getCookieManager().addCookie(new Cookie(".wipo.int", "ABIW", "balancer.cms31"));

        webClient.addRequestHeader("Origin", "http://www.wipo.int");
        webClient.addRequestHeader("Accept-Encoding", "gzip, deflate");
        webClient.addRequestHeader("Accept-Language", "zh-CN,zh-TW;q=0.9,zh;q=0.8,en;q=0.7");
        webClient.addRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
        webClient.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        webClient.addRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        webClient.addRequestHeader("Referer", "http://www.wipo.int/branddb/en/");
        webClient.addRequestHeader("X-Requested-With", "XMLHttpRequest");
        webClient.addRequestHeader("Connection", "keep-alive");


        WebRequest webRequest = new WebRequest(new URL(url), HttpMethod.POST);
        String arguement = "qz=" + encryptionByJs(formatData("1"));
        webRequest.setRequestBody(arguement);
        Page page = WebClientUtils.getWebRequestPage(webRequest, webClient);
        System.out.println(page.getWebResponse().getContentAsString());


    }


    public static String encryptionByJs(String params) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        FileInputStream fileInputStream = null;
        Reader reader = null;
        try {
            File file = new File("e:/a.js");
            fileInputStream = new FileInputStream(file);
            reader = new InputStreamReader(fileInputStream);
            engine.eval(reader);
            Invocable inv = (Invocable) engine;
            String value = (String) inv.invokeFunction("encrypt", params);
            return value;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
