package com.whut.crawler;

import com.common.utils.file.FileUtils;
import com.common.utils.StringUtils;
import com.common.utils.WebClientUtils;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.whut.entiry.KuBao;
import com.whut.entiry.TaskEntity;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 从酷安上抓取App信息
 * <p>
 * Created by Bruce on 2017/9/20.
 */
public class AppInfoCrawler {

    static {
        webClient = WebClientUtils.getWebClient();
    }

    /**
     * 任务队列
     */
    public static BlockingQueue<TaskEntity> taskQuene = new LinkedBlockingQueue<TaskEntity>();
    public static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static String domain = "https://www.coolapk.com";

    public static WebClient webClient;

    public static void main(String[] args) {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    crawlerData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        try {
            for (int i = 0; i < 10; i++) {
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            TaskEntity task = getTaskFromQuene();
                            if (task != null) {
                                crawlerData(task);
                            }
                        }
                    }
                });
                threadPool.execute(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //parseData();
    }

    /**
     * 通过拿到的任务抓取结果
     *
     * @param task
     */
    public static void crawlerData(TaskEntity task) {
        try {
            System.out.println(Thread.currentThread().getName() + ":抓取" + task.getAppName() + "---->" + task.getUrl());
            System.out.println();
            WebRequest webRequest1 = new WebRequest(new URL(task.getUrl()), HttpMethod.GET);
            Page appPage = WebClientUtils.getWebRequestPage(webRequest1, webClient);
            FileUtils.saveToFile("g:/apps01/" + task.getFirst() + "/" + task.getSecond() + "/" + task.getAppName() + ".html",
                    appPage.getWebResponse().getContentAsString());
        } catch (Exception e) {
            System.out.println(task.getAppName() + "抓取出错");
        }
    }

    /**
     * 从任务队列获取任务
     *
     * @return
     */
    public static TaskEntity getTaskFromQuene() {
        try {
            return taskQuene.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析data
     */
    public static void parseData() {
        File file = new File("g:/apps01");
        List<KuBao> kubaos = new ArrayList<KuBao>();
        for (File f : file.listFiles()) {
            for (File f2 : f.listFiles()) {
                for (File f1 : f2.listFiles()) {
                    try {
                        if (!f1.isDirectory()) {
                            KuBao kubao = new KuBao();
                            Document document = Jsoup.parse(FileUtils.readFileContent(f1.getAbsolutePath()));
                            String domain = "应用分类";
                            String first = f1.getParentFile().getParentFile().getName();
                            String second = f1.getParentFile().getName();
                            String appName = document.select("p[class=detail_app_title]").text();
                            String introduction = document.select("div[class=apk_left_title_info]").text();
                            kubao.setDomain(domain);
                            kubao.setFirst(first);
                            kubao.setSecond(second);
                            kubao.setAppName(appName);
                            kubao.setIntroduction(introduction);
                            kubaos.add(kubao);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("解析" + f.getAbsolutePath() + "出错");
                    }
                }
            }
        }

        saveToExcel(kubaos);
    }


    public static String s[] = new String[]{"应用市场名称", "一级分类", "二级分类", "app名称", "app介绍文字详情"};
    /**
     * 创建excel表头
     *
     * @param sheet
     */
    private static void createRowHeader(XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < s.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(s[i]);
        }
    }

    private static void saveToExcel(List<KuBao> lists) {
        String outputPath = "e:/output1121.xlsx";
        FileOutputStream out = null;
        try {
            XSSFWorkbook workBook = new XSSFWorkbook();
            XSSFSheet sheet = workBook.createSheet("111111");
            createRowHeader(sheet);
            for (KuBao kubao : lists) {
                if (kubao != null) {
                    XSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
                    XSSFCell cell = row.createCell(0);
                    cell.setCellValue(kubao.getDomain());

                    cell = row.createCell(1);
                    cell.setCellValue(kubao.getFirst());

                    cell = row.createCell(2);
                    cell.setCellValue(kubao.getSecond());


                    cell = row.createCell(3);
                    cell.setCellValue(kubao.getAppName());

                    cell = row.createCell(4);
                    cell.setCellValue(kubao.getIntroduction());
                }
            }

            out = new FileOutputStream(outputPath);
            workBook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 抓取App页面的信息
     */
    public static void crawlerData() throws Exception {
        String url = "https://www.coolapk.com/apk";
        webClient.getOptions().setJavaScriptEnabled(false);
        WebRequest request = new WebRequest(new URL(url), HttpMethod.GET);
        Page page = WebClientUtils.getWebRequestPage(request, webClient);


        Document document = Jsoup.parse(page.getWebResponse().getContentAsString());
        Elements divs = document.select("div[class=type_list]");
        for (Element div : divs) {
            String firstCategory = div.select("p[class=type_title]").text();
            Elements hrefs = div.select("p[class=type_tag]").select("a");
            for (Element a : hrefs) {
                String href = domain + a.attr("href");
                String secondCategory = a.text();
                crawlerDetail(firstCategory, secondCategory, href);
            }
        }
    }

    /**
     * 抓取分类详情页面
     *
     * @param firstCategory
     * @param href
     */
    private static void crawlerDetail(String firstCategory, String secondCategory, String href) {
        try {
            WebRequest webRequest = new WebRequest(new URL(href), HttpMethod.GET);   //抓取分页的信息
            Page details = WebClientUtils.getWebRequestPage(webRequest, webClient);
            if (details != null) {
                int lastPage = getLastPageNum(details);
                saveApp(firstCategory, secondCategory, details);
                if (lastPage > 1) {
                    int i = 2;
                    while (i <= lastPage) {
                        String url = href + "?p=" + i;
                        webRequest = new WebRequest(new URL(url), HttpMethod.GET);
                        details = WebClientUtils.getWebRequestPage(webRequest, webClient);
                        saveApp(firstCategory, secondCategory, details);
                        i++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(firstCategory + "---" + secondCategory + "抓取出错");
        }
    }


    /**
     * 保存每一个App的信息
     *
     * @param firstCategory
     * @param secondCategory
     * @param details
     */
    private static void saveApp(String firstCategory, String secondCategory, Page details) {
        String url = "";
        String context = details.getWebResponse().getContentAsString();
        Document document = Jsoup.parse(context);
        Elements detailHref = document.select("div[class=app_list_left]").select("a");
        for (Element a : detailHref) {
            if (a.select("div[class=alllist_app_side]").size() != 0) {
                String appName = "";
                try {
                    url = domain + a.attr("href");
                    appName = a.select("p[class=list_app_title]").text();
                    if (appName.contains(":")) {
                        appName = StringUtils.replaceKeyTab(appName).replace(":", "");
                    }
                    TaskEntity task = new TaskEntity();
                    task.setFirst(firstCategory);
                    task.setSecond(secondCategory);
                    task.setUrl(url);
                    task.setAppName(appName);
                    taskQuene.put(task);
                } catch (Exception e) {
                    System.out.println(appName + "抓取出错");
                }
            }
        }
    }

    /**
     * 获取详情页面最后一页
     *
     * @param details
     * @return
     */
    private static int getLastPageNum(Page details) {
        int pageNum = 1;
        try {
            Document doc = Jsoup.parse(details.getWebResponse().getContentAsString());
            Elements lis = doc.select("li");
            for (Element e : lis) {
                if (e.text().contains("尾页")) {
                    String url = e.select("a").attr("href");
                    String numStr = url.substring(url.indexOf("p=") + 2, url.length());
                    pageNum = Integer.parseInt(numStr);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("获取页码错误");
            e.printStackTrace();
        }
        return pageNum;
    }

}
