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
}
