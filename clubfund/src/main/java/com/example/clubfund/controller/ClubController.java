package com.example.clubfund.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.clubfund.common.Result;
import com.example.clubfund.entity.Club;
import com.example.clubfund.service.IClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/club")
@CrossOrigin
public class ClubController {

    @Autowired
    private IClubService clubService;

    /**
     * 新增社团
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Club club) {
        return Result.success(clubService.save(club));
    }

    /**
     * 修改社团信息
     */
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody Club club) {
        return Result.success(clubService.updateById(club));
    }

    /**
     * 删除社团
     */
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(clubService.removeById(id));
    }

    /**
     * 获取社团列表 (支持下拉框搜索)
     */
    @GetMapping("/list")
    public Result<List<Club>> listAll() {
        return Result.success(clubService.list());
    }

    /**
     * 分页查询社团 (后台表格用)
     */
    @GetMapping("/page")
    public Result<Page<Club>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {

        Page<Club> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Club> query = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            query.like("club_name", name);
        }
        return Result.success(clubService.page(page, query));
    }
}