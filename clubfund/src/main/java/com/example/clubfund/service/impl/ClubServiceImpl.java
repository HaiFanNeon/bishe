package com.example.clubfund.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.clubfund.entity.Club;
import com.example.clubfund.mapper.ClubMapper;
import com.example.clubfund.service.IClubService;
import org.springframework.stereotype.Service;

@Service
public class ClubServiceImpl extends ServiceImpl<ClubMapper, Club> implements IClubService {
}