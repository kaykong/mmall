package top.kongk.mmall.dao;

import org.apache.ibatis.annotations.Param;
import top.kongk.mmall.pojo.User;

/**
 * @author : kk
 * @date : 2018/9/22 19:55
 */
public interface UserMapper {

    /**
     * 描述: 通过id删除用户
     *
     * @param id 用户id 
     * @return int 删除失败返回 0 ，删除成功返回 1
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 描述: 在数据库中插入user
     * (username,password,role不能为null，不需要id,createTime,updateTime属性，数据库会自动生成)
     *
     * @param user 用户(不需要id，createTime，updateTime属性，数据库会自动生成)
     * @return int 成功插入返回1，插入失败返回0
     */
    int insert(User user);

    /**
     * 描述: 在数据库中插入user
     * (username,password,role不能为null，不需要id,createTime,updateTime属性，数据库会自动生成)
     *
     * @param user 用户
     * @return int 成功插入返回1，插入失败返回0
     */
    int insertSelectiveWithoutTime(User user);

    /**
     * 描述: 根据id值查找用户
     *
     * @param id 用户id
     * @return top.kongk.mmall.pojo.User
     */
    User selectByPrimaryKey(Integer id);

    /**
     * 描述: 通过传来的用户名和密码查找并返回该用户
     *
     * @param username 用户名
     * @param password 密码
     * @return top.kongk.mmall.pojo.User 返回用户或 null
     */
    User selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    /**
     * 描述: 通过user中的id作为where条件，更新user中不为null的属性
     *
     * @param user 用户表的一行记录
     * @return int 成功更新返回1，没有需要更新的字段或者表中没有此主键则返回0
     */
    int updateByPrimaryKeySelective(User user);

    /**
     * 描述: user的id对应用户表的主键，更新user的不为null的属性（除了id，createTime）
     *
     * @param user 用户表的一行记录
     * @return int
     */
    int updateByPrimaryKey(User user);


    /**
     * 描述: 根据用户名查找用户的id值，可以用来判断用户名是否被占用
     *
     * @param name 用户名
     * @return java.lang.Integer 查到了此用户名返回一个整数，否则返回null
     */
    Integer selectIdByName(String name);


    /**
     * 描述: 根据用户邮箱查找用户的id，可以用来判断邮箱是否被占用
     *
     * @param email 用户邮箱
     * @return java.lang.Integer 查到了此邮箱返回一个整数，否则返回null
     */
    Integer selectIdByEmail(String email);


    /**
     * 描述: 根据手机号查找用户的id，可以用来判断手机号是否被占用
     *
     * @param phone 手机号
     * @return java.lang.Integer 查到了此手机号返回一个整数id，否则返回null
     */
    Integer selectIdByPhone(String phone);

    /**
     * 描述: 根据用户名查找用户的密保问题
     *
     * @param username 用户名
     * @return java.lang.String  查不到返回null
     * */
    String selectQuestionByUsername(String username);

    /**
     * 描述: 根据用户名，密保问题，答案查找此用户id，可以用来检验密保问题答案是否正确
     *
     * @param username 用户名
     * @param question 密保问题
     * @param answer 答案
     * @return java.lang.Integer 成功返回用户id 失败返回null
     */
    Integer selectIdByUsernameQuestionAnswer(@Param("username") String username,
                                             @Param("question") String question,
                                             @Param("answer") String answer);

    /**
     * 描述：根据用户名更新密码
     *
     * @param username 用户名
     * @param newPassword 新密码
     * @return int 成功返回1，失败返回0
     */
    int updateNewPassword(@Param("username") String username, @Param("password") String newPassword);

}