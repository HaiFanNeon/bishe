package com.example.clubfund.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.clubfund.common.Result;
import com.example.clubfund.dto.LoginDTO;
import com.example.clubfund.entity.SysUser;
import com.example.clubfund.mapper.SysUserMapper;
import com.example.clubfund.service.ISysUserService;
import com.example.clubfund.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.clubfund.dto.UserQueryDTO;
import com.example.clubfund.dto.UserUpdateDTO;
import com.example.clubfund.utils.UserContext;
import com.example.clubfund.vo.UserProfileVO;
import com.example.clubfund.dto.UserProfileUpdateDTO;
import org.springframework.beans.BeanUtils;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Result login(LoginDTO loginDTO) {
        if (StrUtil.hasBlank(loginDTO.getUsername(), loginDTO.getPassword())) {
            return Result.error("用户名或密码不能为空");
        }

        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginDTO.getUsername());
        SysUser user = baseMapper.selectOne(queryWrapper);

        if (user == null || !user.getPassword().equals(loginDTO.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        // 生成Token
        String token = jwtUtils.generateToken(user.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user); // 返回用户信息供前端存储

        return Result.success(data);
    }

    @Override
    public Result register(SysUser user) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        if (baseMapper.selectCount(queryWrapper) > 0) {
            return Result.error("用户名已存在");
        }
        // 简单设置默认值
        if (user.getRole() == null) user.setRole(1); // 默认学生
        save(user);
        return Result.success("注册成功");
    }

    @Override
    public Result selectUserPage(UserQueryDTO queryDTO) {
        Page<SysUser> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();

        // 动态拼接查询条件
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            queryWrapper.and(w -> w.like("username", queryDTO.getKeyword())
                    .or().like("real_name", queryDTO.getKeyword()));
        }
        if (queryDTO.getRole() != null) {
            queryWrapper.eq("role", queryDTO.getRole());
        }

        // 按创建时间倒序
        queryWrapper.orderByDesc("create_time");

        Page<SysUser> resultPage = baseMapper.selectPage(page, queryWrapper);

        // 安全起见，返回列表时把密码置空
        resultPage.getRecords().forEach(u -> u.setPassword(null));

        return Result.success(resultPage);
    }

    @Override
    public Result updateUser(UserUpdateDTO updateDTO) {
        SysUser user = baseMapper.selectById(updateDTO.getId());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 更新字段
        user.setRealName(updateDTO.getRealName());
        user.setPhone(updateDTO.getPhone());
        user.setStudentId(updateDTO.getStudentId());
        user.setClubName(updateDTO.getClubName());
        baseMapper.updateById(user);
        return Result.success("更新成功");
    }

    @Override
    public Result resetPassword(Long userId) {
        SysUser user = baseMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 重置为默认密码 123456
        user.setPassword("123456");
        baseMapper.updateById(user);
        return Result.success("密码已重置为：123456");
    }

    @Override
    public Result deleteUser(Long userId) {
        // 逻辑删除
        baseMapper.deleteById(userId);
        return Result.success("删除成功");
    }

    /**
     * 获取当前登录用户的个人信息
     */
    public Result<UserProfileVO> getCurrentUserProfile() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }

        SysUser user = baseMapper.selectById(userId);
        UserProfileVO vo = new UserProfileVO();
        BeanUtils.copyProperties(user, vo);

        return Result.success(vo);
    }

    /**
     * 更新当前登录用户的个人信息
     */
    public Result updateCurrentUserProfile(UserProfileUpdateDTO updateDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }

        SysUser user = baseMapper.selectById(userId);

        // 仅更新允许修改的字段
        if (StrUtil.isNotBlank(updateDTO.getRealName())) user.setRealName(updateDTO.getRealName());
        if (StrUtil.isNotBlank(updateDTO.getPhone())) user.setPhone(updateDTO.getPhone());
        if (StrUtil.isNotBlank(updateDTO.getStudentId())) user.setStudentId(updateDTO.getStudentId());
        // 社团名称修改逻辑可视需求而定，这里暂且允许修改
        if (StrUtil.isNotBlank(updateDTO.getClubName())) user.setClubName(updateDTO.getClubName());

        baseMapper.updateById(user);
        return Result.success("个人信息更新成功");
    }

    @Override
    public SysUser getByUsername(String username) {
        return baseMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));
    }
}