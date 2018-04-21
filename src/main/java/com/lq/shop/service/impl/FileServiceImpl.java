package com.lq.shop.service.impl;

import com.google.common.collect.Lists;
import com.lq.shop.common.util.FtpUtil;
import com.lq.shop.service.IFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author luqing
 * @date 2018/04/21 23:50
 */

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String upload(MultipartFile file, String path){
        String fileName = file.getOriginalFilename();
        //扩展名
        //abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            boolean b = fileDir.setWritable(true);
            if (b){
                logger.info("创建所有前缀文件夹成功");
            }
            boolean mkdirs = fileDir.mkdirs();
            if (mkdirs){
                logger.info("创建文件夹成功");
            }
        }
        File targetFile = new File(path,uploadFileName);


        try {
            file.transferTo(targetFile);
            //文件已经上传成功了
            FtpUtil.uploadFile(Lists.newArrayList(targetFile));
            //已经上传到ftp服务器上
            boolean delete = targetFile.delete();

            if (delete){
                logger.info("上传成功");
            }

        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        //A:abc.jpg
        //B:abc.jpg
        return targetFile.getName();
    }

}
