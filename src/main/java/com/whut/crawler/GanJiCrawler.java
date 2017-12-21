package com.whut.crawler;

import com.alibaba.fastjson.JSONObject;
import com.common.utils.file.FileUtils;
import com.common.utils.StringUtils;
import com.common.utils.WebClientUtils;
import com.gargoylesoftware.htmlunit.*;
import com.whut.entiry.Ganji;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    protected static final Log log = LogFactory.getLog(GanJiCrawler.class);

    private static int MAX_THREAD = 10;
    private static final int MAX_SIZE = 100;
    private static final int MAX_TASK = 200;

    /**
     * 任务队列
     */
    public static BlockingQueue<String> taskQuene = new LinkedBlockingQueue<String>();
    public static ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREAD);
    //缓存
    public static volatile List<Ganji> ganjis = new ArrayList<Ganji>();
    public static Object mutex = new Object();  //缓存锁
    public static Object taskMutex = new Object();  //任务队列锁


    /**
     * 切换代理IP
     */
    public static synchronized void changeIpProxy(WebClient webclient) {
        if (refresh) {
            try {
                log.info(Thread.currentThread().getName() + "开始切换代理ip，refresh = " + refresh);
                String url = "http://api.xdaili.cn/xdaili-api//privateProxy/getDynamicIP/DD201752348743QyvM9/81fa4d06000c11e7942200163e1a31c0?returnType=2";
                WebRequest webRequest = new WebRequest(new URL(url), HttpMethod.GET);
                Page page = WebClientUtils.getWebRequestPage(webRequest, webclient);
                if (page != null && page.getWebResponse().getContentAsString().contains("wanIp")) {
                    String result = page.getWebResponse().getContentAsString();

                    JSONObject object = JSONObject.parseObject(result);
                    String ip = StringUtils.getStringByJsonWhereHave(object, "wanIp");
                    String port = StringUtils.getStringByJsonWhereHave(object, "proxyport");
                    ipStr = ip + ":" + port;
                    refresh = false;
                } else {
                    log.error("切换IP失败，请确认代理地址");
                    return;
                }

            } catch (Exception e) {
                log.error("切换代理出错", e);
            }
        }
        if (!StringUtils.isBlankOrNull(ipStr)) {
            ProxyConfig proxyConfig = new ProxyConfig();
            proxyConfig.setProxyHost(ipStr.split(":")[0]);
            proxyConfig.setProxyPort(Integer.parseInt(ipStr.split(":")[1]));
            webclient.getOptions().setProxyConfig(proxyConfig);
            log.info(Thread.currentThread().getName() + "切换代理ip为" + ipStr);
            count++;
        }

    }

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(new ProductThread());
        t1.start();

        for (int i = 0; i < MAX_THREAD; i++) {
            Thread t2 = new Thread(new ConsumeThread());
            threadPool.execute(t2);
        }

        Thread.sleep(2000);
        Thread t3 = new Thread(new MonitorThread());
        t3.start();

    }

    /**
     * 提供给外部调用
     */
    public void call() {
        Thread t1 = new Thread(new ProductThread());
        t1.start();

        for (int i = 0; i < MAX_THREAD; i++) {
            Thread t2 = new Thread(new ConsumeThread());
            threadPool.execute(t2);
        }

        Thread t3 = new Thread(new MonitorThread());
        t3.start();
    }


    /**
     * 生产者线程
     */
    static class ProductThread implements Runnable {
        WebClient webClientProduct = null;

        /**
         * 必须关闭JS,否则贼消耗CPU！！！！
         */
        ProductThread() {
            webClientProduct = WebClientUtils.getWebClient();
            webClientProduct.getOptions().setJavaScriptEnabled(false);
        }

        @Override
        public void run() {
            try {
                crawlDataIndex();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void crawlDataIndex() throws Exception {
            for (String city : GanjiCrawlerUtils.CITY_LIST) {
                boolean isNext = true;
                int pageNum = 1;
                //增加关于下一页的逻辑
                do {
                    if (taskQuene.size() > MAX_TASK) {
                        synchronized (taskMutex) {
                            log.info(Thread.currentThread().getName() + "任务队列已经满,等待消费");
                            taskMutex.wait();
                        }
                    }
                    log.info("开始抓取【" + city + "】【第" + pageNum + "页】的种子信息");
                    String startUrl = "http://" + city + ".ganji.com/danbaobaoxian/o" + pageNum;
                    log.info(Thread.currentThread().getName() + " startUrl = " + startUrl);
//                    webRequest = new WebRequest(new URL(startUrl), HttpMethod.GET);
                    Thread.sleep(10000);
                    webClientProduct.addRequestHeader("User-Agent", GanjiCrawlerUtils.getRandomAgents());
                    Page page = WebClientUtils.getHtmlPage(startUrl, webClientProduct);
                    if (page != null) {
                        String context = page.getWebResponse().getContentAsString();
                        if (!context.contains("下一页")) {
                            isNext = false;
                        }
                        Document document = Jsoup.parse(page.getWebResponse().getContentAsString());
                        //匹配所有包含list-info-title的class标签
                        Elements urlEles = document.select("a[class*=list-info-title]");
                        for (Element urlEle : urlEles) {
                            String url = urlEle.attr("href");
                            if (url.startsWith("//")) {
                                url = "http:" + url + "contactus/";
                            } else {
                                url = "http://" + city + ".ganji.com" + url + "contactus/";
                            }
                            taskQuene.add(url);
//                            log.info(Thread.currentThread().getName() + "将" + url + "存入任务队列");
                        }
                        pageNum++;
                    }
                } while (isNext);
            }
        }
    }

    static volatile boolean refresh = true;
    static volatile String ipStr;
    static volatile int count = 0;


    /**
     * 消费者线程
     */
    static class ConsumeThread implements Runnable {

        WebClient threadClient = null;

        /**
         * 必须关闭JS,否则贼消耗CPU！！！！
         */
        ConsumeThread() {
            threadClient = WebClientUtils.getWebClient();
            threadClient.getOptions().setJavaScriptEnabled(false);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    crawlCompany();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 爬取某一个具体公司得页面
         *
         * @throws Exception
         */
        private void crawlCompany() throws Exception {
            String url = getTaskFromQuene();
            if (StringUtils.isBlankOrNull(url)) {
                log.info("The task quene is empty now...");
            } else {
                try {
                    log.info("开始抓取" + url + "的信息.....");
                    Thread.sleep(4 * 1000);
//                    WebRequest webRequest = new WebRequest(new URL(url), HttpMethod.GET);
                    Page page = WebClientUtils.getHtmlPage(url, threadClient);
                    Ganji ganji = parseData(page, url);
                    if (ganji != null) {
                        //若此时缓存数据已满，则线程挂起，直到监控线程将缓存数据存入数据库并将缓存清空
                        if (ganjis.size() > MAX_SIZE) {
                            log.info(Thread.currentThread().getName() + "缓存已满，线程挂起");
                            synchronized (mutex) {
                                mutex.wait();
                            }
                        }
                        log.info(Thread.currentThread().getName() + "将" + ganji.toString() + "\n" + "存入缓存...");
                        ganjis.add(ganji);
                        ganji = null;   //置为null,防止内存溢出
                    }
                } catch (Exception e) {
                    log.error("抓取" + url + "出错", e);
                }
            }
        }

        /**
         * 从任务队列获取任务
         *
         * @return
         */
        private String getTaskFromQuene() {
            try {
                return taskQuene.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * 解析页面的数据
         *
         * @param page
         * @return
         */
        private Ganji parseData(Page page, String url) {
            log.info("开始解析" + url + "的信息");
            if (page == null || StringUtils.isBlankOrNull(page.getWebResponse().getContentAsString())) {
                return null;
            }
            Ganji ganji = new Ganji();

            String content = page.getWebResponse().getContentAsString();
            if (content.contains("验证码")) {
                log.info(Thread.currentThread().getName() + "******************************访问过于频繁***********************************");
                // 切换IP，将原来的任务重新加入到任务队列
                changeIpProxy(threadClient);
                taskQuene.add(url);
            } else {
                try {
                    Document document = Jsoup.parse(content);
                    String phone = document.select("a[class=btn yahei displayphonenumber show_noauth_pop]").attr("gjalog");
                    phone = phone.substring(phone.indexOf("@phone=") + "@phone=".length(), phone.indexOf("@mf="));

                    Element info = document.select("div[class=d-info]").first();
                    String company = getCompany(info);
                    String address = getAddress(info);
                    if (StringUtils.isBlankOrNull(company)) {
                        company = document.select("h1[class=p1]").text();
                    }
                    String publishTime = "";
                    String title = "";
                    String category = "";
                    String crawlTime = "";
                    ganji.setCity(getCityFromUrl(url));
                    ganji.setCompany(company);
                    ganji.setAddress(address);
                    ganji.setPhone(phone);
                    ganji.setTitle(title);
                    ganji.setCategory(category);
                    ganji.setCrawlTime(crawlTime);
                    ganji.setPublishTime(publishTime);
                } catch (Exception e) {
                    log.error("解析公司" + url + "信息出错", e);
                }
            }
            return ganji;
        }

        private String getCityFromUrl(String url) {
            String city = "";
            if (StringUtils.isBlankOrNull(url)) {
                return city;

            }

            for (int i = 0; i < GanjiCrawlerUtils.CITY_LIST.length; i++) {
                if (url.contains(GanjiCrawlerUtils.CITY_LIST[i])) {
                    city = GanjiCrawlerUtils.CITY_LIST[i];
                    break;
                }
            }
            return city;
        }


        private String getCompany(Element info) {
            String company = "";
            if (info == null) {
                return company;
            } else {
                Elements lis = info.select("li");
                for (Element li : lis) {
                    if (li.text().contains("公司名称")) {
                        company = li.select("div[class=fl]").text();
                    }
                }
            }
            return company;
        }

        private String getAddress(Element info) {
            String address = "";
            if (info == null) {
                return address;
            } else {
                Elements lis = info.select("li");
                for (Element li : lis) {
                    if (li.text().contains("商家地址")) {
                        address = li.select("p").text();
                    }
                }
            }
            return address;
        }

    }

    /**
     * 监控缓存数据 防止数据量过大导致内存消耗过大
     */
    static class MonitorThread implements Runnable {
        int i = 0;

        @Override
        public void run() {
            while (true) {
                if (GanJiCrawler.ganjis.size() > MAX_SIZE) {
                    log.info("缓存已满，开始将其清空并存入数据库，消费者线程进入等待状态");
                    // TODO: 2017/11/27  保存进入数据库
                    String content = "";
                    try {
                        Thread.sleep(5000);
                        for (Ganji ganji : ganjis) {
                            content += ganji.toString() + "\\r";
                            FileUtils.saveToFile("e://ganji/ganji" + i + ".txt", content);
                        }
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    GanJiCrawler.ganjis.clear();
                    synchronized (mutex) {
                        mutex.notifyAll();
                    }
                }
                if (GanJiCrawler.taskQuene.size() == 0) {
                    synchronized (taskMutex) {
                        taskMutex.notifyAll();
                    }
                }
                if (count == MAX_THREAD) {
                    count = 0;
                    refresh = true;
                }
            }
        }
    }


}
