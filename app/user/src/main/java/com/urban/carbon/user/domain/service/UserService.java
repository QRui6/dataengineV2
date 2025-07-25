package com.urban.carbon.user.domain.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.digest.DigestUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.template.QuickConfig;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.lock.DistributeLock;
import com.urban.carbon.user.domain.entity.Account;
import com.urban.carbon.user.domain.entity.Role;
import com.urban.carbon.user.domain.entity.convertor.UserConvertor;
import com.urban.carbon.user.infrastructure.mapper.AccountMapper;
import com.urban.carbon.api.admin.constants.UserOperateTypeEnum;
import com.urban.carbon.api.user.exception.UserErrorCode;
import com.urban.carbon.api.user.exception.UserException;
import com.urban.carbon.api.user.response.data.UserInfo;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.utils.RandomNameGenerator;
import com.urban.carbon.cache.constant.CacheConstant;
import com.urban.carbon.user.infrastructure.mapper.RoleMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserService extends ServiceImpl<AccountMapper, Account> implements InitializingBean {

    /**
     * 角色服务
     */
    private final RoleMapper roleMapper;

    /**
     * 用户的 Mapper
     */
    private final AccountMapper accountMapper;

    /**
     * redisson 客户端
     */
    private final RedissonClient redissonClient;

    /**
     * 缓存管理工具
     */
    private final CacheManager cacheManager;

    /**
     * 用户操作记录Service
     */
    private final UserOperateStreamService userOperateStreamService;

    /**
     * 用户缓存延迟删除相关服务
     */
    private final CacheDelayDeleteService cacheDelayDeleteService;

    /**
     * 构造方法注入
     *
     * @param accountMapper                  userMapper
     * @param redissonClient              redisson client
     * @param cacheManager                cache manager
     * @param userOperateStreamService    user operate stream service
     * @param cacheDelayDeleteService     user cache delay delete service
     */
    public UserService(AccountMapper accountMapper, RedissonClient redissonClient,
                       CacheManager cacheManager, RoleMapper roleMapper,
                       UserOperateStreamService userOperateStreamService,
                       CacheDelayDeleteService cacheDelayDeleteService) {
        this.accountMapper = accountMapper;
        this.redissonClient = redissonClient;
        this.cacheManager = cacheManager;
        this.roleMapper = roleMapper;
        this.userOperateStreamService = userOperateStreamService;
        this.cacheDelayDeleteService = cacheDelayDeleteService;
    }

    /**
     * 用户名布隆过滤器
     */
    private RBloomFilter<String> nickNameBloomFilter;

    /**
     * <p>通过用户ID对用户信息做的缓存</p>
     *
     * <p>Cache 是 JetCache 中的类, 项目中我们使用了 JetCache + Redisson 两级缓存
     * 所以这里的 Cache 是使用到了 Redis </p>
     */
    private Cache<String, Account> idUserCache;

    /**
     * UserService 类中的初始化方法。
     * <p>该方法使用 {@link PostConstruct} 注解，在 Spring 容器完成依赖注入后自动调用。
     * 主要用于初始化用户 ID 缓存（idUserCache），以便后续业务逻辑能够高效地访问缓存数据。</p>
     * <p>具体功能如下：
     * <ol>
     *   <li>创建一个名为 ":user:cache:id:" 的缓存配置对象（QuickConfig）。</li>
     *   <li>设置缓存类型为 BOTH（可能表示同时使用本地缓存和分布式缓存）。</li>
     *   <li>设置缓存过期时间为 2 小时。</li>
     *   <li>启用本地缓存同步（syncLocal = true），确保本地缓存与分布式缓存保持一致。</li>
     *   <li>通过 cacheManager 创建或获取对应的缓存实例，并将其赋值给 idUserCache。</li>
     * </ol>
     * <p>此缓存的设计目的是优化用户 ID 相关的数据访问性能，减少对底层存储（如数据库）的直接访问频率。</p>
     */
    @PostConstruct
    public void init() {
        // 构建缓存配置对象，指定缓存名称、类型、过期时间和同步策略
        QuickConfig idQc = QuickConfig.newBuilder(CacheConstant.USER_CACHE_KEY_PREFIX)
                .cacheType(CacheType.BOTH) // 设置缓存类型为本地和分布式缓存结合
                .expire(Duration.ofHours(2)) // 设置缓存过期时间为 2 小时
                .syncLocal(true) // 启用本地缓存同步
                .build();
        // 通过 cacheManager 获取或创建缓存实例，并赋值给 idUserCache
        idUserCache = cacheManager.getOrCreateCache(idQc);
    }

    /**
     * 实现自 {@link InitializingBean} 接口的初始化方法。
     * <p>该方法在 Spring 容器完成依赖注入后自动调用，且执行顺序在所有标记了 {@link PostConstruct} 注解的方法之后。</p>
     * <p>具体功能如下：
     * <ol>
     *   <li>通过 Redisson 客户端获取名为 "nickName" 的布隆过滤器实例。</li>
     *   <li>检查布隆过滤器是否存在（{@code isExists()}）：</li>
     *   <ul>
     *     <li>如果布隆过滤器已存在，则直接使用。</li>
     *     <li>如果布隆过滤器不存在或尚未初始化，则尝试初始化一个布隆过滤器。</li>
     *   </ul>
     *   <li>布隆过滤器的初始化参数包括：
     *     <ul>
     *       <li>预期插入量：10000（即预计最多存储 10000 个元素）。</li>
     *       <li>误判率：0.01（即允许的最大误判率为 1%）。</li>
     *     </ul>
     *   </li>
     * </ol>
     * <p>布隆过滤器的设计目的是高效地判断昵称是否已存在，避免频繁查询底层存储（如数据库）。
     * 它特别适用于需要快速判断元素是否存在的场景，例如用户昵称的唯一性校验。</p>
     * <p>执行顺序说明：
     * <ul>
     *   <li>此方法会在 {@link PostConstruct} 注解的方法（如 {@code init()}）执行完毕后调用。</li>
     *   <li>确保在调用此方法时，所有依赖（如 {@code redissonClient}）均已正确注入。</li>
     * </ul>
     */
    @Override
    public void afterPropertiesSet() {
        // 使用 Redisson 客户端获取布隆过滤器，名称为 nickName
        this.nickNameBloomFilter = redissonClient.getBloomFilter("nickName");

        // 如果过滤器不存在，或者返回为 null，就尝试初始化过滤器
        if (nickNameBloomFilter != null && !nickNameBloomFilter.isExists()) {
            this.nickNameBloomFilter.tryInit(10000L, 0.01);
        }
    }

    /**
     * <p>用户注册功能</p>
     *
     * <li>用户注册模块加入了 <strong>分布式锁</strong>，<strong>事务</strong> 的控制</li>
     * <li>在用户注册之后，账户为 INIT 状态， INIT状态的用户需要拥有 <strong>用户管理权限</strong>
     * 的用户进行激活，随后才能进行使用。</li>
     * <li>当用户需要注册的时候，需要提供手机号，密码，想要注册的角色，随后会判断当前是否存在这样的角色
     * 如果不存在则抛出角色不存在的错误，如果存在则开始进行用户注册的具体业务，如果当前手机号已经被注册，
     * 那么注册会失败，随后，将注册的信息写入 用户操作记录 表中，对信息进行持久化存储</li>
     *
     * @param telephone 电话号码
     * @param password  密码
     * @param roleId    角色ID
     * @return 用户操作结果
     */
    @DistributeLock(keyExpression = "#telephone", scene = "USER_REGISTER")
    @Transactional
    public OperateResponse<UserInfo> createAccount(String telephone, String password, Role role) {
        // 创建用户
        String defaultNickName;
        do {
            defaultNickName = RandomNameGenerator.generateRandomName(role.getRoleName(), telephone);
        } while (nickNameExist(defaultNickName));
        // 核心注册代码
        Account user = register(telephone, defaultNickName, password, role.getId(), role.getRoleName());
        if (user == null) {
            throw new UserException(UserErrorCode.USER_CREATE_FAILED);
        }
        // 从读取出来的 role 中读取权限相关信息
        user.setUserPermission(role.getRolePermission());
        // 添加 昵称 到 布隆过滤器，同时更新用户缓存
        addNickName(defaultNickName);
        idUserCache.put(user.getId().toString(), user);
        // 将当前操作加入 用户操作记录表
        long streamResult = userOperateStreamService.insertStream(user, UserOperateTypeEnum.REGISTER);
        Assert.notNull(streamResult, () -> new UserException(UserErrorCode.USER_CREATE_FAILED));
        // 创建 用户操作响应，并将结果设置成成功
        OperateResponse<UserInfo> userOperatorResponse = new OperateResponse<>();
        userOperatorResponse.setSuccess(true);
        userOperatorResponse.setData(UserConvertor.INSTANCE.mapToVo(user));
        // 返回响应结果
        return userOperatorResponse;
    }

    /**
     * <p>更新用户信息</p>
     * <p>与上面激活方法相同, 由于响应速度要求并不严格, 这里使用注解的方式对缓存进行清除</p>
     *
     * @param userId 用户ID
     * @param nickName 昵称
     * @param telephone 手机号
     * @param password 密码
     * @param profilePhotoUrl 头像
     * @return 响应结果
     */
    @CacheInvalidate(name = ":user:cache:id:", key = "#userModifyRequest.userId")
    @Transactional
    public OperateResponse<UserInfo> modifyAccount(Long userId, String nickName, String telephone,
                                                   String password, String profilePhotoUrl) {
        OperateResponse<UserInfo> userOperatorResponse = new OperateResponse<>();
        Account user = accountMapper.findById(userId);
        Assert.notNull(user, () -> new UserException(UserErrorCode.USER_NOT_EXIST));
        Assert.isTrue(user.canModifyInfo(), () -> new UserException(UserErrorCode.USER_STATUS_CANT_OPERATE));
        user.modifyInfo(nickName, telephone, password, profilePhotoUrl);
        // 如果密码不为空，则需要更新密码
        if (StringUtils.isNotBlank(password)) {
            user.setPasswordHash(DigestUtil.md5Hex(password));
        }
        // 通过user的ID更新user
        if (updateById(user)) {
            // 加入流水
            long streamResult = userOperateStreamService.insertStream(user, UserOperateTypeEnum.MODIFY);
            Assert.notNull(streamResult, () -> new UserException(UserErrorCode.USER_OPERATE_FAILED));
            userOperatorResponse.setSuccess(true);
            return userOperatorResponse;
        }
        userOperatorResponse.setSuccess(false);
        userOperatorResponse.setResponseCode(UserErrorCode.USER_OPERATE_FAILED.getCode());
        userOperatorResponse.setResponseMessage(UserErrorCode.USER_OPERATE_FAILED.getMessage());
        return userOperatorResponse;
    }

    /**
     * 删除用户
     * <p>该方法首先检查用户是否存在，如果用户不存在则抛出异常。</p>
     * <p>如果用户存在，则将用户的 deleted 字段设置为 1，并使用 updateById 方法进行更新。</p>
     * <p>如果更新失败，则抛出 RepoErrorCode.INSERT_FAIL 异常。</p>
     * <p>最后，将删除操作记录到操作表中。</p>
     *
     * @param userId 用户ID
     * @return 用户操作结果
     */
    @CacheInvalidate(name = ":user:cache:id:", key = "#userId")
    @Transactional(rollbackFor = Exception.class)
    public OperateResponse<UserInfo> deleteAccount(Long userId) {
        // 查询用户是否存在
        Account user = accountMapper.findById(userId);
        Assert.notNull(user, () -> new UserException(UserErrorCode.USER_NOT_EXIST));
        OperateResponse<UserInfo> userOperatorResponse = new OperateResponse<>();
        if (accountMapper.deleteById(user) > 0) {
            // 将删除操作记录到操作表中
            long streamResult = userOperateStreamService.insertStream(user, UserOperateTypeEnum.DELETE);
            Assert.notNull(streamResult, () -> new UserException(UserErrorCode.ACCOUNT_DELETE_FAIL));
            // 创建用户操作记录
            userOperatorResponse.setSuccess(true);
            userOperatorResponse.setData(UserConvertor.INSTANCE.mapToVo(user));
        } else {
            userOperatorResponse.setSuccess(false);
        }
        // 返回操作结果
        return userOperatorResponse;
    }

    /**
     * 查询用户
     *
     * @param userId 用户ID
     * @return 删除结果
     */
    @Cached(name = CacheConstant.USER_CACHE_KEY_PREFIX, cacheType = CacheType.BOTH,
            key = "#userId", cacheNullValue = true)
    @CacheRefresh(refresh = 60, timeUnit = TimeUnit.MINUTES)
    public Account findById(Long userId) {
        return accountMapper.findById(userId);
    }

    /**
     * 通过手机号查询用户信息
     *
     * @param telephone 通过手机号查询用户信息
     * @return 用户信息
     */
    public Account findByTelephone(String telephone) {
        return accountMapper.findByTelephone(telephone);
    }

    /**
     * 注册核心方法, 注意：一个手机号只能注册一个账户。
     *
     * @param telephone 电话号码
     * @param nickName  昵称
     * @param password  密码
     * @return 返回注册之后的用户信息
     */
    private Account register(String telephone, String nickName, String password,
                             Long roleId, String roleName) {
        Account temp;
        try {
            temp = accountMapper.findByTelephone(telephone);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return null;
        }
        Assert.notNull( temp, () -> new UserException(UserErrorCode.DUPLICATE_TELEPHONE_NUMBER));
        Account user = new Account();
        user.register(telephone, nickName, password, roleId, roleName);
        return save(user) ? user : null;
    }

    /**
     * 基于缓存的布隆过滤器，判断昵称是否重复
     *
     * @param nickName 昵称
     * @return boolean true表示有重复、false表示没有重复
     */
    private boolean nickNameExist(String nickName) {
        // 如果布隆过滤器中存在，再进行数据库二次判断。
        // 过滤器中存在不代表数据库中就真正存在，有可能会出现hash冲突的情况，所以需要进行二次判断
        if (this.nickNameBloomFilter != null && this.nickNameBloomFilter.contains(nickName)) {
            return accountMapper.findByNickname(nickName) != null;
        }
        return false;
    }

    /**
     * 将昵称加入到Boolean过滤器中
     *
     * @param nickName 昵称
     */
    private void addNickName(String nickName) {
        if (this.nickNameBloomFilter != null) {
            this.nickNameBloomFilter.add(nickName);
        }
    }
}
