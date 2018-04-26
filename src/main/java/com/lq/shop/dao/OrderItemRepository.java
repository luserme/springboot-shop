package com.lq.shop.dao;

import com.alipay.api.domain.OrderItem;
import com.lq.shop.entity.OrderItemEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : luqing
 * @date : 2018/4/26 14:31
 */
public interface OrderItemRepository extends JpaRepository<OrderItemEntity,Integer>{

    /**
     * 通过用户id和订单号查找出所有该用户订单的所有详细
     * @param userId 用户id
     * @param orderNo 订单号
     * @return 查询结果
     */
    List<OrderItemEntity> findByUserIdAndOrderNo(Integer userId, Long orderNo);

}
