package com.lq.shop.dao;

import com.lq.shop.entity.CategoryEntity;
import java.util.List;
import java.util.Locale.Category;
import org.springframework.data.jpa.repository.JpaRepository;

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
