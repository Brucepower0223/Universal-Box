package com.whut.recognition;

import com.common.utils.WebClientUtils;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简单的验证码识别
 *
 * PingAnTrain底下的文件是针对这一类的验证的训练模板
 *
 * @author fangjin
 * @date 2017/10/26
 */
public class YZM {

    private static Map<BufferedImage, String> trainMap = null;

    private static String trainPath = "";   //可配置
    private static String tmp = "";


    public static void main(String[] args) throws Exception {
        WebClient webClient = WebClientUtils.getWebClient();
        String src = getVerifcode("http://life.pingan.com/cms-tmplt/validatecode.jsp?rand=89190", webClient);
        System.out.println(src);
    }

    /**
     * 开放的接口
     *
     * @return
     */
    public static String getVerifcode(String url, WebClient client) throws Exception {
        WebRequest webRequest = new WebRequest(new URL(url));
        Page page = WebClientUtils.getWebRequestPage(webRequest, client);

        InputStream in = page.getWebResponse().getContentAsStream();
        FileOutputStream fos = new FileOutputStream(new File(tmp));
        int len = 0;
        byte[] b = new byte[1024];
        while ((len = in.read(b)) != -1) {
            fos.write(b, 0, len);
        }
        fos.close();

        BufferedImage image = ImageIO.read(page.getWebResponse().getContentAsStream());
        //1.图片灰度化处理
        image = gray(image);

        //2.去掉图片的上下边框
        image = clearImageBoarder(image);

        //3.去掉图片的分散的噪点
        image = cleardDisperseImage(image);

        //4.装载训练模板
        trainMap = loadTrainData();

        //5.进行图片的分割
        splitImage(image);
        //6.识别图片
        String result = getAllOcr(image);
        return result;
    }

    /**
     * 识别单个字符
     *
     * @param img
     * @param map
     * @return
     */
    private static String getSingleCharOcr(BufferedImage img,
                                           Map<BufferedImage, String> map) {
        String result = "";
        int width = img.getWidth();
        int height = img.getHeight();
        int min = width * height;
        for (BufferedImage bi : map.keySet()) {
            int count = 0;
            int widthmin = width < bi.getWidth() ? width : bi.getWidth();
            int heightmin = height < bi.getHeight() ? height : bi.getHeight();
            Label1:
            for (int x = 0; x < widthmin; ++x) {
                for (int y = 0; y < heightmin; ++y) {
                    if (isDark(img.getRGB(x, y)) != isDark(bi.getRGB(x, y))) {
                        count++;
                        if (count >= min) {
                            break Label1;
                        }
                    }
                }
            }
            if (count < min) {
                min = count;
                result = map.get(bi);
            }
        }
        return result;
    }

    /**
     * 识别所有字符
     *
     * @return
     * @throws Exception
     */
    private static String getAllOcr(BufferedImage bufferedImage) throws Exception {
        List<BufferedImage> listImg = splitImage(bufferedImage);
        Map<BufferedImage, String> map = loadTrainData();
        String result = "";
        for (BufferedImage bi : listImg) {
            result += getSingleCharOcr(bi, map);
        }
        return result;
    }


    /**
     * 切分验证码到内存
     *
     * @return
     * @throws IOException
     */
    private static List<BufferedImage> splitImage(BufferedImage source) throws IOException {
        List<BufferedImage> images = new ArrayList<BufferedImage>();
        int width = source.getWidth();
        int height = source.getHeight();
        images.add(source.getSubimage(0, 0, width / 4, height));
        images.add(source.getSubimage((width / 4), 0, width / 4, height));
        images.add(source.getSubimage((width / 4) * 2, 0, width / 4, height));
        images.add(source.getSubimage((width / 4) * 3, 0, width / 4, height));
        return images;
    }


    /**
     * 装载训练模板
     *
     * @return
     * @throws Exception
     */
    private static Map<BufferedImage, String> loadTrainData() throws Exception {
        if (trainMap == null) {
            Map<BufferedImage, String> map = new HashMap<BufferedImage, String>();
            File dir = new File(trainPath);
            File[] files = dir.listFiles();
            for (File file : files) {
                map.put(ImageIO.read(file), file.getName().charAt(0) + "");
            }
            trainMap = map;
        }
        return trainMap;
    }

    /**
     * 判断像素点是否为深的颜色
     *
     * @param colorInt
     * @return
     */
    private static int isDark(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() <= 400) {
            return 1;
        }
        return 0;
    }


    /**
     * 图片灰度，黑白
     */
    private static BufferedImage gray(BufferedImage src) {
        try {
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            src = op.filter(src, null);
            ImageIO.write(src, "JPEG", new File(tmp));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return src;
    }


    /**
     * 去掉分散的噪点
     */
    private static BufferedImage cleardDisperseImage(BufferedImage bufferedImage) throws Exception {

        int h = bufferedImage.getHeight();
        int w = bufferedImage.getWidth();

        for (int x = 1; x < w - 1; x++) {
            for (int y = 1; y < h - 1; y++) {
                int count = 0;
                if (isDark(bufferedImage.getRGB(x - 1, y)) == 1) {
                    count++;
                }
                if (isDark(bufferedImage.getRGB(x + 1, y)) == 1) {
                    count++;
                }
                if (isDark(bufferedImage.getRGB(x, y - 1)) == 1) {
                    count++;
                }
                if (isDark(bufferedImage.getRGB(x, y + 1)) == 1) {
                    count++;
                }
                if (count < 2) {
                    bufferedImage.setRGB(x, y, Color.WHITE.getRGB());
                }

            }
        }
        return bufferedImage;
    }

    /**
     * 去掉图片的上边框和下边框
     *
     * @throws IOException
     */
    private static BufferedImage clearImageBoarder(BufferedImage src) throws Exception {

        BufferedImage bufferedImage = ImageIO.read(new File(tmp));


        int h = bufferedImage.getHeight();
        int w = bufferedImage.getWidth();


        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                if (isDark(bufferedImage.getRGB(x, y)) == 1) {
                    bufferedImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    bufferedImage.setRGB(x, y, Color.WHITE.getRGB());
                }
                //去除边框
                if (x < 2 || x > w - 2) {
                    bufferedImage.setRGB(x, y, Color.WHITE.getRGB());
                }
                if (y < 3 || y > 16) {
                    bufferedImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        return bufferedImage;
    }


}
