package com.example.clubfund.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.clubfund.entity.Club;
import com.example.clubfund.entity.Notice;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
