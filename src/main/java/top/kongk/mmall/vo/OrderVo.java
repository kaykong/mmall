package top.kongk.mmall.vo;

import top.kongk.mmall.common.Const;
import top.kongk.mmall.pojo.Order;
import top.kongk.mmall.util.DateTimeUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * 描述：创建订单功能, 前台需要的order信息
 *
 * @author kk
 * @date 2018/10/3 15:16
 */
public class OrderVo {

    private Long orderNo;

    private BigDecimal payment;

    private Integer paymentType;

    /**
     * 支付描述
     */
    private String paymentTypeDesc;

    private Integer postage;

    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    private String paymentTime;

    private String sendTime;

    private String endTime;

    private String closeTime;

    private String createTime;



    /**
     * 订单明细
     */
    private List<OrderItemVo> orderItemVoList;

    private String imageHost;

    private Integer shippingId;

    private String receiverName;

    private ShippingVo shippingVo;

    public OrderVo() {

    }

    public OrderVo(Order order) {
        this.orderNo = order.getOrderNo();

        //shippingId 在此就设置了
        this.shippingId = order.getShippingId();

        this.payment = order.getPayment();
        this.paymentType = order.getPaymentType();
        this.paymentTypeDesc = Const.PaymentTypeEnum.codeOf(order.getPaymentType());
        this.postage = order.getPostage();
        this.status = order.getStatus();
        this.statusDesc = Const.OrderStatus.codeOf(order.getStatus());
        this.paymentTime = DateTimeUtil.dateToString(order.getPaymentTime());
        this.sendTime = DateTimeUtil.dateToString(order.getSendTime());
        this.endTime = DateTimeUtil.dateToString(order.getEndTime());
        this.closeTime = DateTimeUtil.dateToString(order.getCloseTime());
        this.createTime = DateTimeUtil.dateToString(order.getCreateTime());
    }



    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentTypeDesc() {
        return paymentTypeDesc;
    }

    public void setPaymentTypeDesc(String paymentTypeDesc) {
        this.paymentTypeDesc = paymentTypeDesc;
    }

    public Integer getPostage() {
        return postage;
    }

    public void setPostage(Integer postage) {
        this.postage = postage;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public Integer getShippingId() {
        return shippingId;
    }

    public void setShippingId(Integer shippingId) {
        this.shippingId = shippingId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public ShippingVo getShippingVo() {
        return shippingVo;
    }

    public void setShippingVo(ShippingVo shippingVo) {
        this.shippingVo = shippingVo;
    }
}
