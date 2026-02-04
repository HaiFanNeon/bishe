package com.example.clubfund.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.clubfund.common.Result;
import com.example.clubfund.entity.SysMessage;

public interface IMessageService extends IService<SysMessage> {

    // 发送系统通知 (供其他Service调用)
    void sendSysMessage(Long toUserId, String title, String content);

    // 用户查询自己的消息
    Result listMyMessages(Integer pageNum, Integer pageSize, Integer isRead);

    // 标记消息为已读
    Result markAsRead(Long messageId);
}