package top.kongk.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * 描述：项目中用到的常量
 *
 * @author kk
 * @date 2018/9/24 16:38
 */
public class Const {
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String CURRENT_USER = "currentUser";
    public static final String CURRENT_ADMIN = "currentAdmin";
    public static final int ROLE_ADMIN = 0;
    public static final int ROLE_NORMAL = 1;
    public static final int STATUS_ONSALE = 1;
    public static final int STATUS_NOT_ONSALE = 1;
    public static final int STATUS_DELETE = 3;

    public interface ProductSort {
        Set<String> sort = Sets.newHashSet("price asc", "price desc");
    }

    public interface Cart {
        /**
         * 根据购物车的商品的数量是否满足库存来赋值
         */
        String LIMIT_QUANTITY_SUCCESS = "LIMIT_NUM_SUCCESS";
        String LIMIT_QUANTITY_FAIL = "LIMIT_NUM_FAIL";
        /**
         * 购物车选中状态
         */
        int CHECKED = 1;
        int UN_CHECKED = 0;
    }

    public enum OrderStatus {
        /**
         * 订单状态用到的枚举
         */
        CANCELED("已取消", 0),
        NO_PAY("未支付", 10),
        PAIED("已支付", 20),
        SHIPPED("已发货", 40),
        ORDER_SUCCESS("订单已完成", 50),
        ORDER_CLOSE("订单已关闭", 60);

        private String value;
        private int code;

        OrderStatus(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static String codeOf(int code) {
            for (OrderStatus orderStatus : values()) {
                if (orderStatus.getCode() == code) {
                    return orderStatus.getValue();
                }
            }

            return null;
        }
    }

    /**
     * 阿里官方 交易状态码
     */
    public interface AlipayCallback {

        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";

    }

    /**
     * 支付平台
     */
    public enum PayPlatformEnum {
        /**
         * 支付平台
         */
        ALIPAY(1, "支付宝");

        PayPlatformEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 支付方式
     */
    public enum PaymentTypeEnum {
        /**
         * 在线支付
         */
        ONLINE_PAY(1, "在线支付");

        PaymentTypeEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        private int code;
        private String value;

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public static String codeOf(int code) {
            for (PaymentTypeEnum paymentTypeEnum : values()) {
                if (paymentTypeEnum.getCode() == code) {
                    return paymentTypeEnum.getValue();
                }
            }
            return null;
        }
    }
}
