package com.lq.shop.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author luqing
 * @date 2018/04/22 18:09
 */
@Data
public class CartProductVO {

    private Integer id;
    private Integer userId;
    private Integer productId;
    /**
     * 购物车中此商品的数量
     */
    private Integer quantity;
    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private BigDecimal productPrice;
    private Integer productStatus;
    private BigDecimal productTotalPrice;
    private Integer productStock;
    /**
     * 此商品是否勾选
     */
    private Integer productChecked;
    /**
     * 限制数量的一个返回结果
     */
    private String limitQuantity;

}
