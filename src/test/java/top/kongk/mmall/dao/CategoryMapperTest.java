package top.kongk.mmall.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.kongk.mmall.pojo.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：对CategoryMapper进行测试
 *
 * @author kk
 * @date 2018/9/25 9:55
 */
//使用junit的
@RunWith(SpringJUnit4ClassRunner.class)
//spring的配置文件
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class CategoryMapperTest {

    @Test
    public void getCategoriesForeach() throws Exception {

        List<Category> categories = new ArrayList<>(3);

        Category c = new Category();
        c.setName("qqwer");
        c.setId(100001);
        categories.add(c);

        Category c2 = new Category();
        c2.setName("qasdfwer");
        c2.setId(100002);
        categories.add(c2);

        Category c3 = new Category();
        c3.setName("qasdfwer");
        c3.setId(100003);
        categories.add(c3);

        List<Category> categoriesForeach = categoryMapper.getCategoriesForeach(categories);

        System.out.println(categoriesForeach);
    }

    @Test
    public void selectByParentId() throws Exception {
        Integer parentId = null;
        List<Category> categories = null;
        try {
            categories = categoryMapper.selectCategoriesByParentId(parentId);
        } catch (Exception e) {
            logger.error("CategoryMapperTest.selectByParentId Execption",e);
        }
        System.out.println(categories);
    }

    private static final Logger logger = LoggerFactory.getLogger(CategoryMapperTest.class);

    @Autowired
    CategoryMapper categoryMapper;

    @Test
    public void deleteByPrimaryKey() throws Exception {
        Integer id = 100036;

        int count = 0;
        try {
            count = categoryMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            logger.error("CategoryMapperTest.deleteByPrimaryKey Execption",e);
        }
        System.out.println("count:" + count);
    }

    @Test
    public void insert() throws Exception {
        Category category = new Category();

        int count = 0;
        count = categoryMapper.insert(category);
        try {

        } catch (Exception e) {
            logger.error("CategoryMapperTest.insertSelective Execption",e);
        }

        System.out.println("count:" + count);
    }

    @Test
    public void insertSelective() throws Exception {
        Category category = new Category();

        System.out.println(category);

        int count = 0;

        try {
            count = categoryMapper.insertSelective(category);
        } catch (Exception e) {
            logger.error("CategoryMapperTest.insertSelective Execption",e);
        }

        System.out.println("count:" + count);
    }

    @Test
    public void selectByPrimaryKey() throws Exception {


    }

    @Test
    public void updateByPrimaryKeySelective() throws Exception {
        Category category = new Category();

        category.setName("哈哈哈哈");
        category.setId(null);

        int count = 0;
        try {
            count = categoryMapper.updateByPrimaryKeySelective(category);
        } catch (Exception e) {
            logger.error("CategoryMapperTest.updateByPrimaryKeySelective Execption",e);
        }
        if (count == 0) {

        } else {

        }

        System.out.println("count:" + count);
    }

    @Test
    public void updateByPrimaryKey() throws Exception {
    }

}