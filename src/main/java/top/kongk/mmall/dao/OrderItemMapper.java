package top.kongk.mmall.dao;

import org.apache.ibatis.annotations.Param;
import top.kongk.mmall.pojo.OrderItem;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    /**
     * 描述：根据订单号和用户id获取订单里的商品
     *
     * @param orderNo 订单号
     * @param userId 用户id
     * @return java.util.List<top.kongk.mmall.pojo.OrderItem>
     */
    List<OrderItem> selectOrderItemsByOrderNoAndUserId(@Param("orderNo") long orderNo, @Param("userId") Integer userId);
}