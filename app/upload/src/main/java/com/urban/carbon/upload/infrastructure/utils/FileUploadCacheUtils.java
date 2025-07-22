package com.urban.carbon.upload.infrastructure.utils;

import com.urban.carbon.api.upload.constants.SaveSoftType;
import com.urban.carbon.cache.constant.CacheConstant;
import com.urban.carbon.upload.domain.entity.UploadCache;
import org.redisson.api.RBitSet;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.BitSet;
import java.util.List;
import java.util.ArrayList;

/**
 * 文件上传缓存工具类
 */
@Component
public class FileUploadCacheUtils {

    /**
     * Redisson 客户端
     */
    private final RedissonClient redissonClient;

    /**
     * 构造函数
     *
     * @param redissonClient Redisson 客户端
     */
    public FileUploadCacheUtils(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 初始化缓存
     *
     * @param fileId 文件 ID
     * @param dataName 文件名
     * @param dataDesc 文件描述
     * @param chunkSize 分块大小
     * @param totalChunks 总分块数
     * @param fileSize 文件大小
     * @param saveSoft 保存方式
     */
    public void initCache(String fileId, String dataName, String dataDesc, Long chunkSize,
                          Long totalChunks, Long fileSize, SaveSoftType saveSoft) {
        String fileKey = CacheConstant.FILE_CACHE_KEY_PREFIX + fileId;
        // 设置 Bitset
        RBitSet bitSet = redissonClient.getBitSet(fileKey + ":bits");
        bitSet.set(new BitSet((int) (totalChunks * 2)));
        // 设置缓存数据
        UploadCache cacheData = new UploadCache();
        cacheData.createCache(dataName, dataDesc, chunkSize, totalChunks, fileSize, saveSoft);
        RBucket<UploadCache> bucket = redissonClient.getBucket(fileKey + ":info");
        bucket.set(cacheData);
    }

    /**
     * 获取缓存数据
     *
     * @param fileId 文件 ID
     */
    public UploadCache getCacheData(String fileId) {
        String fileKey = CacheConstant.FILE_CACHE_KEY_PREFIX + fileId;
        RBucket<UploadCache> bucket = redissonClient.getBucket(fileKey + ":info");
        return bucket.get();
    }

    /**
     * 获取 Bitset
     *
     * @param fileId 文件 ID
     */
    public RBitSet getChunkBits(String fileId) {
        String fileKey = CacheConstant.FILE_CACHE_KEY_PREFIX + fileId;
        return redissonClient.getBitSet(fileKey + ":bits");
    }

    /**
     * 获取分片状态
     *
     * @param chunkBits 从缓存中取出的 Bitset
     */
    public int getChunkStatus(BitSet chunkBits, int chunkIndex) {
        boolean bit0 = chunkBits.get(chunkIndex * 2);
        boolean bit1 = chunkBits.get(chunkIndex * 2 + 1);
        return (bit1 ? 2 : 0) | (bit0 ? 1 : 0);
    }

    /**
     * 设置分片状态
     *
     * @param chunkBits 从缓存中取出的 Bitset
     * @param status 分片状态码
     * @param chunkIndex 分片索引
     */
    public void setChunkStatus(RBitSet chunkBits, long chunkIndex, int status) {
        if (status < 0 || status > 3) {
            throw new IllegalArgumentException("Status must be between 0 and 3");
        }
        chunkBits.set(chunkIndex * 2, (status & 1) == 1);
        chunkBits.set(chunkIndex * 2 + 1, (status & 2) == 2);
    }

    /**
     * 获取已上传的块索引列表
     *
     * @param chunkBits 从缓存中取出的 Bitset
     */
    public List<Integer> getUploadedChunks(BitSet chunkBits) {
        List<Integer> uploadedChunks = new ArrayList<>();
        for (int idx = 0; idx < chunkBits.size(); idx += 2) {
            if (chunkBits.get(idx) && chunkBits.get(idx + 1)) {
                uploadedChunks.add(idx / 2);
            }
        }
        return uploadedChunks;
    }

    /**
     * 删除缓存
     *
     * @param fileId 文件 ID
     */
    public void deleteCache(String fileId) {
        String fileKey = CacheConstant.FILE_CACHE_KEY_PREFIX + fileId;
        redissonClient.getBitSet(fileKey + ":bits").delete();
        redissonClient.getBucket(fileKey + ":info").delete();
    }

    /**
     * 获取上传失败的分片索引列表
     *
     * @param bitSet 分片位图
     * @return 失败的分片索引列表
     */
    public List<Integer> getFailedChunks(BitSet bitSet) {
        List<Integer> failedChunks = new ArrayList<>();
        for (int i = nextSetBit(bitSet, 0); i >= 0; i = nextSetBit(bitSet, i + 1)) {
            int status = getChunkStatus(bitSet, i);
            if (status == 2) { // 假设状态码 2 表示失败
                failedChunks.add(i);
            }
        }
        return failedChunks;
    }

    private int nextSetBit(BitSet bitSet, int fromIndex) {
        return bitSet.nextSetBit(fromIndex);
    }
}
