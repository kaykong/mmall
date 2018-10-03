package top.kongk.mmall.dao;

import org.apache.ibatis.annotations.Param;
import top.kongk.mmall.pojo.Order;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    /**
     * 描述：根据主键更新
     *
     * @param record 订单记录
     * @return int
     */
    int updateByPrimaryKeySelective(Order record);

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

}