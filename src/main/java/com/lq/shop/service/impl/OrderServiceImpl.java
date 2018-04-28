package com.lq.shop.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayResponse;

import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lq.shop.common.response.Const;
import com.lq.shop.common.response.Const.Cart;
import com.lq.shop.common.response.Const.OrderStatusEnum;

import com.lq.shop.common.response.Const.ProductStatusEnum;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.common.util.BigDecimalUtil;
import com.lq.shop.common.util.DateTimeUtil;
import com.lq.shop.common.util.FtpUtil;
import com.lq.shop.common.util.PropertiesUtil;
import com.lq.shop.dao.CartRepository;
import com.lq.shop.dao.OrderItemRepository;
import com.lq.shop.dao.OrderRepository;
import com.lq.shop.dao.PayInfoRepository;
import com.lq.shop.dao.ProductRepository;
import com.lq.shop.dao.ShippingRepository;
import com.lq.shop.entity.CartEntity;
import com.lq.shop.entity.OrderEntity;
import com.lq.shop.entity.OrderItemEntity;
import com.lq.shop.entity.PayInfoEntity;
import com.lq.shop.entity.ProductEntity;
import com.lq.shop.entity.ShippingEntity;
import com.lq.shop.service.IOrderService;
import com.lq.shop.vo.OrderItemVO;
import com.lq.shop.vo.OrderProductVO;
import com.lq.shop.vo.OrderVO;
import com.lq.shop.vo.ShippingVO;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : luqing
 * @date : 2018/4/26 14:05
 */


@Service("iOrderService")
@NoArgsConstructor
public class OrderServiceImpl implements IOrderService {


    private OrderRepository orderRepository;

    private OrderItemRepository orderItemRepository;

    private PayInfoRepository payInfoRepository;

    private CartRepository cartRepository;

    private ProductRepository productRepository;

    private ShippingRepository shippingRepository;

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Autowired
    public void setOrderItemRepository(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Autowired
    public void setPayInfoRepository(PayInfoRepository payInfoRepository) {
        this.payInfoRepository = payInfoRepository;
    }

    @Autowired
    public void setCartRepository(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setShippingRepository(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }

    private Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static AlipayTradeService tradeService;

    static {

        /*
         *  一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /*
         *  使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }


    @Override
    public ServerResult pay(Long orderNo, Integer userId, String path) {

        Map<String, String> resultMap = Maps.newHashMap();
        OrderEntity order = orderRepository.findByUserIdAndOrderNo(userId, orderNo);

        if (order == null) {
            return ServerResult.createByErrorMessage("没有当前订单");
        }

        //获取支付状态
        ServerResult result = this.alipayTradeRecreate(userId, order);

        //如果支付不成功，直接返回错误状态
        if (!result.isSuccess()) {
            return result;
        }

        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));

        AlipayTradePrecreateResponse response = (AlipayTradePrecreateResponse) result.getData();

        File folder = new File(path);
        if (!folder.exists()) {
            boolean setWritable = folder.setWritable(true);
            boolean mkdirs = folder.mkdirs();
            if (!setWritable || mkdirs) {
                log.info("创建文件夹失败");
            }
        }

        String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
        String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
        ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

        File targetFile = new File(path, qrFileName);
        try {
            FtpUtil.uploadFile(Lists.newArrayList(targetFile));
        } catch (IOException e) {
            log.error("二维码上传异常:", e);
        }

        log.info("qrPath:" + path);

        String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
        resultMap.put("qrUrl", qrUrl);
        return ServerResult.createBySuccess(resultMap);
    }


    /**
     * 测试当面付2.0生成支付二维码
     */
    private ServerResult alipayTradeRecreate(Integer userId, OrderEntity order) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "在线鲜花商城付款,订单号:" + order.getOrderNo();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "订单" + outTradeNo + "购买商品共" + totalAmount + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        //支付的回掉地址
        String notifyUrl = PropertiesUtil.getProperty("alipay.callback.url");

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail

        List<OrderItemEntity> orderItemList = orderItemRepository
            .findByUserIdAndOrderNo(userId, order.getOrderNo());

        // 创建好一个商品后添加至商品明细列表
        for (OrderItemEntity orderItem : orderItemList) {
            GoodsDetail goods = GoodsDetail
                .newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), 100d)
                        .longValue(), orderItem.getQuantity());

            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
            .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
            .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
            .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
            .setTimeoutExpress(timeoutExpress)
            //支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
            .setNotifyUrl(notifyUrl)
            .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                this.dumpResponse(response);
                return ServerResult.createBySuccess(response);
            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResult.createByErrorMessage("支付宝预下单失败!!!");
            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResult.createByErrorMessage("系统异常，预下单状态未知!!!");
            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResult.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    /**
     * 简单打印应答
     */
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                    response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }


