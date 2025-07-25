package com.urban.carbon.admin.domain.entity.convertor;

import com.urban.carbon.admin.domain.entity.Role;
import com.urban.carbon.api.admin.response.data.RoleInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoleConvertor {
    /**
     * 使用单例的模式进行使用
     */
    RoleConvertor INSTANCE = Mappers.getMapper(RoleConvertor.class);

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
     * @param request 需要转换成的 RoleInfo
     * @return 转换完成之后的对象
     */
    @Mapping(target = "id", source = "request.roleId")
    @Mapping(target = "rolePermission", source = "request.permissions", qualifiedByName = "listToString")
    Role mapToEntity(RoleInfo request);

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
