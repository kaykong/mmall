package top.kongk.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.mmall.common.Const;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.dao.*;
import top.kongk.mmall.pojo.*;
import top.kongk.mmall.service.OrderService;
import top.kongk.mmall.util.BigDecimalUtil;
import top.kongk.mmall.util.DateTimeUtil;
import top.kongk.mmall.util.FtpUtil;
import top.kongk.mmall.util.PropertiesUtil;
import top.kongk.mmall.vo.OrderItemVo;
import top.kongk.mmall.vo.OrderProductVo;
import top.kongk.mmall.vo.OrderVo;
import top.kongk.mmall.vo.ShippingVo;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 描述：订单及支付的service接口实现
 *
 * @author kk
 * @date 2018/10/1 18:28
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static AlipayTradeService tradeService;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，
         *  如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse pay(long orderNo, Integer userId, String path) {

        Order order = null;
        try {
            order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        } catch (Exception e) {
            logger.error("OrderServiceImpl.pay  orderMapper.selectByOrderNoAndUserId Execption", e);
        }
        System.out.println("订单:" + order);
        if (order == null) {
            return ServerResponse.createErrorWithMsg("用户没有该订单");
        }

        Map<String, String> map = new HashMap<>(16);

        map.put("orderNo", order.getOrderNo().toString());

        //生成二维码

        AlipayF2FPrecreateResult result = getAlipayPreResult(order);

        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                /**
                 * 预下单成功后, 要生成二维码图片, 并把二维码图片的路径返回给前台
                 *
                 */
                File file = new File(path);
                if (!file.exists()) {
                    file.setWritable(true);
                    file.mkdirs();
                }

                // qrPath 二维码文件的 全文件名
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                // qrName 二维码的文件名
                String qrName = String.format("qr-%s.png", response.getOutTradeNo());
                logger.info("二维码生成的路径 qrPath:" + qrPath);

                //生成二维码文件
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                /**
                 * 把二维码文件上传到ftp服务器, 并得到二维码的url路径
                 */
                String qRCodeImageUrl = uploadQrCodeToFtp(path, qrName);

                map.put("qrUrl", qRCodeImageUrl);

                return ServerResponse.createSuccess(map);

            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createErrorWithMsg("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createErrorWithMsg("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createErrorWithMsg("不支持的交易状态，交易返回异常!!!");
        }

    }

    @Override
    public ServerResponse alipayCallback(Map<String, String> params) {
        //商户订单号 原支付请求的商户订单号
        long orderNo = Long.valueOf(params.get("out_trade_no"));
        //交易状态
        String tradeStatus = params.get("trade_status");
        //支付宝交易号
        String tradeNo = params.get("trade_no");

        Order order = null;
        try {
            order = orderMapper.selectByOrderNo(orderNo);
        } catch (Exception e) {
            logger.error("OrderServiceImpl.alipayCallback Execption", e);
        }

        if (order == null) {
            return ServerResponse.createErrorWithMsg("非商城内订单");
        }

        if (order.getStatus() >= Const.OrderStatus.PAIED.getCode()) {
            //已付款
            return ServerResponse.createSuccess();
        } else {
            if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
                //成功付款
                order.setStatus(Const.OrderStatus.PAIED.getCode());
                //交易付款时间
                order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));

                //在数据库中更新
                int count = 0;
                try {
                    count = orderMapper.updateByPrimaryKeySelective(order);
                } catch (Exception e) {
                    logger.error("OrderServiceImpl.alipayCallback 支付宝回调中更新order表异常 Execption", e);
                }
            }
        }

        //记录下状态信息
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(orderNo);
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        try {
            payInfoMapper.insertSelective(payInfo);
        } catch (Exception e) {
            logger.error("OrderServiceImpl.alipayCallback Execption", e);
        }

        return ServerResponse.createSuccess();
    }

    @Override
    public ServerResponse queryOrderPayStatus(long orderNo, Integer userId) {

        Order order = null;
        try {
            order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        } catch (Exception e) {
            logger.error("OrderServiceImpl.queryOrderPayStatus Execption", e);
        }

        if (order == null) {
            return ServerResponse.createErrorWithMsg("该用户并没有该订单,查询无效");
        }

        if (order.getStatus() >= Const.OrderStatus.PAIED.getCode()) {
            return ServerResponse.createSuccess(true);
        } else {
            return ServerResponse.createErrorWithMsg("尚未付款");
        }
    }


    @Override
    public ServerResponse createOrder(Integer userId, Integer shippingId) {

        if (shippingId == null) {
            return ServerResponse.createErrorWithMsg("请选择收货地址");
        }

        /**
         * 1.从购物车Cart表中获取字段 checked = 1 的记录 (获取购物车中被勾选的商品)
         */
        List<Cart> carts = new ArrayList<>(0);
        try {
            carts = cartMapper.selectCheckedCartsByUserId(userId);
        } catch (Exception e) {
            logger.error("OrderServiceImpl.createOrder 获取购物车中被勾选的商品 Execption", e);
        }

        /**
         * 2.校验被勾选的商品 (是否在售? 商品数量是否在库存之内?)
         * 生成 OrderItem 集合并返回
         */
        ServerResponse serverResponse = getCartOrderItem(carts);

        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }

        /**
         * 3.计算所有商品的总价
         */
        List<OrderItem> orderItems = (List<OrderItem>) serverResponse.getData();

        if (CollectionUtils.isEmpty(orderItems)) {
            return ServerResponse.createErrorWithMsg("购物车空空");
        }

        //获取该订单的总价
        BigDecimal payment = getCartOrderItemTotalPrice(orderItems);

        /**
         * todo 开启事务
         */

        /**
         * 4.生成order, 并在数据库Order表中插入 (此时应该开启事务)
         */
        Order order = assembleOrder(userId, shippingId, payment);

        if (order == null) {
            //此时不用回滚事务, 因为数据库中并没有插入什么数据
            return ServerResponse.createErrorWithMsg("生成订单错误");
        }

        /**
         * 5.设置各个orderItem的orderNo属性, 并批量插入
         */
        for (OrderItem orderItem : orderItems) {
            orderItem.setUserId(userId);
            orderItem.setOrderNo(order.getOrderNo());
        }

        try {
            orderItemMapper.batchInsert(orderItems);
        } catch (Exception e) {
            logger.error("OrderServiceImpl.createOrder 批量插入orderItems Execption", e);
        }

        /**
         * 6.插入后要修改库存, 如果result为false, 应该回滚事务
         */
        // TODO: 2018/10/3
        boolean reduceResult = reduceProductStock(orderItems);

        /**
         * 7.清空购物车 (把勾选的都删除掉)
         */
        boolean cleanResult = cleanCart(userId, carts);

        /**
         * todo 事务结束
         */


        /**
         * 8.封装好 orderVo, 返回给前端数据
         */
        order.setCreateTime(new Date());
        OrderVo orderVo = assembleOrderVo(order, orderItems);

        return ServerResponse.createSuccess(orderVo);
    }

    @Override
    public ServerResponse cancelOrder(Integer userId, Long orderNo) {

        Order order = null;
        try {
            order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        } catch (Exception e) {
            logger.error("OrderServiceImpl.cancelOrder Execption", e);
        }

        if (order == null) {
            return ServerResponse.createErrorWithMsg("该用户没有此订单");
        }

        //如果不是未支付的状态, 那么就返回
        if (order.getStatus() != Const.OrderStatus.NO_PAY.getCode()) {
            return ServerResponse.createErrorWithMsg("此订单已付款，无法被取消");
        }

        Order orderUpdate = new Order();
        orderUpdate.setId(order.getId());
        orderUpdate.setStatus(Const.OrderStatus.CANCELED.getCode());

        int count = 0;
        try {
            count = orderMapper.updateByPrimaryKeySelective(orderUpdate);
        } catch (Exception e) {
            logger.error("OrderServiceImpl.cancelOrder Execption", e);
        }
        if (count == 0) {
            return ServerResponse.createErrorWithMsg("取消订单失败");
        } else {
            return ServerResponse.createSuccess();
        }
    }

    @Override
    public ServerResponse getOrderCartCheckedProduct(Integer userId) {

        /**
         * 1.从购物车Cart表中获取字段 checked = 1 的记录 (获取购物车中被勾选的商品)
         */
        List<Cart> carts = new ArrayList<>(0);
        try {
            carts = cartMapper.selectCheckedCartsByUserId(userId);
        } catch (Exception e) {
            logger.error("OrderServiceImpl.createOrder 获取购物车中被勾选的商品 Execption", e);
        }

        /**
         * 2.校验被勾选的商品 (是否在售? 商品数量是否在库存之内?)
         * 生成 OrderItem 集合并返回
         */
        ServerResponse serverResponse = getCartOrderItem(carts);

        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }

        /**
         * 3.计算所有商品的总价
         */
        List<OrderItem> orderItems = (List<OrderItem>) serverResponse.getData();

        if (CollectionUtils.isEmpty(orderItems)) {
            return ServerResponse.createErrorWithMsg("购物车空空");
        }

        //获取该订单的总价
        BigDecimal payment = getCartOrderItemTotalPrice(orderItems);

        List<OrderItemVo> orderItemVos = new ArrayList<>(orderItems.size());

        //此时orderItems的size肯定大于0, 不会报空指针异常
        for (OrderItem orderItem : orderItems) {
            orderItemVos.add(new OrderItemVo(orderItem));
        }

        /**
         * 封装 OrderProductVo , 并返回
         */
        OrderProductVo orderProductVo = new OrderProductVo();

        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.img.prefix"));
        orderProductVo.setOrderItemVoList(orderItemVos);
        orderProductVo.setProductTotalPrice(payment);


        return ServerResponse.createSuccess(orderProductVo);
    }

    @Override
    public ServerResponse getOrderDetail(long orderNo, Integer userId) {
        Order order = null;
        try {
            order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        } catch (Exception e) {
            logger.error("OrderServiceImpl.getOrderDetail 查找用户订单 Execption", e);
        }
        if (order == null) {
            return ServerResponse.createErrorWithMsg("没有找到该订单");
        }

        List<OrderItem> orderItems = new ArrayList<>(0);
        try {
            orderItems = orderItemMapper.selectOrderItemsByOrderNoAndUserId(orderNo, userId);
        } catch (Exception e) {
            logger.error("OrderServiceImpl.getOrderDetail 获取订单项 Execption", e);
        }

        OrderVo orderVo = assembleOrderVo(order, orderItems);

        return ServerResponse.createSuccess(orderVo);
    }

    private List<OrderVo> assembleOrderVo(Integer userId, List<Order> orderList) {

        List<OrderVo> orderVoList = new ArrayList<>(orderList.size());

        //遍历orderList, 生成orderVo, add进orderVoList
        for (Order order : orderList) {

            //根据order信息生成orderItemList
            List<OrderItem> orderItems = new ArrayList<>(0);
            try {
                if (userId == null) {
                    //管理员查找所有订单列表
                    orderItems = orderItemMapper.selectOrderItemsByOrderNo(order.getOrderNo());
                } else {
                    //普通用户查找自己的订单
                    orderItems = orderItemMapper.selectOrderItemsByOrderNoAndUserId(order.getOrderNo(), userId);
                }
            } catch (Exception e) {
                logger.error("OrderServiceImpl.getList 获取订单项 Execption", e);
            }

            //根据order, orderItems, 生成orderVo
            OrderVo orderVo = assembleOrderVo(order, orderItems);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    @Override
    public ServerResponse getList(Integer userId, Integer pageNum, Integer pageSize) {
        //设置startPage
        PageHelper.startPage(pageNum, pageSize);

        List<Order> orderList = new ArrayList<>(0);
        try {
            orderList = orderMapper.selectByUserId(userId);
        } catch (Exception e) {
            logger.error("OrderServiceImpl.getList 查找用户订单 Execption", e);
        }

        //根据userId,orderList, 生成orderVoList
        List<OrderVo> orderVoList = assembleOrderVo(userId, orderList);

        /**
         * 让orderList 而不是 orderVoList 作为构造函数的参数是因为,
         * 与数据库交互的是orderList, 而不是orderVoList !!
         * 所以, 把orderList传过去,
         * 就可以对获取orderList要执行的sql (监听?拦截?修改?), 先查询count,再计算limit的参数, 最后生成分页sql执行
         */
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);

        return ServerResponse.createSuccess(pageInfo);
    }

    @Override
    public ServerResponse getManageList(Integer pageNum, Integer pageSize) {
        //设置startPage
        PageHelper.startPage(pageNum, pageSize);

        List<Order> orderList = new ArrayList<>(0);
        try {
            orderList = orderMapper.selectAllOrder();
        } catch (Exception e) {
            logger.error("OrderServiceImpl.getList 查找用户订单 Execption", e);
        }

        //根据userId,orderList, 生成orderVoList
        List<OrderVo> orderVoList = assembleOrderVo(null, orderList);

        /**
         * 让orderList 而不是 orderVoList 作为构造函数的参数是因为,
         * 与数据库交互的是orderList, 而不是orderVoList !!
         * 所以, 把orderList传过去,
         * 就可以对获取orderList要执行的sql (监听?拦截?修改?), 先查询count,再计算limit的参数, 最后生成分页sql执行
         */
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);

        return ServerResponse.createSuccess(pageInfo);

    }

    @Override
    public ServerResponse getManageOrderDetail(Long orderNo) {
        Order order = null;
        try {
            order = orderMapper.selectByOrderNo(orderNo);
        } catch (Exception e) {
            logger.error("method: getManageOrderDetail() 查找用户订单 : " + e);
        }
        if (order == null) {
            return ServerResponse.createErrorWithMsg("没有找到该订单");
        }

        List<OrderItem> orderItems = new ArrayList<>(0);
        try {
            orderItems = orderItemMapper.selectOrderItemsByOrderNo(orderNo);
        } catch (Exception e) {
            logger.error("method: getManageOrderDetail() 获取订单项: " + e);
        }

        OrderVo orderVo = assembleOrderVo(order, orderItems);

        return ServerResponse.createSuccess(orderVo);
    }

    @Override
    public ServerResponse manageSearchOrder(Long orderNo, Integer pageNum, Integer pageSize) {
        Order order = null;
        try {
            order = orderMapper.selectByOrderNo(orderNo);
        } catch (Exception e) {
            logger.error("method: getManageOrderDetail() 查找用户订单 : " + e);
        }
        if (order == null) {
            return ServerResponse.createErrorWithMsg("没有找到该订单");
        }
        // 1.设置监听查询sql
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = new ArrayList<>(1);
        orderList.add(order);
        PageInfo pageInfo = new PageInfo(orderList);


        List<OrderItem> orderItems = new ArrayList<>(0);
        try {
            orderItems = orderItemMapper.selectOrderItemsByOrderNo(orderNo);
        } catch (Exception e) {
            logger.error("method: getManageOrderDetail() 获取订单项: " + e);
        }

        OrderVo orderVo = assembleOrderVo(order, orderItems);

        //2. 把转化的orderVo 填充到pageinfo里
        List<OrderVo> orderVoList = new ArrayList<>(1);
        orderVoList.add(orderVo);
        pageInfo.setList(orderVoList);

        return ServerResponse.createSuccess(orderVo);
    }

    @Override
    public ServerResponse manageSendGoods(Long orderNo) {
        //查询订单
        Order order = null;
        try {
            order = orderMapper.selectByOrderNo(orderNo);
        } catch (Exception e) {
            logger.error("OrderServiceImpl.manageSendGoods Execption", e);
        }

        if (order == null) {
            return ServerResponse.createErrorWithMsg("该订单不存在");
        }

        if (order.getStatus() == Const.OrderStatus.PAIED.getCode()) {
            //设置订单的状态和发货时间
            order.setStatus(Const.OrderStatus.SHIPPED.getCode());
            order.setSendTime(new Date());


            int count = 0;
            try {
                count = orderMapper.updateByPrimaryKeySelective(order);
            } catch (Exception e) {
                logger.error("OrderServiceImpl.manageSendGoods Execption", e);
            }
            if (count == 0) {
                return ServerResponse.createErrorWithMsg("订单发货失败, 请重试");
            } else {
                return ServerResponse.createSuccessWithMsg("订单发货成功");
            }
        }

        if (order.getStatus() == Const.OrderStatus.CANCELED.getCode()) {
            return ServerResponse.createErrorWithMsg("订单已取消");
        }
        if (order.getStatus() == Const.OrderStatus.NO_PAY.getCode()) {
            return ServerResponse.createErrorWithMsg("订单未支付");
        }
        if (order.getStatus() > Const.OrderStatus.PAIED.getCode()) {
            return ServerResponse.createErrorWithMsg("订单已经发过货了");
        }
        return ServerResponse.createErrorWithMsg("出现了错误");
    }

    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItems) {

        //设置order相关
        OrderVo orderVo = new OrderVo(order);

        List<OrderItemVo> orderItemVos = new ArrayList<>(orderItems.size());

        //此时orderItems的size肯定大于0, 不会报空指针异常
        for (OrderItem orderItem : orderItems) {
            orderItemVos.add(new OrderItemVo(orderItem));
        }

        //设置List<OrderItemVo>
        orderVo.setOrderItemVoList(orderItemVos);


        //设置其他的imageHost receiverName shippingVo
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.img.prefix"));

        /**
         * 获取收货地址
         */
        Shipping shipping = null;
        try {
            shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        } catch (Exception e) {
            logger.error("OrderServiceImpl.assembleOrderVo 获取收货地址 Execption", e);
        }

        if (shipping != null) {
            ShippingVo shippingVo = new ShippingVo(shipping);

            orderVo.setReceiverName(shippingVo.getReceiverName());
            orderVo.setShippingVo(shippingVo);
        }

        return orderVo;
    }

    private boolean cleanCart(Integer userId, List<Cart> carts) {
        List<String> list = new ArrayList<>(carts.size());
        for (Cart cart : carts) {
            list.add(cart.getProductId().toString());
        }

        int count = 0;
        try {
            count = cartMapper.deleteByUserIdAndProductIdLists(userId, list);
        } catch (Exception e) {
            logger.error("method: cleanCart() 清空购物车异常 : " + e);
        }

        if (count == list.size()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean reduceProductStock(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            /**
             * 更新数据库中product表中 orderItem 对应的商品的库存
             */
            int count = 0;
            try {
                count = productMapper.updateStockByProductIdAndQuantity(orderItem.getProductId(), orderItem.getQuantity());
            } catch (Exception e) {
                logger.error("OrderServiceImpl.reduceProductStock Execption", e);
            }

            if (count == 0) {
                //库存更新失败, 要么没有该商品, 要么该商品的购买数量超过了库存
                // TODO: 2018/10/3, 此时应该根据返回的false回滚事务
                return false;
            }
        }
        return true;
    }

    private Long getOrderNo() {
        //系统当前时间毫秒数
        Long orderNo = System.currentTimeMillis();
        //随机的三位整数
        Integer random = new Random().nextInt(900) + 100;
        //把系统时间和随机的三位数整合
        String randomString = orderNo.toString() + random.toString();

        //把整合后的String转化为 Long
        return Long.valueOf(randomString);
    }

    /**
     * 描述：根据给的数据装配 Order, 并在数据库中order表插入
     *
     * @param userId     用户id
     * @param shippingId 收货地址id
     * @param payment    订单总价
     * @return top.kongk.mmall.pojo.Order
     */
    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        Order order = new Order();
        order.setOrderNo(getOrderNo());
        //订单总价
        order.setPayment(payment);
        //支付方式
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        //邮费
        order.setPostage(0);
        //支付状态
        order.setStatus(Const.OrderStatus.NO_PAY.getCode());
        //谁的订单
        order.setUserId(userId);
        //订单收货地址
        order.setShippingId(shippingId);

        /**
         * 在 order 表中插入该订单, 并根据是否插入成功返回null 或 order
         */

        int count = 0;
        try {
            count = orderMapper.insertSelective(order);
        } catch (Exception e) {
            logger.error("OrderServiceImpl.assembleOrder Execption", e);
        }
        if (count == 0) {
            return null;
        } else {
            return order;
        }
    }

    /**
     * 描述：获取商品list总价
     *
     * @param orderItems 商品list
     * @return java.math.BigDecimal
     */
    private BigDecimal getCartOrderItemTotalPrice(List<OrderItem> orderItems) {

        BigDecimal totalPrice = new BigDecimal("0");
        for (OrderItem orderItem : orderItems) {
            totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }

        return totalPrice;
    }

    /**
     * 描述：根据购物车中被勾选的商品列表, 生成 List<OrderItem>
     *
     * @param carts 购物车中被勾选的商品列表
     * @return top.kongk.mmall.common.ServerResponse
     */
    private ServerResponse getCartOrderItem(List<Cart> carts) {

        if (CollectionUtils.isEmpty(carts)) {
            return ServerResponse.createErrorWithMsg("购物车空空");
        }

        List<OrderItem> orderItems = new ArrayList<>(carts.size());

        /**
         * 检验购物车中商品是否在售, 数量
         */
        for (Cart cart : carts) {
            Product product = null;
            try {
                product = productMapper.selectOnSaleByPrimaryKey(cart.getProductId());
            } catch (Exception e) {
                logger.error("OrderServiceImpl.getCartOrderItem Execption", e);
            }

            if (product == null) {
                return ServerResponse.createErrorWithMsg("购物车中" + product.getName() + "商品不是在售状态");
            }

            if (product.getStock() < cart.getQuantity()) {
                return ServerResponse.createErrorWithMsg("购物车中商品" + product.getName() + "超过了库存");
            }

            /**
             * 组装 orderItem
             */
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            //当前单价
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            //该商品总价
            BigDecimal totalPrice = BigDecimalUtil.multiply(product.getPrice().doubleValue(), cart.getQuantity().doubleValue());
            orderItem.setTotalPrice(totalPrice);

            orderItems.add(orderItem);
        }

        return ServerResponse.createSuccess(orderItems);
    }

    /**
     * 描述：根据订单生成阿里  预支付  的结果 (AlipayF2FPrecreateResult)
     *
     * @param order 订单对象
     * @return com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult
     */
    private AlipayF2FPrecreateResult getAlipayPreResult(Order order) {

        Integer userId = order.getUserId();
        long orderNo = order.getOrderNo();

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("mmall 扫码支付, 订单号:")
                .append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单号:")
                .append(outTradeNo).append(", 金额总计: ")
                .append(order.getPayment()).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";


        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<>();

        /**
         * 这里需要从数据库OrderItem表中根据获取订单的商品信息
         */
        List<OrderItem> orderItems = new ArrayList<>(0);
        try {
            orderItems = orderItemMapper.selectOrderItemsByOrderNoAndUserId(orderNo, userId);
        } catch (Exception e) {
            logger.error("method: getAlipayPreResult() 从数据库OrderItem表中根据获取订单的商品信息异常 : " + e);
        }

        for (OrderItem orderItem : orderItems) {

            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail good = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.multiply(orderItem.getCurrentUnitPrice().doubleValue(), (double) 100).longValue(),
                    orderItem.getQuantity());

            goodsDetailList.add(good);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                //支付宝服务器主动通知商户服务器里指定的页面http路径, 根据需要设置(回调函数)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))
                .setGoodsDetailList(goodsDetailList);

        return tradeService.tradePrecreate(builder);
    }

    /**
     * 描述：把二维码文件上传到ftp服务器, 并返回二维码的url路径
     *
     * @param path   二维码在tomcat中暂存的路径
     * @param qrName 二维码的文件名
     * @return java.lang.String
     */
    private String uploadQrCodeToFtp(String path, String qrName) {
        /**
         * 把二维码文件上传到ftp服务器
         */
        File ftpQrFile = new File(path, qrName);
        String romotePath = PropertiesUtil.getProperty("ftp.server.filePath.qRCode");
        List<File> files = new ArrayList<>(1);
        files.add(ftpQrFile);
        try {
            FtpUtil.uploadToFtp(romotePath, files);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * 上传到服务器之后, 要把tomacat中的图片删除
         */
        ftpQrFile.delete();
        /**
         * 把ftp服务器上的新创建的二维码的url路径返回
         */
        String qRCodeImageUrl =
                new StringBuilder().append(PropertiesUtil.getProperty("ftp.server.http.prefix"))
                        .append(romotePath).append("/").append(qrName).toString();

        return qRCodeImageUrl;
    }

    /**
     * 描述：简单地在日志中记录下
     *
     * @param response
     */
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }
}
