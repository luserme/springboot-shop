package com.lq.shop.dao;

import com.lq.shop.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author luqing
 * @date 2018/04/22 18:01
 */
public interface CartRepository extends JpaRepository<CartEntity,Integer> {


    /** 查找用户所以的购物车商品
     * @param userId 用户id
     * @return 查询结果
     */
    List<CartEntity> findAllByUserId(Integer userId);

    /**
     * 通过用户id查找订单选中的商品
     * 查找是否全选中
     * 即通过查找没有选中的商品的数量为0来判断
     * @param userId 用户id
     * @return 查询结果没有选择的结果
     */
    @Query("select count(c.checked) from CartEntity c where c.checked = 0 and c.userId = :userId")
    int findCartProductCheckedStatusByUserId(@Param("userId") Integer userId);

    /**
     * 通过用户id和商品id查询购物车商品
     * @param userId 用户id
     * @param productId 产品id
     * @return 购物车商品
     */
    CartEntity findByUserIdAndProductId(Integer userId, Integer productId);

    /**
     * 通过用户id和购物车商品集合删除购物车商品集合
     * @param userId 用户id
     * @param productList 商品id集合
     *
     */
    void deleteByUserIdAndProductIdIn(Integer userId, List<Integer> productList);

    /**
     * 通过商品id更新选中状态
     * @param userId 用户id
     * @param productId 商品id
     * @param checked 选中状态
     */
    @Modifying
    @Query("update CartEntity c set c.checked=:checked where c.userId = :userId and c.productId = :productId")
    void updateCheckedByUserIdAndProductId(@Param("checked") Integer checked,@Param("userId") Integer userId,@Param("productId") Integer productId);


    /**
     * 更新全部全部选中状态
     * @param checked 选中状态
     * @param userId 用户id
     */
    @Modifying
    @Query("update CartEntity c set c.checked=:checked where c.userId = :userId")
    void updateCheckedByUserId(@Param("checked") Integer checked,@Param("userId") Integer userId);

    /**
     * 查询当前用户的购物车商品数量
     * @param userId 用户id
     * @return 查询条数
     */
    int countAllByUserId(Integer userId);


    /**
     * 通过选中状态查找购物车中选中的商品
     * @param userId 用户id
     * @param checked 选中状态
     * @return 查找结果
     */
    List<CartEntity> findAllByUserIdAndChecked(Integer userId, int checked);
}
