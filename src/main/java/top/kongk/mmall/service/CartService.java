package top.kongk.mmall.service;

import top.kongk.mmall.common.ServerResponse;

/**
 * 描述：
 *
 * @author kk
 * @date 2018/9/28 10:10
 */
public interface CartService {
    /**
     * 描述：根据用户id获取购物车列表
     *
     * @param id 用户id
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse getList(Integer id);


    /**
     * 描述：根据用户id, 商品id, 商品数量 实现添加购物车操作
     *
     * @param userId    用户id
     * @param productId 商品id
     * @param count     新增的商品数量
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse add(Integer userId, Integer productId, Integer count);

    /**
     * 描述：根据用户id, 商品id, 商品数量 更新Cart表
     *
     * @param userId    用户id
     * @param productId 商品id
     * @param count     新增的商品数量
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse update(Integer userId, Integer productId, Integer count);

    /**
     * 描述：根据商品id字符串删除购物车的商品
     *
     * @param userId    用户id
     * @param productId 商品id 以逗号分隔的商品id字符串 1,2,4
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse deleteProduct(Integer userId, String productId);

    /**
     * 描述：选中用户的Cart中的商品
     *
     * @param userId    用户id
     * @param productId 商品id
     * @param check     选中状态
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse setCheck(Integer userId, Integer productId, int check);

    /**
     * 描述：根据用户id查找他的购物车内商品数量
     *
     * @param userId 用户id
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse getCartProductCount(Integer userId);

    /**
     * 描述：根据 check 决定用户购物车全选或取消全选
     *
     * @param userId 用户id
     * @param check 选中的状态
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse selectAllOrNot(Integer userId, int check);
}
