package top.kongk.mmall.vo;

import top.kongk.mmall.pojo.Cart;
import top.kongk.mmall.pojo.Product;

import java.math.BigDecimal;

/**
 * 描述：购物车模块中, 前台需要的每一个购物车商品数据
 *
 * @author kk
 * @date 2018/9/28 9:37
 */
public class CartProductVo {
    /**
     * Cart的属性
     */
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;

    /**
     * Product的属性
     */
    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private BigDecimal productPrice;
    private Integer productStatus;
    private Integer productStock;
    private Integer productChecked;

    /**
     * 计算的属性
     */

    private BigDecimal productTotalPrice;
    private String limitQuantity;

    public CartProductVo() {
    }

    /**
     * 描述：set 从Cart表中获取到的cart
     *
     * @param cart cart
     */
    public void setCart(Cart cart) {
        this.id = cart.getId();
        this.userId = cart.getUserId();
        this.productId = cart.getProductId();
        this.quantity = cart.getQuantity();
        this.productChecked = cart.getChecked();
    }

    /**
     * 描述：set 从Product表中获取到的 product
     *
     * @param product product
     */
    public void setProduct(Product product) {
        this.productName = product.getName();
        this.productSubtitle = product.getSubtitle();
        this.productMainImage = product.getMainImage();
        this.productPrice = product.getPrice();
        this.productStatus = product.getStatus();
        this.productStock = product.getStock();
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public void setLimitQuantity(String limitQuantity) {
        this.limitQuantity = limitQuantity;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductSubtitle() {
        return productSubtitle;
    }

    public String getProductMainImage() {
        return productMainImage;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public Integer getProductStatus() {
        return productStatus;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public Integer getProductChecked() {
        return productChecked;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public String getLimitQuantity() {
        return limitQuantity;
    }
}
