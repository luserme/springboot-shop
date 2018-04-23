package com.lq.shop.dao;

import com.lq.shop.entity.ShippingEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : luqing
 * @date : 2018/4/23 14:55
 */
public interface ShippingRepository extends JpaRepository<ShippingEntity,Integer> {

    /**
     * 通过用户id和收货地址id删除收货地址
     * @param userId 用户id
     * @param shippingId 收货地址id
     * @return 删除条数
     */
    int deleteByUserIdAndId(Integer userId, Integer shippingId);

//    /**
//     * 通过用户
//     * @param userId
//     * @param id
//     * @return
//     */
//    ShippingEntity findByUserIdAndId(Integer userId, Integer id);

    /**
     * 通过用户id分页查找收货地址
     * @param userId 用户id
     * @param pageable 分页对象
     * @return 收货地址集合
     */
    Page<ShippingEntity> findByUserId(Integer userId, Pageable pageable);
}
