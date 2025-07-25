package com.urban.carbon.base.utils;

import cn.hutool.core.util.RandomUtil;

import java.security.SecureRandom;
import java.util.Random;

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

    // 密码生成相关常量
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String DIGITS = "0123456789";

    private static final String SPECIAL = "!@#$%^&*()_+";

    private static final String ALL = LOWER + UPPER + DIGITS + SPECIAL;

    private static final Random RANDOM = new SecureRandom();

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

    /**
     * 生成随机密码
     *
     * @param length 密码长度
     * @return 随机密码
     */
    public static String generateRandomPassword(int length) {
        if (length < 8) {
            length = 8; // 最小长度为8位
        }

        StringBuilder password = new StringBuilder(length);

        // 确保密码至少包含一个小写字母、一个大写字母、一个数字和一个特殊字符
        password.append(LOWER.charAt(RANDOM.nextInt(LOWER.length())));
        password.append(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));

        // 生成剩余的字符
        for (int i = 4; i < length; i++) {
            password.append(ALL.charAt(RANDOM.nextInt(ALL.length())));
        }

        // 打乱字符顺序
        char[] passwordArray = password.toString().toCharArray();
        for (int i = 0; i < passwordArray.length; i++) {
            int randomIndex = RANDOM.nextInt(passwordArray.length);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[randomIndex];
            passwordArray[randomIndex] = temp;
        }

        return new String(passwordArray);
    }

    /**
     * 生成默认长度(12位)的随机密码
     *
     * @return 随机密码
     */
    public static String generateRandomPassword() {
        return generateRandomPassword(12);
    }
}

