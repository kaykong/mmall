package top.kongk.mmall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.kongk.mmall.common.Const;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.pojo.User;
import top.kongk.mmall.service.OrderService;

import javax.servlet.http.HttpSession;

/**
 * 描述：管理员订单管理模块
 *
 * @author kk
 */
@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getList(HttpSession session,
                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        User user = (User) session.getAttribute(Const.CURRENT_ADMIN);
        if (user == null) {
            return ServerResponse.createNeedloginError("管理员用户未登录,请登录");
        }
        //获取订单list 不需要指定订单号
        return orderService.getManageList(pageNum, pageSize);
    }

    @RequestMapping(value = "/detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_ADMIN);
        if (user == null) {
            return ServerResponse.createNeedloginError("管理员用户未登录,请登录");
        }

        return orderService.getManageOrderDetail(orderNo);
    }

    @RequestMapping(value = "/search.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse search(HttpSession session, Long orderNo,
                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        //一期只根据订单号进行简单的查询, 以后会使用模糊查询, 所以加了分页
        User user = (User) session.getAttribute(Const.CURRENT_ADMIN);
        if (user == null) {
            return ServerResponse.createNeedloginError("管理员用户未登录,请登录");
        }

        return orderService.manageSearchOrder(orderNo, pageNum, pageSize);
    }

    @RequestMapping(value = "/send_goods.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse sendGoods(HttpSession session, Long orderNo) {

        //一期只根据订单号进行简单的查询, 以后会使用模糊查询, 所以加了分页
        User user = (User) session.getAttribute(Const.CURRENT_ADMIN);
        if (user == null) {
            return ServerResponse.createNeedloginError("管理员用户未登录,请登录");
        }

        return orderService.manageSendGoods(orderNo);
    }

}
