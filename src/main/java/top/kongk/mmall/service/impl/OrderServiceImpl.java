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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.mmall.common.Const;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.dao.OrderItemMapper;
import top.kongk.mmall.dao.OrderMapper;
import top.kongk.mmall.dao.PayInfoMapper;
import top.kongk.mmall.pojo.Order;
import top.kongk.mmall.pojo.OrderItem;
import top.kongk.mmall.pojo.PayInfo;
import top.kongk.mmall.service.OrderService;
import top.kongk.mmall.util.BigDecimalUtil;
import top.kongk.mmall.util.DateTimeUtil;
import top.kongk.mmall.util.FtpUtil;
import top.kongk.mmall.util.PropertiesUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
