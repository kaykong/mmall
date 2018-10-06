package top.kongk.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import top.kongk.mmall.util.IpUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：订单以及支付用到的 controller
 *
 * @author kk
 * @date 2018/10/1 18:23
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * 描述： 支付订单
     *
     * @param session session
     * @param orderNo 订单号
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/pay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse pay(HttpSession session, long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        String path = session.getServletContext().getRealPath("upload");
        return orderService.pay(orderNo, user.getId(), path);
    }

    /**
     * 描述：创建订单
     *
     * @param session    session
     * @param shippingId 收货地址id
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/create.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return orderService.createOrder(user.getId(), shippingId);
    }

    /**
     * 描述：在未支付情况下取消订单
     *
     * @param session session
     * @param orderNo 订单号
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/cancel.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse cancel(HttpSession session, Long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return orderService.cancelOrder(user.getId(), orderNo);
    }

    /**
     * 描述：获取购物车中已经被选中的商品详情
     *
     * @param session session
     * @return top.kongk.mmall.common.ServerResponse
     */
    @RequestMapping(value = "/get_order_cart_product.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getOrderCartCheckedProduct(HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return orderService.getOrderCartCheckedProduct(user.getId());
    }

    @RequestMapping(value = "/query_order_pay_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse queryOrderPayStatus(HttpSession session, long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return orderService.queryOrderPayStatus(orderNo, user.getId());
    }

    @RequestMapping(value = "/detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse detail(HttpSession session, long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }

        return orderService.getOrderDetail(orderNo, user.getId());
    }

    @RequestMapping(value = "/list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getList(HttpSession session,
                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createNeedloginError("用户未登录,请登录");
        }
        //获取订单list 不需要指定订单号
        return orderService.getList(user.getId(), pageNum, pageSize);
    }

    @RequestMapping(value = "/alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map requestMap = request.getParameterMap();
        Map<String, String> params = new HashMap<>(13);

        /**
         * 取出request里的key和value, 放入自己的map中, 如果不这样做, 获取的值都类似 [Ljava.lang.String;@537f9762
         */
        for (Object o : requestMap.keySet()) {
            String name = (String) o;
            String[] values = (String[]) requestMap.get(name);

            /**
             * 取出values的值, 并拼装
             */
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < values.length; ++i) {
                if (i != values.length - 1) {
                    stringBuilder.append(values[i]).append(",");
                } else {
                    stringBuilder.append(values[i]);
                }
            }

            params.put(name, stringBuilder.toString());
        }

        logger.info("支付宝回调, sign{}, trade_status{}, 参数{} ",
                params.get("sign"), params.get("trade_status"), params.toString());

        /**
         * 非常重要! 验证参数的正确性, 确保是支付宝发来的
         */

        //按照文档要求, 移除该key
        params.remove("sign_type");

        /**
         *  public static boolean rsaCheckV2(Map<String, String> params, String publicKey,
         *                                  String charset,String signType) throws AlipayApiException
         */
        try {
            //调用阿里的签名来check
            boolean rsaCheckV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            System.out.println("检查:" + rsaCheckV2);
            if (!rsaCheckV2) {
                String ipAddress = IpUtil.getIpAddress(request);
                logger.error("method: alipayCallback() : 来了一个非法请求, 请求的IP地址为: " + ipAddress);
                return ServerResponse.createErrorWithMsg("非法请求, 你的ip地址已经被记录!");
            }

        } catch (AlipayApiException e) {
            String ipAddress = IpUtil.getIpAddress(request);
            logger.error("method: alipayCallback() 支付宝回调异常,  请求的IP地址为:" + ipAddress + e);
            return ServerResponse.createErrorWithMsg("非法请求, 你的ip地址已经被记录!");
        }

        /**
         * 对回调的参数进行处理, 更新数据库中订单状态
         */
        ServerResponse serverResponse = orderService.alipayCallback(params);

        if (serverResponse.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }
}
