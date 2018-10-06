package top.kongk.mmall.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 *
 * @author kk
 * @date 2018/9/28 22:37
 */

//使用junit的
@RunWith(SpringJUnit4ClassRunner.class)
//spring的配置文件
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class CartMapperTest {
    @Test
    public void deleteByUserIdAndProductIdLists() throws Exception {

        List<String> list = new ArrayList<>(3);
        list.add("23");
        list.add("24");
        list.add("25");

        int count = 0;
        try {
            count = cartMapper.deleteByUserIdAndProductIdLists(118, list);
        } catch (Exception e) {
        }
        System.out.println("count:" + count);

    }

    @Autowired
    CartMapper cartMapper;

    @Test
    public void selectCartsByUserId() throws Exception {
//        cartMapper.selectCartsByUserId()
    }

}