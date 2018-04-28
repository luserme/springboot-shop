package com.lq.shop.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.common.util.StringUtils;
import com.lq.shop.dao.CategoryRepository;
import com.lq.shop.entity.CategoryEntity;
import com.lq.shop.service.ICategoryService;
import java.util.List;
import java.util.Locale.Category;
import java.util.Set;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @author : luqing
 * @date : 2018/4/20 15:55
 */
@Service("iCategoryService")
@Log4j
public class CategoryServiceImpl implements ICategoryService{

    private CategoryRepository categoryRepository;

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(rollbackFor = {})
    public ServerResult addCategory(String categoryName, Integer parentId) {

        if (parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResult.createByErrorMessage("新增品类参数有误");
        }

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryName);
        categoryEntity.setParentId(parentId);
        // 新增分类默认可用
        categoryEntity.setStatus(true);

        CategoryEntity saveCategory = categoryRepository.save(categoryEntity);

        if (saveCategory != null){
            return ServerResult.createBySuccess("添加品类成功");
        }

        return ServerResult.createByErrorMessage("添加品类成功");
    }

    @Override
    @Transactional(rollbackFor = {})
    public ServerResult updateCategoryName(Integer categoryId, String categoryName) {

        if (categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResult.createByErrorMessage("更新品类参数有误");
        }

        CategoryEntity categoryEntity = categoryRepository.findOne(categoryId);

        if(categoryEntity == null){
            return ServerResult.createByErrorMessage("当前分类不存在");
        }

        categoryEntity.setName(categoryName);
//
        CategoryEntity saveCategory = categoryRepository.save(categoryEntity);
//
        if (saveCategory != null){
            return ServerResult.createByErrorMessage("更新品类名称成功");
        }

        return ServerResult.createByErrorMessage("更新品类名称失败");

    }

    @Override
    public ServerResult<List<CategoryEntity>> getChildrenParallelCategory(Integer categoryId) {

        List<CategoryEntity> categoryList = categoryRepository.findAllByParentId(categoryId);

        if (CollectionUtils.isEmpty(categoryList)){
            log.info("未找到当前分类子分类");
        }

        return ServerResult.createBySuccess(categoryList);
    }

    @Override
    public ServerResult<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        Set<CategoryEntity> categorySet = Sets.newHashSet();
        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null){
            categorySet = getChildrenCategory(categorySet,categoryId);

            for (CategoryEntity categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }

        }
        return ServerResult.createBySuccess(categoryIdList);
    }

    private Set<CategoryEntity> getChildrenCategory(Set<CategoryEntity> categorySet, Integer categoryId) {
        CategoryEntity category = categoryRepository.findOne(categoryId);

        if (category != null){
            categorySet.add(category);
        }

        List<CategoryEntity> categoryList = categoryRepository.findAllByParentId(categoryId);

        for (CategoryEntity categoryEntity : categoryList){
            getChildrenCategory(categorySet,categoryEntity.getId());
        }

        return categorySet;
    }


}
