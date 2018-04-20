package com.lq.shop.entity;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author : luqing
 * @date : 2018/4/19 10:50
 */
@Entity
@Table(name = "shop_shipping")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingEntity {

    @Id
    @GeneratedValue
    private int id;

    @Column(length = 20 ,columnDefinition = "INT(20) COMMENT '用户id'")
    private Integer userId;

    @Column(length = 20 ,columnDefinition = "VARCHAR(20) COMMENT '收货姓名'")
    private String receiverName;

    @Column(length = 20 ,columnDefinition = "VARCHAR(20) COMMENT '收货固定电话'")
    private String receiverPhone;

    @Column(length = 20 ,columnDefinition = "VARCHAR(20) COMMENT '收货移动电话'")
    private String receiverMobile;

    @Column(length = 20 ,columnDefinition = "VARCHAR(20) COMMENT '省份'")
    private String receiverProvince;

    @Column(length = 20 ,columnDefinition = "VARCHAR(20) COMMENT '城市'")
    private String receiverCity;

    @Column(length = 20 ,columnDefinition = "VARCHAR(20) COMMENT '区/县'")
    private String receiverDistrict;

    @Column(length = 200 ,columnDefinition = "VARCHAR(200) COMMENT '详细地址'")
    private String receiverAddress;

    @Column(length = 6 ,columnDefinition = "VARCHAR(6) COMMENT '邮编'")
    private String receiverZip;

    @CreatedDate
    private Timestamp createTime;

    @LastModifiedDate
    private Timestamp updateTime;

}
