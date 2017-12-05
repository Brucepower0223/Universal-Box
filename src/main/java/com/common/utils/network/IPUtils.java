//package com.common.utils.network;
//
//import com.common.utils.WebClientUtils;
//import com.gargoylesoftware.htmlunit.Page;
//import com.gargoylesoftware.htmlunit.ProxyConfig;
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.qihoo.common.util.ElementUtil;
//import com.qihoo.common.util.WebClientUtil;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.util.Map;
//
///**
// * 切换代理IP的工具类
// * @author Bruce
// * @date 2017/12/5
// */
//public class IPUtils {
//    protected static final Log log = LogFactory.getLog(IPUtils.class);
//
//
//    /**
//     * 切换代理IP
//     */
//    public static void changeIpProxy(WebClient webclient, Map<String,String> ipMap) {
//        try {
//            if (ipMap.size() <= 0) {
//                WebClient client = WebClientUtils.getWebClient();
//                client.getOptions().setJavaScriptEnabled(false);
//                client.getOptions().setThrowExceptionOnScriptError(false);
//                client.getOptions().setThrowExceptionOnFailingStatusCode(false);
//                String url = "http://www.ip181.com/";
//
//                try {
//                    url = "http://www.89ip.cn/tiqv.php?sxb=&tqsl=50&ports=&ktip=&xl=on&submit=%CC%E1++%C8%A1";
//                    Page p = WebClientUtils.getUrlPage(url, client);
//                    Document doc = Jsoup.parse(p.getWebResponse().getContentAsString());
//                    String tds = doc.select("div[class=mass]").first().ownText();
//
//                    String[] ipArr = tds.split(" ");
//                    int ipSize = ipMap.size();
//                    log.info("代理ip数量  " + ipSize);
//                    for (int i = 0; i < ipArr.length; i++) {
//                        try {
//
//                            if (ipArr[i].contains("高效高匿名代理IP提取地址")) {
//                                break;
//                            }
//                            ipMap.put(i + ipSize + "", ipArr[i]);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            continue;
//                        }
//                    }
//                } catch (Exception e) {
//                    log.info("获取代理ip22异常了");
//                }
//
//                try {
//                    url = "http://www.66ip.cn/mo.php?sxb=&tqsl=50&port=&export=&ktip=&sxa=&submit=%CC%E1++%C8%A1&textarea=";
//                    Page p = WebClientUtils.getUrlPage(url, client);
//                    Document doc = Jsoup.parse(p.getWebResponse().getContentAsString());
//                    String tds = doc.select("body").first().ownText();
//
//                    String[] ipArr = tds.split(" ");
//                    int ipSize = ipMap.size();
//                    log.info("代理ip数量  " + ipSize);
//                    for (int i = 0; i < ipArr.length; i++) {
//                        try {
//
//                            if (ipArr[i].contains("高效高匿名代理IP提取地址")) {
//                                break;
//                            }
//                            ipMap.put(i + ipSize + "", ipArr[i]);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            continue;
//                        }
//                    }
//                } catch (Exception e) {
//                    log.info("获取代理ip33异常了");
//                }
//
//                try {
//                    url = "http://www.ip181.com/";
//                    Page p = WebClientUtils.getUrlPage(url, client);
//                    Document doc = Jsoup.parse(p.getWebResponse().getContentAsString());
//                    Elements tds = doc.select("table[class=table table-hover panel-default panel ctable]").select("tr");
//
//                    int ipSize = ipMap.size();
//                    log.info("代理ip数量  " + ipSize);
//                    for (int i = 1; i < tds.size(); i++) {
//                        try {
//                            Element tr = ElementUtil.getElementByIndex(tds, i);
//
//                            ipMap.put(i + ipSize + "", ElementUtil.getElementByIndex(tr.select("td"), 0).text() + ":" + ElementUtil.getElementByIndex(tr.select("td"), 1).text());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            continue;
//                        }
//                    }
//                } catch (Exception e) {
//                    log.info("获取代理ip异常了");
//                }
//
//                try {
//                    url = "http://www.nianshao.me/";
//                    Page p = WebClientUtil.getUrlPage(url, client);
//                    Document doc = Jsoup.parse(p.getWebResponse().getContentAsString());
//                    Elements tds = doc.select("table[class=table]").select("tr");
//
//                    int ipSize = ipMap.size();
//                    log.info("代理ip数量  " + ipSize);
//                    for (int i = 0; i < tds.size(); i++) {
//                        try {
//                            Element tr = ElementUtil.getElementByIndex(tds, i);
//
//                            ipMap.put(i + ipSize + "", ElementUtil.getElementByIndex(tr.select("td"), 0).text() + ":" + ElementUtil.getElementByIndex(tr.select("td"), 1).text());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            continue;
//                        }
//                    }
//                } catch (Exception e) {
//                    log.info("获取代理ip4444444异常了");
//                }
//
//                try {
//                    for (int j = 1; j < 4; j++) {
//                        url = "http://www.66ip.cn/areaindex_" + j + "/1.html";
//                        Page p = WebClientUtil.getUrlPage(url, client);
//                        Document doc = Jsoup.parse(p.getWebResponse().getContentAsString());
//                        Elements tds = doc.select("div[id=footer]").select("table").select("tr");
//
//                        int ipSize = ipMap.size();
//                        log.info("代理ip数量  " + ipSize);
//                        for (int i = 1; i < tds.size(); i++) {
//                            try {
//                                Element tr = ElementUtil.getElementByIndex(tds, i);
//
//                                ipMap.put(i + ipSize + "", ElementUtil.getElementByIndex(tr.select("td"), 0).text() + ":" + ElementUtil.getElementByIndex(tr.select("td"), 1).text());
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                continue;
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    log.info("获取代理ip55555555555异常了");
//                }
//
//
//                try {
//                    url = "http://www.xicidaili.com/";
//                    Page p = WebClientUtil.getUrlPage(url, client);
//                    Document doc = Jsoup.parse(p.getWebResponse().getContentAsString());
//                    Elements tds = doc.select("table[id=ip_list]").select("tr[class=odd]");
//
//                    int ipSize = ipMap.size();
//                    log.info("代理ip数量  " + ipSize);
//                    for (int i = 1; i < 23; i++) {
//                        try {
//                            Element tr = ElementUtil.getElementByIndex(tds, i);
//
//                            ipMap.put(i + ipSize + "", ElementUtil.getElementByIndex(tr.select("td"), 1).text() + ":" + ElementUtil.getElementByIndex(tr.select("td"), 2).text());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            continue;
//                        }
//                    }
//                } catch (Exception e) {
//                    log.info("获取代理ip6666异常了");
//                }
//
//            }
//
//            if (ipMap.size() > 0) {
//
//                String ip = ipMap.remove(ipMap.size() + "");
//                while ((ip == null || ip.contains("null")) && ipMap.size() > 0) {
//                    ip = ipMap.remove(ipMap.size() + "");
//                }
//                log.info("代理ip数量  " + ipMap.size());
//                ProxyConfig proxyConfig = new ProxyConfig();
//                proxyConfig.setProxyHost(ip.split(":")[0]);
//                proxyConfig.setProxyPort(Integer.parseInt(ip.split(":")[1]));
//                webclient.getOptions().setProxyConfig(proxyConfig);
//
//                log.info(Thread.currentThread().getName() + "切换代理ip为" + ip);
//            }
//
//        } catch (Exception e) {
//            log.error("切换代理出错", e);
//        }
//
//    }
//}
