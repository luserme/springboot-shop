package com.lq.shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author : luqing
 * @date : 2018/4/19 10:10
 */
@Entity
@Table(name = "shop_user")
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity{

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false,length = 50)
    private String username;

    @Column(nullable = false,length = 50)
    @JsonIgnore
    private String password;

    @Column(length = 50)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String question;

    @Column(length = 100)
    @JsonIgnore
    private String answer;

    @Column(nullable = false,length = 4)
    private Integer role;

    @CreatedDate
    @Column(nullable = false,updatable = false)
    private Date createTime;

    @Column(nullable = false)
    @LastModifiedDate
    private Date updateTime;

}
