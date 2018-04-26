package com.lq.shop.dao;

import com.lq.shop.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : luqing
 * @date : 2018/4/26 14:07
 */
public interface OrderRepository extends JpaRepository<OrderEntity,Integer>{

    /**
     * 通过当前用户该订单号 避免横向越权问题
     * @param userId 用户id
     * @param orderNo 订单号
     * @return 查询结果
     */
    OrderEntity findByUserIdAndOrderNo(Integer userId, Long orderNo);

    /**
     * 通过订单号查找订单
     * @param orderNo 订单号
     * @return 查找结果
     */
    OrderEntity findByOrderNo(Long orderNo);

}
