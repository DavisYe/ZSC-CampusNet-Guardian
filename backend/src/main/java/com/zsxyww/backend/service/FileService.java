package com.zsxyww.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件服务接口
 *
 * @author DavisYe
 * @since 1.0.0
 */
public interface FileService {
    
    /**
     * 上传单个文件
     *
     * @param file 文件
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file);
    
    /**
     * 批量上传文件
     *
     * @param files 文件列表
     * @return 文件访问URL列表
     */
    List<String> uploadFiles(List<MultipartFile> files);
    
    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    void deleteFile(String fileUrl);
    
    /**
     * 批量删除文件
     *
     * @param fileUrls 文件URL列表
     */
    void deleteFiles(List<String> fileUrls);
}