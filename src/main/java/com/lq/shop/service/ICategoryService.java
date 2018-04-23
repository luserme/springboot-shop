package com.lq.shop.service;

import com.lq.shop.common.response.ServerResult;

import java.util.List;

/**
 * @author : luqing
 * @date : 2018/4/20 15:54
 */

public interface ICategoryService {

    /**
     * 新增分类
     * @param categoryName 分类名称
     * @param parentId 父分类id
     * @return 新增结果
     */
    ServerResult addCategory(String categoryName, Integer parentId);


    /**
     * 更新品类名称
     * @param categoryId 品类id
     * @param categoryName 新的品类名称
     * @return 更新结果
     */
    ServerResult updateCategoryName(Integer categoryId, String categoryName);

    /**
     * 获取一级的子品类
     * @param categoryId 品类id
     * @return 查询结果
     */
    ServerResult getChildrenParallelCategory(Integer categoryId);

    /**
     * 递归获取所有子品类
     * @param categoryId 品类id
     * @return 查询结果
     */
    ServerResult<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
