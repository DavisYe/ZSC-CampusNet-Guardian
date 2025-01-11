package com.zsxyww.backend.controller;

import com.zsxyww.backend.common.Result;
import com.zsxyww.backend.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传控制器
 *
 * @author DavisYe
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 上传单个文件
     *
     * @param file 文件
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return Result.success(fileService.uploadFile(file));
    }

    /**
     * 批量上传文件
     *
     * @param files 文件列表
     * @return 文件访问URL列表
     */
    @PostMapping("/batch-upload")
    public Result<List<String>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        return Result.success(fileService.uploadFiles(files));
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    @DeleteMapping
    public Result<Void> deleteFile(@RequestParam String fileUrl) {
        fileService.deleteFile(fileUrl);
        return Result.success();
    }

    /**
     * 批量删除文件
     *
     * @param fileUrls 文件URL列表
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteFiles(@RequestBody List<String> fileUrls) {
        fileService.deleteFiles(fileUrls);
        return Result.success();
    }
}