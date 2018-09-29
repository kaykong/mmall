package top.kongk.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 描述：购物车模块中, 最终返回到前台的 data
 *
 * @author kk
 * @date 2018/9/28 9:36
 */
public class CartVo {
    /**
     * 购物车列表
     */
    private List<CartProductVo> cartProductVoList;
    /**
     * 购物车是否全被勾选
     */
    private boolean allChecked;
    /**
     * 购物车被勾选的总价格
     */
    private BigDecimal cartTotalPrice;

    public CartVo() {
    }

    public CartVo(List<CartProductVo> cartProductVoList, boolean allChecked, BigDecimal cartTotalPrice) {
        this.cartProductVoList = cartProductVoList;
        this.allChecked = allChecked;
        this.cartTotalPrice = cartTotalPrice;
    }

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public boolean isAllChecked() {
        return allChecked;
    }

    public void setAllChecked(boolean allChecked) {
        this.allChecked = allChecked;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }
}
