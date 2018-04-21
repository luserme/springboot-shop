package com.lq.shop.controller.backend;

import com.google.common.collect.Maps;
import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.common.util.PropertiesUtil;
import com.lq.shop.entity.ProductEntity;
import com.lq.shop.entity.UserEntity;
import com.lq.shop.service.IFileService;
import com.lq.shop.service.IProductService;
import com.lq.shop.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author luqing
 * @date 2018/04/21 12:03
 */

@RestController
@RequestMapping("/manage/product")
public class ProductManageController {

    private IUserService iUserService;

    private IProductService iProductService;

    private IFileService iFileService;

    @Autowired
    public void setIUserService(IUserService iUserService) {
        this.iUserService = iUserService;
    }

    @Autowired
    public void setIProductService(IProductService iProductService) {
        this.iProductService = iProductService;
    }

    @Autowired
    public void setIFileService(IFileService iFileService) {
        this.iFileService = iFileService;
    }

    @RequestMapping("/save")
    public ServerResult productProduct(HttpSession session, ProductEntity productEntity){
        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        ServerResult result = iUserService.checkAdmin(userEntity);
        if (result.isSuccess()){
            return iProductService.saveOrUpdateProduct(productEntity);
        }

        return result;
    }


    @RequestMapping("/sale/status")
    public ServerResult setSaleStatus(HttpSession session,Integer productId,Integer status){
        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        ServerResult result = iUserService.checkAdmin(userEntity);
        if (result.isSuccess()){
            return iProductService.setSaleStatus(productId,status);
        }
        return result;
    }

    @RequestMapping("/detail")
    public ServerResult getDetail(HttpSession session,Integer productId){
        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        ServerResult result = iUserService.checkAdmin(userEntity);
        if (result.isSuccess()){
            return iProductService.manageProductDetail(productId);
        }
        return result;
    }

    @RequestMapping("/list")
    public ServerResult getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "0") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        ServerResult result = iUserService.checkAdmin(userEntity);
        if (result.isSuccess()){
            return iProductService.getProductList(pageNum,pageSize);
        }
        return result;
    }

    @RequestMapping("/search")
    public ServerResult productSearch(HttpSession session,String productName,Integer productId, @RequestParam(value = "pageNum",defaultValue = "0") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        ServerResult result = iUserService.checkAdmin(userEntity);
        if (result.isSuccess()){
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }
        return result;
    }


    @RequestMapping("/upload")
    public ServerResult upload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        ServerResult result = iUserService.checkAdmin(userEntity);
        if (result.isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");

            if(file == null){
                return ServerResult.createByErrorMessage("请上传正确的文件");
            }

            String targetFileName = iFileService.upload(file,path);

            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            Map<String,String> fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);

            return ServerResult.createBySuccess(fileMap);
        }

        return result;
    }


    @RequestMapping("/upload/richtxt")
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map<String,String> resultMap = Maps.newHashMap();
        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
        ServerResult result = iUserService.checkAdmin(userEntity);
        if (result.isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);

            if(StringUtils.isBlank(targetFileName)){
                ServerResult messageNameIsBlank = ServerResult.createByErrorMessage("上传失败");
                resultMap.put("status", String.valueOf(messageNameIsBlank.getStatus()));
                resultMap.put("msg",messageNameIsBlank.getMsg());
                return resultMap;
            }

            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            ServerResult<String> success = ServerResult.createBySuccess("上传成功", url);
            resultMap.put("status", String.valueOf(success.getStatus()));
            resultMap.put("msg",success.getMsg());
            resultMap.put("data",success.getData());
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }

        resultMap.put("status", String.valueOf(result.getStatus()));
        resultMap.put("msg",result.getMsg());
        return resultMap;
    }

    }
