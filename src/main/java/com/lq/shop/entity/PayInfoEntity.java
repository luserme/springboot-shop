package com.lq.shop.entity;


import java.util.Date;
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
 * @date : 2018/4/19 10:45
 */

@Entity
@Table(name = "shop_pay_info")
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayInfoEntity {

    @Id
    @GeneratedValue
    private int id;

    @Column(columnDefinition = "INT COMMENT '用户id'")
    private Integer userId;

    @Column(columnDefinition = "BIGINT COMMENT '订单号'")
    private Long orderNo;

    @Column(length = 10,columnDefinition = "INT(10) COMMENT '支付平台:1-支付宝,2-微信'")
    private Integer payPlatform;

    @Column(length = 200,columnDefinition = "VARCHAR(200) COMMENT '支付宝支付流水号'")
    private String platformNumber;

    @Column(length = 20,columnDefinition = "VARCHAR(20) COMMENT '支付宝支付状态'")
    private String platformStatus;

    @CreatedDate
    @Column(columnDefinition = "DATETIME COMMENT '创建时间'")
    private Date createTime;

    @LastModifiedDate
    @Column(columnDefinition = "DATETIME COMMENT '更新时间'")
    private Date updateTime;

}
