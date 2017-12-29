package com.whut.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.utils.StringUtils;
import com.common.utils.WebClientUtils;
import com.common.utils.file.FileUtils;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruce
 * @date 2017/12/21
 */
public class KuaidiCrawler {

    public static String[] companies = {""};

    public static void main(String[] args) throws Exception {

        WebClient webClient = WebClientUtils.getWebClient();
        String url = "https://www.kuaidi100.com/network/www/searchapi.do";
        WebRequest webRequest = new WebRequest(new URL(url), HttpMethod.POST);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new NameValuePair("method", "hintlist"));
        nvps.add(new NameValuePair("area", "111400"));
        nvps.add(new NameValuePair("text", "输入街道名、小区名或大厦名"));
        webRequest.setRequestParameters(nvps);
        Page page = WebClientUtils.getWebRequestPage(webRequest, webClient);
        List<QueryEntity> arguments = getArgsFromJsonStr(convertTextToUTF8(page.getWebResponse().getContentAsString()));

        for (QueryEntity query : arguments) {
            webRequest = new WebRequest(new URL(url), HttpMethod.POST);
            nvps = new ArrayList<NameValuePair>();
            nvps.add(new NameValuePair("method", "searchnetwork"));
            nvps.add(new NameValuePair("area", query.getFullName()));
            nvps.add(new NameValuePair("company", "zhongtong"));
            nvps.add(new NameValuePair("keyword", query.getText()));
            nvps.add(new NameValuePair("offset", "0"));
            nvps.add(new NameValuePair("size", "8"));
            nvps.add(new NameValuePair("from", "null"));
            nvps.add(new NameValuePair("channel", "2"));
            nvps.add(new NameValuePair("auditStatus", "0"));
            webRequest.setRequestParameters(nvps);
            page = WebClientUtils.getWebRequestPage(webRequest, webClient);
            FileUtils.saveToFile("g:/kuaidi/" + query.getText() + ".html", convertTextToUTF8(page.getWebResponse().getContentAsString()));
        }
    }


    private static List<QueryEntity> getArgsFromJsonStr(String jsonStr) {
        if (StringUtils.isBlankOrNull(jsonStr)) {
            return null;
        }
        JSONObject object = JSONObject.parseObject(jsonStr);
        if (object.get("status").toString().equals("200")) {
            JSONArray keywordList = object.getJSONArray("keywordList");
            if (keywordList.size() > 0) {
                List<QueryEntity> queryEntities = JSON.parseArray(keywordList.toString(), QueryEntity.class);
                return queryEntities;
            } else {
                return null;
            }
        }

        return null;
    }


    private static String convertTextToUTF8(String src) throws Exception {
        if (StringUtils.isBlankOrNull(src)) {
            throw new IllegalArgumentException("error string args");
        }
        return new String(src.getBytes("8859_1"), "UTF-8");
    }
}


class QueryEntity {

    private String areaCode;
    private String fullName;
    private String text;
    private String alias;
    private String level;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
