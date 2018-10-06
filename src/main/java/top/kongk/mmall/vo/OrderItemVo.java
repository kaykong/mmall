package top.kongk.mmall.vo;

import top.kongk.mmall.pojo.OrderItem;
import top.kongk.mmall.util.DateTimeUtil;

import java.math.BigDecimal;

/**
 * 描述：创建订单 前台需要用到的OrderItem信息
 *
 * @author kk
 * @date 2018/10/3 15:12
 */
public class OrderItemVo {

    private Long orderNo;

    private Integer productId;

    private String productName;

    private String productImage;

    private BigDecimal currentUnitPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    private String createTime;

    public OrderItemVo() {
    }

    public OrderItemVo(OrderItem orderItem) {
        this.orderNo = orderItem.getOrderNo();
        this.productId = orderItem.getProductId();
        this.productName = orderItem.getProductName();
        this.productImage = orderItem.getProductImage();
        this.currentUnitPrice = orderItem.getCurrentUnitPrice();
        this.quantity = orderItem.getQuantity();
        this.totalPrice = orderItem.getTotalPrice();
        this.createTime = DateTimeUtil.dateToString(orderItem.getCreateTime());
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public BigDecimal getCurrentUnitPrice() {
        return currentUnitPrice;
    }

    public void setCurrentUnitPrice(BigDecimal currentUnitPrice) {
        this.currentUnitPrice = currentUnitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
