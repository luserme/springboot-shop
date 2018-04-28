package com.lq.shop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : luqing
 * @date : 2018/4/28 10:19
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingVO {

    private String receiverName;

    private String receiverPhone;

    private String receiverMobile;

    private String receiverProvince;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverAddress;

    private String receiverZip;
}
