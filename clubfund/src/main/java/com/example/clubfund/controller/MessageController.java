package com.example.clubfund.controller;

import com.example.clubfund.common.Result;
import com.example.clubfund.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private IMessageService messageService;

    /**
     * 获取我的消息列表 (支持筛选未读/已读)
     */
    @GetMapping("/my")
    public Result list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false) Integer isRead) {
        return messageService.listMyMessages(pageNum, pageSize, isRead);
    }

    /**
     * 标记某条消息为已读
     */
    @PutMapping("/read/{id}")
    public Result read(@PathVariable Long id) {
        return messageService.markAsRead(id);
    }
}