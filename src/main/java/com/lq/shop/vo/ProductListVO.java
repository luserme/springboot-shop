package com.lq.shop.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author luqing
 * @date 2018/04/23 21:08
 */

@Data
public class ProductListVO {
    private Integer id;
    private Integer categoryId;

    private String name;
    private String subtitle;
    private String mainImage;
    private BigDecimal price;

    private Integer status;

    private String imageHost;
}
