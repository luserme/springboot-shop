package com.lq.shop.vo;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : luqing
 * @date : 2018/4/28 10:17
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVO {

    private Long orderNo;

    private BigDecimal payment;

    private Integer paymentType;

    private String paymentTypeDesc;

    private Integer postage;

    private Integer status;


    private String statusDesc;

    private String paymentTime;

    private String sendTime;

    private String endTime;

    private String closeTime;

    private String createTime;

    /**
     * 订单的明细
     */
    private List<OrderItemVO> orderItemVOList;

    private String imageHost;

    private Integer shippingId;

    private String receiverName;

    private ShippingVO shippingVo;
}
