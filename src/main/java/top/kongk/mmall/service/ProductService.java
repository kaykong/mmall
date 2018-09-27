package top.kongk.mmall.service;

import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.pojo.Product;

/**
 * 描述：产品 service
 *
 * @author kk
 * @date 2018/9/26 11:23
 */
public interface ProductService {

    /**
     * 描述：获取产品列表(后台使用)
     *
     * @param pageNum 第几页
     * @param pageSize 每页显示多少个
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse getManageProductList(Integer pageNum, Integer pageSize);

    /**
     * 描述：前台按条件搜索list
     *
     * @param categoryId 商品种类id
     * @param keyword 搜索关键词
     * @param pageNum 第几页
     * @param pageSize 每页显示多少个
     * @param orderBy 按什么顺序排
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse list(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy);

    /**
     * 描述：根据传入的produce的id是否是null, 来判断是插入还是更新
     *
     * @param product 产品
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse insertOrUpdate(Product product);

    /**
     * 描述：更改产品状态(在售1,下架2,删除3)
     *
     * @param productId 产品id
     * @param status 状态码
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse setSaleStatus(Integer productId, Integer status);

    /**
     * 描述：产品详情(后台使用)
     *
     * @param productId 产品id
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse getManageDetail(Integer productId);

    /**
     * 描述：产品详情(前台使用)
     *
     * @param productId 产品id
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse getDetail(Integer productId);

    /**
     * 描述：根据产品名和产品id 搜索
     *
     * @param productName 产品名
     * @param productId   产品id
     * @param pageNum     第几页
     * @param pageSize    每页显示多少个
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse search(String productName, Integer productId, Integer pageNum, Integer pageSize);
}
