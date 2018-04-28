package com.lq.shop.vo;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : luqing
 * @date : 2018/4/28 11:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductVO {

    private List<OrderItemVO> orderItemVoList;

    private BigDecimal productTotalPrice;

    private String imageHost;
}
