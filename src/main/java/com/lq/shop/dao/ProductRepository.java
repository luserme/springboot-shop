package com.lq.shop.dao;

import com.lq.shop.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

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

//    Page<ProductEntity> findAllByIdAndName(Integer productId, String productName, Pageable pageable);
}
