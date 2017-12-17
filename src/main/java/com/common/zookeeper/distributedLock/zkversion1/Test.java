package com.common.zookeeper.distributedLock.zkversion1;


import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * @author Bruce
 * @date 2017/11/23
 */
public class Test {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 500; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DistrubutedLock lock = null;
                        lock = new BaseDistributedLock("192.168.41.106:2181");
                        System.out.println(Thread.currentThread().getName() + "正在运行");
                        lock.acquire();
                        System.out.println(Thread.currentThread().getName() + "处理自己的业务");
                        lock.release();
//                        ZooKeeper zooKeeper = new ZooKeeper("192.168.41.106:2181", 3000, new Watcher() {
//                            @Override
//                            public void process(WatchedEvent watchedEvent) {
//                                num++;
//                                System.out.println("num = " + num);
//                            }
//                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }
}
