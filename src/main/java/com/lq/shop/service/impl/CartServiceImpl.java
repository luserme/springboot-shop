package com.lq.shop.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.ResultCode;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.common.util.BigDecimalUtil;
import com.lq.shop.common.util.PropertiesUtil;
import com.lq.shop.common.util.StringUtils;
import com.lq.shop.dao.CartRepository;
import com.lq.shop.dao.ProductRepository;
import com.lq.shop.entity.CartEntity;
import com.lq.shop.entity.ProductEntity;
import com.lq.shop.service.ICartService;
import com.lq.shop.vo.CartProductVO;
import com.lq.shop.vo.CartVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author luqing
 * @date 2018/04/22 18:00
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {


    private CartRepository cartRepository;

    private ProductRepository productRepository;

    @Autowired
    public void setCartRepository(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ServerResult<CartVO> getCartList(Integer userId) {

        CartVO cartVo = this.getCartVOLimit(userId);
        return ServerResult.createBySuccess(cartVo);

    }

    @Override
    @Transactional(rollbackFor = {})
    public ServerResult<CartVO> add(Integer userId, Integer productId, Integer count) {

        if(productId == null || count == null){
            return ServerResult.createByErrorCodeMessage(ResultCode.ILLEGAL_ARGUMENT.getCode(), ResultCode.ILLEGAL_ARGUMENT.getDesc());
        }

        CartEntity cart = cartRepository.findByUserIdAndProductId(userId,productId);
        if(cart == null){
            //这个产品不在这个购物车里,需要新增一个这个产品的记录
            CartEntity cartItem = new CartEntity();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartRepository.save(cartItem);
        }else{
            //这个产品已经在购物车里了.
            //如果产品已存在,数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartRepository.save(cart);
        }
        return this.getCartList(userId);
    }


    @Override
    @Transactional(rollbackFor = {})
    public ServerResult<CartVO> update(Integer userId, Integer productId, Integer count) {

        if(productId == null || count == null){
            return ServerResult.createByErrorCodeMessage(ResultCode.ILLEGAL_ARGUMENT.getCode(),ResultCode.ILLEGAL_ARGUMENT.getDesc());
        }
        CartEntity cart = cartRepository.findByUserIdAndProductId(userId,productId);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartRepository.save(cart);
        return this.getCartList(userId);

    }

    @Override
    @Transactional(rollbackFor = {})
    public ServerResult<CartVO> deleteProduct(Integer userId, String productIds) {

        if (StringUtils.isBlank(productIds)){
            return ServerResult.createByErrorMessage("请输入正确的格式");
        }

        List<Integer> productList = Lists.newArrayList();

        for (String productId : productIds.split(Const.Cart.PRODCUTID_DEC)){
            productList.add(Integer.parseInt(productId));
        }

        if(CollectionUtils.isEmpty(productList)){
            return ServerResult.createByErrorCodeMessage(ResultCode.ILLEGAL_ARGUMENT.getCode(),ResultCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartRepository.deleteByUserIdAndProductIdIn(userId,productList);
        return this.getCartList(userId);
    }

    @Override
    @Transactional(rollbackFor = {})
    public ServerResult<CartVO> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {

        if(productId != null){
            cartRepository.updateCheckedByUserIdAndProductId(checked,userId,productId);
        }else {
            cartRepository.updateCheckedByUserId(checked,userId);
        }

        return this.getCartList(userId);
    }

    @Override
    public ServerResult<Integer> getCartProductCount(Integer userId) {
        if(userId == null){
            return ServerResult.createBySuccess();
        }
        return ServerResult.createBySuccess(cartRepository.countAllByUserId(userId));
    }


    private CartVO getCartVOLimit(Integer userId){
        CartVO cartVO = new CartVO();
        List<CartEntity> cartList = cartRepository.findAllByUserId(userId);
        List<CartProductVO> cartProductVOList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if(CollectionUtils.isNotEmpty(cartList)){
            for(CartEntity cartItem : cartList){
                CartProductVO cartProductVO = new CartProductVO();
                cartProductVO.setId(cartItem.getId());
                cartProductVO.setUserId(userId);
                cartProductVO.setProductId(cartItem.getProductId());

                ProductEntity product = productRepository.findOne(cartItem.getProductId());
                if(product != null){
                    cartProductVO.setProductMainImage(product.getMainImage());
                    cartProductVO.setProductName(product.getName());
                    cartProductVO.setProductSubtitle(product.getSubtitle());
                    cartProductVO.setProductStatus(product.getStatus());
                    cartProductVO.setProductPrice(product.getPrice());
                    cartProductVO.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount;
                    if(product.getStock() >= cartItem.getQuantity()){
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount = product.getStock();
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        cartItem.setQuantity(buyLimitCount);
                        cartRepository.save(cartItem);
                    }
                    cartProductVO.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVO.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVO.getQuantity()));
                    cartProductVO.setProductChecked(cartItem.getChecked());
                }

                if(cartItem.getChecked() == Const.Cart.CHECKED){
                    //如果已经勾选,增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());
                }
                cartProductVOList.add(cartProductVO);
            }
        }
        cartVO.setCartTotalPrice(cartTotalPrice);
        cartVO.setCartProductVOList(cartProductVOList);
        cartVO.setAllChecked(this.getAllCheckedStatus(userId));
        cartVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVO;
    }



    private boolean getAllCheckedStatus(Integer userId) {
        return userId != null && cartRepository.findCartProductCheckedStatusByUserId(userId) == 0;
    }


}
