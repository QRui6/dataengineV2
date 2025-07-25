package com.urban.carbon.admin.domain.entity.convertor;

import com.urban.carbon.admin.domain.entity.User;
import com.urban.carbon.api.admin.response.data.BasicUserInfo;
import com.urban.carbon.api.admin.response.data.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * User 与 UserInfo 之间的转换器. 添加 字符串 到 List 的相互转换
 *
 * @author bjcug
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
    UserInfo mapToVo(User request);

    /**
     * 转换为 VO
     *
     * @param request 需要转换的 User 列表
     * @return 转化玩的 UserInfo 列表
     */
    @Mapping(target = "userId", source = "request.id")
    @Mapping(target = "userPermission", source = "request.userPermission", qualifiedByName = "stringToList")
    List<UserInfo> mapToVo(List<User> request);

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
     * 转换为实体
     *
     * @param request 需要转换成的 UserInfo
     * @return 转换完成之后的对象
     */
    @Mapping(target = "id", source = "request.userId")
    @Mapping(target = "userPermission", source = "request.userPermission", qualifiedByName = "listToString")
    User mapToEntity(UserInfo request);

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

    /**
     * 转换为简单的VO
     *
     * @param request 需要转换的 User
     * @return 转换完成的 BasicUserInfo
     */
    @Mapping(target = "userId", source = "request.id")
    BasicUserInfo mapToBasicVo(User request);

}
