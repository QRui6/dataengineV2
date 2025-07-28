package com.urban.carbon.personal.domain.entity.convertor;

import com.urban.carbon.api.admin.response.data.UserInfo;
import com.urban.carbon.personal.domain.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AccountConvertor {

    /**
     * 使用单例的模式进行使用
     */
    AccountConvertor INSTANCE = Mappers.getMapper(AccountConvertor.class);

    /**
     * 映射VO
     *
     * @param account 响应
     * @return 响应
     */
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "plainPassword", ignore = true)
    @Mapping(target = "userPermission", source = "account.userPermission", qualifiedByName = "stringToList")
    UserInfo mapToVo(Account account);

    @Named(value = "stringToList")
    default List<String> stringToList(String value) {
        if (value == null || value.isEmpty()) {
            return List.of(); // 返回空列表
        }
        return Arrays.asList(value.split(","));
    }
}
