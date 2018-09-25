package top.kongk.mmall.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.dao.CategoryMapper;
import top.kongk.mmall.pojo.Category;
import top.kongk.mmall.service.CategoryService;

import java.util.LinkedList;
import java.util.List;

/**
 * 描述：商品类别service实现
 *
 * @author kk
 * @date 2018/9/25 11:08
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public ServerResponse selectCategoriesByParentId(Integer parentId) {
        if (parentId == null) {
            parentId = 0;
        }

        List<Category> categories = null;
        try {
            categories = categoryMapper.selectCategoriesByParentId(parentId);
        } catch (Exception e) {
            logger.error("CategoryServiceImpl.selectCategoriesByParentId Execption",e);
        }

        if (CollectionUtils.isEmpty(categories)) {
            return ServerResponse.createErrorWithMsg("未找到该品类");
        } else {
            return ServerResponse.createSuccess(categories);
        }
    }

    @Override
    public ServerResponse addCategory(Integer parentId, String categoryName) {
        if (parentId == null) {
            parentId = 0;
        }
        if (StringUtils.isBlank(categoryName)) {
            return ServerResponse.createErrorWithMsg("商品类别名称格式错误");
        }

        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);

        int count = 0;
        try {
            count = categoryMapper.insertSelective(category);
        } catch (Exception e) {
            logger.error("CategoryServiceImpl.addCategory Execption",e);
        }

        if (count == 0) {
            return ServerResponse.createErrorWithMsg("添加商品类别失败");
        } else {
            return ServerResponse.createSuccessWithMsg("添加商品类别成功");
        }
    }

    @Override
    public ServerResponse setCategoryName(Integer categoryId, String categoryName) {
        if (StringUtils.isBlank(categoryName)) {
            return ServerResponse.createErrorWithMsg("商品类别名称格式错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int count = 0;
        try {
            count = categoryMapper.updateByPrimaryKeySelective(category);
        } catch (Exception e) {
            logger.error("CategoryServiceImpl.setCategoryName Execption",e);
        }
        if (count == 0) {
            return ServerResponse.createErrorWithMsg("更改商品类别名称失败");
        } else {
            return ServerResponse.createSuccessWithMsg("更改商品类别名称成功");
        }

    }

    @Override
    public ServerResponse getDeepCategory(Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);

        List<Integer> categories = new LinkedList<>();

        findAllChildrenCategories(categories, category);

        if (CollectionUtils.isEmpty(categories)) {
            return ServerResponse.createErrorWithMsg("获取商品类别失败");
        } else {
            return ServerResponse.createSuccess(categories);
        }

    }

    private void findAllChildrenCategories(List<Integer> categories, Category category) {
        if (category != null) {
            categories.add(category.getId());
        } else {
            return;
        }
        //获取当前 category 的第一层孩子
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(category.getId());

        //对第一层的每一个孩子递归调用此方法
        for (Category categoryItem : categoryList) {
            findAllChildrenCategories(categories, categoryItem);
        }
    }

    /*private List<Category> findAllChildrenCategories(List<Category> categories, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);

        if (category != null) {
            categories.add(category);
        }

        //获取当前category 的第一层孩子
        List<Category> categoryList = categoryMapper.selectFirstCategoriesByParentId(category.getId());

        // 对第一层的每一个孩子,再查找他的子孩子
        for (Category categoryItem : categoryList) {
            findAllChildrenCategories(categories, categoryItem.getId());
        }

        return categories;
    }*/


}
