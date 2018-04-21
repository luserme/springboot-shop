package com.lq.shop.entity;

import java.math.BigDecimal;
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
 * @date : 2018/4/19 10:48
 */
@Entity
@Table(name = "shop_product")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "INT COMMENT '商品id'")
    private Integer id;

    @Column(columnDefinition = "INT COMMENT '分类id,对应shop_category表的主键'")
    private int categoryId;

    @Column(length = 100,columnDefinition = "VARCHAR(100) COMMENT '商品名称'")
    private String name;

    @Column(length = 200,columnDefinition = "VARCHAR(200) COMMENT '商品副标题'")
    private String subtitle;

    @Column(length = 500,columnDefinition = "VARCHAR(500) COMMENT '产品主图,url相对地址'")
    private String mainImage;

    @Column(columnDefinition = "TEXT COMMENT '图片地址,json格式,扩展用'")
    private String subImages;

    @Column(columnDefinition = "TEXT COMMENT '商品详情'")
    private String detail;

    @Column(precision = 20,scale = 2,columnDefinition = "DECIMAL(20, 2) COMMENT '价格,单位-元保留两位小数'")
    private BigDecimal price;

    @Column(columnDefinition = "INT COMMENT '库存数量'")
    private int stock;

    @Column(length = 6,columnDefinition = "INT default 1 COMMENT '商品状态.1-在售 2-下架 3-删除'")
    private Integer status;

    @CreatedDate
    @Column(columnDefinition = "DATETIME COMMENT '创建时间'")
    private Timestamp createTime;

    @LastModifiedDate
    @Column(columnDefinition = "DATETIME COMMENT '更新时间'")
    private Timestamp updateTime;
}
