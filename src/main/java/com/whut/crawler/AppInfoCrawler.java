package com.whut.crawler;

import com.common.utils.FileUtils;
import com.common.utils.WebClientUtils;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.whut.entiry.KuBao;
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

/**
 * 从酷安上抓取App信息
 * <p>
 * Created by Bruce on 2017/9/20.
 */
public class AppInfoCrawler {

    static {
        webClient = WebClientUtils.getWebClient();
    }

    public static String domain = "https://www.coolapk.com";

    public static WebClient webClient;

    public static void main(String[] args) {
        try {
            crawlerData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // parseData();
    }

    /**
     * 解析data
     */
    public static void parseData() {
        File file = new File("");
        List<KuBao> kubaos = new ArrayList<KuBao>();
        for (File f : file.listFiles()) {
            for (File f1 : f.listFiles()) {
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
        }

        saveToExcel(kubaos);
    }

    private static void createRowHeader(XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < s.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(s[i]);
        }
    }

    private static void saveToExcel(List<KuBao> lists) {
        String outputPath = "d:/output1121.xlsx";
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


                    cell = row.createCell(4);
                    cell.setCellValue(kubao.getAppName());

                    cell = row.createCell(5);
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

    public static String s[] = new String[]{"应用市场名称", "一级分类", "二级分类", "app名称", "app介绍文字详情"};

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
                        appName.replace(":", "");
                    }
                    WebRequest webRequest1 = new WebRequest(new URL(url), HttpMethod.GET);
                    Page appPage = WebClientUtils.getWebRequestPage(webRequest1, webClient);
                    FileUtils.saveToFile("g:/apps/" + firstCategory + "/" + secondCategory + "/" + appName + ".html",
                            appPage.getWebResponse().getContentAsString());
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
