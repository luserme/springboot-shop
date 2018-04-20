package com.lq.shop.dao;

import com.lq.shop.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author : luqing
 * @date : 2018/4/19 14:03
 */
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    /**
     * 校验用户名是否存在
     *
     * @param username 用户名
     * @return 通过username查询到的数量
     */
    @Query(value = "select count(u.username) from UserEntity u where u.username = ?1")
    long checkUsername(@Param(value = "username") String username);

    /**
     * 校验用户登录密码是否正确
     *
     * @param username 用户名
     * @param password 用户密码
     * @return 登录的用户信息
     */
    @Query(value = "select u from UserEntity u where u.username = ?1  and u.password = ?2")
    UserEntity selectLogin(String username, String password);

    /**
     * 校验邮箱是否存在
     * @param email 邮箱
     * @return 通过email查询到的数量
     */
    @Query(value = "select count(u.email) from UserEntity as u where u.email = ?1")
    long checkEmail(String email);

    /**
     * 通过用户名查找问题
     * @param username 用户名
     * @return 问题
     */
    @Query(value = "select u.question from UserEntity u where u.username = ?1")
    String findQuestionByUsername(String username);


    /**
     * 校验用户名 问题 和答案
     * @param username 用户名
     * @param question 问题
     * @param answer 答案
     * @return 满足条件校验条数
     */
    @Query(value = "select count(u.username) from UserEntity u where u.username = ?1 and u.question = ?2 and u.answer = ?3")
    long checkAnswer(String username, String question, String answer);

    /**
     * 更新密码
     * @param username 用户名
     * @param md5Password 新密码
     * @return 更新行数
     */
    @Modifying
    @Query(value = "update UserEntity u set u.password = :password , u.updateTime = CURRENT_DATE where u.username = :username")
    int updatePasswordByUsername(@Param("username") String username, @Param("password") String md5Password);

    /**
     * 通过查找用户名查找对象
     * @param username 用户名
     * @return 用户对象
     */
    UserEntity findByUsername(String username);

    /**
     * 通过id查找邮箱是否属于该用户
     * @param email 邮箱
     * @param id 用户id
     * @return 0:邮箱可用状态 >0:邮箱不可用状态
     */
    @Query(value = "select count(u.username) from UserEntity u where u.id <> :id and u.email = :email")
    int checkEmailByUserId(@Param("email") String email, @Param("id") Integer id);
}
