package com.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 操作文件工具类
 * Created by Bruce on 2017/9/15.
 * <p>
 * Java实现文件复制、剪切、删除操作,文件指文件或文件夹 文件分割符统一用"//"
 */
public class FileUtils {

    private static final Log logger = LogFactory.getLog(FileUtils.class);


    /**
     * 递归创建文件
     * @param filePath
     */
    public static void createNewFile(String filePath) {
        try {
            File file = new File(filePath);
            // 文件不存在
            if (!file.exists()) {
                // 父目录不存在，创建
                if (!file.getParentFile().exists()) {
                    newFolder(file.getParentFile());
                }
                file.createNewFile();
            }
        } catch (Exception e) {
            logger.info("新建目录操作出错");
            e.printStackTrace();
        }

    }

    /**
     * 递归创建文件夹
     * @param file
     */
    public static void newFolder(File file) {

        try {
            File file3 = file;
            for (int j = 0; j < 10; j++) {
                File file2 = file;
                for (int i = 0; i < 10; i++) {
                    if (!file2.exists()) {
                        file3 = file2;
                        file2 = file2.getParentFile();
                    } else {
                        file3.mkdir();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.info("新建目录操作出错,path:" + file.getAbsolutePath());
            e.printStackTrace();
        }
    }

    public synchronized static void newFolder(String folderPath) {
        try {
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            newFolder(myFilePath);
        } catch (Exception e) {
            logger.info("新建目录操作出错,目录：" + folderPath);
            e.printStackTrace();
        }
    }

    /**
     * 创建文件夹
     * @param foldParh
     */
    public static void createFolder(String foldParh) {
        try {
            File fold = new File(foldParh);
            if (!fold.exists()) {
                fold.mkdirs();
            }
        } catch (Exception e) {
            logger.error("createFolder创建文件路径失败：" + foldParh, e);
        }
    }

    public static void newFile(String filePathAndName, String fileContent) {
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
            FileWriter resultFile = new FileWriter(myFilePath);
            PrintWriter myFile = new PrintWriter(resultFile);
            String strContent = fileContent;
            myFile.println(strContent);
            resultFile.close();

        } catch (Exception e) {
            logger.info("新建目录操作出错");
            e.printStackTrace();

        }
    }

    /**
     * 删除文件
     * @param filePathAndName
     */
    public static void delFile(String filePathAndName) {
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            java.io.File myDelFile = new java.io.File(filePath);
            myDelFile.delete();
            logger.info("删除文件成功，path：" + filePath);
        } catch (Exception e) {
            logger.info("删除文件操作出错");
        }

    }


    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); // 删除空文件夹

        } catch (Exception e) {
            logger.info("删除文件夹操作出错");
            e.printStackTrace();

        }

    }

    public synchronized static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
            }
        }
    }


    public static void copyFile(String oldPath, String newPath, String newDir) {
        try {

            if (null != newDir) {
                (new File(newDir)).mkdirs();
            }

            @SuppressWarnings("unused")
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = null; // 读入原文件
                FileOutputStream fs = null;
                try {
                    inStream = new FileInputStream(oldPath); // 读入原文件
                    fs = new FileOutputStream(newPath);
                    byte[] buffer = new byte[1444];
                    @SuppressWarnings("unused")
                    int length;
                    while ((byteread = inStream.read(buffer)) != -1) {
                        bytesum += byteread; // 字节数 文件大小
                        // logger.info("" + bytesum);
                        fs.write(buffer, 0, byteread);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (inStream != null) {
                        inStream.close();
                    }
                    if (fs != null) {
                        fs.close();
                    }
                }
            }
        } catch (Exception e) {
            logger.info("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = null;
                    FileOutputStream output = null;
                    try {
                        input = new FileInputStream(temp);
                        output = new FileOutputStream(newPath
                                + "/" + (temp.getName()).toString());
                        byte[] b = new byte[1024 * 5];
                        int len;
                        while ((len = input.read(b)) != -1) {
                            output.write(b, 0, len);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (output != null)
                            output.flush();
                        output.close();
                    }
                    if (input != null) {
                        input.close();
                    }
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            logger.info("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }

    public static void moveFile(String oldPath, String newPath, String newDir, boolean isCopyFile) {
        if (isCopyFile) copyFile(oldPath, newPath, newDir);
        delFile(oldPath);

    }

    public static void moveFolder(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        delFolder(oldPath);

    }

    public static Document loadFile(String fileName) {

        // 载入抓取的文件
        File file = new File(fileName);
        Document doc = null;
        try {
            doc = Jsoup.parse(file, "UTF-8");
        } catch (IOException e) {

            e.printStackTrace();
        }
        return doc;
    }

    /**
     * 读取某个文件夹下的所有文件
     */
    public static String[] readFile(String filePath) {

        File file = new File(filePath);
        if (!file.isDirectory()) {
            logger.error("读取不正确，每个用户的解析文件应该保持在文件夹下面" + file.getPath());
        } else if (file.isDirectory()) {
            logger.debug("读取解析文件夹：" + file.getPath());
            String[] fileList = file.list();
            return fileList;
        }
        return null;
    }

    /**
     * 读文件，返回字符串
     * @param path
     * @return
     */
    public static String readFileContent(String path) {
        BufferedReader reader = null;
        String laststr = "";
        FileInputStream stream = null;
        InputStreamReader inputstream = null;
        try {
            stream = new FileInputStream(path);
            inputstream = new InputStreamReader(stream, "UTF-8");
            reader = new BufferedReader(inputstream);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr = laststr + tempString;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputstream != null) {
                    inputstream.close();
                }
                if (stream != null) {
                    stream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return laststr;
    }

    /**
     * 读取文件内容 线程安全
     * @param file
     * @return
     */
    public synchronized static String getFileContext(File file) {
        @SuppressWarnings("unused")
        List<String> returnList = new ArrayList<String>();
        BufferedReader reader = null;
        String context = "";
        InputStreamReader isr = null;
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            isr = new InputStreamReader(stream, "UTF-8");
            reader = new BufferedReader(isr);

            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                if (!"".equals(tempString)) {
                    context = context + tempString;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e1) {
            }
        }
        return context;
    }


    /**
     * 读取某个文件夹下的所有文件(生成文件时minuteNum分钟之前的）
     */
    public static List<String> readFileTime(String filePath, int minuteNum, int maxFileCount) {

        List<String> fileNameList = new ArrayList<String>();

        File file = new File(filePath);
        if (!file.isDirectory()) {
            logger.error("读取文件夹信息不正确，请检查文件夹目录是否存在：" + file.getPath());
        } else if (file.isDirectory()) {
            String[] fileList = file.list();
            long currentTime = System.currentTimeMillis();

            logger.info("文件夹[" + file.getPath() + "]中还有" + fileList.length + "个文件没有解析");

            for (int i = 0; i < fileList.length; i++) {
                File file1 = new File(filePath + "/" + fileList[i]);
                long lastModifyTime = file1.lastModified();
                if ((currentTime - lastModifyTime) / (1000 * 60) > minuteNum) {
                    fileNameList.add(file1.getPath());
                }

                if (fileNameList.size() > maxFileCount) {
                    break;
                }
            }
        }
        return fileNameList;

    }

    public static void delNullFolder(String folderPath) {
        try {
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            if (myFilePath.isDirectory()) {
                String[] fileList = myFilePath.list();
                if (fileList.length == 0) {
                    myFilePath.delete(); // 删除空文件夹
                }
            }
        } catch (Exception e) {
            logger.error("删除文件夹操作出错");
            e.printStackTrace();
        }

    }

    /**
     * 计算文件或者文件夹的大小 ，单位 MB
     * @param file 要计算的文件或者文件夹 ， 类型：java.io.File
     * @return 大小，单位：MB
     */
    public static double getSize(File file) {
        // 判断文件是否存在
        if (file.exists()) {
            // 如果是目录则递归计算其内容的总大小，如果是文件则直接返回其大小
            if (!file.isFile()) {
                // 获取文件大小
                File[] fl = file.listFiles();
                double ss = 0;
                for (File f : fl)
                    ss += getSize(f);
                return ss;
            } else {
                double ss = (double) file.length() / 1024 / 1024;
                //System.out.println(file.getName() + " : " + ss + "MB");
                return ss;
            }
        } else {
            logger.info(file.getPath() + "文件或者文件夹不存在，请检查路径是否正确！");
            return 0.0;
        }
    }

    public synchronized static void writerToFile(String filePath, List<String> seedList, boolean append) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    getFileOutputStream(filePath, append), "UTF-8"));
            // 如果是有记录
            if (seedList.size() > 0 && new File(filePath).length() > 0) {
                bw.newLine();
            }
            for (int i = 0; i < seedList.size(); i++) {
                bw.write(seedList.get(i));
                // 不是最后一个记录,换行
                if (i < seedList.size() - 1) {
                    bw.newLine();
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static List<String> getSeedFromFile(String seed1File) {
        List<String> returnList = new ArrayList<String>();
        BufferedReader reader = null;
        InputStreamReader isr = null;
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(seed1File);
            isr = new InputStreamReader(stream, "UTF-8");
            reader = new BufferedReader(isr);

            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                if (!"".equals(tempString)) {
                    returnList.add(tempString);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
                if (stream != null) {
                    stream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return returnList;
    }


    public synchronized static List<String> getSeedFromFileAndRemoveFile(String seed1File) {
        List<String> returnList = getSeedFromFile(seed1File);
        File seedFile = new File(seed1File);
        seedFile.delete();
        return returnList;
    }

    /**
     * 增加同步处理，为了线程安全，原来多线程同时操作会操作有问题.
     * @param fileRootPath
     * @param successSeedList
     * @throws Exception
     */
    public synchronized void removeLocalSucessSeed(String fileRootPath, List<String> successSeedList) throws Exception {
        List<String> seedList = getSeedFromFile(fileRootPath);
        int bfSize = seedList.size();
        logger.info("移除前，文件中种子数为：" + bfSize);
        for (String seed : successSeedList) {
            seedList.remove(seed);
        }
        logger.info("移除后，文件中种子数为：" + bfSize);
        int successNum = bfSize - seedList.size();
        logger.info(" 本次需要从远程种子文件中移除成功种子数" + successSeedList.size() + ";移除成功数：" + successNum + "在远程种子文件中找不到记录的种子数:" + (successSeedList.size() - successNum));
    }


    public static String readFiletoStr(String filePath) {
        BufferedReader reader = null;
        StringBuffer jsonStr = new StringBuffer("");
        InputStreamReader isr = null;
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(filePath);
            isr = new InputStreamReader(stream, "UTF-8");
            reader = new BufferedReader(isr);
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                jsonStr.append(tempString);
            }
        } catch (FileNotFoundException e) {
            try {
                createNewFile(filePath);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
                if (stream != null) {
                    stream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e1) {
            }
        }
        return jsonStr.toString();
    }


    public static FileOutputStream getFileOutputStream(String filePath, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath, append);
        } catch (FileNotFoundException e) {
            try {
                FileUtils.createNewFile(filePath);
                fos = new FileOutputStream(filePath, append);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return fos;
    }

    public static ExecutorService pool = Executors.newFixedThreadPool(15);

    public static void saveToFile(final String filePath, final String content) {
        try {
            if (content == null || "".equals(content)) return;
            pool.execute(new Thread() {
                public void run() {
                    FileOutputStream fos = null;
                    try {
                        fos = getFileOutputStream(filePath, false);
                        fos.write(content.getBytes("UTF-8"));
                    } catch (Exception e) {
                        logger.info(e.getMessage(), e);
                    } finally {
                        if (null == fos) {
                            return;
                        }

                        try {
                            fos.close();
                        } catch (Exception e) {
                            logger.error("关闭OutputStream失败", e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }
    }

    public synchronized static void addToFile(final String filePath, final String content) {
        try {
            if (content == null || "".equals(content)) return;
            pool.execute(new Thread() {
                public void run() {
                    FileOutputStream fos = null;
                    try {
                        fos = getFileOutputStream(filePath, true);
                        fos.write(content.getBytes("UTF-8"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (null == fos) {
                            return;
                        }

                        try {
                            fos.close();
                        } catch (Exception e) {
                            logger.error("关闭OutputStream失败", e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }
    }

    public static void writeListToFile(final String filePath, final List<String> seedList, final boolean append) {
        try {
            if (seedList == null || seedList.size() == 0) return;
            pool.execute(new Thread() {
                public void run() {
                    try {
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                                getFileOutputStream(filePath, append), "UTF-8"));

                        // 如果是有记录
                        if (seedList.size() > 0 && new File(filePath).length() > 0) {
                            bw.newLine();
                        }

                        for (int i = 0; i < seedList.size(); i++) {
                            bw.write(seedList.get(i));
                            // 不是最后一个记录,换行
                            if (i < seedList.size() - 1) {
                                bw.newLine();
                            }
                        }
                        bw.close();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }
    }


    public static List<String[]> readFiletoStrs(String filePath) {
        BufferedReader reader = null;
        List<String[]> list = new ArrayList<String[]>();
        InputStreamReader isr = null;
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(filePath);
            isr = new InputStreamReader(stream, "UTF-8");
            reader = new BufferedReader(isr);
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                list.add(tempString.split("\001", 30));
            }
        } catch (FileNotFoundException e) {
            try {
                createNewFile(filePath);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
                if (stream != null) {
                    stream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e1) {
            }
        }
        return list;
    }

    /**
     * 递归创建文件夹
     * @param filePath
     */
    public static void createNewFolder(String filePath) {
        try {
            File file = new File(filePath);

            // 文件不存在
            if (!file.exists()) {
                // 父目录不存在，创建
                if (!file.getParentFile().exists()) {
                    newFolder(file.getParentFile());
                }
                file.mkdir();
            }

        } catch (Exception e) {
            logger.info("新建目录操作出错");
            e.printStackTrace();
        }

    }
}

