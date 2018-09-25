package top.kongk.mmall.service;

import top.kongk.mmall.common.ServerResponse;

/**
 * 描述：商品类别
 *
 * @author kk
 * @date 2018/9/25 10:44
 */

public interface CategoryService {


    /**
     * 描述：根据父节点 id 获取他的第一层孩子
     *
     * @param parentId 父节点id
     * @return ServerResponse
     */
    ServerResponse selectCategoriesByParentId(Integer parentId);

    /**
     * 描述：根据父节点id, 类别名称添加一条新的类别
     *
     * @param parentId 父节点id
     * @param categoryName 类别名称
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse addCategory(Integer parentId, String categoryName);

    /**
     * 描述：根据商品类别 id, 更新新的商品类别名称
     *
     * @param categoryId 商品类别 id
     * @param categoryName 新的商品类别名称
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse setCategoryName(Integer categoryId, String categoryName);

    /**
     * 描述：根据商品类别id获取他的所有子节点
     *
     * @param categoryId 商品类别id
     * @return top.kongk.mmall.common.ServerResponse
     */
    ServerResponse getDeepCategory(Integer categoryId);
}
