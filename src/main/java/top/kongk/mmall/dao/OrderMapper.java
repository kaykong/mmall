package top.kongk.mmall.dao;

import org.apache.ibatis.annotations.Param;
import top.kongk.mmall.pojo.Order;

import java.util.List;

/**
 * 描述：订单dao接口
 *
 * @author kk
 */
public interface OrderMapper {
    /**
     * 描述：根据主键删除订单
     *
     * @param id 主键
     * @return int
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 描述：插入order (除了自动生成的主键, createTime, updateTime)
     *
     * @param record 订单记录
     * @return int
     */
    int insert(Order record);

    /**
     * 描述：插入order中不为null的字段(除了自动生成的主键, createTime, updateTime)
     *
     * @param record 订单记录
     * @return int
     */
    int insertSelective(Order record);

    /**
     * 描述：根据主键查找 订单记录
     *
     * @param id 主键
     * @return top.kongk.mmall.pojo.Order
     */
    Order selectByPrimaryKey(Integer id);

    /**
     * 描述：根据主键更新 不为null的字段(除了createTime)
     *
     * @param record 订单记录
     * @return int
     */
    int updateByPrimaryKeySelective(Order record);

    /**
     * 描述：根据主键更新 (除了createTime)
     *
     * @param record 订单记录
     * @return int
     */
    int updateByPrimaryKey(Order record);

    /**
     * 描述：根据订单号和用户id查找订单
     *
     * @param orderNo 订单号
     * @param userId 用户id
     * @return top.kongk.mmall.pojo.Order
     */
    Order selectByOrderNoAndUserId(@Param("orderNo") long orderNo, @Param("userId") Integer userId);

    /**
     * 描述：根据订单号查找订单记录
     *
     * @param orderNo 订单号
     * @return top.kongk.mmall.pojo.Order
     */
    Order selectByOrderNo(long orderNo);

    /**
     * 描述：根据用户id 查找他的所有订单(按照时间降序)
     *
     * @param userId 用户id
     * @return java.util.List<top.kongk.mmall.pojo.Order>
     */
    List<Order> selectByUserId(Integer userId);

    /**
     * 描述：查找所有的订单
     *
     * @return java.util.List<top.kongk.mmall.pojo.Order>
     */
    List<Order> selectAllOrder();

}