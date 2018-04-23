package com.lq.shop.service;

import com.lq.shop.common.response.ServerResult;
import com.lq.shop.entity.ShippingEntity;
import org.springframework.data.domain.Page;

/**
 * @author : luqing
 * @date : 2018/4/23 14:53
 */
public interface IShippingService {


    /**
     * 新增收货地址
     * @param userId 用户id
     * @param shipping 收货地址对象
     * @return 增加结果
     */
    ServerResult addShipping(Integer userId, ShippingEntity shipping);

    /**
     * 删除收获地址
     * @param userId 用户id
     * @param shippingId 收获地址id
     * @return 删除结果
     */
    ServerResult delShipping(Integer userId, Integer shippingId);

    /**
     * 更新收获地址
     * @param userId 用户id
     * @param shipping 收获地址对象
     * @return 更新结果
     */
    ServerResult updateShipping(Integer userId, ShippingEntity shipping);

    /**
     * 通过收货地址id查询收货地址
     * @param userId 用户id
     * @param shippingId 收货地址id
     * @return 查询对象结果
     */
    ServerResult<ShippingEntity> selectShipping(Integer userId, Integer shippingId);

    /**
     * 分页查询当前用户所有的收货地址
     * @param userId 用户id
     * @param pageNum 分页
     * @param pageSize 每页大小
     * @return 查询结果集合
     */
    ServerResult<Page> getShippingList(Integer userId, int pageNum, int pageSize);
}
