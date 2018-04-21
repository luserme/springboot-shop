package com.lq.shop.dao;

import com.lq.shop.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : luqing
 * @date : 2018/4/20 16:19
 */
public interface CategoryRepository extends JpaRepository<CategoryEntity,Integer> {

    /**
     * jpa 反射方法
     * @param categoryId 品类id
     * @return 查询的内容
     */
    List<CategoryEntity> findAllByParentId(Integer categoryId);
}
