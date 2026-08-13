package com.aimms.modules.system.service.impl;

import com.aimms.modules.system.entity.SysUser;
import com.aimms.modules.system.mapper.SysUserMapper;
import com.aimms.modules.system.service.SysUserService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}
