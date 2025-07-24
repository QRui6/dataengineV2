package com.urban.carbon.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.urban.carbon.auth.exception.AuthErrorCode;
import com.urban.carbon.auth.exception.AuthException;
import com.urban.carbon.web.vo.Result;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.urban.carbon.cache.constant.CacheConstant.CACHE_KEY_SEPARATOR;

/**
 * @author bjcug
 */
@Slf4j
@RestController
@RequestMapping("/api/token")
public class TokenController {

    /**
     * 令牌前缀
     */
    private static final String TOKEN_PREFIX = "uc:";

    /**
     * Redis操作类
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 构造函数
     *
     * @param stringRedisTemplate Redis操作类
     */
    public TokenController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @GetMapping("/get")
    public Result<String> get(@NotBlank String scene) {
        if (StpUtil.isLogin()) {
            String token = UUID.randomUUID().toString();
            String tokenKey = TOKEN_PREFIX + scene + CACHE_KEY_SEPARATOR + token;
            stringRedisTemplate.opsForValue().set(tokenKey, token, 30, TimeUnit.MINUTES);
            return Result.success(tokenKey);
        }
        throw new AuthException(AuthErrorCode.USER_NOT_LOGIN);
    }
}
