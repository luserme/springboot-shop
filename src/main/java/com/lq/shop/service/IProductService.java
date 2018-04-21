package com.lq.shop.service;

import com.lq.shop.common.response.ServerResult;
import com.lq.shop.entity.ProductEntity;

/**
 * @author luqing
 * @date 2018/04/21 12:05
 */
public interface IProductService {

    /**
     * 保存或更新产品
     *
     * @param productEntity 订单实体对象
     * @return 保存或更新结果
     */
    ServerResult saveOrUpdateProduct(ProductEntity productEntity);

    /**
     * 修改产品在售状态
     *
     * @param productId 产品id
     * @param status    状态
     * @return 修改结果
     */
    ServerResult setSaleStatus(Integer productId, Integer status);

    /**
     * 查询产品细节
     *
     * @param productId 产品id
     * @return 查询结果
     */
    ServerResult manageProductDetail(Integer productId);

    /**
     * 分页查询所有产品
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 查询结果
     */
    ServerResult getProductList(int pageNum, int pageSize);


    /**
     * 搜索产品
     * @param productName 搜索关键字
     * @param productId 产品变化
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 查询结果
     */
    ServerResult searchProduct(String productName, Integer productId, int pageNum, int pageSize);
}
