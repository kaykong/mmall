package top.kongk.mmall.common;

import org.apache.commons.lang3.StringUtils;
import top.kongk.mmall.pojo.User;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 描述：用户验证工具类
 *
 * @author kk
 * @date: 2018/9/23 21:59
 */
public class UserValidatorUtil {
    /**
     * 正则表达式：验证用户名
     */
    public static final String REGEX_USERNAME = "^[a-zA-Z0-9]\\w{6,20}$";

    /**
     * 正则表达式：验证密码
     */
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,20}$";

    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_PHONE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";


    /**
     * 校验用户名
     *
     * @param username 用户名
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return username != null && Pattern.matches(REGEX_USERNAME, username);
    }

    /**
     * 校验密码
     *
     * @param password 密码
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return password != null && Pattern.matches(REGEX_PASSWORD, password);
    }

    /**
     * 校验手机号
     *
     * @param phone 手机号
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPhone(String phone) {
        return phone != null && Pattern.matches(REGEX_PHONE, phone);
    }

    /**
     * 校验邮箱
     *
     * @param email 邮箱
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {

        return email != null && Pattern.matches(REGEX_EMAIL, email);
    }

    /**
     * 描述：检查传来的字符串是否是空白的，或者包含空格
     *
     * @param string 字符串
     * @return boolean
     */
    private static boolean isBlankOrContainsWhitespace(String string) {
        return StringUtils.isBlank(string) || StringUtils.containsWhitespace(string);
    }

    /**
     * 描述：检查用户角色是否合法，目前只有0-管理员,1-普通用户
     *
     * @param role 用户角色
     * @return boolean
     */
    public static boolean isRole(Integer role) {
        return role != null && (role == Const.ROLE_ADMIN || role == Const.ROLE_NORMAL);
    }

    /**
     * 描述：检查密保答案不是空白，不包含空格
     *
     * @param answer 密保答案
     * @return boolean
     */
    public static boolean isAnswer(String answer) {
        return !isBlankOrContainsWhitespace(answer);
    }

    /**
     * 描述：判断用户表必须的用户名，密码，角色是否格式正确
     *
     * @param user 用户
     * @return java.util.List<java.lang.String> 如果全部正确，返回null；否则返回list错误信息
     */
    public static List<String> isUserWithInfo(User user) {
        List<String> list = new ArrayList<>();

        //数据库必须要的用户名，密码，角色
        if (!UserValidatorUtil.isUsername(user.getUsername())) {
            list.add("用户名格式错误");
        }
        if (!UserValidatorUtil.isPassword(user.getPassword())) {
            list.add("密码格式错误");
        }
        if (!UserValidatorUtil.isRole(user.getRole())) {
            list.add("用户身份格式错误");
        }

        //非必须的
        if (user.getEmail() != null && !UserValidatorUtil.isEmail(user.getEmail())) {
            list.add("邮箱格式错误");
        }
        if (user.getPhone() != null && !UserValidatorUtil.isPhone(user.getPhone())) {
            list.add("手机号格式错误");
        }

        return list.isEmpty() ? null : list;
    }


}
