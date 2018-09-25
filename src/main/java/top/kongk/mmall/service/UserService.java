package top.kongk.mmall.service;

import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.pojo.User;

/**
 * 描述：用户模块的service接口
 *
 * @author kk
 * @date 2018/9/23 21:01
 */
public interface UserService {


    /**
     * 描述: 检查用户名是否可用
     *
     * @param username 用户名
     * @return ServerResponse
     *     用户名可以使用返回的status属性为ResponseCode.SUCCESS,否则为 ResponseCode.ERROR
     */
    ServerResponse checkUsername(String username);


    /**
     * 描述: 检验邮箱是否可用
     *
     * @param email 邮箱
     * @return top.kongk.mmall.common.ServerResponse
     *     邮箱可以使用返回的status属性为ResponseCode.SUCCESS,否则为 ResponseCode.ERROR
     */
    ServerResponse checkEmail(String email);

    /**
     * 描述：检查手机号是否可用
     *
     * @param phone 手机号
     * @return top.kongk.mmall.common.ServerResponse
     *      手机号可以使用返回的status属性为ResponseCode.SUCCESS,否则为 ResponseCode.ERROR
     */
    ServerResponse checkPhone(String phone);


    /**
     * 描述: 注册用户
     *
     * @param user 用户
     * @return top.kongk.mmall.common.ServerResponse
     *     注册成功返回的status属性为ResponseCode.SUCCESS,否则为 ResponseCode.ERROR
     */
    ServerResponse register(User user);


    /**
     * 描述: 通过用户名和密码验证该用户是否存在
     *
     * @param username 用户名
     * @param password 密码
     * @return top.kongk.mmall.common.ServerResponse
     *     验证成功, data属性为 user(不包含password);
     *     验证失败, msg为提示信息;
     */
    ServerResponse loginByUsernameAndPwd(String username, String password);


    /**
     * 描述：根据用户名返回密保问题
     *
     * @param username 用户名
     * @return top.kongk.mmall.common.ServerResponse
     *      如果查找到了密保问题，则data属性为密保问题，status属性为ResponseCode.SUCCESS；
     *      否则，会在msg属性中给出提示信息，status属性为ResponseCode.ERROR。
     */
    ServerResponse getQuestion(String username);

    /**
     * 描述: 检查 用户名，密保问题，密保答案是否匹配
     *
     * @param username 用户名
     * @param question 问题
     * @param answer 答案
     * @return top.kongk.mmall.common.ServerResponse
     *      验证成功 data属性为token;
     *      验证失败 msg属性为提示信息;
     */
    ServerResponse checkAnswer(String username, String question, String answer);



    /**
     * 描述：重置密码
     *
     * @param username 用户名
     * @param newPassword 新密码
     * @param forgetToken 重置密码的token
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse resetPasswordByToken(String username, String newPassword, String forgetToken);


    /**
     * 描述：把用户的密码改为新密码，可用于在登录状态下修改密码
     *
     * @param username 用户名
     * @param password 密码
     * @param newPassword 新密码
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse resetPasswordByOldPwd(String username, String password, String newPassword);


    /**
     * 描述：根据用户id更新（邮箱，手机，问题，答案）中不为null的字段
     *
     * @param user 用户（邮箱，手机，问题，答案）
     * @return top.kongk.mmall.common.ServerResponse
     *  更新成功返回的 status 为ResponseCode.SUCCESS，否则为ResponseCode.ERROR
     */
    ServerResponse updateInformation(User user);

    /**
     * 描述：根据用户id，查找用户除了密码之外的所有详细信息
     *
     * @param id 用户id
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse getAllInformationWithoutPwd(Integer id);

}
