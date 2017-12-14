package com.common.redis;

import redis.clients.jedis.Jedis;

/**
 * 自定义的Bitset
 * @author Bruce
 * @date 2017/12/14
 */
public class RedisBitSet {


    private Jedis jedis;
    private String name;


    public RedisBitSet() {

    }

    public RedisBitSet(Jedis jedisCluster, String name) {
        this.jedis = jedis;
        this.name = name;
    }

    public void set(int bitIndex) {
        this.jedis.setbit(this.name, bitIndex, true);
    }

    public void set(int bitIndex, boolean value) {
        this.jedis.setbit(this.name, bitIndex, value);
    }

    public boolean get(int bitIndex) {
        return this.jedis.getbit(this.name, bitIndex);
    }

    public void clear(int bitIndex) {
        this.jedis.setbit(this.name, bitIndex, false);
    }

    public void clear() {
        this.jedis.del(this.name);
    }

    public long size() {
        return this.jedis.bitcount(this.name);
    }

    public boolean isEmpty() {
        return size() <= 0;
    }


}
