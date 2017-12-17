package com.common.zookeeper.distributedLock.zkversion1;


/**
 * @author Bruce
 * @date 2017/11/23
 */
public class Test {
    public static void main(String[] args) throws Exception {


        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DistrubutedLock lock = null;
                        lock = new BaseDistributedLock("192.168.41.106:2181", "test1");
                        System.out.println(Thread.currentThread().getName() + "正在运行");
                        Thread.sleep(1000);
                        lock.acquire();
                        System.out.println(Thread.currentThread().getName() + "处理自己的业务");
                        Thread.sleep(10000);
                        lock.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    ZooKeeper client;
//                    try {
//                        client = new ZooKeeper("192.168.41.106:2181", 6000, null);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                }
            });
            thread.start();
        }
    }
}
