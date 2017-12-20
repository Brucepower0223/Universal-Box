package com.whut.crawler;

import com.alibaba.fastjson.JSONObject;
import com.common.utils.FileUtils;
import com.common.utils.WebClientUtils;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 抓取网站排行的信息
 *
 * @author Bruce
 * @date 2017/12/19
 */
public class WebsiteCrawler {

    public String categories[] = {"购物网站", "交通旅游", "休闲娱乐", "教育文化", "生活服务", "医疗健康"};
    WebClient webClient = null;
    public String prefix = "http://top.chinaz.com/hangye/";
    public String indexUrl = "http://top.chinaz.com/hangye/index.html";
    private static List<Task> tasks = new ArrayList<Task>();


    public static void main(String[] args) throws Exception {

        WebsiteCrawler websiteCrawler = new WebsiteCrawler();
        websiteCrawler.getTask();
        System.out.println("所有分类起始网站抓取完毕");
        for (Task task : tasks) {
            System.out.println("【" + task.getFirst() + "---" + task.getSecond() + "】开始抓取");
            new Thread(new CrawlUrlThread(task)).start();
        }
    }

    public void getTask() throws Exception {
        webClient = WebClientUtils.getWebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        Page page = webClient.getPage(indexUrl);
        if (page != null) {
            String result = page.getWebResponse().getContentAsString();
            Document docment = Jsoup.parse(result);
            Elements elements = docment.select("div[class=HeadFilter clearfix]").select("a");
            for (Element a : elements) {
                String text = a.text();
                if (isContains(text)) {
                    String href = prefix + a.attr("href");
                    tasks.addAll(getSecondTask(href, text));
                }
            }
        }
        System.out.println(JSONObject.toJSONString(tasks, true));
    }

    /**
     * 获取二级页面连接
     *
     * @param href
     * @return
     */
    private List<Task> getSecondTask(String href, String first) throws Exception {
        System.out.println("开始抓取【" + first + "】分类");
        Page page = webClient.getPage(href);
        if (page != null) {
            List<Task> tasks = new ArrayList<Task>();
            Document document = Jsoup.parse(page.getWebResponse().getContentAsString());
            Elements as = document.select("div[class=HeadFilter clearfix]").select("a");
            for (Element element : as) {
                String second = element.text();
                String url = element.attr("href");
                Task task = new Task(prefix + url, first, second);
                tasks.add(task);
            }
            System.out.println("抓取【" + first + "】分类完毕");

            return tasks;
        }
        return null;
    }


    private boolean isContains(String src) {
        for (String category : categories) {
            if (category.equals(src)) {
                return true;
            }
        }
        return false;
    }


}


class CrawlUrlThread implements Runnable {
    public String prefix = "http://top.chinaz.com/hangye/";
    Task task;
    WebClient webClient;
    List<String> urls = new ArrayList<String>();

    CrawlUrlThread() {

    }

    CrawlUrlThread(Task task) {
        webClient = WebClientUtils.getWebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setTimeout(10000);
        this.task = task;
    }

    @Override
    public void run() {
        String url = "";
        try {
            url = task.getUrl();
            Page page = webClient.getPage(url);
            if (page != null) {
                String context = page.getWebResponse().getContentAsString();
                int pageNum = getPageNum(context);
                System.out.println(Thread.currentThread().getName() + task.getFirst() + task.getSecond() + "总共" + pageNum + "页");
                for (int i = 1; i <= pageNum; i++) {
                    String crawlUrl = getUrl(i, url);
                    System.out.println(Thread.currentThread().getName() + "开始抓取" + task.getSecond() + "第" + i + "页信息");
                    try {
                        Page page1 = webClient.getPage(crawlUrl);
                        FileUtils.saveToFile("G:/allurls/" + task.getFirst() + "/" + task.getSecond() + "/" + i + ".html", page1.getWebResponse().getContentAsString());
                        System.out.println(Thread.currentThread().getName() + "----------------抓取" + task.getSecond() + "第" + i + "页信息完毕-------------------");
                    } catch (Exception e) {
                        System.out.println("********************抓取" + url + "出错");
                        e.printStackTrace();
                        continue;
                    }

                }
            }
        } catch (IOException e) {
            System.out.println("【" + task.getFirst() + "-" + task.getSecond() + "】" + url + "抓取失败");
        }
    }


//    public static void main(String[] args) {
//        CrawlUrlThread crawlUrlThread = new CrawlUrlThread();
//        System.out.println(crawlUrlThread.getUrl(1, "http://top.chinaz.com/hangye/index_yule_youxi.html"));
//
//    }


