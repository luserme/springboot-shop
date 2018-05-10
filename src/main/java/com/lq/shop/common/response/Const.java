package com.lq.shop.common.response;

import com.google.common.collect.Sets;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * @author : luqing
 * @date : 2018/4/19 14:20
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public static final Integer CHECK_CODE_LENGTH=6;

    @Getter
    @AllArgsConstructor
    public enum PayPlayFromEnum {

        /**
         * 支付方式
         */
        ALIPAY(1,"支付宝"),
        WECHATPAY(2,"微信");

        private Integer code;
        private String value;

    }

    @Getter
    @AllArgsConstructor
    public enum PaymentTypeEnum {

        /**
         * 支付方式
         */
        ONLINE_PAY(1,"在线支付");

        private Integer code;
        private String value;

        public static PaymentTypeEnum codeOf(Integer code) {
            for(PaymentTypeEnum paymentTypeEnum : values()){
                if(Objects.equals(paymentTypeEnum.getCode(), code)){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }


    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

    @Getter
    @AllArgsConstructor
    public enum ProductStatusEnum{
        /**
         * 在售状态
         */
        ON_SALE(1,"在线");

        private Integer code;
        private String value;

    }


    @Getter
    @AllArgsConstructor
    public enum OrderStatusEnum {

        /**
         * code 订单状态对应的状态码
         * value 订单状态
         */
        CANCELED(0,"已取消"),
        NO_PAY(10,"未付款"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");

        private Integer code;
        private String value;

        public static OrderStatusEnum codeOf(Integer code) {
            for(OrderStatusEnum orderStatusEnum : values()){
                if(Objects.equals(orderStatusEnum.getCode(), code)){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public interface Role {

        /**
         * 普通用户
         */
        int ROLE_CUSTOMER = 0;

        /**
         * 管理员
         */
        int ROLE_ADMIN = 1;
    }


    public interface Page {

        /**
         * 分页默认条数
         */
        String PAGE_DEFAULT_SIZE = "10";

        /**
         * 分页默认的页码
         */
        String PAGE_DEFAULT_NUM = "0";
    }


    public interface Cart{

        /**
         * 购物车选中状态
         */
        int CHECKED = 1;
        /**
         * 购物车中未选中状态
         */
        int UN_CHECKED = 0;

        /**
         * 增加数量大于库存数量 返回fail
         */
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        /**
         * 增加数量大于库存数量 返回success
         */
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";

        /**
         * 购物车中商品ids分隔符
         */
        String PRODUCT_ID_DEC = ",";
    }

    public interface AlipayCallback {
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public interface RedisCacheExtime {

        int REDIS_SESSION_EXTIME = 60*60;
    }
}
