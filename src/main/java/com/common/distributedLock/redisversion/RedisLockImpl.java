package com.common.distributedLock.redisversion;

/**
 * Created by admin on 2017/12/18.
 */
public class RedisLockImpl implements RedisLock {



    @Override
    public boolean lock(String var1) {
        return false;
    }

    @Override
    public boolean lock(String var1, long var2) {
        return false;
    }

    @Override
    public boolean tryLock(String var1) {
        return false;
    }

    @Override
    public boolean tryLockWithLockTime(String var1, long var2) {
        return false;
    }

    @Override
    public boolean tryLock(String var1, long var2) {
        return false;
    }

    @Override
    public boolean tryLockWithLockTime(String var1, long var2, long var4) {
        return false;
    }

    @Override
    public void unLock(String var1) {

    }
}
