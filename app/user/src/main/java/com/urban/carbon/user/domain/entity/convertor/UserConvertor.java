package com.urban.carbon.user.domain.entity.convertor;

import com.urban.carbon.user.domain.entity.Account;
import com.urban.carbon.user.domain.entity.Role;
import com.urban.carbon.api.user.response.data.RoleInfo;
import com.urban.carbon.api.user.response.data.BasicUserInfo;
import com.urban.carbon.api.user.response.data.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * User 与 UserInfo 之间的转换器.
 * Role 与 RoleInfo 之间的转换器.
 * 添加 字符串 到 List 的相互转换
 *
 * @author XuGaoran
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserConvertor {

    /**
     * 使用单例的模式进行使用
     */
    UserConvertor INSTANCE = Mappers.getMapper(UserConvertor.class);

    /**
     * 转换为 VO, User/UserInfo两个类之间转换
     *
     * @param request 需要转换的 User
     * @return 转换完成的 UserInfo
     */
    @Mapping(target = "userId", source = "request.id")
    @Mapping(target = "userPermission", source = "request.userPermission", qualifiedByName = "stringToList")
    UserInfo mapToVo(Account request);

    /**
     * 转换为 VO
     *
     * @param request 需要转换的 User 列表
     * @return 转化玩的 UserInfo 列表
     */
    @Mapping(target = "userId", source = "request.id")
    @Mapping(target = "userPermission", source = "request.userPermission", qualifiedByName = "stringToList")
    List<UserInfo> mapToVo(List<Account> request);

    /**
     * 转换为 VO, Role/RoleInfo两个类之间转换
     *
     * @param request 需要转换的 Role
     * @return 转换完成的 RoleInfo
     */
    @Mapping(target = "roleId", source = "request.id")
    @Mapping(target = "permissions", source = "request.rolePermission", qualifiedByName = "stringToList")
    RoleInfo mapToVo(Role request);

    /**
     * 转换为 VO
     *
     * @param request 需要转换的 Role 列表
     * @return 转化玩的 RoleInfo 列表
     */
    @Mapping(target = "roleId", source = "request.id")
    @Mapping(target = "permissions", source = "request.rolePermission", qualifiedByName = "stringToList")
    List<RoleInfo> listMapToVo(List<Role> request);

    /**
     * 转换为实体
     *
     * @param request 需要转换成的 UserInfo
     * @return 转换完成之后的对象
     */
    @Mapping(target = "id", source = "request.userId")
    @Mapping(target = "userPermission", source = "request.userPermission", qualifiedByName = "listToString")
    Account mapToEntity(UserInfo request);

    /**
     * 转换为简单的VO
     *
     * @param request 需要转换的 User
     * @return 转换完成的 BasicUserInfo
     */
    @Mapping(target = "userId", source = "request.id")
    BasicUserInfo mapToBasicVo(Account request);

    /**
     * 将数据中的String按照要求进行分割
     *
     * @param value 需要处理的数值
     * @return 返回处理好的列表
     */
    @Named(value = "stringToList")
    default List<String> stringToList(String value) {
        if (value == null || value.isEmpty()) {
            return List.of(); // 返回空列表
        }
        return Arrays.asList(value.split(","));
    }

    /**
     * 将列表使用逗号进行连接
     *
     * @param value 列表
     * @return 组装好的字符串
     */
    @Named(value = "listToString")
    default String listToString(List<String> value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        StringJoiner stringJoiner = new StringJoiner(",", "", "");
        for (String str : value) {
            stringJoiner.add(str);
        }
        return stringJoiner.toString();
    }

}