    @Override
    @Transactional(rollbackFor = {})
    public ServerResult aliCallback(Map<String, String[]> requestParams) {

        Map<String, String> params = Maps.newHashMap();

        for (Map.Entry<String, String[]> entity : requestParams.entrySet()) {
            String name = entity.getKey();
            String[] values = entity.getValue();
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr =
                    (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        log.info("支付宝回调结果");
        log.info("sign：" + params.get("sign"));
        log.info("trade_status：" + params.get("trade_status"));
        log.info("参数：" + params.toString());

        //验证回调的正确性，是不是支付宝发的，并且还要避免重读通知
        params.remove("sign_type");

        try {
            boolean rsaCheckV2 = AlipaySignature
                .rsaCheckV2(params, Configs.getPublicKey(), "UTF-8");

            if (!rsaCheckV2) {
                log.info("非法请求，验证不通过");
                return ServerResult.createByErrorMessage("非法请求，验证不通过");
            }

        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常", e);
        }

        //各种数据的验证
        return aliCallbackCheck(params);
    }


    private ServerResult aliCallbackCheck(Map<String, String> params) {

        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        OrderEntity order = orderRepository.findByOrderNo(orderNo);

        if (order == null) {
            log.warn("找不到该订单");
            return ServerResult.createBySuccess("找不到该订单");
        }

        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            log.warn("重复调用");
            return ServerResult.createBySuccess("重复调用");
        }

        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(OrderStatusEnum.PAID.getCode());
            orderRepository.save(order);
        }

        PayInfoEntity payInfo = new PayInfoEntity();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlayFromEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoRepository.save(payInfo);

        return ServerResult.createBySuccess();
    }


    @Override
    public ServerResult findOrderPayStatus(Integer userId, Long orderNo) {

        OrderEntity order = orderRepository.findByUserIdAndOrderNo(userId, orderNo);

        if (order == null) {
            return ServerResult.createByErrorMessage("用户没有该订单");
        }

        if (order.getStatus() >= OrderStatusEnum.PAID.getCode()) {
            return ServerResult.createBySuccess(true);
        }

        return ServerResult.createByError();
    }

    @Override
    @Transactional(rollbackFor = {})
    public ServerResult createOrder(Integer userId, Integer shippingId) {

        List<CartEntity> cartEntityList = cartRepository
            .findAllByUserIdAndChecked(userId, Cart.CHECKED);

        ServerResult<List<OrderItemEntity>> result = this.getCartOrderItem(userId, cartEntityList);

        if (!result.isSuccess()) {
            return result;
        }

        List<OrderItemEntity> orderItemEntityList = result.getData();
        BigDecimal payment = this.getOrderTotalPrice(orderItemEntityList);

        OrderEntity order = this.assembleOrder(userId, shippingId, payment);

        if (order == null){
            return ServerResult.createBySuccess("生成订单错误");
        }

        for (OrderItemEntity orderItem : orderItemEntityList){
            orderItem.setOrderNo(order.getOrderNo());
        }

        orderItemRepository.save(orderItemEntityList);

        this.reduceProductStock(orderItemEntityList);

        this.cleanCart(cartEntityList);

        OrderVO orderVO = this.assembleOrderVO(order,orderItemEntityList);

        return ServerResult.createBySuccess(orderVO);
    }

    @Override
    @Transactional(rollbackFor = {})
    public ServerResult cancel(Integer userId, Long orderNo) {

        OrderEntity order = orderRepository.findByUserIdAndOrderNo(userId,orderNo);

        if (order == null){
            return ServerResult.createByErrorMessage("没有该订单");
        }

        if (!Objects.equals(order.getStatus(), OrderStatusEnum.NO_PAY.getCode())){
            return ServerResult.createByErrorMessage("该订单无法取消付款");
        }

        order.setStatus(OrderStatusEnum.CANCELED.getCode());

        OrderEntity save = orderRepository.save(order);

        if(save != null){
            return ServerResult.createBySuccess("取消订单成功");
        }

        return ServerResult.createByErrorMessage("取消订单失败");
    }

    @Override
    public ServerResult getOrderProduct(Integer userId) {

        OrderProductVO orderProductVO = new OrderProductVO();

        List<CartEntity> cartEntityList = cartRepository.findAllByUserIdAndChecked(userId, Cart.CHECKED);

        ServerResult<List<OrderItemEntity>> result = this.getCartOrderItem(userId,cartEntityList);

        if (!result.isSuccess()){
            return result;
        }

        List<OrderItemEntity> orderItemEntityList = result.getData();

        List<OrderItemVO> orderItemVOList = Lists.newArrayList();

        BigDecimal payment = new BigDecimal("0");
        for (OrderItemEntity orderItem : orderItemEntityList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVOList.add(assembleOrderItemVO(orderItem));
        }

        orderProductVO.setProductTotalPrice(payment);
        orderProductVO.setOrderItemVoList(orderItemVOList);
        orderProductVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return ServerResult.createBySuccess(orderProductVO);
    }

