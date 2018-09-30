package top.kongk.mmall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.kongk.mmall.common.Const;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.pojo.Shipping;
import top.kongk.mmall.pojo.User;
import top.kongk.mmall.service.ShippingService;

import javax.servlet.http.HttpSession;

/**
 * 描述：收货地址模块的 controller
 *
 * @author kk
 * @date 2018/9/29 17:24
 */
@Controller
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    ShippingService shippingService;

    @RequestMapping(value = "/add.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return shippingService.add(user.getId(), shipping);
    }

    @RequestMapping(value = "/del.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse del(HttpSession session, Integer shippingId) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return shippingService.delete(user.getId(), shippingId);
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse update(HttpSession session, Shipping shipping) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return shippingService.update(user.getId(), shipping);
    }

    @RequestMapping(value = "/select.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse select(HttpSession session, Integer shippingId) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return shippingService.select(user.getId(), shippingId);
    }

    @RequestMapping(value = "/list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse list(HttpSession session,
                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum ,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return shippingService.list(user.getId(), pageNum, pageSize);
    }
}
