package com.zero.service;

import com.zero.dao.UserMapper;
import com.zero.enums.CodeEnum;
import com.zero.exception.BaseException;
import com.zero.po.User;
import com.zero.po.UserExample;
import com.zero.vo.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/4/29
 */
@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    @Resource
    private UserMapper userMapper;
    @Resource
    private MailService mailService;

    public int login(String username, String password) throws BaseException {
        UserExample example = new UserExample();
        example.createCriteria().andNameEqualTo(username).andPasswordEqualTo(password);
        java.util.List<User> users = userMapper.selectByExample(example);
        if (users.isEmpty()) {
            throw new com.zero.exception.BaseException(CodeEnum.LOGIN_FAIL, "用户名或者密码错误!");
        } else {
            Integer userId = users.get(0).getId();
            LOG.info("userId={} login success", userId);
            return userId;
        }
    }

    public User getUserInfo(int userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        user.setPassword("******");
        return user;
    }

    public int add(UserDto userDto) throws IOException {
        User tmp = new User();
        tmp.setAge(userDto.getAge());
        String name = userDto.getName();
        tmp.setName(name);
        tmp.setPassword(userDto.getPassword());
        String phone = userDto.getPhone();
        tmp.setPhone(phone);
        String email = userDto.getEmail();
        tmp.setEmail(email);
        userMapper.insertSelective(tmp);
        mailService.sendMail(email, "注册邮件", String.format("<h1>%s注册成功!</h1>", name));
        LOG.info("name={} phone={} email={} register success", name, phone, email);
        return tmp.getId();
    }
}
