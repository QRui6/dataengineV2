package com.urban.carbon.base.utils;

import cn.hutool.core.util.RandomUtil;

/**
 * RandomNameGenerator类用于生成随机字符串、文件名和URL。
 *
 * <p>功能包括：
 * - 根据角色名与手机号生成随机字符串；
 * - 生成指定长度的随机文件名；
 * - 生成随机URL。</p>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
public class RandomNameGenerator {

    /**
     * 按照角色名与手机号创建随机字符串
     *
     * @param prefix 前缀字符串
     * @param telephone 电话号码
     * @return 返回随机生成的字符串
     */
    public static String generateRandomName(String prefix, String telephone) {
        return prefix + "_" + RandomUtil.randomString(3).toUpperCase() + telephone.substring(7, 11);
    }

    /**
     * 生成随机文件名
     *
     * @param strLen 文件名长度
     * @param fileType 文件类型
     * @return 文件名
     */
    public static String generateRandomFileName(Integer strLen, String fileType) {
        String fileName = RandomUtil.randomString(strLen);
        if (fileType != null) {
            fileName += "." + fileType;
        }
        return fileName;
    }

    /**
     * 生成随机URL
     * @return URL (部分，并非全部)
     */
    public static String generateRandomURL() {
        return "/api/service/" + RandomUtil.randomString(32);
    }
}

