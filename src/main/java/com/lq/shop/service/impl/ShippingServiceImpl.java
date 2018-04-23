package com.lq.shop.service.impl;

import com.google.common.collect.Maps;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.dao.ShippingRepository;
import com.lq.shop.entity.ShippingEntity;
import com.lq.shop.service.IShippingService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : luqing
 * @date : 2018/4/23 14:53
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {


    private ShippingRepository shippingRepository;

    @Autowired
    public void setShippingRepository(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }

    @Override
    @Transactional(rollbackFor = {})
    public ServerResult addShipping(Integer userId, ShippingEntity shipping) {
        shipping.setUserId(userId);
        ShippingEntity save = shippingRepository.save(shipping);
        if (save != null) {
            Map<String, Integer> result = Maps.newHashMap();
            result.put("shippingId", shipping.getId());
            return ServerResult.createBySuccess("新建地址成功", result);
        }
        return ServerResult.createByErrorMessage("新建地址失败");

    }

    @Override
    @Transactional(rollbackFor = {})
    public ServerResult delShipping(Integer userId, Integer shippingId) {
        int resultCount = shippingRepository.deleteByUserIdAndId(userId, shippingId);
        if (resultCount > 0) {
            return ServerResult.createBySuccess("删除地址成功");
        }
        return ServerResult.createByErrorMessage("删除地址失败");
    }

    @Override
    @Transactional(rollbackFor = {})
    public ServerResult updateShipping(Integer userId, ShippingEntity shipping) {
        ServerResult<ShippingEntity> result = this
            .checkShippingBelongUser(userId, shipping.getId());
        if (result.isSuccess()) {
            shipping.setUserId(userId);
            ShippingEntity save = shippingRepository.save(shipping);
            if (save != null) {
                return ServerResult.createBySuccess("更新地址成功",save.getId());
            }
            return ServerResult.createByErrorMessage("更新地址失败");
        }
        return result;
    }

    @Override
    public ServerResult<ShippingEntity> selectShipping(Integer userId, Integer shippingId) {
        return this.checkShippingBelongUser(userId, shippingId);
    }

    @Override
    public ServerResult<Page> getShippingList(Integer userId, int pageNum, int pageSize) {
        Pageable pageable = new PageRequest(pageNum, pageSize);
        Page<ShippingEntity> shippingPage = shippingRepository.findByUserId(userId, pageable);
        return ServerResult.createBySuccess(shippingPage);
    }


    private ServerResult<ShippingEntity> checkShippingBelongUser(Integer userId, Integer shippingId) {

        if (shippingId == null){
            return ServerResult.createByErrorMessage("请输入正确的参数");
        }

        ShippingEntity one = shippingRepository.findOne(shippingId);

        if (one == null || !userId.equals(one.getUserId())) {
            return ServerResult.createByErrorMessage("该收货地址不存在或不属于您");
        }
        return ServerResult.createBySuccess(one);
    }
}
