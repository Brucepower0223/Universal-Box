package com.common.redis;

import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 基于redis的布隆过滤器实现
 *
 * @author Bruce
 * @date 2017/12/14
 */
public class RedisBloomFilter implements Serializable {

    private RedisBitSet bitset;
    private int bitSetSize;
    private double bitsPerElement;
    private int expectedNumberOfFilterElements; // expected (maximum) number of elements to be added
    private int numberOfAddedElements; // number of elements actually added to the Bloom filter
    private int k; // number of hash functions


    static final String charset = "UTF-8"; // encoding used for storing hash values as strings


    static final String hashName = "MD5"; // MD5 gives good enough accuracy in most circumstances. Change to SHA1 if it's needed
    static final MessageDigest digestFunction;

    static { // The digest method is reused between instances
        MessageDigest tmp;
        try {
            tmp = MessageDigest.getInstance(hashName);
        } catch (NoSuchAlgorithmException e) {
            tmp = null;
        }
        digestFunction = tmp;
    }

    /**
     * Constructs an empty Bloom filter. The total length of the Bloom filter w    ill be
     * c*n.
     *
     * @param c is the number of bits used per element.
     * @param n is the expected number of elements the filter will contain.
     * @param k is the number of hash functions used.
     */
    public RedisBloomFilter(double c, int n, int k) {
        this.expectedNumberOfFilterElements = n;
        this.k = k;
        this.bitsPerElement = c;
        this.bitSetSize = (int)Math.ceil(c * n);
        numberOfAddedElements = 0;
    }

    /**
     * Bind the Redis cluster and the key which will be opreated.
     * @param jedis
     * @param name
     */
    public void bind(Jedis jedis, String name) {
        this.bitset = new RedisBitSet(jedis, name);
    }

    /**
     * Constructs an empty Bloom filter. The optimal number of hash functions (k) is estimated from the total size of the Bloom
     * and the number of expected elements.
     *
     * @param bitSetSize defines how many bits should be used in total for the filter.
     * @param expectedNumberOElements defines the maximum number of elements the filter is expected to contain.
     */
    public RedisBloomFilter(int bitSetSize, int expectedNumberOElements) {
        this(bitSetSize / (double)expectedNumberOElements,
                expectedNumberOElements,
                (int) Math.round((bitSetSize / (double)expectedNumberOElements) * Math.log(2.0)));
    }

    public RedisBloomFilter(double falsePositiveProbability, int expectedNumberOfElements) {
        this(Math.ceil(-(Math.log(falsePositiveProbability) / Math.log(2))) / Math.log(2), // c = k / ln(2)
                expectedNumberOfElements,
                (int)Math.ceil(-(Math.log(falsePositiveProbability) / Math.log(2)))); // k = ceil(-log_2(false prob.))
    }

    public RedisBloomFilter(int bitSetSize, int expectedNumberOfFilterElements, int actualNumberOfFilterElements, RedisBitSet filterData) {
        this(bitSetSize, expectedNumberOfFilterElements);
        this.bitset = filterData;
        this.numberOfAddedElements = actualNumberOfFilterElements;
    }


