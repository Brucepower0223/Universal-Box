package com.common.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Bruce
 * @date 2017/11/23
 */
public class BaseDistributedLock implements Watcher, DistrubutedLock {

    private ZooKeeper client = null;
    private static final String LOCK_ROOT = "/Locker01";
    private static final String LOCK_NAME = "lock";

    private String waitLock;
    private String currentLock;

    private int sessionTimeout = 30000;
    private Object mutex = new Object();

    public static volatile boolean isDisconnect = false;


    public BaseDistributedLock() {
    }


    public BaseDistributedLock(String config, String lockName) {
        try {
            client = new ZooKeeper(config, sessionTimeout, this);
            Stat exists = client.exists(LOCK_ROOT, false);
            if (exists == null) {
                client.create(LOCK_ROOT, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("received the type = " + watchedEvent.getType());
        if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
            isDisconnect = true;
            System.out.println("isDisconnected = true");
            synchronized (mutex) {
                mutex.notify();
            }
        }
    }


    @Override
    public boolean acquire(long time, TimeUnit unit) throws Exception {
        return false;
    }

    @Override
    public void acquire() throws Exception {
        currentLock = client.create(LOCK_ROOT + "/" + LOCK_NAME,
                new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        currentLock = currentLock.substring(currentLock.lastIndexOf("/") + 1);
        System.out.println(Thread.currentThread().getName() + currentLock + " has been created!!!");
        List<String> children = client.getChildren(LOCK_ROOT, false);
        Collections.sort(children);
        if (currentLock.equals(children.get(0))) {
            System.out.println(Thread.currentThread().getName() + " has the lock named " + currentLock);
        } else {
            //若不是最小的节点
            waitLock = children.get(children.indexOf(currentLock) - 1);
            waitForLock();
        }
    }

    /**
     * 等待锁,直到等到锁
     *
     * @return
     */
    private boolean waitForLock() throws Exception {
        //对等待的节点进行监听
        Stat stat = client.exists(LOCK_ROOT + "/" + waitLock, true);
        if (stat != null) {
            System.out.println(Thread.currentThread().getName() + "等待锁 " + LOCK_ROOT + "/" + waitLock);
            while (true) {
                if (isDisconnect) {
                    List<String> children = client.getChildren(LOCK_ROOT, false);
                    Collections.sort(children);
                    if (currentLock.equals(children.get(0))) {
                        System.out.println(Thread.currentThread().getName() + " has the lock named " + currentLock);
                        System.out.println(Thread.currentThread().getName() + " 等到了锁");
                    }
                    isDisconnect = false;
                    return true;
                } else {
                    synchronized (mutex) {
                        mutex.wait();
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void release() throws Exception {
        //删除节点
        client.delete(LOCK_ROOT + "/" + currentLock, -1);
    }


}
