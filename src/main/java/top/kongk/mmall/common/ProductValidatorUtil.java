package top.kongk.mmall.common;

/**
 * 描述：
 *
 * @author kk
 * @date 2018/9/26 12:19
 */
public class ProductValidatorUtil {

    public static boolean isStatus(Integer status) {
        if (status == null) {
            return false;
        }

        if (status == Const.STATUS_ONSALE || status == Const.STATUS_NOT_ONSALE || status == Const.STATUS_DELETE) {
            return true;
        } else {
            return false;
        }
    }
}
