package com.urban.carbon.user.domain.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.user.domain.entity.Account;
import com.urban.carbon.user.infrastructure.mapper.AccountMapper;
import org.springframework.stereotype.Service;

@Service
public class AccountService extends ServiceImpl<AccountMapper, Account> {

}
