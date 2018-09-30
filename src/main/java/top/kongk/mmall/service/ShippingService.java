package top.kongk.mmall.service;

import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.pojo.Shipping; /**
 * 描述：收货地址模块的 service
 *
 * @author kk
 * @date 2018/9/29 17:37
 */
public interface ShippingService {

    /**
     * 描述：给用户添加收货地址
     *
     * @param userId 用户id
     * @param shipping 收获地址
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse add(Integer userId, Shipping shipping);

    /**
     * 描述：删除用户的收货地址
     *
     * @param userId 用户id
     * @param shippingId 收货地址id
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse delete(Integer userId, Integer shippingId);

    /**
     * 描述：根据用户id 和 shipping 中的id属性来更新 shipping 中其他不为 null 的字段
     *
     * @param userId 用户id
     * @param shipping 收货地址
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse update(Integer userId, Shipping shipping);

    /**
     * 描述：根据userId 和 shippingId 查询
     *
     * @param userId 用户id
     * @param shippingId 收货地址id
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse select(Integer userId, Integer shippingId);

    /**
     * 描述：根据用户id分页查找他的收货地址
     *
     * @param userId 用户id
     * @param pageNum 第几页
     * @param pageSize 每页显示多少个
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse list(Integer userId, Integer pageNum, Integer pageSize);
}
