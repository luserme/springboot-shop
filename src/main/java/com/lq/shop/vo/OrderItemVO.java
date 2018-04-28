package com.lq.shop.vo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : luqing
 * @date : 2018/4/28 10:18
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemVO {

    private Long orderNo;

    private Integer productId;

    private String productName;

    private String productImage;

    private BigDecimal currentUnitPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    private String createTime;
}
