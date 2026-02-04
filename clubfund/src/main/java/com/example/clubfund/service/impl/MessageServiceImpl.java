package com.example.clubfund.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.clubfund.common.Result;
import com.example.clubfund.entity.SysMessage;
import com.example.clubfund.mapper.SysMessageMapper;
import com.example.clubfund.service.IMessageService;
import com.example.clubfund.utils.UserContext;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements IMessageService {

    @Override
    public void sendSysMessage(Long toUserId, String title, String content) {
        SysMessage msg = new SysMessage();
        msg.setUserId(toUserId);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setIsRead(0); // 默认未读
        save(msg);
    }

    @Override
    public Result listMyMessages(Integer pageNum, Integer pageSize, Integer isRead) {
        Long userId = UserContext.getUserId();
        Page<SysMessage> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SysMessage> wrapper = new QueryWrapper<>();

        wrapper.eq("user_id", userId);

        if (isRead != null) {
            wrapper.eq("is_read", isRead);
        }

        wrapper.orderByDesc("create_time"); // 最新消息在最前
        wrapper.orderByAsc("is_read");      // 未读消息优先

        return Result.success(baseMapper.selectPage(page, wrapper));
    }

    @Override
    public Result markAsRead(Long messageId) {
        Long userId = UserContext.getUserId();
        SysMessage msg = baseMapper.selectById(messageId);

        if (msg == null) return Result.error("消息不存在");
        if (!msg.getUserId().equals(userId)) return Result.error("无权操作");

        msg.setIsRead(1);
        baseMapper.updateById(msg);
        return Result.success();
    }
}