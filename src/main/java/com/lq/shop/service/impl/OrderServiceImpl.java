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
import com.lq.shop.common.response.Const.OrderStatusEnum;
import com.lq.shop.common.response.ServerResult;
import com.lq.shop.common.util.BigDecimalUtil;
import com.lq.shop.common.util.DateTimeUtil;
import com.lq.shop.common.util.FtpUtil;
import com.lq.shop.common.util.PropertiesUtil;
import com.lq.shop.dao.OrderItemRepository;
import com.lq.shop.dao.OrderRepository;
import com.lq.shop.dao.PayInfoRepository;
import com.lq.shop.entity.OrderEntity;
import com.lq.shop.entity.OrderItemEntity;
import com.lq.shop.entity.PayInfoEntity;
import com.lq.shop.service.IOrderService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : luqing
 * @date : 2018/4/26 14:05
 */

@Service("iOrderService")
@Log4j
public class OrderServiceImpl implements IOrderService {


    private OrderRepository orderRepository;

    private OrderItemRepository orderItemRepository;

    private PayInfoRepository payInfoRepository;

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
        ServerResult result = this.alipayTradePrecreate(userId,order);

        //如果支付不成功，直接返回错误状态
        if (!result.isSuccess()){
            return result;
        }

        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));

        AlipayTradePrecreateResponse response = (AlipayTradePrecreateResponse) result.getData();

        File folder = new File(path);
        if (!folder.exists()){
            boolean setWritable = folder.setWritable(true);
            boolean mkdirs = folder.mkdirs();
            if (!setWritable || mkdirs){
                log.info("创建文件夹失败");
            }
        }

        String qrPath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
        String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
        ZxingUtils.getQRCodeImge(response.getQrCode(),256,qrPath);

        File targetFile = new File(path,qrFileName);
        try {
            FtpUtil.uploadFile(Lists.newArrayList(targetFile));
        }catch (IOException e){
            log.error("二维码上传异常:",e);
        }

        log.info("qrPath:"+path);

        String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
        resultMap.put("qrUrl",qrUrl);
        return ServerResult.createBySuccess(resultMap);
    }


    /**
     * 测试当面付2.0生成支付二维码
     */
    private ServerResult alipayTradePrecreate(Integer userId, OrderEntity order) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "鲜花商店在线扫码付款,订单号:" + order.getOrderNo();

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
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
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
    public ServerResult aliCallback(Map<String, String[]> requestParams) {

        Map<String,String> params = Maps.newHashMap();

        for (Map.Entry<String, String[]> entity : requestParams.entrySet()){
            String name = entity.getKey();
            String[] values = entity.getValue();
            String valueStr = "";
            for(int i = 0 ; i <values.length;i++){
                valueStr = (i == values.length -1)?valueStr + values[i]:valueStr + values[i]+",";
            }
            params.put(name,valueStr);
        }
        log.info("支付宝回调结果");
        log.info("sing："+params.get("sing"));
        log.info("trade_status："+params.get("trade_status"));
        log.info("参数："+params.toString());

        //验证回调的正确性，是不是支付宝发的，并且还要避免重读通知
        params.remove("sign_type");

        try {
            boolean rsaCheckV2 = AlipaySignature
                .rsaCheckV2(params, Configs.getPublicKey(), "utf-8", Configs.getSignType());

            if (!rsaCheckV2){
                return ServerResult.createBySuccess("非法请求，验证不通过");
            }

        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常",e);
        }

        //各种数据的验证
        return aliCallbackCheck(params);
    }



    private ServerResult aliCallbackCheck(Map<String, String> params) {

        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        OrderEntity order = orderRepository.findByOrderNo(orderNo);

        if (order == null){
            return ServerResult.createBySuccess("找不到该订单");
        }

        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResult.createBySuccess("重复调用");
        }

        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(OrderStatusEnum.PAID.getCode());
            orderRepository.save(order);
        }

        PayInfoEntity payInfo = new PayInfoEntity();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlayformEnm.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoRepository.save(payInfo);

        return ServerResult.createBySuccess();
    }


    @Override
    public ServerResult findOrderPayStatus(Integer userId, Long orderNo) {

        OrderEntity order = orderRepository.findByUserIdAndOrderNo(userId,orderNo);

        if (order == null){
            return ServerResult.createByErrorMessage("用户没有该订单");
        }

        if (order.getStatus() >= OrderStatusEnum.PAID.getCode()){
            return ServerResult.createBySuccess(true);
        }

        return ServerResult.createByError();
    }



}
