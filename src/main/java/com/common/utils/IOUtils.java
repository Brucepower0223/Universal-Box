package com.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * IO流操作工具类
 * Created by Bruce on 2017/9/15.
 */
public class IOUtils {
    private static final Log logger = LogFactory.getLog(IOUtils.class);

    /**
     * 释放资源
     *
     * @param input
     */
    public static void releaseResource(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException var2) {
            ;
        }
    }

    public static void releaseResource(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException var2) {
            ;
        }
    }

    public static void releaseResource(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException var2) {
            ;
        }
    }

    public static void releaseResource(Writer writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException var2) {
            ;
        }
    }

}