    private String getUrl(int pageNum, String url) {
        String ret = "";
        if (pageNum == 1) {
            ret = url;
        } else {
            ret = url.replace(".html", "_" + pageNum + ".html");
        }
        return ret;
    }

    private int getPageNum(String context) {
        Document document = Jsoup.parse(context);
        Elements as = document.select("div[class=ListPageWrap]").select("a");
        return Integer.parseInt(as.get(as.size() - 2).text());
    }


}

class Parser {

    public String s[] = new String[]{"一级分类", "二级分类", "网站名称", "网址", "Alexa周排名"};


    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.parserData();
    }

    /**
     * 创建excel表头
     *
     * @param sheet
     */
    private void createRowHeader(XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < s.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(s[i]);
        }
    }


    private void saveToExcel(List<WebsiteEntity> lists, String first) {
        String outputPath = "g:/" + first + ".xlsx";
        FileOutputStream out = null;
        try {
            XSSFWorkbook workBook = new XSSFWorkbook();
            XSSFSheet sheet = workBook.createSheet("111111");
            createRowHeader(sheet);
            for (WebsiteEntity website : lists) {
                if (website != null) {
                    XSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
                    XSSFCell cell = row.createCell(0);
                    cell.setCellValue(website.getFirst());

                    cell = row.createCell(1);
                    cell.setCellValue(website.getSecond());

                    cell = row.createCell(2);
                    cell.setCellValue(website.getName());

                    cell = row.createCell(3);
                    cell.setCellValue(website.getWebsite());

                    cell = row.createCell(4);
                    cell.setCellValue(website.getRank());
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

    public void parserData() {
        String prefix = "www.";
        String savePath = "G:/allurls";
        File file = new File(savePath);
        System.out.println(file.listFiles().length);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File firstFile : files) {
                List<WebsiteEntity> entities = new ArrayList<WebsiteEntity>();
                String first = firstFile.getName();
                File[] secondFiles = firstFile.listFiles();
                for (File secondFile : secondFiles) {
                    String second = secondFile.getName();
                    for (File htmlFile : secondFile.listFiles()) {
                        String context = FileUtils.readFileContent(htmlFile.getAbsolutePath());
                        Document document = Jsoup.parse(context);
                        Elements lis = document.select("ul[class=listCentent]").select("li");
                        for (Element li : lis) {
                            try {
                                WebsiteEntity entity = new WebsiteEntity();
                                String url = prefix + li.select("span[class=col-gray]").text();
                                String rank = li.select("p[class=RtCData]").get(0).select("a").text();
                                String name = li.select("h3[class=rightTxtHead]").select("a").attr("title");
                                entity.setWebsite(url);
                                int rankInt = 0;
                                try {
                                    rankInt = Integer.parseInt(rank);
                                } catch (Exception e) {
                                    rankInt = 0;
                                }
                                entity.setRank(rankInt);
                                entity.setFirst(first);
                                entity.setSecond(second);
                                entity.setName(name);
                                entities.add(entity);
                            } catch (Exception e) {
                                System.out.println("解析" + htmlFile.getAbsolutePath() + "出错");
                                e.printStackTrace();
                            }

                        }

                    }
                }

                Collections.sort(entities, new Comparator<WebsiteEntity>() {
                    @Override
                    public int compare(WebsiteEntity o1, WebsiteEntity o2) {
                        if (o1.getRank() > o2.getRank()) {
                            return 1;
                        }
                        if (o1.getRank() < o2.getRank()) {
                            return -1;
                        }
                        return 0;
                    }
                });

                saveToExcel(entities, first);

            }
        }
    }

}


class Task {
    private String url;
    private String first;
    private String second;

    Task() {
    }

    Task(String url, String first, String second) {
        this.url = url;
        this.first = first;
        this.second = second;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }
}

class WebsiteEntity {
    String first;
    String second;
    String website;
    int rank;
    String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
