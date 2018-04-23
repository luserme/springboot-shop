package com.lq.shop.common.util;

import lombok.Data;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static com.lq.shop.common.util.PropertiesUtil.getProperty;

/**
 * @author luqing
 * @date 2018/04/21 23:55
 */
@Data
public class FtpUtil {

    private static  final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    private static String ftpIp = getProperty("ftp.server.ip");
    private static String ftpUser = getProperty("ftp.user");
    private static String ftpPass = getProperty("ftp.pass");
    private static String ftpPortStr = getProperty("ftp.port");

    /**
     * 默认端口21
     */
    private static int ftpPort = ftpPortStr==null?21:Integer.parseInt(ftpPortStr);

    private FtpUtil(String ip,int port,String user,String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException {
        FtpUtil ftpUtil = new FtpUtil(ftpIp,ftpPort,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器");
        boolean result = ftpUtil.uploadFile("upload/img",fileList);
        logger.info("开始连接ftp服务器,结束上传,上传结果:{}",result);

        return result;
    }


    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded = false;
        FileInputStream fis = null;
        //连接FTP服务器
        if(connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for(File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
                uploaded = true;
            } catch (IOException e) {
                logger.error("上传文件异常",e);
                e.printStackTrace();
            } finally {
                assert fis != null;
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }



    private boolean connectServer(String ip,int port,String user,String pwd){

        boolean isSuccess;
        ftpClient = new FTPClient();
        ftpClient.setUseEPSVwithIPv4(true);
        try {
            ftpClient.connect(ip,port);
            isSuccess = ftpClient.login(user,pwd);
        } catch (IOException e) {
            isSuccess = false;
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }


}
