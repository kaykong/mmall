package top.kongk.mmall.dao;

import org.apache.ibatis.annotations.Param;
import top.kongk.mmall.pojo.Product;

import java.util.List;
/**
 * 描述：产品的数据库接口
 *
 * @author kk
 * @date 2018/9/26 18:33
 */
public interface ProductMapper {

    /**
     * 描述：根据主键删除产品
     *
     * @param id 主键
     * @return int
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 描述：插入产品的属性(除了主键, 创建时间, 更新时间)
     *
     * @param record  产品
     * @return int
     */
    int insert(Product record);

    /**
     * 描述：插入产品不为null的字段 (除了主键, 创建时间, 更新时间)
     *
     * @param record 产品
     * @return int
     */
    int insertSelective(Product record);

    /**
     * 描述：通过主键id查询
     *
     * @param id 主键
     * @return top.kongk.mmall.pojo.Product
     */
    Product selectByPrimaryKey(Integer id);

    /**
     * 描述：通过主键id查询在售状态的商品
     *
     * @param id 主键
     * @return top.kongk.mmall.pojo.Product
     */
    Product selectOnSaleByPrimaryKey(Integer id);

    /**
     * 描述：根据主键id更新不为null的属性
     *
     * @param record 产品
     * @return int
     */
    int updateByPrimaryKeySelective(Product record);

    /**
     * 描述：根据主键id更新
     *
     * @param record 产品
     * @return int
     */
    int updateByPrimaryKey(Product record);

    /**
     * 描述：获取产品列表
     *
     * @return java.util.List<top.kongk.mmall.pojo.Product>
     */
    List<Product> selectList();

    /**
     * 描述：根据条件查询
     *
     * @param categoryId 分类id
     * @param keyword 关键词
     * @param orderBy 按啥排序
     * @return java.util.List<top.kongk.mmall.pojo.Product>
     */
    List<Product> selectListByCategoryIdKeywordOrderBy(@Param("categoryId") Integer categoryId,
                                         @Param("keyword") String keyword,
                                         @Param("orderBy") String orderBy);

    /**
     * 描述：通过主键更新状态码
     *
     * @param id 主键
     * @param status 状态码
     * @return int
     */
    int updateStatusByPrimaryKey(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 描述：根据提供的产品名或者产品id进行搜索
     *
     * @param productName 产品名
     * @param productId 产品id
     * @return java.util.List<top.kongk.mmall.pojo.Product>
     */
    List<Product> selectListByNameAndId(@Param("productName") String productName,
                                        @Param("productId") Integer productId);


    /**
     * 描述：根据productId 更新 product 的库存
     *
     * @param productId id
     * @param quantity 要减去的商品数量
     * @return int
     */
    int updateStockByProductIdAndQuantity(@Param("productId") Integer productId, @Param("quantity") Integer quantity);


}