    /**
     * Generates a digest based on the contents of a String.
     *
     * @param val specifies the input data.
     * @param charset specifies the encoding of the input data.
     * @return digest as long.
     */
    public static int createHash(String val, String charset) {
        int hash = 0;
        try {
            hash = createHash(val.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;
    }

    /**
     * Generates a digest based on the contents of a String.
     *
     * @param val specifies the input data. The encoding is expected to be UTF-8.
     * @return digest as long.
     */
    public static int createHash(String val) {
        return createHash(val, charset);
    }

    /**
     * Generates a digest based on the contents of an array of bytes.
     *
     * @param data specifies input data.
     * @return digest as long.
     */
    public static int createHash(byte[] data) {
        return createHashes(data, 1)[0];
    }

    /**
     * Generates digests based on the contents of an array of bytes and splits the result into 4-byte int's and store them in an array. The
     * digest function is called until the required number of int's are produced. For each call to digest a salt
     * is prepended to the data. The salt is increased by 1 for each call.
     *
     * @param data specifies input data.
     * @param hashes number of hashes/int's to produce.
     * @return array of int-sized hashes
     */
    public static int[] createHashes(byte[] data, int hashes) {
        int[] result = new int[hashes];

        int k = 0;
        byte salt = 0;
        while (k < hashes) {
            byte[] digest;
            synchronized (digestFunction) {
                digestFunction.update(salt);
                salt++;
                digest = digestFunction.digest(data);
            }

            for (int i = 0; i < digest.length/4 && k < hashes; i++) {
                int h = 0;
                for (int j = (i*4); j < (i*4)+4; j++) {
                    h <<= 8;
                    h |= ((int) digest[j]) & 0xFF;
                }
                result[k] = h;
                k++;
            }
        }
        return result;
    }

    /**
     * Compares the contents of two instances to see if they are equal.
     *
     * @param obj is the object to compare to.
     * @return True if the contents of the objects are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RedisBloomFilter other = (RedisBloomFilter) obj;
        if (this.expectedNumberOfFilterElements != other.expectedNumberOfFilterElements) {
            return false;
        }
        if (this.k != other.k) {
            return false;
        }
        if (this.bitSetSize != other.bitSetSize) {
            return false;
        }
        if (this.bitset != other.bitset && (this.bitset == null || !this.bitset.equals(other.bitset))) {
            return false;
        }
        return true;
    }

    /**
     * Calculates a hash code for this class.
     * @return hash code representing the contents of an instance of this class.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.bitset != null ? this.bitset.hashCode() : 0);
        hash = 61 * hash + this.expectedNumberOfFilterElements;
        hash = 61 * hash + this.bitSetSize;
        hash = 61 * hash + this.k;
        return hash;
    }

    /**
     * Calculates the expected probability of false positives based on
     * the number of expected filter elements and the size of the Bloom filter.
     * <br /><br />
     * The value returned by this method is the <i>expected</i> rate of false
     * positives, assuming the number of inserted elements equals the number of
     * expected elements. If the number of elements in the Bloom filter is less
     * than the expected value, the true probability of false positives will be lower.
     *
     * @return expected probability of false positives.
     */
    public double expectedFalsePositiveProbability() {
        return getFalsePositiveProbability(expectedNumberOfFilterElements);
    }

    /**
     * Calculate the probability of a false positive given the specified
     * number of inserted elements.
     *
     * @param numberOfElements number of inserted elements.
     * @return probability of a false positive.
     */
    public double getFalsePositiveProbability(double numberOfElements) {
        // (1 - e^(-k * n / m)) ^ k
        return Math.pow((1 - Math.exp(-k * (double) numberOfElements
                / (double) bitSetSize)), k);

    }

    /**
     * Get the current probability of a false positive. The probability is calculated from
     * the size of the Bloom filter and the current number of elements added to it.
     *
     * @return probability of false positives.
     */
    public double getFalsePositiveProbability() {
        return getFalsePositiveProbability(numberOfAddedElements);
    }


    /**
     * Returns the value chosen for K.<br />
     * <br />
     * K is the optimal number of hash functions based on the size
     * of the Bloom filter and the expected number of inserted elements.
     *
     * @return optimal k.
     */
    public int getK() {
        return k;
    }
    /**
     * Sets all bits to false in the Bloom filter.
     */
    public void clear() {
        bitset.clear();
        numberOfAddedElements = 0;
    }

    /**
     * Adds an object to the Bloom filter. The output from the object's
     * toString() method is used as input to the hash functions.
     *
     * @param element is an element to register in the Bloom filter.
     */
    public void add(String element) {
        try {
            add(element.toString().getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an array of bytes to the Bloom filter.
     *
     * @param bytes array of bytes to add to the Bloom filter.
     */
    public void add(byte[] bytes) {
        int[] hashes = createHashes(bytes, k);
        for (int hash : hashes){
            bitset.set(Math.abs(hash % bitSetSize), true);
        }
        numberOfAddedElements ++;
    }

    /**
     * Adds all elements from a Collection to the Bloom filter.
     * @param strs Collection of elements.
     */
    public void addAll(List<String> strs) {
        for (String element : strs) {
            add(element);
        }
    }

    /**
     * Returns true if the element could have been inserted into the Bloom filter.
     * Use getFalsePositiveProbability() to calculate the probability of this
     * being correct.
     *
     * @param str element to check.
     * @return true if the element could have been inserted into the Bloom filter.
     */
    public boolean contains(String str) {
        boolean ret = false;
        try {
            ret = contains(str.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Returns true if the array of bytes could have been inserted into the Bloom filter.
     * Use getFalsePositiveProbability() to calculate the probability of this
     * being correct.
     *
     * @param bytes array of bytes to check.
     * @return true if the array could have been inserted into the Bloom filter.
     */
    public boolean contains(byte[] bytes) {
        int[] hashes = createHashes(bytes, k);
        for (int hash : hashes) {
            if (!bitset.get(Math.abs(hash % bitSetSize))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if all the elements of a Collection could have been inserted
     * into the Bloom filter. Use getFalsePositiveProbability() to calculate the
     * probability of this being correct.
     * @param strs elements to check.
     * @return true if all the elements in c could have been inserted into the Bloom filter.
     */
    public boolean containsAll(List<String> strs) {
        for (String element : strs) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Read a single bit from the Bloom filter.
     * @param bit the bit to read.
     * @return true if the bit is set, false if it is not.
     */
    public boolean getBit(int bit) {
        return bitset.get(bit);
    }

    /**
     * Set a single bit in the Bloom filter.
     * @param bit is the bit to set.
     * @param value If true, the bit is set. If false, the bit is cleared.
     */
    public void setBit(int bit, boolean value) {
        bitset.set(bit, value);
    }

    /**
     * Return the bit set used to store the Bloom filter.
     * @return bit set representing the Bloom filter.
     */
    public RedisBitSet getBitSet() {
        return bitset;
    }

    /**
     * Returns the number of bits in the Bloom filter. Use count() to retrieve
     * the number of inserted elements.
     *
     * @return the size of the bitset used by the Bloom filter.
     */
    public long size() {
        return this.bitset.size();
    }

    /**
     * Returns the number of elements added to the Bloom filter after it
     * was constructed or after clear() was called.
     *
     * @return number of elements added to the Bloom filter.
     */
    public int count() {
        return this.numberOfAddedElements;
    }

}
