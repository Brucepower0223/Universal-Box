package com.whut.test;

import com.alibaba.fastjson.JSONObject;
import com.common.utils.WebClientUtils;
import com.common.utils.file.FileUtils;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Bruce
 * @date 2017/12/25
 */
public class DomainCrawler {


    private static int CRAWL_DEEP = 3;
    private static BlockingQueue<String> task = new LinkedBlockingQueue<String>();
    private WebClient client ;
//    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/root/data/ret.log"), "UTF-8"));

    static Map<String, String> map = new HashMap<String, String>();

    public DomainCrawler() {
       client = WebClientUtils.getWebClient();
        client.getOptions().setJavaScriptEnabled(false);
    }

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("G:/website.txt"), "UTF-8"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            try {
                task.add("http://" + line);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("获取" + line + "失败");
            }
        }
        reader.close();

        System.out.println("-----------------" + task.size());

        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DomainCrawler crawler = null;
                    crawler = new DomainCrawler();
                    crawler.crawlUrl();
                }
            }).start();
        }

        new Thread(new Monitor()).start();


    }


    private void crawlUrl() {
        start:
        while (task.size() > 0) {
            int num = 0;            String initUrl = task.poll();
            try {
                List<String> first = getHyperLinks(initUrl, initUrl);
                out:
                for (String url : first) {
                    System.out.println("第一层" + url);
                    if (!isDomain(url)) {
                        List<String> second = getHyperLinks(url, initUrl);
                        num += 2;
                        if (num > 2000) {
                            break start;
                        }
                        for (String url2 : second) {
                            System.out.println("第二层" + url2);
                            if (!isDomain(url2)) {
                                List<String> third = getHyperLinks(url2, initUrl);
                                num += 2;
                                if (num > 2000) {
                                    break start;
                                }
                                for (String url3 : third) {
                                    System.out.println("第三层" + url3);
                                    if (isDomain(url3)) {
                                        System.out.println("抓到域名" + url3);
                                        map.put(initUrl, url3);
                                        break out;
                                    } else {
                                        List<String> four = getHyperLinks(url3, initUrl);
                                        for (String url4 : four) {
                                            System.out.println("第四层" + url4);
                                            if (isDomain(url4)) {
                                                num += 2;
                                                if (num > 2000) {
                                                    break start;
                                                }
                                                System.out.println("抓到域名" + url4);
                                                map.put(initUrl, url4);
                                                break out;
                                            }
                                        }
                                    }
                                }
                            } else {
                                System.out.println("抓到域名" + url2);
                                map.put(initUrl, url2);
                                break out;
                            }

                        }
                    } else {
                        System.out.println("抓到域名" + url);
                        map.put(initUrl, url);
                        break out;
                    }
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }


    private List<String> getHyperLinks(String url, String initUrl) {
        List<String> hrefs = new ArrayList<String>();
        client.getOptions().setTimeout(10000);
        client.getOptions().setJavaScriptEnabled(false);
        Page page = null;
        try {
            page = client.getPage(url);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        if (page != null) {
            String context = page.getWebResponse().getContentAsString();
            Document document = Jsoup.parse(context);
            Elements as = document.select("a");
            int startNum = as.size() / 2;
            for (int i = startNum; i < as.size(); i++) {
                String href = as.get(i).attr("href");
                if (href.contains("javascript")) {
                    continue;
                }
                if (href.startsWith("http")) {
                    hrefs.add(href);
                } else {
                    if (href.startsWith("//")) {
                        hrefs.add("http:" + href);
                    } else if (href.startsWith("/")) {
                        hrefs.add(initUrl + href);
                    } else {
                        hrefs.add("http://" + href);
                    }

                }

            }
        }
        return hrefs;
    }


    private boolean isDomain(String url) {
        Page page = null;
        try {
            client.getOptions().setJavaScriptEnabled(false);
            client.getOptions().setTimeout(10000);
            page = client.getPage(url);
        } catch (Exception e) {
            System.out.println("判断失败");
        }
        if (page != null) {
            String context = page.getWebResponse().getContentAsString();
            if ((context.contains("加入购物车") || context.contains("放入购物袋")) && (url.contains("item") || url.contains("detail") || url.contains("product") || url.contains("product-detail") || url.contains("products") || url.contains("chanping"))) {
                return true;
            }
        }
        return false;
    }
    static class  Monitor implements Runnable{

        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(60000);
                    FileUtils.saveToFile("e://sbxq.txt", JSONObject.toJSONString(map,true));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}





