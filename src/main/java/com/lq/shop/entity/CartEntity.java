package com.lq.shop.entity;

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
 * @date : 2018/4/19 10:06
 */

@Entity
@Table(name = "shop_cart",indexes = @Index(name = "user_id_index",columnList = "userId"))
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private Integer checked;

    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private Date updateTime;
}
