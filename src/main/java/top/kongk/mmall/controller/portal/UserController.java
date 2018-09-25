package top.kongk.mmall.controller.portal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.kongk.mmall.common.Const;
import top.kongk.mmall.common.ResponseCode;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.pojo.User;
import top.kongk.mmall.service.UserService;
import javax.servlet.http.HttpSession;

/**
 * 描述：用户模块 controller 层
 *
 * @author kk
 * @date 2018/9/24 13:37
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    /**
     * 描述：登录
     *
     * @param username 用户名
     * @param password 密码
     * @param session  session
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse login(String username, String password, HttpSession session) {

        ServerResponse loginResponse = userService.loginByUsernameAndPwd(username, password);

        if (loginResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            session.setAttribute(Const.CURRENT_USER, loginResponse.getData());
        }

        return loginResponse;
    }

    /**
     * 描述：注册
     *
     * @param user username,password,email,phone,question,answer
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse register(User user) {
        return userService.register(user);
    }

    /**
     * 描述：检查用户名或者邮箱是否有效
     * /check_valid.do?str=admin&type=username就是检查用户名。
     * /check_valid.do?str=12345@qq.com&type=email就是检查邮箱。
     *
     * @param str  用户名或者邮箱
     * @param type username | email
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkValid(String str, String type) {

        if (Const.USERNAME.equals(type)) {
            return userService.checkUsername(str);
        } else if (Const.EMAIL.equals(type)) {
            return userService.checkEmail(str);
        }

        return ServerResponse.createErrorWithMsg("李耀干森麽？");
    }

    /**
     * 描述：获取登录用户信息
     *
     * @param session session
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getUserinfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        if (user != null) {
            return ServerResponse.createSuccess(user);
        } else {
            return ServerResponse.createErrorWithMsg("用户未登录,无法获取当前用户信息");
        }
    }

    /**
     * 描述：忘记密码时，根据用户名返回密保问题
     *
     * @param username 用户名
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getQuestion(String username) {
        return userService.getQuestion(username);
    }

    /**
     * 描述：提交问题答案
     *
     * @param username 用户名
     * @param question 问题
     * @param answer   答案
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkAnswer(String username, String question, String answer) {

        return userService.checkAnswer(username, question, answer);
    }

    /**
     * 描述：忘记密码的重设密码
     *
     * @param username    用户名
     * @param passwordNew 新密码
     * @param forgetToken 验证问题后获得的token
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse resetPasswordByToken(String username, String passwordNew, String forgetToken) {

        return userService.resetPasswordByToken(username, passwordNew, forgetToken);
    }

    /**
     * 描述：登录中状态重置密码
     *
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @param session     session
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse resetPasswordByOldPwd(String passwordOld, String passwordNew, HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);

        if (user == null) {
            return ServerResponse.createErrorWithMsg("用户未登录");
        } else {
            return userService.resetPasswordByOldPwd(user.getUsername(), passwordOld, passwordNew);
        }
    }


    /**
     * 描述：登录状态更新个人信息 email,phone,question,answer
     *
     * @param user    从前台传来的user
     * @param session session
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateInformation(User user, HttpSession session) {

        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);

        if (currentUser == null) {
            return ServerResponse.createErrorWithMsg("用户未登录");
        }
        user.setId(currentUser.getId());

        return userService.updateInformation(user);
    }

    /**
     * 描述：获取当前登录用户除了密码之外的所有详细信息，并强制登录
     *
     * @param session session
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getAllInformationWithoutPwd(HttpSession session) {

        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);

        if (currentUser == null) {
            return ServerResponse.createNeedloginError("用户未登录,无法获取当前用户信息,status=10,强制登录");
        }

        return userService.getAllInformationWithoutPwd(currentUser.getId());
    }

    /**
     * 描述：退出登录,出错的话返回错误信息
     *
     * @param session session
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse logout(HttpSession session) {
        try {
            session.setAttribute(Const.CURRENT_USER, null);
        } catch (Exception e) {
            logger.error("UserController.logout Execption", e);
            return ServerResponse.createErrorWithMsg("服务器错误");
        }

        return ServerResponse.createSuccessWithMsg("退出成功");
    }

}
