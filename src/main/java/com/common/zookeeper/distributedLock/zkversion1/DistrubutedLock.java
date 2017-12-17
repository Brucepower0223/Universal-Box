package com.common.zookeeper.distributedLock.zkversion1;

import java.util.concurrent.TimeUnit;

/**
 * @author Bruce
 * @date 2017/11/23
 */
public interface DistrubutedLock {

    /**
     * 获取锁，没有则一直等待
     *
     * @throws Exception
     */
    public void acquire() throws Exception;


    /**
     * 释放锁
     *
     * @throws Exception
     */
    public void release() throws Exception;

}
