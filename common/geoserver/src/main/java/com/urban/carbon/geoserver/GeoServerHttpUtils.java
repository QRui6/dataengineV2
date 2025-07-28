package com.urban.carbon.geoserver;

import com.urban.carbon.geoserver.config.GeoServerProperties;
import com.urban.carbon.geoserver.entity.*;
import com.urban.carbon.geoserver.exception.GeoServerErrorCode;
import com.urban.carbon.geoserver.exception.GeoServerException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GeoServer 服务类，用于通过 HTTP REST API 与 GeoServer 进行交互。
 * 封装了工作空间、数据存储、图层等资源的增删改查操作。
 *
 * @author ChangBaorui
 * @since 0.0.2
 */
@Slf4j
public class GeoServerHttpUtils {

    /**
     * HTTP 客户端
     */
    private final HttpClientBuilder httpClientBuilder;

    /**
     * GeoServer 配置类
     */
    @Getter
    private final GeoServerProperties geoServerProperties; // 注入配置类

    /**
     *  接受参数的类型是 JSON
     */
    private static final String ACCEPT_TYPE_JSON = "application/json";

    /**
     * 内容的类型是 JSON
     */
    private static final String CONTENT_TYPE_JSON = "application/json";

    // 使用构造函数注入依赖
    public GeoServerHttpUtils(HttpClientBuilder httpClientBuilder,
                              GeoServerProperties geoServerProperties) {
        this.httpClientBuilder = httpClientBuilder;
        this.geoServerProperties = geoServerProperties;
    }

    /**
     * 获取所有工作空间
     */
    public List<String> getWorkspaces() throws IOException {
        String url = geoServerProperties.getRestServiceBaseUrl() + "/workspaces";
        String response = getRequest(url, ACCEPT_TYPE_JSON, CONTENT_TYPE_JSON, null);
        // TODO 如何切分相应, 同时, 需要确定是否需要进行错误的判断
        String[] split = response.split(",");
        return List.of(split);
    }

    /**
     * 创建工作空间
     *
     * @param workspaceName 工作空间名称
     */
    public String createWorkspace(String workspaceName) throws IOException {
        String workspaceUrl = geoServerProperties.getRestServiceBaseUrl() + "/workspaces";
        // 检查工作空间是否存在
        if (resourceExists(workspaceUrl + "/" + workspaceName)) {
            log.warn("WorkSpace {} already Exists.", workspaceName);
            return workspaceName;
        }
        // 如果不存在，则使用创建工作空间的集合 URL 进行 创建操作
        String resp = postRequest(workspaceUrl, ACCEPT_TYPE_JSON, CONTENT_TYPE_JSON,
                new WorkSpace(workspaceName).toString())
                .replace("\n", "").trim();
        if (resp.isBlank()) {
            throw new GeoServerException(GeoServerErrorCode.WORKSPACE_DELETE_FAILED);
        }
        log.info("Create WorkSpace {} Success!", workspaceName);
        return resp;
    }

    /**
     * 在指定工作空间中创建Shapefile数据存储
     *
     * @param workspaceName 工作空间名称
     * @param dataStoreName 数据存储名称
     * @param dataStorePath 数据存储路径
     * @return 创建的数据存储名称
     * @throws IOException 如果创建过程中发生I/O错误
     */
    public String createShapefileDataStore(String workspaceName, String dataStoreName, String dataStorePath)
            throws IOException {
        String dataStoreUrl = String.format("%s/workspaces/%s/datastores",
                geoServerProperties.getRestServiceBaseUrl(), workspaceName);
        // 检查数据存储是否存在
        if (resourceExists(dataStoreUrl + "/" + dataStoreName)) {
            log.warn("Data Store {}:{} Already Exists", workspaceName, dataStoreName);
            return dataStoreName;
        }
        String resp = postRequest(dataStoreUrl, ACCEPT_TYPE_JSON, CONTENT_TYPE_JSON,
                DataStore.buildDataStore(workspaceName, "Shapefile", true,
                        dataStoreName, dataStorePath, "", "",
                        "UTF-8", "", "", "",
                        "", ""))
                .replace("\n", "").trim();
        if (resp.isBlank()) {
            throw new GeoServerException(GeoServerErrorCode.DATASTORE_CREATE_FAILED);
        }
        log.info("Create Shapefile Data Stores {} Success!", dataStoreName);
        return resp;
    }

