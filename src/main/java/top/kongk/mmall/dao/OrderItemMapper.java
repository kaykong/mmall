package top.kongk.mmall.dao;

import org.apache.ibatis.annotations.Param;
import top.kongk.mmall.pojo.OrderItem;

import java.util.List;

/**
 * 描述：OrderItem表的 接口
 *
 * @author kk
 * @date 2018/10/4 10:24
 */
public interface OrderItemMapper {
    /**
     * 描述：根据主键删除订单项
     *
     * @param id 主键
     * @return int
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 描述：插入订单项(除了 id, createTime, updateTime)
     *
     * @param record 订单项
     * @return int
     */
    int insert(OrderItem record);

    /**
     * 描述：插入订单项不为null的属性(除了 id, createTime, updateTime)
     *
     * @param record 订单项
     * @return int
     */
    int insertSelective(OrderItem record);

    /**
     * 描述：根据主键查找
     *
     * @param id 主键
     * @return top.kongk.mmall.pojo.OrderItem
     */
    OrderItem selectByPrimaryKey(Integer id);

    /**
     * 描述：根据主键更新订单项里不为null的属性(除了 updateTime)
     *
     * @param record 订单项
     * @return int
     */
    int updateByPrimaryKeySelective(OrderItem record);

    /**
     * 描述：根据主键更新订单项
     *
     * @param record 订单项
     * @return int
     */
    int updateByPrimaryKey(OrderItem record);

    /**
     * 描述：根据订单号和用户id获取订单里的商品
     *
     * @param orderNo 订单号
     * @param userId 用户id
     * @return java.util.List<top.kongk.mmall.pojo.OrderItem>
     */
    List<OrderItem> selectOrderItemsByOrderNoAndUserId(@Param("orderNo") long orderNo, @Param("userId") Integer userId);

    /**
     * 描述：批量插入 orderItem
     *
     * @param orderItems 订单项集合
     * @return int
     */
    int batchInsert(@Param("orderItems") List<OrderItem> orderItems);

    /**
     * 描述：根据订单号查找订单
     *
     * @param orderNo 订单号
     * @return java.util.List<top.kongk.mmall.pojo.OrderItem>
     */
    List<OrderItem> selectOrderItemsByOrderNo(Long orderNo);
}