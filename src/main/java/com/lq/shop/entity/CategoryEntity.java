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
 * @date : 2018/4/19 10:16
 */
@Entity
@Table(name = "shop_category")
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "INT COMMENT '类别Id'")
    private Integer id;

    @Column(columnDefinition = "INT COMMENT 'DECIMAL(20, 2) 父类别id当id=0时说明是根节点,一级类别'")
    private Integer parentId;

    @Column(length = 50,columnDefinition = "VARCHAR(50) COMMENT '类别名称'")
    private String name;

    @Column(length = 1,columnDefinition = "INT(1) COMMENT '类别状态1-正常,2-已废弃'")
    private Boolean status;

    @Column(length = 4,columnDefinition = "INT(4) COMMENT '排序编号,同类展示顺序,数值相等则自然排序'")
    private Integer sortOrder;

    @CreatedDate
    @Column(columnDefinition = "DATETIME COMMENT '创建时间'")
    private Date createTime;

    @LastModifiedDate
    @Column(columnDefinition = "DATETIME COMMENT '更新时间'")
    private Date updateTime;
}
