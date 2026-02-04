package com.example.clubfund.controller;

import com.example.clubfund.common.Result;
import com.example.clubfund.dto.*;
import com.example.clubfund.entity.SysUser;
import com.example.clubfund.service.IReimbursementService;
import com.example.clubfund.service.ISysUserService;
import com.example.clubfund.vo.UserProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IReimbursementService reimburseService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    @PostMapping("/register")
    public Result register(@RequestBody SysUser user) {
        return userService.register(user);
    }

    @GetMapping("/info")
    public Result getUserInfo(@RequestParam String username) {
        return Result.success();
    }

    /**
     * 分页获取用户列表 (管理员)
     */
    @PostMapping("/list")
    public Result list(@RequestBody UserQueryDTO queryDTO) {
        return userService.selectUserPage(queryDTO);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result update(@RequestBody UserUpdateDTO updateDTO) {
        return userService.updateUser(updateDTO);
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPwd/{id}")
    public Result resetPwd(@PathVariable Long id) {
        return userService.resetPassword(id);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    /**
     * 获取当前用户信息 (用户端/管理端通用)
     */
    @GetMapping("/profile")
    public Result<UserProfileVO> getProfile() {
        // 不需要传参数，Service内部自动从 UserContext 获取 ID
        return userService.getCurrentUserProfile();
    }

    /**
     * 修改当前用户信息
     */
    @PutMapping("/profile")
    public Result updateProfile(@RequestBody UserProfileUpdateDTO updateDTO) {
        return userService.updateCurrentUserProfile(updateDTO);
    }

    /**
     * 用户端：提交报销申请
     * 前端只需传: activityId, amount, voucherUrl, description
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody ReimburseSubmitDTO submitDTO) {
        // 简单的参数校验
        if (submitDTO.getActivityId() == null || submitDTO.getAmount() == null) {
            return Result.error("活动ID和报销金额不能为空");
        }
        return reimburseService.submit(submitDTO);
    }

    /**
     * 用户端：查看我的报销进度
     */
    @GetMapping("/my/list")
    public Result myList(@RequestParam(defaultValue = "1") Integer pageNum,
                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return reimburseService.listMyReimbursements(pageNum, pageSize);
    }
}