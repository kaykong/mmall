package top.kongk.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.kongk.mmall.common.Const;
import top.kongk.mmall.common.ProductValidatorUtil;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.dao.CategoryMapper;
import top.kongk.mmall.dao.ProductMapper;
import top.kongk.mmall.pojo.Category;
import top.kongk.mmall.pojo.Product;
import top.kongk.mmall.service.ProductService;
import top.kongk.mmall.util.PropertiesUtil;
import top.kongk.mmall.vo.ProductDetailVo;
import top.kongk.mmall.vo.ProductListVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：ProductService 实现
 *
 * @author kk
 * @date 2018/9/26 11:24
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryMapper categoryMapper;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public ServerResponse getManageProductList(Integer pageNum, Integer pageSize) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        /**
         * 1 PageHelper.startPage开始
         * 2 填充sql
         * 3 PageInfo 收尾
         */
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = new ArrayList<>();

        try {
            products = productMapper.selectList();
        } catch (Exception e) {
            logger.error("ProductServiceImpl.getProductList Execption", e);
        }

        List<ProductListVo> productListVos = new ArrayList<>(products.size());
        for (Product product : products) {
            productListVos.add(new ProductListVo(product));
        }

        /**
         * 不是很明白的地方 :
         * 如果写成这样,返回的结果就是不正确的
         * PageInfo pageInfo = new PageInfo(productListVos);
         *
         */
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVos);

        return ServerResponse.createSuccess(pageInfo);
    }

    @Override
    public ServerResponse list(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy) {
        //校验, 设置参数
        if (StringUtils.isBlank(keyword)) {
            keyword = null;
        } else {
            keyword = new StringBuilder().append('%').append(keyword.trim()).append('%').toString();
        }

        if (StringUtils.isBlank(orderBy)) {
            orderBy = null;
        } else {
            orderBy = orderBy.replace("_", " ");
            if (!Const.ProductSort.sort.contains(orderBy)) {
                orderBy = null;
            }
        }

        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;

        //分页
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = new ArrayList<>();
        try {
            products = productMapper.selectListByCategoryIdKeywordOrderBy(categoryId, keyword, orderBy);
        } catch (Exception e) {
            logger.error("ProductController.getDetail Execption", e);
        }

        List<ProductListVo> productListVos = new ArrayList<>(products.size());
        for (Product product : products) {
            productListVos.add(new ProductListVo(product));
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVos);

        return ServerResponse.createSuccess(pageInfo);
    }

    @Override
    public ServerResponse insertOrUpdate(Product product) {

        if (product == null) {
            return ServerResponse.createIllegalArgumentError();
        } else {
            //把从前端传来的图片信息,截取第一个存到mainImage属性中
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] split = product.getSubImages().split(",");
                if (split.length > 0) {
                    product.setMainImage(split[0]);
                }
            }

        }

        if (product.getId() == null) {
            //新增产品
            int count = 0;
            try {
                count = productMapper.insertSelective(product);
            } catch (Exception e) {
                logger.error("ProductServiceImpl.insertOrUpdate Execption", e);
            }
            if (count == 0) {
                return ServerResponse.createErrorWithMsg("新增产品失败");
            } else {
                return ServerResponse.createSuccessWithMsg("新增产品成功");
            }
        } else {
            //更新产品
            int count = 0;
            try {
                count = productMapper.updateByPrimaryKeySelective(product);
            } catch (Exception e) {
                logger.error("ProductServiceImpl.insertOrUpdate Execption", e);
            }
            if (count == 0) {
                return ServerResponse.createErrorWithMsg("更新产品失败");
            } else {
                return ServerResponse.createSuccessWithMsg("更新产品成功");
            }
        }

    }

    @Override
    public ServerResponse setSaleStatus(Integer productId, Integer status) {
        if (productId == null || !ProductValidatorUtil.isStatus(status)) {
            return ServerResponse.createIllegalArgumentError();
        }

        int count = 0;
        try {
            count = productMapper.updateStatusByPrimaryKey(productId, status);
        } catch (Exception e) {
            logger.error("ProductServiceImpl.setSaleStatus Execption", e);
        }
        if (count == 0) {
            return ServerResponse.createErrorWithMsg("修改产品状态失败");
        } else {
            return ServerResponse.createErrorWithMsg("修改产品状态成功");
        }
    }

    @Override
    public ServerResponse getManageDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createIllegalArgumentError();
        }

        Product product = null;
        try {
            product = productMapper.selectByPrimaryKey(productId);
        } catch (Exception e) {
            logger.error("ProductServiceImpl.getDetail Execption", e);
        }

        if (product == null) {
            return ServerResponse.createErrorWithMsg("获取产品详情失败");
        } else {
            ProductDetailVo productDetailVo = assembleProductDetailVo(product);
            return ServerResponse.createSuccess(productDetailVo);
        }
    }

    @Override
    public ServerResponse getDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createIllegalArgumentError();
        }

        Product productOnSale = null;
        try {
            productOnSale = productMapper.selectOnSaleByPrimaryKey(productId);
        } catch (Exception e) {
            logger.error("ProductController.getDetail Execption", e);
        }
        if (productOnSale == null) {
            return ServerResponse.createErrorWithMsg("该商品已下架或删除");
        } else {
            ProductDetailVo productDetailVo = new ProductDetailVo(productOnSale);
            return ServerResponse.createSuccess(productDetailVo);
        }
    }

    @Override
    public ServerResponse search(String productName, Integer productId, Integer pageNum, Integer pageSize) {

        if (StringUtils.isBlank(productName)) {
            productName = null;
        } else {
            productName = new StringBuilder().append("%").append(productName.trim()).append("%").toString();
        }

        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = new ArrayList<>();
        try {
            products = productMapper.selectListByNameAndId(productName, productId);
        } catch (Exception e) {
            logger.error("ProductServiceImpl.search Execption", e);
        }

        List<ProductListVo> productListVos = new ArrayList<>(products.size());

        for (Product product : products) {
            productListVos.add(new ProductListVo(product));
        }

        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVos);

        return ServerResponse.createSuccess(pageInfo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo(product);

        //从配置文件中获取
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        //从数据库中获取parentId
        Category category = null;
        try {
            category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        } catch (Exception e) {
            logger.error("ProductServiceImpl.assembleProductDetailVo Execption", e);
        }

        if (category != null) {
            productDetailVo.setParentCategoryId(category.getParentId());
        } else {
            productDetailVo.setParentCategoryId(0);
        }

        return productDetailVo;
    }
}
