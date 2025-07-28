package com.urban.carbon.personal.domain.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.personal.domain.entity.Account;
import com.urban.carbon.personal.infrastructure.mapper.AccountMapper;
import org.springframework.stereotype.Service;

@Service
public class AccountService extends ServiceImpl<AccountMapper, Account> {

}
