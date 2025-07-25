package com.urban.carbon.user.domain.entity.convertor;

import com.urban.carbon.api.admin.response.data.UserInfo;
import com.urban.carbon.user.domain.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

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
    UserInfo mapToVo(Account account);
}
