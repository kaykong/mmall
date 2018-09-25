package top.kongk.mmall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.kongk.mmall.common.Const;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.pojo.User;
import top.kongk.mmall.service.UserService;

import javax.servlet.http.HttpSession;

/**
 * 描述：后台管理用户模块
 *
 * @author kk
 * @date 2018/9/25 8:25
 */

@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse login(String username, String password, HttpSession session) {

        ServerResponse userResponse = userService.loginByUsernameAndPwd(username, password);

        if (userResponse.isSuccess()) {
            User user = (User) userResponse.getData();
            if (user.getRole() == Const.ROLE_ADMIN) {
                session.setAttribute(Const.CURRENT_ADMIN, user);
            } else {
                return ServerResponse.createSuccessWithMsg("普通用户无权限");
            }
        }

        return userResponse;
    }

}