    /**
     * 创建栅格数据存储 (GeoTIFF)，已增加存在性检查。
     *
     * @param workspaceName 工作空间名称
     * @param storeName     栅格数据存储名
     * @param storePath     栅格文件路径
     */
    public String createCoverageStore(String workspaceName, String storeName, String storePath)
            throws IOException {
        String coverageStoreUrl = geoServerProperties.getRestServiceBaseUrl() +
                "/workspaces/" + workspaceName + "/coveragestores";
        if (resourceExists(coverageStoreUrl + "/" + storeName)) {
            log.warn("Coverage Store {}/{} Create Failed!", workspaceName, storeName);
            return storeName;
        }
        String resp = postRequest(coverageStoreUrl, ACCEPT_TYPE_JSON, CONTENT_TYPE_JSON,
                new CoverageStore(workspaceName, storeName, storePath).toString())
                .replace("\n", "").trim();
        if (resp.isBlank()) {
            throw new GeoServerException(GeoServerErrorCode.COVERAGE_STORE_CREATE_FAILED);
        }
        log.info("Coverage Store {}/{} Create Success.", workspaceName, storeName);
        return resp;
    }

    /**
     * 从数据存储创建 FeatureType (发布矢量图层)，增加存在性检查。
     *
     * @param workspaceName   工作空间名称
     * @param dataStoreName   数据存储名称
     * @param featureTypeName FeatureType 名称
     */
    public String createFeatureType(String workspaceName, String dataStoreName, String featureTypeName)
            throws IOException {
        String featureTypeUrl = geoServerProperties.getRestServiceBaseUrl() +
                "/workspaces/" + workspaceName + "/datastores/" + dataStoreName + "/featuretypes";
        if (resourceExists(featureTypeUrl + "/" + featureTypeName)) {
            log.warn("Shape File Layer {} Already Exists.", featureTypeName);
        }
        String resp = postRequest(featureTypeUrl, ACCEPT_TYPE_JSON, CONTENT_TYPE_JSON,
                new FeatureType(featureTypeName).toString())
                .replace("\n", "").trim();
        if (resp.isBlank()) {
            throw new GeoServerException(GeoServerErrorCode.FEATURE_TYPE_CREATE_FAILED);
        }
        log.info("Create FeatureType {} Success.", featureTypeName);
        return resp;
    }

    /**
     * 从栅格存储创建 Coverage (发布栅格图层)，加存在性检查。
     *
     * @param workspaceName     工作空间名称
     * @param coverageStoreName 栅格存储名称
     * @param coverageName      Coverage 名称
     */
    public String createCoverage(String workspaceName, String coverageStoreName, String coverageName)
            throws IOException {
        String coverageUrl = geoServerProperties.getRestServiceBaseUrl() +
                "/workspaces/" + workspaceName + "/coveragestores/" + coverageStoreName + "/coverages";
        if (resourceExists(coverageUrl + "/" + coverageName)) {
            log.warn("Coverage Layer {} Already Exists", coverageName);
            return coverageName;
        }
        String resp = postRequest(coverageUrl, ACCEPT_TYPE_JSON, CONTENT_TYPE_JSON,
                new CoverageConfig(coverageName).toString())
                .replace("\n", "").trim();
        if (resp.isBlank()) {
            throw new GeoServerException(GeoServerErrorCode.COVERAGE_CREATE_FAILED);
        }
        log.info("Coverage Layer {} Created Successfully", coverageName);
        return resp;
    }

    /**
     * 删除工作空间
     *
     * @param workspaceName 工作空间名称
     * @param recurse       是否递归删除 (删除工作空间下的所有内容)
     */
    public String removeWorkspace(String workspaceName, boolean recurse)
            throws IOException {
        String url = geoServerProperties.getRestServiceBaseUrl() + "/workspaces/" + workspaceName;
        if (!resourceExists(url)) {
            log.warn("WorkSpace {} not Exists!", workspaceName);
            return workspaceName;
        }
        String resp = getDeleteResponse(url, recurse);
        log.info("WorkSpace {} Deleted Success!", workspaceName);
        return resp;
    }