    @Override
    public ServerResult getOrderDetail(Integer userId, Long orderNo) {
        OrderEntity order = orderRepository.findByUserIdAndOrderNo(userId,orderNo);

        if (order == null){
            return  ServerResult.createByErrorMessage("该订单不存在");
        }

        List<OrderItemEntity> orderItemEntityList = orderItemRepository.findByUserIdAndOrderNo(userId,orderNo);
        OrderVO orderVO = this.assembleOrderVO(order,orderItemEntityList);
        return ServerResult.createBySuccess(orderVO);
    }

    @Override
    public ServerResult getOrderList(Integer userId, Integer pageNum, Integer pageSize) {

        Pageable pageable = new PageRequest(pageNum,pageSize);

        Page<OrderEntity> orderPage = orderRepository.findAllByUserId(userId,pageable);
        List<OrderVO> orderVOList = this.assembleOrderVOList(userId,orderPage.getContent());

        Page page = new PageImpl<>(orderVOList,pageable,orderPage.getTotalElements());

        return ServerResult.createBySuccess(page);
    }

    @Override
    @Transactional(rollbackFor = {})
    public ServerResult deliveryGoods(Integer userId, Long orderNo) {

        OrderEntity order = orderRepository.findByUserIdAndOrderNo(userId,orderNo);

        if (order == null){
            return ServerResult.createByErrorMessage("找不到订单");
        }

        if (!Objects.equals(OrderStatusEnum.SHIPPED.getCode(),order.getStatus())){
            return ServerResult.createByErrorMessage("该订单状态无法收货");
        }

        order.setStatus(OrderStatusEnum.ORDER_SUCCESS.getCode());
        order.setEndTime(new Date());
        orderRepository.save(order);
        return ServerResult.createBySuccess("订单收货成功");
    }

    @Override
    public ServerResult manageList(Integer pageNum, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNum,pageSize);

        Page<OrderEntity> orderPage = orderRepository.findAll(pageable);
        List<OrderVO> orderVOList = this.assembleOrderVOList(null,orderPage.getContent());

        Page page = new PageImpl<>(orderVOList,pageable,orderPage.getTotalElements());

