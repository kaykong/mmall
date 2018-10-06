package top.kongk.mmall.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.kongk.mmall.common.Const;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.dao.CartMapper;
import top.kongk.mmall.dao.ProductMapper;
import top.kongk.mmall.pojo.Cart;
import top.kongk.mmall.pojo.Product;
import top.kongk.mmall.service.CartService;
import top.kongk.mmall.util.BigDecimalUtil;
import top.kongk.mmall.vo.CartProductVo;
import top.kongk.mmall.vo.CartVo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 描述：
 *
 * @author kk
 * @date 2018/9/28 10:10
 */
@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductMapper productMapper;

    @Override
    public ServerResponse getList(Integer id) {
        if (id == null) {
            return ServerResponse.createIllegalArgumentError();
        }

        CartVo cartVo = new CartVo();
        List<Cart> carts = new ArrayList<>(0);
        try {
            carts = cartMapper.selectCartsByUserId(id);
        } catch (Exception e) {
            logger.error("CartServiceImpl.getList cartMapper.selectCartsByUserId Execption", e);
        }

        /**
         * productVos : 前台需要的购物车商品数据(每种商品总价, 商品的数量是否符合库存, 商品是否被勾选)
         * totalPrice : 使用 BigDecimalUtil 工具类, 计算被勾选的商品总价
         */
        List<CartProductVo> cartProductVoList = new ArrayList<>(carts.size());
        BigDecimal cartTotalPrice = new BigDecimal("0");
        boolean isAllChecked = true;

        if (!CollectionUtils.isEmpty(carts)) {
            /**
             * 如果购物车不是空的, 那么就对购物车的商品数量进行检验, 并计算出总价
             */
            for (Cart cart : carts) {
                //如果购物车的商品数量被修改为负数
                if (cart.getQuantity() < 0) {
                    continue;
                }

                //new一个前台需要的购物车商品对象
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setCart(cart);
                Product product = null;
                try {
                    product = productMapper.selectByPrimaryKey(cart.getProductId());
                } catch (Exception e) {
                    logger.error("CartServiceImpl.getList productMapper.selectByPrimaryKey Execption", e);
                }
                if (product != null) {

                    cartProductVo.setProduct(product);

                    if (cart.getQuantity() <= product.getStock()) {
                        //如果购物车的商品数量满足条件
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_QUANTITY_SUCCESS);
                    } else {
                        //如果购物车的商品数量大于库存, 把数量修改为商品库存
                        cart.setQuantity(product.getStock());
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_QUANTITY_FAIL);

                        //在cart表把此商品数量更新为最大库存
                        try {
                            cartMapper.updateQuantityByUserIdAndProductId(cart.getUserId(), cart.getProductId(), product.getStock());
                        } catch (Exception e) {
                            logger.error("CartServiceImpl.getList Execption", e);
                        }
                    }

                    //该商品的总价
                    BigDecimal productTotalPrice = BigDecimalUtil.multiply(product.getPrice().doubleValue(), cart.getQuantity().doubleValue());
                    if (cart.getChecked() == Const.Cart.CHECKED) {
                        //购物车内商品的总价
                        cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), productTotalPrice.doubleValue());
                    } else {
                        isAllChecked = false;
                    }
                    cartProductVo.setProductTotalPrice(productTotalPrice);
                    cartProductVoList.add(cartProductVo);
                } //end if (product != null)
            } // end for (Cart cart : carts)
        }  else {
            isAllChecked = false;
        }

        // set 购物车列表, 购物车被勾选的总价, 购物车是否被全勾选
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setAllChecked(isAllChecked);

        return ServerResponse.createSuccess(cartVo);
    }

    @Override
    public ServerResponse add(Integer userId, Integer productId, Integer count) {
        //参数校验
        if (productId == null || count == null || count < 0) {
            return this.getList(userId);
        }

        //校验产品是否在售
        Product product = null;
        try {
            product = productMapper.selectOnSaleByPrimaryKey(productId);
        } catch (Exception e) {
            logger.error("CartServiceImpl.add Execption", e);
        }
        //如果无此商品, 则直接返回
        if (product == null) {
            return this.getList(userId);
        }

        Cart cart = null;
        try {
            cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        } catch (Exception e) {
            logger.error("CartServiceImpl.add Execption", e);
        }

        if (cart == null) {
            //该用户购物车中无此商品
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setChecked(Const.Cart.CHECKED);

            try {
                cartMapper.insertSelective(cart);
            } catch (Exception e) {
                logger.error("CartServiceImpl.add cartMapper.insertSelective Execption", e);
            }
        } else {
            int quantity = cart.getQuantity() + count;

            cart.setQuantity(quantity);

            //把新增的商品设为选中状态
            cart.setChecked(Const.Cart.CHECKED);

            try {
                cartMapper.updateQuantityByUserIdAndProductId(userId, productId, cart.getQuantity());
            } catch (Exception e) {
                logger.error("CartServiceImpl.add Execption", e);
            }
        }

        return this.getList(userId);
    }

    @Override
    public ServerResponse update(Integer userId, Integer productId, Integer count) {
        //参数校验
        if (productId == null || count == null || count < 0) {
            return this.getList(userId);
        }

        //校验产品是否在售
        Product product = null;
        try {
            product = productMapper.selectOnSaleByPrimaryKey(productId);
        } catch (Exception e) {
            logger.error("CartServiceImpl.add Execption", e);
        }
        if (product == null) {
            return this.getList(userId);
        }

        Cart cart = null;
        try {
            cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        } catch (Exception e) {
            logger.error("CartServiceImpl.add Execption", e);
        }

        if (cart != null){
            cart.setQuantity(count);
            cart.setChecked(Const.Cart.CHECKED);
            try {
                cartMapper.updateQuantityByUserIdAndProductId(userId, productId, cart.getQuantity());
            } catch (Exception e) {
                logger.error("CartServiceImpl.update cartMapper.updateQuantityByUserIdAndProductId Execption", e);
            }
        }
        return this.getList(userId);
    }

    @Override
    public ServerResponse deleteProduct(Integer userId, String productId) {
        if (StringUtils.isBlank(productId)) {
            return this.getList(userId);
        }
        /*//把传来的 "1,2,3" 替换成 "1 2 3"
        productId = productId.replaceAll(",", " ");*/
        String[] split = productId.split(",");
        List<String> productIdList = Arrays.asList(split);
        int count = 0;
        try {
            count = cartMapper.deleteByUserIdAndProductIdLists(userId, productIdList);
        } catch (Exception e) {
            logger.error("CartServiceImpl.deleteProduct Execption", e);
        }

        return this.getList(userId);
    }

    @Override
    public ServerResponse setCheck(Integer userId, Integer productId, int check) {
        Cart cart = null;
        try {
            cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        } catch (Exception e) {
            logger.error("CartServiceImpl.setChecked Execption", e);
        }
        //根据check设置商品的状态
        if (cart != null && cart.getChecked() != check) {
            cart.setChecked(check);
            try {
                cartMapper.updateByPrimaryKeySelective(cart);
            } catch (Exception e) {
                logger.error("CartServiceImpl.setChecked Execption", e);
            }
        }
        return this.getList(userId);
    }

    @Override
    public ServerResponse getCartProductCount(Integer userId) {
        List<Cart> carts = null;
        try {
            carts = cartMapper.selectCartsByUserId(userId);
        } catch (Exception e) {
            logger.error("CartServiceImpl.getCartProductCount Execption", e);
        }
        if (carts == null || carts.isEmpty()) {
            return ServerResponse.createSuccess(0);
        } else {
            int sum = 0;
            for (Cart cart : carts) {
                sum += cart.getQuantity();
            }
            return ServerResponse.createSuccess(sum);
        }
    }

    @Override
    public ServerResponse selectAllOrNot(Integer userId, int check) {
        try {
            cartMapper.updateSelectAllOrNot(userId, check);
        } catch (Exception e) {
            logger.error("CartServiceImpl.selectAllOrNot Execption", e);
        }
        return this.getList(userId);
    }


}
