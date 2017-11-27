package com.whut.crawler;

import com.common.utils.StringUtils;
import com.common.utils.WebClientUtils;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.whut.entiry.Ganji;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 抓取赶集网信息
 *
 * @author Bruce
 * @date 2017/11/27
 */
public class GanJiCrawler {

    public static WebClient webClient = null;

    static {
        webClient = WebClientUtils.getWebClient();
    }


    /**
     * 任务队列
     */
    public static BlockingQueue<String> taskQuene = new LinkedBlockingQueue<String>();
    public static ExecutorService threadPool = Executors.newFixedThreadPool(10);
    //缓存
    public static List<Ganji> ganjis = new ArrayList<Ganji>();
    public static Object mutex = new Object();

    public void crawlDataIndex() throws Exception {
        WebRequest webRequest = null;
        for (String city : GanjiCrawlerUtils.CITY_LIST) {
            String startUrl = "http://" + city + ".ganji.com/danbaobaoxian/";
            System.out.println("startUrl = " + startUrl);
            webRequest = new WebRequest(new URL(startUrl), HttpMethod.GET);
            webClient.addRequestHeader("User-Agent", GanjiCrawlerUtils.getRandomAgents());
            Page page = WebClientUtils.getWebRequestPage(webRequest, webClient);
            if (page != null) {
                Document document = Jsoup.parse(page.getWebResponse().getContentAsString());
                Elements urlEles = document.select("a[class=f14 list-info-title js_wuba_stas]");
                for (Element urlEle : urlEles) {
                    System.out.println("url  = " + urlEle.attr("href"));
                    taskQuene.add(urlEle.attr("href"));
                }
            }
        }
    }


    /**
     * 从任务队列获取任务
     *
     * @return
     */
    public String getTaskFromQuene() {
        try {
            return taskQuene.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 爬取某一个具体公司得页面
     *
     * @throws Exception
     */


    public void crawlCompany() throws Exception {
        Thread.sleep(2000);
        WebClient webClient = WebClientUtils.getWebClient();
        String url = "http:" + getTaskFromQuene();
        //String url = "http://anshan.ganji.com/wuba_info/580129710321898685/";
        if (StringUtils.isBlankOrNull(url)) {
            System.out.println("The task quene is empty now...");
        }

        Page page = WebClientUtils.getHtmlPage(url, webClient);
        Ganji ganji = parseData(page);
        if (ganji != null) {
            if (ganjis.size() > 1000) {
                mutex.wait();
            }
            ganjis.add(ganji);
            System.out.println(Thread.currentThread().getName() + "crawl ganji  =  " + ganji.toString());

        }
        System.out.println(Thread.currentThread().getName() + "crawl success " + ganjis.size());
    }

    public Ganji parseData(Page page) {
        if (page == null || StringUtils.isBlankOrNull(page.getWebResponse().getContentAsString())) {
            System.out.println(Thread.currentThread().getName() + " illegal params");
            return null;
        }
        String context = page.getWebResponse().getContentAsString();
        Document document = Jsoup.parse(context);
        String company = document.select("h1[class=p1]").first().text();
        String address = document.select("div[class=map]").text();
        String phone = document.select("span[class=num phone]").text();
        String title = "";
        String category = "";
        String publishTime = "";
        String crawlTime = "";

        Ganji ganji = new Ganji();
        ganji.setCompany(company);
        ganji.setAddress(address);
        ganji.setPhone(phone);
        ganji.setTitle(title);
        ganji.setCategory(category);
        ganji.setCrawlTime(crawlTime);
        ganji.setPublishTime(publishTime);

        return ganji;

    }


    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(new ProductThread());
        t1.start();


        for (int i = 0; i < 10; i++) {
            Thread t2 = new Thread(new ConsumeThread());
            threadPool.execute(t2);
        }


    }


    /**
     * 生产者线程
     */
    static class ProductThread implements Runnable {

        @Override
        public void run() {
            try {
                crawlDataIndex();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void crawlDataIndex() throws Exception {
            WebRequest webRequest = null;
            for (String city : GanjiCrawlerUtils.CITY_LIST) {
                String startUrl = "http://" + city + ".ganji.com/danbaobaoxian/";
                System.out.println("startUrl = " + startUrl);
                webRequest = new WebRequest(new URL(startUrl), HttpMethod.GET);
                webClient.addRequestHeader("User-Agent", GanjiCrawlerUtils.getRandomAgents());
                Page page = WebClientUtils.getWebRequestPage(webRequest, webClient);
                if (page != null) {
                    Document document = Jsoup.parse(page.getWebResponse().getContentAsString());
                    Elements urlEles = document.select("a[class=f14 list-info-title js_wuba_stas]");
                    for (Element urlEle : urlEles) {
                        System.out.println("url  = " + urlEle.attr("href"));
                        taskQuene.add(urlEle.attr("href"));
                    }
                }
            }
        }
    }

    /**
     * 消费者线程
     */
    static class ConsumeThread implements Runnable {

        @Override
        public void run() {
            try {
                crawlCompany();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 从任务队列获取任务
         *
         * @return
         */
        public String getTaskFromQuene() {
            try {
                return taskQuene.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }


        /**
         * 爬取某一个具体公司得页面
         *
         * @throws Exception
         */


        public void crawlCompany() throws Exception {
            String url = getTaskFromQuene();
            if (StringUtils.isBlankOrNull(url)) {
                System.out.println("The task quene is empty now...");
            }
            WebRequest webRequest = new WebRequest(new URL(url), HttpMethod.GET);
            Page page = WebClientUtils.getWebRequestPage(webRequest, webClient);
            Ganji ganji = parseData(page);
            if (ganji != null) {
                ganjis.add(ganji);
            }
        }

        public Ganji parseData(Page page) {
            if (page == null || StringUtils.isBlankOrNull(page.getWebResponse().getContentAsString())) {
                return null;
            }
            String context = page.getWebResponse().getContentAsString();
            Document document = Jsoup.parse(context);
            String company = document.select("h1[class=p1]").first().text();
            String address = StringUtils.clearNBSP(document.select("div[class=map]"), 0);
            String phone = document.select("span[class=num phone]").text();
            String title = "";
            String category = "";
            String publishTime = "";
            String crawlTime = "";
            Ganji ganji = new Ganji();
            ganji.setCompany(company);
            ganji.setAddress(address);
            ganji.setPhone(phone);
            ganji.setTitle(title);
            ganji.setCategory(category);
            ganji.setCrawlTime(crawlTime);
            ganji.setPublishTime(publishTime);
            return ganji;
        }
    }

    /**
     * 监控缓存数据 防止数据量过大导致内存小号过大
     */
    static class MonitorThread implements Runnable {

        private static final int MAX_SIZE = 1000;

        @Override
        public void run() {
            while (true) {
                if (GanJiCrawler.ganjis.size() > MAX_SIZE) {
                    // TODO: 2017/11/27  保存进入数据库
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    GanJiCrawler.mutex.notifyAll();
                }
            }
        }
    }

}
