package com.lq.shop.service;

import org.springframework.web.multipart.MultipartFile; /**
 * @author luqing
 * @date 2018/04/21 23:50
 */
public interface IFileService {


    /**
     * 文件上传
     * @param file 上传的文件
     * @param path 上传的路径
     * @return 保存文件名
     */
    String upload(MultipartFile file, String path);
}
