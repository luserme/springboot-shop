package com.lq.shop.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
 * @date : 2018/4/19 10:43
 */
@Entity
@Table(
    name = "shop_order_item",
    indexes = {
        @Index(name = "order_no_index",columnList = "orderNo"),
        @Index(name = "order_no_user_id_index",columnList = "userId,orderNo")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "INT COMMENT '订单子表id'")
    private int id;

    private Integer userId;

    private Long orderNo;

    @Column(columnDefinition = "INT COMMENT '商品id'")
    private Integer productId;

    @Column(length = 100,columnDefinition = "VARCHAR(100) COMMENT '商品名称'")
    private String productName;

    @Column(length = 500,columnDefinition = "VARCHAR(500) COMMENT '商品图片地址'")
    private String productImage;

    @Column(precision = 20,scale = 2,columnDefinition = "DECIMAL(20, 2) COMMENT '生成订单时的商品单价，单位是元,保留两位小数'")
    private BigDecimal currentUnitPrice;

    @Column(length = 10,columnDefinition = "INT COMMENT '商品数量'")
    private Integer quantity;

    @Column(precision = 20,scale = 2,columnDefinition = "DECIMAL(20, 2) COMMENT '商品总价,单位是元,保留两位小数'")
    private BigDecimal totalPrice;

    @CreatedDate
    private Timestamp createTime;

    @LastModifiedDate
    private Timestamp updateTime;
}
