package com.lq.shop.service;

import com.lq.shop.common.response.ServerResult;
import com.lq.shop.vo.CartVO;

/**
 * @author luqing
 * @date 2018/04/22 18:00
 */
public interface ICartService {

    /**
     * 获取用户购物车中所有商品
     * @param userId 用户id
     * @return 所有商品
     */
    ServerResult<CartVO> getCartList(Integer userId);

    /**
     * 增加购物车中商品数量
     * @param userId 用户id
     * @param productId 商品id
     * @param count 数量
     * @return 增加结果
     */
    ServerResult<CartVO> add(Integer userId, Integer productId, Integer count);

    /**
     * 更新购物车中商品数量
     * @param userId 用户id
     * @param productId 商品id
     * @param count 数量
     * @return 更新结果
     */
    ServerResult<CartVO> update(Integer userId, Integer productId, Integer count);

    /** 删除购物车中的商品
     * @param userId 用户id
     * @param productIds 商品id数组(使用,分割)
     * @return 删除结果
     */
    ServerResult<CartVO> deleteProduct(Integer userId, String productIds);


    /**
     * 通过id 选择或者反选某个购物车中产品
     * @param userId 用户id
     * @param productId 商品id (为null时为全部id)
     * @param checked 是否选择
     * @return 选择结果
     */
    ServerResult<CartVO> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    /** 获取购物车中的商品数量
     * @param userId 用户id
     * @return 查询结果
     */
    ServerResult<Integer> getCartProductCount(Integer userId);
}
