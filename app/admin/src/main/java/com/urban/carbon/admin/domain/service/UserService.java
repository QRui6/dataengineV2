package com.urban.carbon.admin.domain.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.digest.DigestUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.template.QuickConfig;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.admin.domain.entity.Role;
import com.urban.carbon.admin.domain.entity.User;
import com.urban.carbon.admin.domain.entity.convertor.UserConvertor;
import com.urban.carbon.admin.infrastructure.mapper.UserMapper;
import com.urban.carbon.admin.infrastructure.mapper.RoleMapper;
import com.urban.carbon.api.admin.constants.UserOperateTypeEnum;
import com.urban.carbon.api.admin.constants.UserStateEnum;
import com.urban.carbon.api.admin.exception.RoleErrorCode;
import com.urban.carbon.api.admin.exception.RoleException;
import com.urban.carbon.api.admin.exception.UserException;
import com.urban.carbon.api.admin.exception.UserErrorCode;
import com.urban.carbon.api.admin.request.UserActiveRequest;
import com.urban.carbon.api.admin.request.UserRegisterRequest;
import com.urban.carbon.api.admin.response.data.UserInfo;
import com.urban.carbon.api.admin.request.UserModifyRequest;
import com.urban.carbon.api.data.manager.constants.SaveSoftType;
import com.urban.carbon.base.exception.BizException;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.PageResponse;
import com.urban.carbon.base.utils.RandomNameGenerator;
import com.urban.carbon.file.strategy.FileStrategy;
import com.urban.carbon.file.strategy.FileStrategyFactory;
import com.urban.carbon.lock.DistributeLock;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户服务类
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> implements InitializingBean {

    /**
     * 临时图片存储路径
     */
    private static final String TMP_PHOTO_PATH = System.getProperty("java.io.tmpdir");

    /**
     * 用户的 Mapper
     */
    private final UserMapper userMapper;

    /**
     * redisson 客户端
     */
    private final RedissonClient redissonClient;

    /**
     * 缓存管理工具
     */
    private final CacheManager cacheManager;

    /**
     * 角色的 Mapper 类
     */
    private final RoleMapper roleMapper;

    /**
     * 用户操作记录Service
     */
    private final UserOperateStreamService userOperateStreamService;

    /**
     * 用户缓存延迟删除相关服务
     */
    private final UserCacheDelayDeleteService userCacheDelayDeleteService;

    /**
     * 文件策略工厂
     */
    private final FileStrategyFactory fileStrategyFactory;

    /**
     * 构造方法注入
     *
     * @param userMapper                  userMapper
     * @param redissonClient              redisson client
     * @param cacheManager                cache manager
     * @param userOperateStreamService    user operate stream service
     * @param userCacheDelayDeleteService user cache delay delete service
     */
    public UserService(UserMapper userMapper, RedissonClient redissonClient,
                       CacheManager cacheManager, RoleMapper roleMapper,
                       UserOperateStreamService userOperateStreamService,
                       UserCacheDelayDeleteService userCacheDelayDeleteService, FileStrategyFactory fileStrategyFactory) {
        this.userMapper = userMapper;
        this.redissonClient = redissonClient;
        this.cacheManager = cacheManager;
        this.roleMapper = roleMapper;
        this.userOperateStreamService = userOperateStreamService;
        this.userCacheDelayDeleteService = userCacheDelayDeleteService;
        this.fileStrategyFactory = fileStrategyFactory;
    }

    /**
     * 用户名布隆过滤器
     */
    private RBloomFilter<String> nickNameBloomFilter;

    /**
     * <p>
     * 通过用户ID对用户信息做的缓存
     * <p>
     * Cache 是 JetCache 中的类, 项目中我们使用了 JetCache + Redisson 两级缓存
     * 所以这里的 Cache 是使用到了 Redis
     */
    private Cache<String, User> idUserCache;

    /**
     * UserService 类中的初始化方法。
     * <p>
     * 该方法使用 {@link PostConstruct} 注解，在 Spring 容器完成依赖注入后自动调用。
     * 主要用于初始化用户 ID 缓存（idUserCache），以便后续业务逻辑能够高效地访问缓存数据。
     * <p>
     * 具体功能如下：
     * <ol>
     * <li>创建一个名为 ":user:cache:id:" 的缓存配置对象（QuickConfig）。</li>
     * <li>设置缓存类型为 BOTH（可能表示同时使用本地缓存和分布式缓存）。</li>
     * <li>设置缓存过期时间为 2 小时。</li>
     * <li>启用本地缓存同步（syncLocal = true），确保本地缓存与分布式缓存保持一致。</li>
     * <li>通过 cacheManager 创建或获取对应的缓存实例，并将其赋值给 idUserCache。</li>
     * </ol>
     * <p>
     * 此缓存的设计目的是优化用户 ID 相关的数据访问性能，减少对底层存储（如数据库）的直接访问频率。
     */
    @PostConstruct
    public void init() {
        // 构建缓存配置对象，指定缓存名称、类型、过期时间和同步策略
        QuickConfig idQc = QuickConfig.newBuilder(":admin:user:cache:id:")
                .cacheType(CacheType.BOTH) // 设置缓存类型为本地和分布式缓存结合
                .expire(Duration.ofHours(2)) // 设置缓存过期时间为 2 小时
                .syncLocal(true) // 启用本地缓存同步
                .build();
        // 通过 cacheManager 获取或创建缓存实例，并赋值给 idUserCache
        idUserCache = cacheManager.getOrCreateCache(idQc);
    }

    /**
     * 实现自 {@link InitializingBean} 接口的初始化方法。
     * <p>
     * 该方法在 Spring 容器完成依赖注入后自动调用，且执行顺序在所有标记了 {@link PostConstruct} 注解的方法之后。
     * <p>
     * 具体功能如下：
     * <ol>
     *  <li>通过 Redisson 客户端获取名为 "nickName" 的布隆过滤器实例。</li>
     *  <li>检查布隆过滤器是否存在（{@code isExists()}）：</li>
     *  <ul>
     *      <li>如果布隆过滤器已存在，则直接使用。</li>
     *      <li>如果布隆过滤器不存在或尚未初始化，则尝试初始化一个布隆过滤器。</li>
     *  </ul>
     *  <li>布隆过滤器的初始化参数包括：
     *  <ul>
     *      <li>预期插入量：10000（即预计最多存储 10000 个元素）。</li>
     *      <li>误判率：0.01（即允许的最大误判率为 1%）。</li>
     *  </ul>
     *  </li>
     * </ol>
     * <p>
     * 布隆过滤器的设计目的是高效地判断昵称是否已存在，避免频繁查询底层存储（如数据库）。
     * 它特别适用于需要快速判断元素是否存在的场景，例如用户昵称的唯一性校验。
     * <p>
     * 执行顺序说明：
     * <ul>
     * <li>此方法会在 {@link PostConstruct} 注解的方法（如 {@code init()}）执行完毕后调用。</li>
     * <li>确保在调用此方法时，所有依赖（如 {@code redissonClient}）均已正确注入。</li>
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
     * 用户注册功能
     * <p>
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
    @SuppressWarnings("all")
    @DistributeLock(keyExpression = "#telephone", scene = "USER_REGISTER")
    @CacheInvalidate(name = ":admin:user:cache:telephone:", key = "#userRegisterRequest.telephone")
    @Transactional
    public OperateResponse<UserInfo> register(
            UserRegisterRequest userRegisterRequest, Long loginId) {
        String roleName = "ROLE_USER";
        Role role = roleMapper.findByRoleName(roleName);
        // 创建用户
        String defaultNickName;
        do {
            defaultNickName = RandomNameGenerator.generateRandomName(
                    roleName, userRegisterRequest.getTelephone());
        } while (nickNameExist(defaultNickName));
        // 核心注册代码
        User user = register(userRegisterRequest.getTelephone(), defaultNickName,
                userRegisterRequest.getPassword(), role.getId(), roleName, true);
        Assert.notNull(user, () -> new UserException(UserErrorCode.USER_OPERATE_FAILED));
        // 从读取出来的 role 中读取权限相关信息
        user.setUserPermission(role.getRolePermission());
        // 添加 昵称 到 布隆过滤器，同时更新用户缓存
        addNickName(defaultNickName);
        idUserCache.put(user.getId().toString(), user);
        // 将当前操作加入 用户操作记录表
        long streamResult = userOperateStreamService.insertStream(user, loginId, UserOperateTypeEnum.REGISTER);
        Assert.notNull(streamResult, () -> new BizException(UserErrorCode.USER_OPERATE_FAILED));
        // 创建 用户操作响应，并将结果设置成成功
        OperateResponse<UserInfo> userOperatorResponse = new OperateResponse<>();
        userOperatorResponse.setSuccess(true);
        userOperatorResponse.setData(UserConvertor.INSTANCE.mapToVo(user));
        // 返回响应结果
        return userOperatorResponse;
    }

    /**
     * 注册核心方法, 注意：一个手机号只能注册一个账户。
     *
     * @param telephone 电话号码
     * @param nickName  昵称
     * @param password  密码
     * @return 返回注册之后的用户信息
     */
    private User register(String telephone, String nickName, String password, Long roleId,
                          String roleName, Boolean mode) {
        User temp;
        try {
            temp = userMapper.findByTelephone(telephone);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return null;
        }
        if (temp != null) {
            throw new UserException(UserErrorCode.DUPLICATE_TELEPHONE_NUMBER);
        }
        User user = new User();
        user.register(telephone, nickName, password, roleId, roleName, mode);
        return save(user) ? user : null;
    }

    /**
     * 更新用户信息
     * <p>
     * 与上面激活方法相同, 由于响应速度要求并不严格, 这里使用注解的方式对缓存进行清除
     *
     * @param userModifyRequest 更新请求
     * @return 用户操作记录
     */
    @CacheInvalidate(name = ":admin:user:cache:id:", key = "#userModifyRequest.userId")
    @Transactional
    public OperateResponse<UserInfo> modify(UserModifyRequest userModifyRequest) {

        OperateResponse<UserInfo> userOperatorResponse = new OperateResponse<>();

        Long userId = userModifyRequest.getUserId();
        User user = userMapper.findById(userId);
        Assert.notNull(user, () -> new UserException(UserErrorCode.USER_NOT_EXIST));
        Assert.isTrue(user.canModifyInfo(), () -> new UserException(UserErrorCode.USER_STATUS_CANT_OPERATE));

        // 查询角色是否存在
        Role role = roleMapper.findByRoleName(userModifyRequest.getRoleName());
        Assert.notNull(role, () -> new RoleException(RoleErrorCode.ROLE_NOT_EXIST));
        user.setRoleId(role.getId());

        // 如果当前昵称已经存在，则不能使用该昵称
        if (StringUtils.isNotBlank(userModifyRequest.getNickName()) &&
                nickNameExist(userModifyRequest.getNickName())) {
            throw new UserException(UserErrorCode.NICK_NAME_EXIST);
        }

        // 如果手机号已存在且不是当前用户的手机号，则不能使用该手机号
        String telephone = userModifyRequest.getTelephone();
        if (StringUtils.isNotBlank(telephone) && !telephone.equals(user.getTelephone())) {
            User existUser = userMapper.findByTelephone(telephone);
            if (existUser != null && !existUser.getId().equals(userId)) {
                throw new UserException(UserErrorCode.DUPLICATE_TELEPHONE_NUMBER);
            }
        }

        // 如果密码不为空，则需要更新密码
        if (StringUtils.isNotBlank(userModifyRequest.getPassword())) {
            user.setPasswordHash(DigestUtil.md5Hex(userModifyRequest.getPassword()));
        }

        // 如果 InputStream 不为空，则需要更新用户头像
        if (userModifyRequest.getPhotoInputStream() != null) {
            user.setProfilePhotoUrl(uploadPhoto(userId, userModifyRequest.getPhotoInputStream()));
        }

        // 通过user的ID更新user
        if (updateById(user)) {
            // 加入流水
            long streamResult = userOperateStreamService.insertStream(
                    user, userId, UserOperateTypeEnum.MODIFY);
            Assert.notNull(streamResult, () -> new UserException(UserErrorCode.USER_UPDATE_FAILED));
            addNickName(userModifyRequest.getNickName());
            userOperatorResponse.setSuccess(true);
        } else {
            userOperatorResponse.setSuccess(false);
        }
        return userOperatorResponse;
    }

    /**
     * 上传用户头像图片
     *
     * @param photoInputStream 图片输入流
     * @return 上传后的文件访问路径
     */
    private String uploadPhoto(Long userId, InputStream photoInputStream) {
        // 获取文件存储策略（使用MINIO存储）
        FileStrategy strategy = fileStrategyFactory.getStrategy(SaveSoftType.MINIO.name());

        // 生成随机文件名
        String photo = RandomNameGenerator.generateRandomFileName(16, "png");
        String tmp_path = TMP_PHOTO_PATH + File.separator + photo;

        // 将输入流写入临时文件
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(tmp_path);
            fileOutputStream.write(photoInputStream.readAllBytes());
            // 上传临时文件并返回访问路径
            String dstPath = strategy.uploadFile(tmp_path, userId);
            // 删除临时文件
            Path path = Paths.get(tmp_path);
            Files.deleteIfExists(path);
            fileOutputStream.close();
            // 返回结果
            return dstPath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 用户激活
     * <p>
     * CacheInvalidate 注解的主要作用是根据指定的缓存名称和键值，从缓存中移除对应的数据 。
     * 通常在更新或删除数据时使用，以确保缓存与数据库或其他数据源保持一致性。
     * <p>
     * CacheInvalidate 注解被用于用户激活功能的实现。它的作用是清除与用户相关的缓存数据 ，
     * 以确保在用户激活操作完成后，系统能够从数据库或其他数据源重新加载最新的用户状态，
     * 而不是继续使用可能已经过期的缓存数据。
     * <p>
     * 在之后的方法中, 并没有使用该注解, 而是使用手动清除缓存的方式进行操作, 这是因为冻结与
     * 解冻的准确性, 响应速度要求较高, 但对于激活来说并没有很高的响应速度要求.
     *
     * @param userActiveRequest 激活请求
     * @return 返回用户操作结果
     */
    @CacheInvalidate(name = ":admin:user:cache:id:", key = "#userActiveRequest.userId")
    @Transactional
    public OperateResponse<UserInfo> active(UserActiveRequest userActiveRequest, Long loginId) {
        // 创建用户操作记录
        OperateResponse<UserInfo> userOperatorResponse = new OperateResponse<>();
        User user = userMapper.findById(userActiveRequest.getUserId());
        Assert.notNull(user, () -> new UserException(UserErrorCode.USER_NOT_EXIST));
        Assert.isTrue(user.getState() != UserStateEnum.FROZEN,
                () -> new UserException(UserErrorCode.USER_STATUS_CANT_OPERATE));
        // 用户激活
        user.setState(UserStateEnum.ACTIVE);
        boolean result = updateById(user);
        if (result) {
            // 随后加入流水
            long streamResult = userOperateStreamService.insertStream(user, loginId, UserOperateTypeEnum.ACTIVE);
            Assert.notNull(streamResult, () -> new BizException(UserErrorCode.USER_OPERATE_FAILED));
            userOperatorResponse.setSuccess(true);
            userOperatorResponse.setData(UserConvertor.INSTANCE.mapToVo(user));
        } else {
            userOperatorResponse.setSuccess(false);
        }
        return userOperatorResponse;
    }

    /**
     * 冻结
     *
     * @param userId 用户ID
     * @return 用户操作记录
     */
    @Transactional(rollbackFor = Exception.class)
    public OperateResponse<UserInfo> freeze(Long userId, Long loginId) {
        OperateResponse<UserInfo> userOperatorResponse = new OperateResponse<>();
        User user = userMapper.findById(userId);
        Assert.notNull(user, () -> new UserException(UserErrorCode.USER_NOT_EXIST));
        Assert.isTrue(user.getState() == UserStateEnum.ACTIVE,
                () -> new UserException(UserErrorCode.USER_STATUS_IS_ACTIVE));
        // 第一次删除缓存
        idUserCache.remove(user.getId().toString());
        if (user.getState() == UserStateEnum.FROZEN) {
            userOperatorResponse.setSuccess(true);
            return userOperatorResponse;
        }
        user.setState(UserStateEnum.FROZEN);
        boolean updateResult = updateById(user);
        Assert.isTrue(updateResult, () -> new BizException(UserErrorCode.USER_OPERATE_FAILED));
        // 加入流水
        long result = userOperateStreamService.insertStream(user, loginId, UserOperateTypeEnum.FREEZE);
        Assert.notNull(result, () -> new BizException(UserErrorCode.USER_OPERATE_FAILED));
        // 第二次删除缓存
        userCacheDelayDeleteService.delayedCacheDelete(idUserCache, user);
        userOperatorResponse.setSuccess(true);
        userOperatorResponse.setData(UserConvertor.INSTANCE.mapToVo(user));
        return userOperatorResponse;
    }

    /**
     * 解冻
     *
     * @param userId 用户ID
     * @return 操作记录
     */
    @Transactional(rollbackFor = Exception.class)
    public OperateResponse<UserInfo> unfreeze(Long userId, Long loginId) {
        OperateResponse<UserInfo> userOperatorResponse = new OperateResponse<>();
        User user = userMapper.findById(userId);
        Assert.notNull(user, () -> new UserException(UserErrorCode.USER_NOT_EXIST));
        // 第一次删除缓存
        idUserCache.remove(user.getId().toString());
        if (user.getState() == UserStateEnum.ACTIVE) {
            userOperatorResponse.setSuccess(true);
            return userOperatorResponse;
        }
        user.setState(UserStateEnum.ACTIVE);
        // 更新数据库
        boolean updateResult = updateById(user);
        Assert.isTrue(updateResult, () -> new BizException(UserErrorCode.USER_OPERATE_FAILED));
        // 加入流水
        long result = userOperateStreamService.insertStream(user, loginId, UserOperateTypeEnum.UNFREEZE);
        Assert.notNull(result, () -> new BizException(UserErrorCode.USER_OPERATE_FAILED));
        // 第二次删除缓存
        userCacheDelayDeleteService.delayedCacheDelete(idUserCache, user);
        userOperatorResponse.setSuccess(true);
        userOperatorResponse.setData(UserConvertor.INSTANCE.mapToVo(user));
        return userOperatorResponse;
    }

    /**
     * 分页查询用户信息，支持按角色、状态和用户名筛选
     *
     * @param userName    用户名关键词（匹配昵称）
     * @param state       状态
     * @param role        角色名称
     * @param currentPage 当前页
     * @param pageSize    页码
     * @return 返回分页查询结果
     */
    public PageResponse<User> pageQueryUsers(String userName, String state, String role,
                                             int currentPage, int pageSize) {
        Page<User> page = new Page<>(currentPage, pageSize);
        // 使用新的分页查询方法，包含角色权限信息
        IPage<User> userPage = userMapper.pageQueryUsersWithRole(page, userName, state, role);
        return PageResponse.of(userPage.getRecords(), (int) userPage.getTotal(), pageSize, currentPage);
    }

    /**
     * 通过手机号和密码查询用户信息, 登陆时主要使用的方法
     *
     * @param telephone 电话号码
     * @param password  密码 (明文)
     * @return 用户查询结果
     */
    @Cached(name = ":admin:user:cache:telephone:pass:", cacheType = CacheType.BOTH,
            key = "#telephone + ':' + #password", cacheNullValue = true)
    @CacheRefresh(refresh = 60, timeUnit = TimeUnit.MINUTES)
    public User findByTelephoneAndPass(String telephone, String password) {
        User byTelephone = findByTelephone(telephone);
        Assert.notNull(byTelephone, () -> new UserException(UserErrorCode.USER_NOT_EXIST));
        User byTelephoneAndPass = userMapper.findByTelephoneAndPass(
                telephone, DigestUtil.md5Hex(password));
        Assert.notNull(byTelephoneAndPass, () -> new UserException(UserErrorCode.USER_PASSWORD_ERROR));
        return byTelephoneAndPass;
    }

    /**
     * 通过手机号查询用户信息
     *
     * @param telephone 通过手机号查询用户信息
     * @return 用户信息
     */
    @Cached(name = ":admin:user:cache:telephone:", cacheType = CacheType.BOTH, key = "#telephone", cacheNullValue = true)
    @CacheRefresh(refresh = 60, timeUnit = TimeUnit.MINUTES)
    public User findByTelephone(String telephone) {
        return userMapper.findByTelephone(telephone);
    }

    /**
     * 通过用户ID查询用户信息, 先查本地缓存，再查redis，如果都没有才去数据库查询。
     *
     * @param userId 通过用户ID查询用户信息
     * @return 查询结果
     */
    @Cached(name = ":admin:user:cache:id:", cacheType = CacheType.BOTH, key = "#userId", cacheNullValue = true)
    @CacheRefresh(refresh = 60, timeUnit = TimeUnit.MINUTES)
    public User findById(Long userId) {
        return userMapper.findById(userId);
    }

    /**
     * 基于缓存的布隆过滤器，判断昵称是否重复
     *
     * @param nickName 昵称
     * @return boolean true表示有重复、false表示没有重复
     */
    public boolean nickNameExist(String nickName) {
        // 如果布隆过滤器中存在，再进行数据库二次判断。
        // 过滤器中存在不代表数据库中就真正存在，有可能会出现hash冲突的情况，所以需要进行二次判断
        if (this.nickNameBloomFilter != null && this.nickNameBloomFilter.contains(nickName)) {
            return userMapper.findByNickname(nickName) != null;
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

    /**
     * <p>
     * 创建用户功能
     * </p>
     *
     * <li>用户创建模块加入了 <strong>分布式锁</strong>，<strong>事务</strong> 的控制</li>
     * <li>在用户创建之后，账户为 ACTIVE 状态，可以直接使用</li>
     * <li>当用户需要创建的时候，需要提供用户名，手机号，角色，随后会判断当前是否存在这样的角色
     * 如果不存在则抛出角色不存在的错误，如果存在则开始进行用户创建的具体业务，如果当前手机号已经被注册，
     * 那么创建会失败，随后，将创建的信息写入 用户操作记录 表中，对信息进行持久化存储</li>
     *
     * @param name      用户名
     * @param telephone 电话号码
     * @param password  密码
     * @param roleName  角色名称
     * @param loginId   操作人ID
     * @return 用户操作结果
     */
    @SuppressWarnings("all")
    @DistributeLock(keyExpression = "#telephone", scene = "USER_CREATE")
    @Transactional
    public OperateResponse<UserInfo> createUser(
            String name, String telephone, String roleName, Long loginId) {
        // 查询角色是否存在
        Role role = roleMapper.findByRoleName(roleName);
        Assert.notNull(role, () -> new RoleException(RoleErrorCode.ROLE_NOT_EXIST));

        // 检查用户名是否已存在
        if (nickNameExist(name)) {
            throw new UserException(UserErrorCode.NICK_NAME_EXIST);
        }

        // 生成随机密码
        String randomPassword = RandomNameGenerator.generateRandomPassword();

        // 创建用户
        User user = register(telephone, name, randomPassword, role.getId(), roleName, false);
        Assert.notNull(user, () -> new UserException(UserErrorCode.USER_OPERATE_FAILED));

        // 从读取出来的 role 中读取权限相关信息
        user.setUserPermission(role.getRolePermission());

        // 添加 昵称 到 布隆过滤器，同时更新用户缓存
        addNickName(name);
        idUserCache.put(user.getId().toString(), user);

        // 将当前操作加入 用户操作记录表
        long streamResult = userOperateStreamService.insertStream(user, loginId, UserOperateTypeEnum.CREATE);
        Assert.notNull(streamResult, () -> new UserException(UserErrorCode.USER_CREATE_FAILED));

        // 创建 用户操作响应，并将结果设置成功
        OperateResponse<UserInfo> userOperatorResponse = new OperateResponse<>();
        userOperatorResponse.setSuccess(true);
        UserInfo userInfo = UserConvertor.INSTANCE.mapToVo(user);
        // 设置明文密码返回给前端
        userInfo.setPlainPassword(randomPassword);
        userOperatorResponse.setData(userInfo);

        // 返回响应结果
        return userOperatorResponse;
    }

    /**
     * 删除用户
     * <p>
     * 该方法首先检查用户是否存在，如果用户不存在则抛出异常。
     * <p>
     * 使用MyBatisPlus的removeById方法进行逻辑删除，该方法会自动处理@TableLogic注解的字段。
     * <p>
     * 如果删除失败，则抛出 UserErrorCode.USER_OPERATE_FAILED 异常。
     * <p>
     * 最后，将删除操作记录到操作表中。
     *
     * @param userId  用户ID
     * @param loginId 操作人ID
     * @return 用户操作结果
     */
    @CacheInvalidate(name = ":admin:user:cache:id:", key = "#userId")
    @Transactional(rollbackFor = Exception.class)
    public OperateResponse<UserInfo> deleteUser(Long userId, Long loginId) {
        // 查询用户是否存在
        User user = userMapper.findById(userId);
        Assert.notNull(user, () -> new UserException(UserErrorCode.USER_NOT_EXIST));
        // 使用MyBatisPlus的removeById方法进行逻辑删除，而不是手动设置deleted字段
        Assert.isTrue(removeById(userId), () -> new UserException(UserErrorCode.ACCOUNT_DELETE_FAIL));
        // 将删除操作记录到操作表中
        long streamResult = userOperateStreamService.insertStream(
                user, loginId, UserOperateTypeEnum.DELETE);
        Assert.notNull(streamResult, () -> new UserException(UserErrorCode.USER_OPERATE_FAILED));

        // 创建用户操作记录
        OperateResponse<UserInfo> userOperatorResponse = new OperateResponse<>();
        userOperatorResponse.setSuccess(true);
        userOperatorResponse.setData(UserConvertor.INSTANCE.mapToVo(user));

        // 返回操作结果
        return userOperatorResponse;
    }

    /**
     * 批量删除用户
     * <p>
     * 该方法接收一个用户ID列表，对列表中的每个用户ID执行逻辑删除操作。
     * <p>
     * 使用MyBatisPlus的removeByIds方法进行批量逻辑删除，该方法会自动处理@TableLogic注解的字段。
     * <p>
     * 如果删除失败，则抛出 UserErrorCode.USER_OPERATE_FAILED 异常。
     * <p>
     * 最后，将删除操作记录到操作表中。
     *
     * @param userIds 用户ID列表
     * @param loginId 操作人ID
     * @return 操作结果，包含被删除的用户信息列表
     */
    @Transactional(rollbackFor = Exception.class)
    public OperateResponse<List<UserInfo>> batchDeleteUsers(List<Long> userIds, Long loginId) {
        if (userIds == null || userIds.isEmpty()) {
            OperateResponse<List<UserInfo>> response = new OperateResponse<>();
            response.setSuccess(true);
            response.setData(new ArrayList<>());
            return response;
        }

        // 查询所有要删除的用户信息
        List<User> usersToDelete = new ArrayList<>();
        for (Long userId : userIds) {
            User user = userMapper.findById(userId);
            if (user != null) {
                usersToDelete.add(user);
                // 清除缓存
                idUserCache.remove(userId.toString());
            }
        }

        if (usersToDelete.isEmpty()) {
            OperateResponse<List<UserInfo>> response = new OperateResponse<>();
            response.setSuccess(true);
            response.setData(new ArrayList<>());
            return response;
        }

        // 使用MyBatisPlus的removeByIds方法进行批量逻辑删除
        boolean result = removeByIds(userIds);
        Assert.isTrue(result, () -> new UserException(UserErrorCode.ACCOUNT_DELETE_FAIL));

        // 将删除操作记录到操作表中
        long streamResult = userOperateStreamService.insertStream(
                usersToDelete, loginId, UserOperateTypeEnum.DELETE);
        Assert.notNull(streamResult, () -> new UserException(UserErrorCode.USER_OPERATE_FAILED));

        // 转换被删除的用户为UserInfo对象
        List<UserInfo> deletedUserInfos = usersToDelete.stream()
                .map(UserConvertor.INSTANCE::mapToVo)
                .collect(Collectors.toList());

        // 创建操作响应
        OperateResponse<List<UserInfo>> response = new OperateResponse<>();
        response.setSuccess(true);
        response.setData(deletedUserInfos);

        return response;
    }
}
