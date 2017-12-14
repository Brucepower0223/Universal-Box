package com.common.zookeeper;


/**
 * @author Bruce
 * @date 2017/11/23
 */
public class Test {
    public static void main(String[] args) throws Exception {


        for (int i = 0; i < 4; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DistrubutedLock lock = null;
                        lock = new BaseDistributedLock("192.168.234.128:2181", "test1");
                        System.out.println(Thread.currentThread().getName() + "正在运行");
                        lock.acquire();
                        System.out.println(Thread.currentThread().getName() + "处理自己的业务");
                        Thread.sleep(10000);
                        lock.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }
}
