package top.kongk.mmall.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.kongk.mmall.pojo.User;
import top.kongk.mmall.util.MD5Util;
import top.kongk.mmall.util.RandomUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//使用junit的
@RunWith(SpringJUnit4ClassRunner.class)
//spring的配置文件
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class UserMapperTest {
    @Test
    public void updateNewPassword() throws Exception {
        String username = "admin123";
        String password = "12345";
        int count = userMapper.updateNewPassword(username, password);

        System.out.println("count:" + count);
    }


    @Test
    public void selectIdByUsernameQuestionAnswer() throws Exception {
        String username = "5bcf531b";
        String question = "乐婷爱-庞明生";
        String answer = "不知道";

        Integer id = userMapper.selectIdByUsernameQuestionAnswer(username, question, answer);
        System.out.println(id);
    }

    @Test
    public void checkEmail() throws Exception {
        String email = "k12312@126.com";
        System.out.println(userMapper.selectIdByEmail(email));
    }

    @Test
    public void selectQuestionByUsername() throws Exception {
        String username = "5bcf531b";
        System.out.println(userMapper.selectQuestionByUsername(username));
    }

    @Test
    public void countByName() throws Exception {
        String username = "5bcf531b";
        Integer count = userMapper.selectIdByName(username);
        System.out.println("id:" + count);
    }

    private Logger logger = LoggerFactory.getLogger(UserMapperTest.class);

    @Autowired
    UserMapper userMapper;


    @Test
    public void testBlank() {
        /*String username = null;
        if (org.apache.commons.lang.StringUtils.isBlank(username)) {
            System.out.println("apache-blank");
        }else
            System.out.println("apache-not blank");
        if (StringUtils.isBlank(username)) {
            System.out.println("blank");
        }else
            System.out.println("not blank");*/
        //System.out.println(CheckVariables.userName(" 1"));
    }

    @Test
    public void getMd5() {

        //加的盐值长度为 100+

        // 两个相同的 password
        String password1  = "12345asdfkjlweqr" + "asdfjklqwer";
        String password2 = "12345asdfkjlweqr" + "asdfjklqwer";

        String s1 = MD5Util.MD5EncodeUtf8(password1);
        String s2 = MD5Util.MD5EncodeUtf8(password2);

        // 输出: true
        System.out.println(s1.equals(s2));

        // 两个不同的差别很小的 password
        String password3 = "12345asdfkjlweqr" + "asdfjklqwer";
        String password4 = "12345asdfkjlweqr" + "asdfjklqwe";

        String s3 = MD5Util.MD5EncodeUtf8(password3);
        String s4 = MD5Util.MD5EncodeUtf8(password4);

        // 输出: false
        System.out.println(s3.equals(s4));
    }


    @Test
    public void selectByUsernameAndPassword() throws Exception {
        String username = null;
        String password = null;

        User user = null;
        try {
            user = userMapper.selectByUsernameAndPassword(username, password);
        } catch (Exception e) {
            System.out.println("出现了异常:" + e);
        }

        System.out.println("user:" + user);

    }



    //id username password email phone question answer role create_time update_time
    @Test
    public void deleteByPrimaryKey() throws Exception {

        int count = 0;
        try {
            count = userMapper.deleteByPrimaryKey(null);
            System.out.println(count);
        } catch (Exception e) {
            System.out.println("出现了异常:" + e);
        }

        System.out.println("count:" + count);
    }

    @Test
    public void insert() throws Exception {

        User user = RandomUser.getNormalUser();
        //故意设置一个重复的 自增id
        user.setId(90);
        System.out.println(user);

        int count = 0;
        try {
            count = userMapper.insert(user);
            System.out.println(count);
        } catch (Exception e) {
            System.out.println("出现了异常:" + e);
        }

        System.out.println("count:" + count);
    }


    @Test
    public void insertSelectiveWithoutTime() throws Exception {
        //RandomUser.getNormalUser() 没有设置createTime, updateTime
        //它们都为null
        User user = RandomUser.getNormalUser();

        //故意设置一个重复的 自增id，那么会捕获到异常，count 将为 0
        user.setId(90);
        System.out.println(user);

        int count = 0;
        try {
            count = userMapper.insertSelectiveWithoutTime(user);
        } catch (Exception e) {
            System.out.println("出现了异常: " + e);
        }

        System.out.println("count: " + count);
    }

    @Test
    public void selectByPrimaryKey() throws Exception {
        int id = 104;

        User user = userMapper.selectByPrimaryKey(id);
        User user2 = userMapper.selectByPrimaryKey(1000);

        System.out.println(user);
        System.out.println(user2);
    }

    @Test
    public void updateByPrimaryKeySelective() throws Exception {
        User user = RandomUser.getNormalUser();
        user.setId(104);

        //获取时间加一年或加一月或加一天
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//设置起时间

        cal.add(Calendar.DATE, 3);//增加一天  
        System.out.println("time:"+cal.getTime());

        //数据库设置的时间值 是有效的,这里代码带过去的时间不起作用
        user.setCreateTime(cal.getTime());
        user.setUpdateTime(cal.getTime());
        System.out.println(user);

        int count = 0;
        try {
            count = userMapper.updateByPrimaryKeySelective(null);
        } catch (Exception e) {
            System.out.println("出现了异常: " + e);
        }

        System.out.println("count: " + count);
    }

    @Test
    public void updateByPrimaryKey() throws Exception {
        User user = RandomUser.getNormalUser();
        user.setId(104);

        //获取时间加一年或加一月或加一天
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//设置起时间

        cal.add(Calendar.DATE, 3);//增加一天  
        System.out.println("time:"+cal.getTime());

        //数据库设置的时间值 是有效的,这里代码带过去的时间不起作用
        user.setCreateTime(cal.getTime());
        user.setUpdateTime(cal.getTime());
        System.out.println(user);

        int count = 0;
        try {
            count = userMapper.updateByPrimaryKeySelective(null);
        } catch (Exception e) {
            System.out.println("出现了异常: " + e);
            logger.error(e.toString());
        }

        System.out.println("count: " + count);
    }

    @Test
    public void testLoggerRuntime() throws Exception {

        int count = 0;
        try {
            int c = 5 / count;
        } catch (Exception e) {
            logger.error(e.toString());
        }

        System.out.println("count: " + count);
    }

    @Test
    public void testNewList() throws Exception {

        List<String> list = new ArrayList<>();

        list.add("hh");
        list.add("qq");

        System.out.println(list);
    }

    @Test
    public void testRegister() throws Exception {

        User user = new User();
        user.setUsername("sfdf23423");
        user.setPassword("12343wegsdf");
        user.setRole(1);
        user.setEmail("k123@126.com");


        //在数据库中插入user
        int count = 0;
        try {
            count = userMapper.insertSelectiveWithoutTime(user);
        } catch (Exception e) {
            logger.error("method: testRegister() : " + e);
            System.out.println("返回服务器出错信息");
        }

        if (count == 0) {
            System.out.println("注册失败");
        } else {
            System.out.println("注册成功");
        }
    }
}