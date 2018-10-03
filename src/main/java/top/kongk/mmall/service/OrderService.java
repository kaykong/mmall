package top.kongk.mmall.service;

import top.kongk.mmall.common.ServerResponse;

import java.util.Map;

/**
 * 描述：订单及支付的service接口
 *
 * @author kk
 * @date 2018/10/1 18:27
 */
public interface OrderService {

    /**
     * 描述：根据订单号, 用户id, 生成二维码
     *
     *
     * @param orderNo 订单号
     * @param userId 用户id
     * @param path 二维码在tomcat存放的路径
     * @return top.kongk.mmall.common.ServerResponse 返回订单号及二维码在ftp服务器上的路径
     */
    ServerResponse pay(long orderNo, Integer userId, String path);

    /**
     * 描述：根据阿里的回调结果操作订单表中的记录
     *
     * @param params 回调结果 map
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse alipayCallback(Map<String, String> params);

    /**
     * 描述：根据订单号和用户id查看 付款情况
     *
     * @param orderNo 订单号
     * @param userId 用户id
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse queryOrderPayStatus(long orderNo, Integer userId);
}
