package com.lq.shop.controller.backend;

import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.entity.UserEntity;
import com.lq.shop.service.ICategoryService;
import com.lq.shop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author : luqing
 * @date : 2018/4/20 15:50
 */


@RestController
@RequestMapping("/manage/category")
public class CategoryManageController {

    private IUserService iUserService;

    private ICategoryService iCategoryService;

    @Autowired
    public void setIUserService(IUserService iUserService) {
        this.iUserService = iUserService;
    }

    @Autowired
    public void setICategoryService(ICategoryService iCategoryService) {
        this.iCategoryService = iCategoryService;
    }

    @RequestMapping("/add/category")
    public ServerResult addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
//        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
//        ServerResult result = iUserService.checkAdmin(userEntity);
//        if (result.isSuccess()){
            return iCategoryService.addCategory(categoryName,parentId);
//        }
//        return result;
    }


    @RequestMapping("/update/category")
    public ServerResult setCategoryName(HttpSession session,Integer categoryId,String categoryName){
//        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
//        ServerResult result = iUserService.checkAdmin(userEntity);
//        if (result.isSuccess()){
            return iCategoryService.updateCategoryName(categoryId,categoryName);
//        }
//        return result;
    }

    @RequestMapping("/category")
    public ServerResult getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
//        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
//        ServerResult result = iUserService.checkAdmin(userEntity);
//        if (result.isSuccess()){
            return iCategoryService.getChildrenParallelCategory(categoryId);
//        }
//        return result;
    }
    @RequestMapping("/deep/category")
    public ServerResult getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
//        UserEntity userEntity = (UserEntity) session.getAttribute(Const.CURRENT_USER);
//        ServerResult result = iUserService.checkAdmin(userEntity);
//        if (result.isSuccess()){
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
//        }
//        return result;
    }

}
