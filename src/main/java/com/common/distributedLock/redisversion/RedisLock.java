package com.common.distributedLock.redisversion;

/**
 * 基于redis的锁的实现
 *
 * @author Bruce
 * @date 2017/12/18
 */
public interface RedisLock {


    boolean lock(String var1);

    boolean lock(String var1, long var2);

    boolean tryLock(String var1);

    boolean tryLockWithLockTime(String var1, long var2);

    boolean tryLock(String var1, long var2);

    boolean tryLockWithLockTime(String var1, long var2, long var4);

    void unLock(String var1);

}
