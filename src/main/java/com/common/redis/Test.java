package com.common.redis;

import redis.clients.jedis.Jedis;

import java.io.*;

/**
 * Created by Bruce on 2017/12/14.
 */
public class Test {


    public static void main(String[] args) {




    }



    public static void bloomFilterTest() throws Exception {
        int dataCnt = 575270;
        int judgeCnt = dataCnt;
        double errorCnt = 0;

        Jedis jedis = new Jedis("192.168.234.128",6379);


        RedisBloomFilter bloomFilter = new RedisBloomFilter(0.000001, (int) (173070 * 1.5));
        bloomFilter.bind(jedis, "abc");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("g:/data2.txt"), "UTF-8"));
//        BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream("g:/data3.txt"), "UTF-8"));
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("g:/ret.log"), "UTF-8"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            bloomFilter.add(line);
        }
        long startTime = System.nanoTime();
//        while ((line = reader2.readLine()) != null) {
//            if (!bloomFilter.contains(line)) {
//                System.out.println("集合中不存在该url"+ line);
//                errorCnt ++;
//            }
//        }
        reader.close();
//        reader2.close();
        long consumTime = System.nanoTime() - startTime;
        String result = "初始化：" + dataCnt * 1 + "\n" + "插入数据：" + dataCnt + "\n" + "查询数据：" + judgeCnt + "\n"
                + "耗时：" + consumTime / judgeCnt + "ms" + "\n" + "内存：" + bloomFilter.getBitSet().size() / 8 / 1024 + "KB" + "\n" + "失误：" + errorCnt;
//        writer.write(result);
//        writer.close();
        System.out.println("初始化：" + dataCnt * 1);
        System.out.println("插入数据：" + dataCnt);
        System.out.println("查询数据：" + judgeCnt);
        System.out.println("耗时：" + consumTime / judgeCnt + "ns");
        System.out.println("内存：" + bloomFilter.getBitSet().
                size() / 8 / 1024 + "KB");
        System.out.printf("失误率：%.2f%%\n", (errorCnt / judgeCnt) * 100);
    }

}
