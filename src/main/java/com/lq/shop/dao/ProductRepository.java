package com.lq.shop.dao;

import com.lq.shop.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author luqing
 * @date 2018/04/21 12:07
 */
public interface ProductRepository extends JpaRepository<ProductEntity,Integer>{
    /** 重写findAll方法
     * @param specification 接口
     * @param pageable 分页
     * @return 查询结果
     */
    Page<ProductEntity> findAll(Specification<ProductEntity> specification, Pageable pageable);

    /**
     * 通过关键字和商品id集合分页查询商品
     * @param name 关键字
     * @param categoryIdList 商品id集合
     * @param pageable 分页
     * @return 查询结果
     */
    Page<ProductEntity> findAllByNameLikeAndCategoryIdIn(String name, List<Integer> categoryIdList, Pageable pageable);

    /**
     * 通过关键字分页查询商品
     * @param name 关键字
     * @param pageable 分页
     * @return 查询结果
     */
    Page<ProductEntity> findAllByNameLike(String name,Pageable pageable);

    List<ProductEntity> findAllByNameLike(String name);

    /**
     * 通过商品id集合分页查询商品
     * @param categoryIdList 商品id集合
     * @param pageable 分页
     * @return 查询结果
     */
    Page<ProductEntity> findPageByCategoryIdIn(List<Integer> categoryIdList, Pageable pageable);
}
