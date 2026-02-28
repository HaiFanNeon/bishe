package com.example.clubfund.controller;

import com.example.clubfund.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return Result.error("文件名无效");
        }

        String fileExt = fileName.substring(fileName.lastIndexOf("."));
        String[] allowedExts = {".jpg", ".jpeg", ".png", ".gif"};
        boolean allowed = false;
        for (String ext : allowedExts) {
            if (ext.equalsIgnoreCase(fileExt)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            return Result.error("只允许上传 jpg/jpeg/png/gif 格式的图片");
        }

        long fileSize = file.getSize();
        if (fileSize > 5 * 1024 * 1024) {
            return Result.error("文件大小不能超过 5MB");
        }

        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String newFileName = UUID.randomUUID().toString().replace("-", "") + fileExt;
        String relativePath = datePath + "/" + newFileName;

        String absoluteUploadPath;
        if (uploadPath.startsWith("./")) {
            absoluteUploadPath = System.getProperty("user.dir") + uploadPath.substring(1);
        } else {
            absoluteUploadPath = uploadPath;
        }
        String fullPath = absoluteUploadPath + File.separator + relativePath;

        try {
            Path path = Paths.get(fullPath);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败: " + e.getMessage());
        }

        String fileUrl = "/uploads/" + relativePath;
        return Result.success(fileUrl);
    }
}
