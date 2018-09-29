package top.kongk.mmall.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    @Autowired
    CartMapper cartMapper;

    @Test
    public void selectCartsByUserId() throws Exception {
//        cartMapper.selectCartsByUserId()
    }

}