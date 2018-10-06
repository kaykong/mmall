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

    /**
     * 描述：查找用户的购物车已经被勾选的 cart 记录, 封装成订单返回
     *
     * @param userId 用户id
     * @param shippingId 收货地址id
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse createOrder(Integer userId, Integer shippingId);

    /**
     * 描述：在未支付状态下取消订单
     *
     * @param userId 用户id
     * @param orderNo 订单号
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse cancelOrder(Integer userId, Long orderNo);

    /**
     * 描述：获取购物车中已经被选中的商品详情
     *
     * @param userId 用户id
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse getOrderCartCheckedProduct(Integer userId);

    /**
     * 描述：获取订单详情
     *
     * @param orderNo 订单号
     * @param userId 用户id
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse getOrderDetail(long orderNo, Integer userId);

    /**
     * 描述：根据用户id 获取他的所有订单
     *
     * @param userId 用户id
     * @param pageNum 显示第几页
     * @param pageSize 每页显示多少个
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse getList(Integer userId, Integer pageNum, Integer pageSize);

    /**
     * 描述：管理员获取订单
     *
     * @param pageNum 第几页
     * @param pageSize 每页显示多少个
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse getManageList(Integer pageNum, Integer pageSize);

    /**
     * 描述：管理用户通过订单号获取订单详情
     *
     * @param orderNo 订单号
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse getManageOrderDetail(Long orderNo);

    /**
     * 描述：一期只根据订单号进行简单的查询, 以后会使用模糊查询, 所以加了分页
     *
     * @param orderNo 订单号
     * @param pageNum 第几页
     * @param pageSize 每页显示多少个
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse manageSearchOrder(Long orderNo, Integer pageNum, Integer pageSize);

    /**
     * 描述：发货
     *
     * @param orderNo 订单号
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse manageSendGoods(Long orderNo);
}