    /**
     * 删除数据存储 (矢量或栅格)
     *
     * @param workspaceName 工作空间名称
     * @param storeName     数据存储名称
     * @param storeType     "datastores" (矢量) or "coveragestores" (栅格)
     * @param recurse       是否递归删除
     */
    public String removeStore(String workspaceName, String storeName, String storeType, boolean recurse)
            throws IOException {
        String url = geoServerProperties.getRestServiceBaseUrl() + "/workspaces/" +
                workspaceName + "/" + storeType + "/" + storeName;
        if (!resourceExists(url)) {
            log.warn("Store '{}' does not exist, deletion failed.", storeName);
            return storeName;
        }
        String resp = getDeleteResponse(url, recurse);
        log.info("Store '{}' of type {} deleted successfully.", storeName, storeType);
        return resp;
    }

    /**
     * 删除 FeatureType (矢量图层)
     *
     * @param workspaceName   工作空间
     * @param dataStoreName   数据存储
     * @param featureTypeName 图层名
     * @param recurse         是否递归
     */
    public String removeFeatureType(String workspaceName, String dataStoreName,
                                    String featureTypeName, boolean recurse)
            throws IOException {
        String url = geoServerProperties.getRestServiceBaseUrl() +
                "/workspaces/" + workspaceName + "/datastores/" + dataStoreName +
                "/featuretypes/" + dataStoreName;
        if (!resourceExists(url)) {
            log.warn("FeatureType {} not Exists!", featureTypeName);
            return featureTypeName;
        }
        String resp = getDeleteResponse(url, recurse);
        log.info("FeatureType {} Deleted Success!", featureTypeName);
        return resp;
    }

    /**
     * 删除 Coverage (栅格图层)
     *
     * @param workspaceName     工作空间
     * @param coverageStoreName 栅格存储
     * @param coverageName      图层名
     * @param recurse           是否递归
     */
    public String removeCoverage(String workspaceName, String coverageStoreName,
                                 String coverageName, boolean recurse)
            throws IOException {
        String url = geoServerProperties.getRestServiceBaseUrl() +
                "/workspaces/" + workspaceName + "/coveragestores/" + coverageStoreName +
                "/coverages/" + coverageName;
        if (!resourceExists(url)) {
            log.warn("Coverage {} not Exists!", coverageName);
            return coverageName;
        }
        String resp = getDeleteResponse(url, recurse);
        log.info("Coverage {} Deleted Success!", coverageName);
        return resp;
    }

    /**
     * 发送删除请求并获取响应
     *
     * @param url     待删除资源的URL
     * @param recurse 是否递归删除如果为true，将递归删除指定资源下的所有子资源
     * @return 删除操作的响应内容
     * @throws IOException        如果在执行HTTP请求过程中发生I/O错误
     * @throws GeoServerException 如果删除操作失败
     */
    private String getDeleteResponse(String url, boolean recurse) throws IOException {
        // 创建参数映射，用于指定删除操作是否递归
        Map<String, String> params = new HashMap<>();
        params.put("recurse", String.valueOf(recurse));

        // 执行删除请求，并移除响应中的换行和空白字符，以简化响应内容格式
        String resp = deleteRequest(url, ACCEPT_TYPE_JSON, CONTENT_TYPE_JSON, params)
                .replace("\n", "").trim();

        // 如果响应内容为空，则抛出异常，指示删除操作失败
        if (resp.isBlank()) {
            throw new GeoServerException(GeoServerErrorCode.DATASTORE_DELETE_FAILED);
        }

        // 返回删除操作的响应内容
        return resp;
    }

    /**
     * 判断的 GeoServer 资源是否存在。
     *
     * @param resourceUrl 资源的完整 REST URL
     * @return 如果资源存在（HTTP 200 OK）则返回 true，否则返回 false。
     */
    private boolean resourceExists(String resourceUrl) throws IOException {
        String request = this.getRequest(resourceUrl, null, null, null);
        return request != null;
    }

    /**
     * 发送 GET 请求。
     *
     * @param url         请求的 URL
     * @param acceptType  接受的类型
     * @param contentType 发送的请求内容类型
     * @param params      请求参数
     * @return 响应结果
     */
    public String getRequest(String url, String acceptType, String contentType,
                             @Nullable Map<String, String> params) throws IOException {
        String finalUrl = construct_url(url, params);
        HttpGet request = new HttpGet(finalUrl);
        request.setHeader("Accept", acceptType);
        request.setHeader("Content-Type", contentType);
        return executeRequest(finalUrl, request);

    }

