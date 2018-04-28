package com.lq.shop.service;

import com.lq.shop.common.response.ServerResult;
import java.util.Map;

/**
 * @author : luqing
 * @date : 2018/4/26 14:05
 */
public interface IOrderService {

    /**
     * 支付功能
     * @param orderNo 订单号
     * @param userId 用户id
     * @param path 保存二维码的路径
     * @return 支付信息
     */
    ServerResult pay(Long orderNo, Integer userId, String path);

    /**
     * 支付宝回调验证
     * @param params 参数map
     * @return 验证结果
     */
    ServerResult aliCallback(Map<String, String[]> params);

    /**
     * 通过用户id和订单号查找订单状态
     * @param userId 用户id
     * @param orderNo 订单号
     * @return 查找结果
     */
    ServerResult findOrderPayStatus(Integer userId, Long orderNo);

    /**
     * 创建订单
     * @param userId 用户id
     * @param shippingId 收货地址id
     * @return 创建结果
     */
    ServerResult createOrder(Integer userId, Integer shippingId);

    /**
     * 取消订单
     * @param userId 用户id
     * @param orderNo 订单号
     * @return 取消结果
     */
    ServerResult cancel(Integer userId,Long orderNo);

    /**
     * 获取订单的商品信息
     * @param userId 用户id
     * @return 获取结果
     */
    ServerResult getOrderProduct(Integer userId);

    /**
     * 获取订单详情
     * @param userId 用户id
     * @param orderNo 订单号
     * @return 详情
     */
    ServerResult getOrderDetail(Integer userId, Long orderNo);

    /**
     * 获取订单集合
     * @param userId 用户id
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 订单集合
     */
    ServerResult getOrderList(Integer userId, Integer pageNum, Integer pageSize);


    /**
     * 订单收货功能
     * @param userId 用户id
     * @param orderNo 订单号
     * @return 收货结果
     */
    ServerResult deliveryGoods(Integer userId, Long orderNo);

    /**
     * 管理员分页获取订单及和
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 查询结果
     */
    ServerResult manageList(Integer pageNum, Integer pageSize);

    /**
     * 管理员查询订单详情
     * @param orderNo 订单号
     * @return 查询结果
     */
    ServerResult manageDetail(Long orderNo);

    /**
     * 通过订单号查找订单中所有商品
     * @param orderNo 订单号
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 搜索结果
     */
    ServerResult manageSearch(Long orderNo, Integer pageNum, Integer pageSize);

    /**
     * 发货
     * @param orderNo 订单号
     * @return 发货结果
     */
    ServerResult manageSendGoods(Long orderNo);

}