        return ServerResult.createBySuccess(page);
    }

    @Override
    public ServerResult manageDetail(Long orderNo) {

        OrderEntity order = orderRepository.findByOrderNo(orderNo);

        if (order == null){
            return  ServerResult.createByErrorMessage("该订单不存在");
        }

        List<OrderItemEntity> orderItemEntityList = orderItemRepository.findByOrderNo(orderNo);
        OrderVO orderVO = this.assembleOrderVO(order,orderItemEntityList);
        return ServerResult.createBySuccess(orderVO);

    }

    @Override
    public ServerResult manageSearch(Long orderNo, Integer pageNum, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNum,pageSize);
        OrderEntity order = orderRepository.findByOrderNo(orderNo);
        if (order == null) {
            return ServerResult.createByErrorMessage("没有该订单");
        }

        Page<OrderItemEntity> orderItemPage = orderItemRepository.findByOrderNo(orderNo,pageable);
        OrderVO orderVO = this.assembleOrderVO(order,orderItemPage.getContent());
        Page<OrderVO> page = new PageImpl<>(Lists.newArrayList(orderVO),pageable,orderItemPage.getTotalElements());

        return ServerResult.createBySuccess(page);

    }

    @Override
    @Transactional(rollbackFor = {})
    public ServerResult manageSendGoods(Long orderNo) {

        OrderEntity order = orderRepository.findByOrderNo(orderNo);

        if (order == null){
            return ServerResult.createByErrorMessage("订单不存在");
        }

        if (!Objects.equals(order.getStatus(),OrderStatusEnum.PAID.getCode())){
            return ServerResult.createByErrorMessage("订单未付款或已经发货，无法发货");
        }
        order.setStatus(OrderStatusEnum.SHIPPED.getCode());
        order.setSendTime(new Date());

        orderRepository.save(order);
        return ServerResult.createBySuccess("发货成功");
    }

    private List<OrderVO> assembleOrderVOList(Integer userId, List<OrderEntity> orderEntityList) {

        List<OrderVO> orderVOList = Lists.newArrayList();
        for (OrderEntity order : orderEntityList){
            List<OrderItemEntity> orderItemEntityList = Lists.newArrayList();
            if (userId == null){
                orderItemEntityList = orderItemRepository.findByOrderNo(order.getOrderNo());
            }else {
                orderItemEntityList = orderItemRepository.findByUserIdAndOrderNo(userId,order.getOrderNo());
            }
            OrderVO orderVO = assembleOrderVO(order,orderItemEntityList);
            orderVOList.add(orderVO);
        }

        return orderVOList;
    }

    private OrderVO assembleOrderVO(OrderEntity order, List<OrderItemEntity> orderItemEntityList) {
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setPayment(order.getPayment());
        orderVO.setPaymentType(order.getPaymentType());
        orderVO.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVO.setPostage(order.getPostage());
        orderVO.setStatus(order.getStatus());
        orderVO.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVO.setShippingId(order.getShippingId());

        ShippingEntity shipping = shippingRepository.findOne(order.getShippingId());

        if (shipping != null){
            orderVO.setReceiverName(shipping.getReceiverName());
            orderVO.setShippingVo(this.assembleShippingVO(shipping));
        }

        orderVO.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVO.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVO.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVO.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVO.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

        orderVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        List<OrderItemVO> orderItemVOList = Lists.newArrayList();

        for (OrderItemEntity orderItem : orderItemEntityList){
            OrderItemVO orderItemVO = assembleOrderItemVO(orderItem);
            orderItemVOList.add(orderItemVO);
        }

        orderVO.setOrderItemVOList(orderItemVOList);

        return orderVO;
    }

    private OrderItemVO assembleOrderItemVO(OrderItemEntity orderItem) {
        OrderItemVO orderItemVO = new OrderItemVO();
        orderItemVO.setOrderNo(orderItem.getOrderNo());
        orderItemVO.setProductId(orderItem.getProductId());
        orderItemVO.setProductName(orderItem.getProductName());
        orderItemVO.setProductImage(orderItem.getProductImage());
        orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVO.setQuantity(orderItem.getQuantity());
        orderItemVO.setTotalPrice(orderItem.getTotalPrice());

        orderItemVO.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVO;
    }

    private ShippingVO assembleShippingVO(ShippingEntity shipping) {
        ShippingVO shippingVO = new ShippingVO();
        shippingVO.setReceiverName(shipping.getReceiverName());
        shippingVO.setReceiverAddress(shipping.getReceiverAddress());
        shippingVO.setReceiverProvince(shipping.getReceiverProvince());
        shippingVO.setReceiverCity(shipping.getReceiverCity());
        shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVO.setReceiverMobile(shipping.getReceiverMobile());
        shippingVO.setReceiverZip(shipping.getReceiverZip());
        shippingVO.setReceiverPhone(shipping.getReceiverPhone());
        return shippingVO;
    }

    private void cleanCart(List<CartEntity> cartEntityList) {
        for (CartEntity cartEntity : cartEntityList){
            cartRepository.delete(cartEntity.getId());
        }
    }

    private void reduceProductStock(List<OrderItemEntity> orderItemEntityList) {
        for (OrderItemEntity orderItemEntity : orderItemEntityList){
            ProductEntity product = productRepository.findOne(orderItemEntity.getProductId());
            product.setStock(product.getStock()-orderItemEntity.getQuantity());
            productRepository.save(product);
        }
    }

    private OrderEntity assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {

        OrderEntity order = new OrderEntity();
        //生成订单号
        long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);

        OrderEntity save = orderRepository.save(order);
        if (save != null) {
            return save;
        }

        return null;

    }

    private long generateOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);

    }

    private BigDecimal getOrderTotalPrice(List<OrderItemEntity> orderItemEntityList) {

        BigDecimal payment = new BigDecimal("0");

        for (OrderItemEntity orderItem : orderItemEntityList) {
            payment = BigDecimalUtil
                .add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }

        return payment;
    }

    private ServerResult<List<OrderItemEntity>> getCartOrderItem(Integer userId,
        List<CartEntity> cartEntityList) {

        List<OrderItemEntity> orderItemList = Lists.newArrayList();

        if (CollectionUtils.isEmpty(cartEntityList)) {
            return ServerResult.createByErrorMessage("购物车为空,无法创建订单");
        }

        for (CartEntity cartItem : cartEntityList) {
            OrderItemEntity orderItem = new OrderItemEntity();
            ProductEntity product = productRepository.findOne(cartItem.getProductId());

            if (!Objects.equals(ProductStatusEnum.ON_SALE.getCode(), product.getStatus())) {
                return ServerResult.createByErrorMessage("产品" + product.getName() + "不在销售状态");
            }

            if (cartItem.getQuantity() > product.getStock()) {
                return ServerResult.createByErrorMessage("产品" + product.getName() + "库存不足");
            }

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(
                BigDecimalUtil.mul(product.getPrice().doubleValue(), cartItem.getQuantity()));
            orderItemList.add(orderItem);
        }

        return ServerResult.createBySuccess(orderItemList);
    }

}