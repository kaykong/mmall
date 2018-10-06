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
 * @date 2018/10/3 22:34
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    public void updateStockByProductIdAndQuantity() throws Exception {
        int count = productMapper.updateStockByProductIdAndQuantity(26, 1);
        System.out.println("count:" + count);
    }

}