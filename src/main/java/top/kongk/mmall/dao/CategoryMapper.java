package top.kongk.mmall.dao;

import top.kongk.mmall.pojo.Category;

import java.util.List;

/**
 * 描述：Category表的映射文件
 *
 * @author kk
 * @date 2018/9/25 9:49
 */
public interface CategoryMapper {
    /**
     * 描述：根据主键id, 删除Category表的一行记录
     *
     * @param id 主键
     * @return int 成功返回1,失败返回0
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 描述：插入Category中除了主键id,创建更新时间之外的所有字段.
     * 属性name 和 parentId不能为null
     *
     * @param category 商品类别
     * @return int 成功返回1,失败返回0
     */
    int insert(Category category);

    /**
     * 描述：插入Category中除了主键id,创建更新时间之外的不为null的字段.
     * 属性name 和 parentId不能为null
     *
     * @param category 商品类别
     * @return int 成功返回1,失败返回0
     */
    int insertSelective(Category category);

    /**
     * 描述：根据主键查询 Category
     *
     * @param id 主键
     * @return top.kongk.mmall.pojo.Category 返回null或者Category对象
     */
    Category selectByPrimaryKey(Integer id);

    /**
     * 描述：根据主键id更新 category 中除了创建更新时间之外的不为null的字段
     *
     * @param category 商品类别
     * @return int 返回 1
     */
    int updateByPrimaryKeySelective(Category category);

    /**
     * 描述：根据主键id更新 category 中除了创建更新时间之外的所有字段
     * name 和 parentId 不能为null
     *
     * @param category 商品类别
     * @return int
     */
    int updateByPrimaryKey(Category category);

    /**
     * 描述：根据父节点的id,查询他的所有第一层的孩子
     *
     * @param parentId 父节点的id
     * @return java.util.List<top.kongk.mmall.pojo.Category>
     */
    List<Category> selectCategoriesByParentId(Integer parentId);

    /**
     * 描述：
     *
     * @param categories
     * @return java.util.List<top.kongk.mmall.pojo.Category>
     */
    List<Category> getCategoriesForeach(List<Category> categories);


}