    /**
     * 发送 POST 请求
     *
     * @param url         请求的 URL
     * @param acceptType  响应的 Content-Type
     * @param contentType 请求的 Content-Type
     * @param requestBody 请求的 body
     * @return 响应结果
     * @throws UnsupportedEncodingException 不支持的编码
     */
    public String postRequest(String url, String acceptType, String contentType, String requestBody)
            throws IOException {
        HttpPost request = new HttpPost(url);
        request.setHeader("Accept", acceptType);
        request.setHeader("Content-Type", contentType);
        request.setEntity(new StringEntity(requestBody));
        return executeRequest(url, request);
    }

    /**
     * 发送 PUT 请求
     *
     * @param url         请求地址
     * @param acceptType  接受类型
     * @param contentType 请求类型
     * @param requestBody 请求体
     * @return 响应结果
     * @throws UnsupportedEncodingException 编码异常
     */
    public String putRequest(String url, String acceptType, String contentType, String requestBody)
            throws IOException {
        HttpPut request = new HttpPut(url);
        request.setHeader("Accept", acceptType);
        request.setHeader("Content-Type", contentType);
        request.setEntity(new StringEntity(requestBody));
        return executeRequest(url, request);
    }

    /**
     * 发送 DELETE 请求
     *
     * @param url         请求地址
     * @param acceptType  接受类型
     * @param contentType 请求类型
     * @return 响应结果
     */
    public String deleteRequest(String url, String acceptType, String contentType,
                                @Nullable Map<String, String> params) throws IOException {
        String finalUrl = construct_url(url, params);
        HttpDelete request = new HttpDelete(finalUrl);
        request.setHeader("Accept", acceptType);
        request.setHeader("Content-Type", contentType);
        return executeRequest(finalUrl, request);
    }

    /**
     * 根据基础URL和参数构造完整的URL
     * 如果提供了参数，则将这些参数附加到基础URL上，形成最终的请求URL
     *
     * @param url    基础URL，用于构建最终的请求URL
     * @param params 附加参数，将被附加到基础URL的末尾如果不为空，则构建带有参数的URL
     * @return 构建完成的URL字符串，如果参数为空，则返回原始URL
     */
    private static String construct_url(String url, Map<String, String> params) {
        String finalUrl;
        if (params != null && !params.isEmpty()) {
            StringBuilder builder = new StringBuilder(url);
            builder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            finalUrl = builder.toString();
        } else {
            finalUrl = url;
        }
        return finalUrl;
    }

    /**
     * 执行HTTP请求并处理响应
     * 该方法使用给定的HTTP请求执行一个资源的获取操作，并根据响应状态码处理结果
     * 如果响应状态码表示请求成功（200-299），则返回响应的实体内容作为字符串
     * 如果响应状态码不在成功范围内，则记录错误日志并抛出异常
     *
     * @param resourceUrl 资源的URL，用于记录错误日志
     * @param request     准备执行的HTTP请求对象
     * @return 成功时返回响应的实体内容字符串
     * @throws IOException        如果在执行请求过程中发生I/O错误
     * @throws GeoServerException 如果响应状态码不在成功范围内，表示请求失败
     */
    private String executeRequest(String resourceUrl, BasicClassicHttpRequest request) throws IOException {
        // 创建并使用一个CloseableHttpClient对象执行请求
        try (CloseableHttpClient client = httpClientBuilder.build()) {
            // 执行请求并处理响应
            return client.execute(request, (ClassicHttpResponse response) -> {
                // 获取响应状态码
                int statusCode = response.getCode();
                // 检查响应状态码是否表示成功
                if (statusCode >= 200 && statusCode < 300) {
                    // 如果成功，返回响应的实体内容作为字符串
                    return EntityUtils.toString(response.getEntity());
                } else {
                    // 如果不成功，记录错误日志并抛出异常
                    log.error("Url {} Get Unexpected status code: {}", resourceUrl, statusCode);
                    throw new GeoServerException("检查资源存在性时出错，状态码: " + statusCode,
                            GeoServerErrorCode.GEOSERVER_REQUEST_FAILED);
                }
            });
        }
    }
}

