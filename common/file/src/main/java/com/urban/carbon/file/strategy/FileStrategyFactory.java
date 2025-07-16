package com.urban.carbon.file.strategy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * 文件上传策略工厂
 *
 * @author XuGaoran
 * @since 0.0.2
 */
@Slf4j
public class FileStrategyFactory {

    /**
     * 策略模式 Map
     */
    private final Map<String, FileStrategy> strategyMap = new HashMap<>();

    /**
     * 构造函数，初始化策略模式 Map
     *
     * @param strategies 策略对象数组
     */
    public FileStrategyFactory(FileStrategy... strategies) {
        for (FileStrategy strategy : strategies) {
            strategyMap.put(strategy.getFileStrategyName(), strategy);
        }
        log.info("File Upload Strategy Load Success! {} been loaded!", 
                Arrays.toString(strategyMap.keySet().toArray()));
    }

    /**
     * 根据类型获取对应的策略
     *
     * @param type 策略类型
     * @return 策略对象
     */
    public FileStrategy getStrategy(String type) {
        FileStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No strategy found for type: " + type);
        }
        return strategy;
    }
}
