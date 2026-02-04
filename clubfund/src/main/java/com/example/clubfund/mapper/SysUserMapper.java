package com.example.clubfund.mapper;

import com.example.clubfund.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    SysUser selectOne();
    // 基础CRUD已由BaseMapper提供，如有复杂SQL可在此扩展
}