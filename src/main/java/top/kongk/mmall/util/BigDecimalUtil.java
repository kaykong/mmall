package top.kongk.mmall.util;

import java.math.BigDecimal;

/**
 * 描述：计算价格的工具类
 *
 * @author kk
 * @date 2018/9/28 11:09
 */
public class BigDecimalUtil {
    public static BigDecimal add(Double a, Double b) {
        BigDecimal a1 = new BigDecimal(a.toString());
        BigDecimal b1 = new BigDecimal(b.toString());
        return a1.add(b1);
    }

    public static BigDecimal subtract(Double a, Double b) {
        BigDecimal a1 = new BigDecimal(a.toString());
        BigDecimal b1 = new BigDecimal(b.toString());
        return a1.subtract(b1);
    }

    public static BigDecimal multiply(Double a, Double b) {
        BigDecimal a1 = new BigDecimal(a.toString());
        BigDecimal b1 = new BigDecimal(b.toString());
        return a1.multiply(b1);
    }

    public static BigDecimal divide(Double a, Double b) {
        BigDecimal a1 = new BigDecimal(a.toString());
        BigDecimal b1 = new BigDecimal(b.toString());
        //四舍五入, 保留两位小数
        return a1.divide(b1, 2, BigDecimal.ROUND_UP);
    }

}
