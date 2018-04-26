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
}
