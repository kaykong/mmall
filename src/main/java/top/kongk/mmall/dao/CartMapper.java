package top.kongk.mmall.dao;

import org.apache.ibatis.annotations.Param;
import top.kongk.mmall.pojo.Cart;

import java.util.List;

/**
 * 描述：购物车接口
 *
 * @author kk
 * @date 2018/9/28 10:41
 */
public interface CartMapper {

    /**
     * 描述：根据主键删除用户的购物车中的商品
     *
     * @param id 主键
     * @return int
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 描述：插入用户的购物车中的商品
     *
     * @param record 用户的购物车中的商品
     * @return int
     */
    int insert(Cart record);

    /**
     * 描述：插入用户的购物车中的商品
     *
     * @param record 用户的购物车中的商品
     * @return int
     */
    int insertSelective(Cart record);

    /**
     * 描述：根据主键查找
     *
     * @param id cart表的主键
     * @return top.kongk.mmall.pojo.Cart
     */
    Cart selectByPrimaryKey(Integer id);

    /**
     * 描述：更新用户的购物车中的商品
     *
     * @param record 用户的购物车中的商品
     * @return int
     */
    int updateByPrimaryKeySelective(Cart record);

    int updateQuantityByUserIdAndProductId(@Param("userId") Integer userId,
                                           @Param("productId") Integer productId,
                                           @Param("quantity") Integer quantity);

    /**
     * 描述：更新用户的购物车中的商品
     *
     * @param record 用户的购物车中的商品
     * @return int
     */
    int updateByPrimaryKey(Cart record);

    /**
     * 描述：根据用户id获取他的购物车列表
     *
     * @param id 用户id
     * @return java.util.List<top.kongk.mmall.pojo.Cart>
     */
    List<Cart> selectCartsByUserId(Integer id);

    /**
     * 描述: 查询用户购买的此商品id的记录
     *
     * @param userId 用户id
     * @param productId 商品id
     * @return top.kongk.mmall.pojo.Cart
     */
    Cart selectCartByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    /**
     * 描述：根据用户id和字符串格式的商品id 删除Cart表中的商品
     *
     * @param userId 用户id
     * @param productId 字符串形式 "1 2 3"
     * @return int
     */
    int deleteByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") String productId);

    /**
     * 描述：根据用户id和 list整数的商品id 删除Cart表中的商品
     *
     * @param userId 用户id
     * @param productIdList 整数数组
     * @return int
     */
    int deleteByUserIdAndProductIdLists(@Param("userId") Integer userId, @Param("productIdList") List<String> productIdList);

    /**
     * 描述：根据 check 决定用户购物车全选或取消全选
     *
     * @param userId 用户id
     * @param check 状态
     * @return int
     */
    int updateSelectAllOrNot(@Param("userId") Integer userId, @Param("check") int check);
}