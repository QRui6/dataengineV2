package com.urban.carbon.file.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.urban.carbon.file.exception.FileErrorCode;
import com.urban.carbon.file.exception.FileException;
import lombok.extern.slf4j.Slf4j;

/**
 * MD5HashUtils MD5 hash计算工具类
 * 
 * @author XuGaoran
 */
@Slf4j
public class MD5HashUtils {

    private static final MessageDigest md5Digest;

    static {
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException | NullPointerException e) {
            log.error("MD5 Tool init failed, Cause by: {}", e.getMessage());
            throw new FileException(FileErrorCode.MD5_TOOL_INIT_ERROR);
        }
    }

    /**
     * 计算文件的 MD5 Hash
     *
     * @param buffer 存储数据块的缓冲区
     * @param bytesRead 读取的字节数
     */
    public static void calArrayDegist(byte[] buffer, int bytesRead) {
        // 计算Hash
        md5Digest.update(buffer, 0, bytesRead);
    }

    /**
     * 重置
     */
    public static void reset() {
        // 清空上一次计算的缓存
        md5Digest.reset();
    }

    /**
     * 获取结果
     * @return 16进制字符串
     */
    public static String getResult() {
        return byte2Hex(md5Digest.digest());
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes byte[] 待转换的byte数组
     * @return String 转换后的16进制字符串
     * @author CaoLu.
     */
    public static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
    
}
