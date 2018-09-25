package top.kongk.mmall.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.kongk.mmall.common.ServerResponse;
import top.kongk.mmall.common.TokenCache;
import top.kongk.mmall.common.UserValidatorUtil;
import top.kongk.mmall.dao.UserMapper;
import top.kongk.mmall.pojo.User;
import top.kongk.mmall.service.UserService;
import top.kongk.mmall.util.MD5Util;

import java.util.List;
import java.util.UUID;

/**
 * @author kk
 * @date 2018/9/23 18:27
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Override
    public ServerResponse checkUsername(String username) {
        //校验格式
        if (!UserValidatorUtil.isUsername(username)) {
            return ServerResponse.createFormatError(username + "用户名格式错误!");
        }

        //在数据库中 判断用户名是否存在
        Integer id = null;
        try {
            id = userMapper.selectIdByName(username);
        } catch (Exception e) {
            logger.error("method: checkUsername() : " + e);
        }
        if (id != null) {
            return ServerResponse.createErrorWithMsg("用户名已被占用");
        } else {
            return ServerResponse.createSuccessWithMsg("用户名尚未存在");
        }

    }

    @Override
    public ServerResponse checkEmail(String email) {
        //校验格式
        if (!UserValidatorUtil.isEmail(email)) {
            return ServerResponse.createFormatError(email + "邮箱格式错误!");
        }

        //判断邮箱是否存在
        Integer id = null;
        try {
            id = userMapper.selectIdByEmail(email);
        } catch (Exception e) {
            logger.error("method: checkEmail() : " + e);
        }
        if (id != null) {
            return ServerResponse.createError("邮箱已存在", id);
        } else {
            return ServerResponse.createSuccessWithMsg("邮箱尚未存在");
        }

    }

    @Override
    public ServerResponse checkPhone(String phone) {
        //校验格式
        if (!UserValidatorUtil.isPhone(phone)) {
            return ServerResponse.createFormatError(phone + "手机号格式错误!");
        }

        //判断手机号是否存在
        Integer id = null;
        try {
            id = userMapper.selectIdByPhone(phone);
        } catch (Exception e) {
            logger.error("method: checkPhone() : " + e);
        }
        if (id != null) {
            return ServerResponse.createError("手机号已存在", id);
        } else {
            return ServerResponse.createSuccessWithMsg("手机号尚未存在");
        }
    }

    @Override
    public ServerResponse loginByUsernameAndPwd(String username, String password) {
        //检查用户名
        ServerResponse usernameResponse = checkUsername(username);
        //如果出现格式错误或者用户名不存在
        if (!usernameResponse.isError()) {
            return ServerResponse.createErrorWithMsg(usernameResponse.getMsg());
        }

        User user = null;
        //从数据库中获取数据
        try {
            //给密码加密
            password = MD5Util.MD5EncodeUtf8(password);
            user = userMapper.selectByUsernameAndPassword(username, password);
        } catch (Exception e) {
            logger.error("method: loginByUsernameAndPwd() : " + e);
        }
        if (user == null) {
            return ServerResponse.createErrorWithMsg("密码错误!");
        } else {
            return ServerResponse.createSuccess("登录成功", user);
        }
    }

    @Override
    public ServerResponse register(User user) {
        //先校验格式
        List<String> list = UserValidatorUtil.isUserWithInfo(user);
        if (!CollectionUtils.isEmpty(list)) {
            return ServerResponse.createErrorWithMsg(list.toString());
        }

        //如果用户名格式不对，或者已经被占用
        ServerResponse checkUsername = checkUsername(user.getUsername());
        if (!checkUsername.isSuccess()) {
            return checkUsername;
        }

        //检查邮箱
        if (user.getEmail() != null) {
            ServerResponse emailResponse = checkEmail(user.getEmail());
            //如果邮箱格式不对，或者已经被占用
            if (!emailResponse.isSuccess()) {
                return emailResponse;
            }
        }

        //检查手机号
        if (user.getPhone() != null) {
            ServerResponse phoneResponse = checkPhone(user.getPhone());
            //如果手机号格式不对，或者已经被占用
            if (!phoneResponse.isSuccess()) {
                return phoneResponse;
            }
        }

        //给密码加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        //在数据库中插入user
        int count = 0;
        try {
            count = userMapper.insertSelectiveWithoutTime(user);
        } catch (Exception e) {
            logger.error("method: register() : " + e);
        }
        if (count == 0) {
            return ServerResponse.createErrorWithMsg("注册失败!");
        } else {
            return ServerResponse.createErrorWithMsg("注册成功!");
        }

    }

    @Override
    public ServerResponse getQuestion(String username) {
        //验证用户名的格式 以及是否存在
        ServerResponse responseUsername = checkUsername(username);
        if (!responseUsername.isError()) {
            return ServerResponse.createErrorWithMsg(responseUsername.getMsg());
        }

        //从数据库中查找question
        String question = null;
        try {
            question = userMapper.selectQuestionByUsername(username);
        } catch (Exception e) {
            logger.error("method: getQuestion() : " + e);
        }
        if (question == null) {
            return ServerResponse.createErrorWithMsg("该用户未设置密保问题");
        } else {
            return ServerResponse.createSuccess(question);
        }
    }

    @Override
    public ServerResponse checkAnswer(String username, String question, String answer) {
        //不用检查用户名是否存在 只检查格式就行
        if (!UserValidatorUtil.isUsername(username)) {
            return ServerResponse.createErrorWithMsg("用户名格式错误");
        }

        if (StringUtils.isBlank(question)) {
            return ServerResponse.createErrorWithMsg("找回密码问题为空");
        }

        if (StringUtils.isBlank(answer)) {
            return ServerResponse.createErrorWithMsg("问题答案为空");
        }

        //验证数据库
        Integer id = null;
        try {
            id = userMapper.selectIdByUsernameQuestionAnswer(username, question, answer);
        } catch (Exception e) {
            logger.error("method: checkAnswer() : " + e);
        }
        if (id == null) {
            return ServerResponse.createErrorWithMsg("问题答案错误");
        } else {
            /*
              如果用户名，答案，密码都验证通过了
              那么就在服务端生成一个带过期时间的token（UUID）返回给浏览器，并put到缓存中
             */
            String token = UUID.randomUUID().toString();

            //将username 和 token 放入缓存中
            TokenCache.put(username, token);

            //把token返回到前台
            return ServerResponse.createSuccess(token);
        }
    }

    @Override
    public ServerResponse resetPasswordByToken(String username, String newPassword, String forgetToken) {
        //先判断username是否存在
        ServerResponse usernameResponse = checkUsername(username);
        //如果username格式错误，或者不存在
        if (!usernameResponse.isError()) {
            return ServerResponse.createErrorWithMsg(usernameResponse.getMsg());
        }

        String token = TokenCache.get(username);

        //如果此username没有对应的token
        if (StringUtils.isBlank(token) || !token.equals(forgetToken)) {
            return ServerResponse.createErrorWithMsg("token无效");
        }

        //更新密码
        newPassword = MD5Util.MD5EncodeUtf8(newPassword);
        int count = 0;
        try {
            count = userMapper.updateNewPassword(username, newPassword);
        } catch (Exception e) {
            logger.error("UserServiceImpl.resetPassword Execption", e);
        }
        if (count == 0) {
            return ServerResponse.createErrorWithMsg("修改密码失败");
        } else {
            //清除缓存中该用户的token
            TokenCache.invalidateToken(username);

            return ServerResponse.createSuccessWithMsg("修改密码成功");
        }
    }

    @Override
    public ServerResponse resetPasswordByOldPwd(String username, String password, String newPassword) {
        //先验证账号密码
        ServerResponse loginResponse = loginByUsernameAndPwd(username, password);
        if (!loginResponse.isSuccess()) {
            return ServerResponse.createErrorWithMsg("请先重新登录");
        }

        //更新密码
        newPassword = MD5Util.MD5EncodeUtf8(newPassword);

        int count = 0;
        try {
            count = userMapper.updateNewPassword(username, newPassword);
        } catch (Exception e) {
            logger.error("UserServiceImpl.resetPassword Execption", e);
        }
        if (count == 0) {
            return ServerResponse.createErrorWithMsg("修改密码失败");
        } else {
            return ServerResponse.createSuccessWithMsg("修改密码成功");
        }
    }

    @Override
    public ServerResponse updateInformation(User user) {
        //检查邮箱
        if (user.getEmail() != null) {
            ServerResponse emailResponse = checkEmail(user.getEmail());
            //如果邮箱格式不对，或者已经被占用, 并且不是自己占用的
            if (!emailResponse.isSuccess() && emailResponse.getData() != user.getId()) {
                emailResponse.setData(null);
                return emailResponse;
            }
        }

        //检查手机号
        if (user.getPhone() != null) {
            ServerResponse phoneResponse = checkPhone(user.getPhone());
            //如果手机号格式不对，或者已经被占用, 并且不是自己占用的
            if (!phoneResponse.isSuccess() && phoneResponse.getData() != user.getId()) {
                phoneResponse.setData(null);
                return phoneResponse;
            }
        }

        if (user.getQuestion() != null) {
            if (StringUtils.isBlank(user.getQuestion())) {
                return ServerResponse.createErrorWithMsg("密保问题不能为空白");
            }
        }

        if (user.getAnswer() != null) {
            if (StringUtils.isBlank(user.getAnswer())) {
                return ServerResponse.createErrorWithMsg("答案不能为空白");
            }
        }

        user.setRole(null);
        user.setPassword(null);
        user.setUsername(null);

        int count = 0;
        try {
            count = userMapper.updateByPrimaryKeySelective(user);
        } catch (Exception e) {
            logger.error("UserServiceImpl.updateInformation Execption", e);
        }
        if (count == 0) {
            return ServerResponse.createErrorWithMsg("更新失败");
        } else {
            return ServerResponse.createSuccessWithMsg("更新成功");
        }
    }

    @Override
    public ServerResponse getAllInformationWithoutPwd(Integer id) {
        if (id == null) {
            return ServerResponse.createNeedloginError("用户未登录,无法获取当前用户信息,status=10,强制登录");
        }

        User user = null;
        try {
            user = userMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            logger.error("UserServiceImpl.getAllInformationWithoutPwd Execption", e);
        }
        if (user == null) {
            return ServerResponse.createErrorWithMsg("获取信息失败");
        } else {
            user.setPassword(null);
            return ServerResponse.createSuccess(user);
        }
    }

}
