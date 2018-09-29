package top.kongk.mmall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.kongk.mmall.common.Const;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.pojo.User;
import top.kongk.mmall.service.CartService;

import javax.servlet.http.HttpSession;

/**
 * 描述：购物车 service
 *
 * @author kk
 * @date 2018/9/28 10:17
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return cartService.getList(user.getId());
    }

    @RequestMapping(value = "/add.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse add(HttpSession session, Integer productId, Integer count) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return cartService.add(user.getId(), productId, count);
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse update(HttpSession session, Integer productId, Integer count) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return cartService.update(user.getId(), productId, count);
    }

    @RequestMapping(value = "/delete_product.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse update(HttpSession session, String productIds) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return cartService.deleteProduct(user.getId(), productIds);
    }

    @RequestMapping("/select.do")
    @ResponseBody
    public ServerResponse select(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return cartService.setCheck(user.getId(), productId, Const.Cart.CHECKED);
    }

    @RequestMapping("/un_select.do")
    @ResponseBody
    public ServerResponse unSelect(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return cartService.setCheck(user.getId(), productId, Const.Cart.UN_CHECKED);
    }

    @RequestMapping("/get_cart_product_count.do")
    @ResponseBody
    public ServerResponse getCartProductCount(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createSuccess(0);
        }

        return cartService.getCartProductCount(user.getId());
    }

    @RequestMapping("/select_all.do")
    @ResponseBody
    public ServerResponse selectAll(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return cartService.selectAllOrNot(user.getId(), Const.Cart.CHECKED);
    }

    @RequestMapping("/un_select_all.do")
    @ResponseBody
    public ServerResponse unSelectAll(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return cartService.selectAllOrNot(user.getId(), Const.Cart.UN_CHECKED);
    }
}
