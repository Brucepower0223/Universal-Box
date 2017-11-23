package com.common.test.zookeeper;

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
     * 获取锁，直到超时
     *
     * @param time 超时时间
     * @param unit 参数的单位
     * @throws Exception
     * @return是否获取到锁
     */
    public boolean acquire(long time, TimeUnit unit) throws Exception;

    /**
     * 释放锁
     *
     * @throws Exception
     */
    public void release() throws Exception;

}
