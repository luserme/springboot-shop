package com.lq.shop.dao;

import com.alipay.api.domain.OrderItem;
import com.lq.shop.entity.OrderItemEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * 通过订单号获取订单详细
     * @param orderNo 订单号
     * @return 查询结果
     */
    List<OrderItemEntity> findByOrderNo(Long orderNo);

    /**
     * 通过订单号获取订单分页详细
     * @param orderNo 订单号
     * @param pageable 分页对象
     * @return 分页结果
     */
    Page<OrderItemEntity> findByOrderNo(Long orderNo, Pageable pageable);
}
