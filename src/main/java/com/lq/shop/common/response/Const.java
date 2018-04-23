package com.lq.shop.common.response;

/**
 * @author : luqing
 * @date : 2018/4/19 14:20
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

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
}
