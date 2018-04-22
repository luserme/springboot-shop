package com.lq.shop.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author luqing
 * @date 2018/04/22 18:11
 */

@Data
public class CartVO {

    private List<CartProductVO> cartProductVOList;
    private BigDecimal cartTotalPrice;
    /**
     * 是否已经都勾选
     */
    private Boolean allChecked;
    private String imageHost;
}
