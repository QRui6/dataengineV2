package com.urban.carbon.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.urban.carbon.user.domain.entity.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    /**
     * 根据id查询用户
     *
     * @param userId 用户id
     * @return 用户信息
     */
    Account findById(Long userId);

    /**
     * 根据手机号查询用户
     *
     * @param telephone 手机号
     * @return 用户信息
     */
    Account findByTelephone(String telephone);

    /**
     * 根据昵称查询用户
     *
     * @param nickName 昵称
     * @return 用户信息
     */
    Account findByNickname(String nickName);
}