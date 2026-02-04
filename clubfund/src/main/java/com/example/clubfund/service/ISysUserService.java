package com.example.clubfund.service;

import com.example.clubfund.dto.UserQueryDTO;
import com.example.clubfund.dto.UserUpdateDTO;
import com.example.clubfund.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.clubfund.common.Result;
import com.example.clubfund.dto.LoginDTO;
import com.example.clubfund.utils.UserContext;
import com.example.clubfund.vo.UserProfileVO;
import com.example.clubfund.dto.UserProfileUpdateDTO;
import org.springframework.beans.BeanUtils;

public interface ISysUserService extends IService<SysUser> {
    Result login(LoginDTO loginDTO);
    Result register(SysUser user);

    /**
     * 分页查询用户列表
     */
    Result selectUserPage(UserQueryDTO queryDTO);

    /**
     * 更新用户信息
     */
    Result updateUser(UserUpdateDTO updateDTO);

    /**
     * 重置密码
     */
    Result resetPassword(Long userId);

    /**
     * 删除用户
     */
    Result deleteUser(Long userId);

    SysUser getByUsername(String username);

    Result updateCurrentUserProfile(UserProfileUpdateDTO updateDTO);

    Result<UserProfileVO> getCurrentUserProfile();
}