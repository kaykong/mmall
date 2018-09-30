package top.kongk.mmall.dao;

import org.apache.ibatis.annotations.Param;
import top.kongk.mmall.pojo.Shipping;

import java.util.List;

/**
 * 描述：收货地址 dao 层接口
 *
 * @author kk
 * @date 2018/9/29 21:34
 */
public interface ShippingMapper {

    /**
     * 描述：根据主键删除记录
     *
     * @param id 主键
     * @return int
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 描述：插入收货地址(除了id, time 之外的属性)
     *
     * @param record 插入收货地址
     * @return int 成功返回插入的主键id, 否则返回0
     */
    int insert(Shipping record);

    /**
     * 描述：插入收货地址
     *
     * @param record 插入收货地址(除了id, time 之外不为 null 的属性)
     * @return int 成功返回插入的主键id, 否则返回0
     */
    int insertSelective(Shipping record);

    /**
     * 描述：根据主键查找 shipping
     *
     * @param id 主键
     * @return top.kongk.mmall.pojo.Shipping
     */
    Shipping selectByPrimaryKey(Integer id);

    /**
     * 描述：根据主键更新 shipping 不为 null 的字段(除了 id , 创建时间, 更新时间)
     *
     * @param record shipping
     * @return int
     */
    int updateByPrimaryKeySelective(Shipping record);

    /**
     * 描述：根据主键更新 shipping 的所有属性(除了 id , 创建时间, 更新时间)
     *
     * @param record shipping
     * @return int
     */
    int updateByPrimaryKey(Shipping record);

    /**
     * 描述：根据用户id 和 shippingId 删除收货地址
     *
     * @param userId 用户id
     * @param shippingId Shipping表主键
     * @return int
     */
    int deleteByUserIdAndShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    /**
     * 描述：根据用户id 查找他的收货地址
     *
     * @param userId 用户id
     * @return java.util.List<top.kongk.mmall.pojo.Shipping>
     */
    List<Shipping> selectByUserId(Integer userId);

    /**
     * 描述：根据shipping 中的id 更新Shipping表
     *
     * @param shipping 收货地址
     * @return int
     */
    int updateByUserIdAndShippingIdSelective(Shipping shipping);
}