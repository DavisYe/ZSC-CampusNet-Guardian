package com.zsxyww.backend.service.impl;

import com.zsxyww.backend.config.FileConfig;
import com.zsxyww.backend.exception.BusinessException;
import com.zsxyww.backend.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务实现类
 *
 * @author DavisYe
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileConfig fileConfig;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public String uploadFile(MultipartFile file) {
        validateFile(file);
        String fileName = generateFileName(file);
        String relativePath = getRelativePath(fileName);
        String fullPath = fileConfig.getFullPath() + "/" + relativePath;
        
        try {
            // 创建目录
            Path directory = Paths.get(fullPath).getParent();
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            
            // 保存文件
            file.transferTo(new File(fullPath));
            
            // 返回文件访问URL
            return fileConfig.getUrlPrefix() + relativePath;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败");
        }
    }

    @Override
    public List<String> uploadFiles(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(uploadFile(file));
        }
        return urls;
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return;
        }
        
        try {
            String relativePath = fileUrl.substring(fileUrl.indexOf(fileConfig.getUrlPrefix()) + fileConfig.getUrlPrefix().length());
            Path filePath = Paths.get(fileConfig.getFullPath(), relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("文件删除失败", e);
            throw new BusinessException("文件删除失败");
        }
    }

    @Override
    public void deleteFiles(List<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return;
        }
        fileUrls.forEach(this::deleteFile);
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(fileConfig.getAllowedTypes()).contains(contentType)) {
            throw new BusinessException("不支持的文件类型");
        }
        
        // 验证文件大小
        long maxSize = parseSize(fileConfig.getMaxSize());
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小超过限制");
        }
    }

    /**
     * 生成文件名
     */
    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename);
        return UUID.randomUUID().toString() + (extension != null ? "." + extension : "");
    }

    /**
     * 获取相对路径
     * 按日期生成目录结构：yyyy/MM/dd/filename
     */
    private String getRelativePath(String fileName) {
        String datePath = LocalDate.now().format(DATE_FORMATTER);
        return datePath + "/" + fileName;
    }

    /**
     * 解析文件大小配置
     * 支持的单位：B, KB, MB, GB
     */
    private long parseSize(String size) {
        size = size.toUpperCase();
        long multiplier = 1;
        
        if (size.endsWith("KB")) {
            multiplier = 1024;
        } else if (size.endsWith("MB")) {
            multiplier = 1024 * 1024;
        } else if (size.endsWith("GB")) {
            multiplier = 1024 * 1024 * 1024;
        }
        
        String number = size.replaceAll("[^\\d.]", "");
        return (long) (Double.parseDouble(number) * multiplier);
    }
}