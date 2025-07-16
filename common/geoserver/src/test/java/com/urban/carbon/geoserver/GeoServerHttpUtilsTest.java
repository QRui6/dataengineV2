package com.urban.carbon.geoserver;

import com.urban.carbon.geoserver.config.GeoServerConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

// 确保你的测试配置类能被加载
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoServerConfiguration.class) // 指向测试配置类
public class GeoServerHttpUtilsTest {

    private static final Logger log = LoggerFactory.getLogger(GeoServerHttpUtilsTest.class);

    @Autowired
    private GeoServerHttpUtils geoServerHttpUtils;

    /**
     * 测试工作空间、矢量图层、栅格图层的完整生命周期
     * 创建 -> 发布 -> 删除
     */
    @Test
    public void testFullLifecycle() throws IOException {
        //--- 1. 创建工作空间 ---
        // 定义测试中使用的常量
        String testWorkspace = "my_test_workspace";
        log.info("正在创建工作空间: {}", testWorkspace);
        String result = geoServerHttpUtils.createWorkspace(testWorkspace);
        log.info("创建工作空间结果: {}", result);

        // --- 2. 创建并发布矢量图层 ---
        //这里的名称要和上传的文件名称一致
        String testVectorStore = "carbon_Ze4xqPIZ6sXEJjxV";
        log.info("正在创建矢量数据存储: {}", testVectorStore);
        // 文件路径
        String vectorFilePath = "file:/root/Documents/data/dataengine/file/5/carbon_Ze4xqPIZ6sXEJjxV.shp";
        result = geoServerHttpUtils.createShapefileDataStore(testWorkspace, testVectorStore, vectorFilePath);
        log.info("创建矢量数据存储结果: {}", result);

        log.info("正在发布矢量图层 (FeatureType): {}", testVectorStore);
        result = geoServerHttpUtils.createFeatureType(testWorkspace, testVectorStore, testVectorStore);
        log.info("发布矢量图层结果: {}", result);

        // --- 3. 创建并发布栅格图层 ---
        String testRasterStore = "z9JRyK7vYCcIoXrc";
        log.info("正在创建栅格数据存储: {}", testRasterStore);
        String rasterFilePath = "file:/root/Documents/data/dataengine/file/5/z9JRyK7vYCcIoXrc.tif";
        result = geoServerHttpUtils.createCoverageStore(testWorkspace, testRasterStore, rasterFilePath);
        log.info("创建栅格数据存储结果: {}", result);

        log.info("正在发布栅格图层 (Coverage): {}", testRasterStore);
        result = geoServerHttpUtils.createCoverage(testWorkspace, testRasterStore, testRasterStore);
        log.info("发布栅格图层结果: {}", result);
    }

    /**
     * 测试删除 GeoServer 中的资源，包括矢量图层、矢量数据存储、栅格图层、栅格数据存储和工作空间
     * 此方法主要用于清理测试环境中创建的资源，以确保环境的整洁和后续测试的准确性
     *
     * @throws IOException 如果与 GeoServer 的 HTTP 通信中发生错误
     */
    public void testRemove() throws IOException {
        // 删除矢量图层
        log.info("正在删除矢量图层...");
        String testWorkspace = "my_test_workspace";
        String testVectorStore = "carbon_Ze4xqPIZ6sXEJjxV";
        String result = geoServerHttpUtils.removeFeatureType(testWorkspace, testVectorStore, testVectorStore, true);
        log.info("删除矢量图层结果: {}", result);

        // 删除矢量数据存储
        log.info("正在删除矢量数据存储...");
        result = geoServerHttpUtils.removeStore(testWorkspace, testVectorStore, "datastores", true);
        log.info("删除矢量数据存储结果: {}", result);

        // 删除栅格图层
        log.info("正在删除栅格图层...");
        String testRasterStore = "z9JRyK7vYCcIoXrc";
        result = geoServerHttpUtils.removeCoverage(testWorkspace, testRasterStore, testRasterStore, true);
        log.info("删除栅格图层结果: {}", result);

        // 删除栅格数据存储
        log.info("正在删除栅格数据存储...");
        result = geoServerHttpUtils.removeStore(testWorkspace, testRasterStore, "coveragestores", true);
        log.info("删除栅格数据存储结果: {}", result);

        // 删除工作空间
        log.info("正在删除工作空间...");
        result = geoServerHttpUtils.removeWorkspace(testWorkspace, true);
        log.info("删除工作空间结果: {}", result);

        // 测试清理完成
        log.info(" 清理完成！测试结束。");
    }
}

