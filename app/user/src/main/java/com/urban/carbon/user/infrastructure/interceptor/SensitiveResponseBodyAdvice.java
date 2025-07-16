package com.urban.carbon.user.infrastructure.interceptor;

import com.github.houbb.sensitive.core.api.SensitiveUtil;
import com.urban.carbon.api.user.response.data.UserInfo;
import com.urban.carbon.web.vo.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collection;

/**
 * 信息脱敏拦截器
 * <p>
 * 该拦截器用于在响应体写入之前对特定类型的响应数据进行脱敏处理。
 * 主要针对 {@link Result} 类型的响应数据，如果数据包含 {@link UserInfo} 或 {@link Collection} 类型，
 * 则会对敏感信息进行脱敏处理。
 * </p>
 * <p>ResponseBodyAdvice 在 Spring MVC 处理 HTTP 响应时启动，用于在响应体写入客户端之前对响应内容进行自定义处理，
 * 如修改、脱敏等。它在控制器方法返回响应体之后、实际写入 HTTP 响应之前被调用。</p>
 *
 * @author XuGaoran
 */
@ControllerAdvice
public class SensitiveResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 判断是否支持对响应体进行处理
     * <p>
     * 该方法用于判断当前响应是否需要进行脱敏处理。只有当响应体的类型是 {@link Result} 的子类时，
     * 才会进行后续的脱敏处理。
     * </p>
     *
     * @param returnType    返回值的类型参数
     * @param converterType 用于转换响应体的 HTTP 消息转换器类型
     * @return 如果支持处理则返回 {@code true}，否则返回 {@code false}
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        // 只对特定类型的返回值执行处理逻辑，这里可以根据需要调整判断条件
        return Result.class.isAssignableFrom(returnType.getParameterType());
    }

    /**
     * 在响应体写入之前进行脱敏处理
     * <p>
     * 该方法在响应体写入之前对响应数据进行脱敏处理。具体处理逻辑如下：
     * <ol>
     *     <li>如果响应体是 {@link Result} 类型且数据不为 {@code null}，则进行进一步处理。</li>
     *     <li>如果数据是 {@link Collection} 类型，则对集合中的每个元素进行脱敏处理。</li>
     *     <li>如果数据是 {@link UserInfo} 类型，则对用户信息进行脱敏处理。</li>
     *     <li>其他类型的数据不做处理。</li>
     * </ol>
     * </p>
     *
     * @param body                  响应体对象
     * @param returnType            返回值的类型参数
     * @param selectedContentType   选择的媒体类型
     * @param selectedConverterType 选择的 HTTP 消息转换器类型
     * @param request               当前请求对象
     * @param response              当前响应对象
     * @return 处理后的响应体对象
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object beforeBodyWrite(
            @Nullable Object body, @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        // 如果返回的对象是Result类型，进行脱敏处理
        if (body instanceof Result<?> result) {
            // 如果 Result 中的数据为 null，直接返回原始 body
            if (result.getData() == null) {
                return body;
            }
            // 如果 Result 中的数据是 Collection 类型，对集合中的每个元素进行脱敏处理
            if (result.getData() instanceof Collection<?> collection) {
                Result<Collection<?>> resultWithCollection = (Result<Collection<?>>) result;
                resultWithCollection.setData(SensitiveUtil.desCopyCollection(collection));
                return resultWithCollection;
            }
            // 如果 Result 中的数据是 UserInfo 类型，对用户信息进行脱敏处理
            if (result.getData() instanceof UserInfo userInfo) {
                Result<UserInfo> resultWithUserInfo = (Result<UserInfo>) result;
                resultWithUserInfo.setData(SensitiveUtil.desCopy(userInfo));
                return resultWithUserInfo;
            }
        }
        // 如果不是 Result 类型或不匹配任何条件，直接返回原始 body
        return body;
    }
}
