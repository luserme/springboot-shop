package com.lq.shop.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author : luqing
 * @date : 2018/4/19 10:19
 */
@Entity
@Table(
        name = "shop_order",
        indexes = @Index(name = "order_no_index", columnList = "orderNo")
)
@Data
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {



    @Id
    @GeneratedValue
    @Column(columnDefinition = "INT COMMENT '订单id'")
    private Integer id;

    @Column(columnDefinition = "BIGINT COMMENT '订单号'")
    private Long orderNo;

    @Column(columnDefinition = "INT COMMENT '用户id'")
    private Integer userId;

    private Integer shippingId;

    @Column(precision = 20,scale = 2,columnDefinition = "DECIMAL(20, 2) COMMENT '实际付款金额,单位是元,保留两位小数'")
    private BigDecimal payment;

    @Column(length = 4,columnDefinition = "INT(4) COMMENT '支付类型,1-在线支付'")
    private Integer paymentType;

    @Column(length = 10,columnDefinition = "INT(10) COMMENT '运费,单位是元'")
    private Integer postage;

    @Column(length = 10,columnDefinition = "INT(10) COMMENT '订单状态:0-已取消-10-未付款，20-已付款，40-已发货，50-交易成功，60-交易关闭'")
    private Integer status;

    @Column(columnDefinition = "DATETIME COMMENT '支付时间'")
    private Date paymentTime;

    @Column(columnDefinition = "DATETIME COMMENT '发货时间'")
    private Date sendTime;

    @Column(columnDefinition = "DATETIME COMMENT '交易完成时间'")
    private Date endTime;

    @Column(columnDefinition = "DATETIME COMMENT '交易关闭时间'")
    private Date closeTime;

    @CreatedDate
    @Column(columnDefinition = "DATETIME COMMENT '创建时间'")
    private Date createTime;

    @LastModifiedDate
    @Column(columnDefinition = "DATETIME COMMENT '更新时间'")
    private Date updateTime;
}
