package com.zero.user.service;

import com.zero.dao.UserMapper;
import com.zero.enums.CodeEnum;
import com.zero.exception.BaseException;
import com.zero.po.User;
import com.zero.po.UserExample;
import com.zero.user.util.RegexUtil;
import com.zero.user.vo.BindEmailVo;
import com.zero.user.vo.dto.UserDto;
import com.zero.util.DateHelper;
import com.zero.util.JsonHelper;
import com.zero.util.MediaHelper;
import com.zero.util.RedisHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/4/29
 */
@Service
public class UserService {

    private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
        public DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
    };
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private static final int EMAIL_EXPIRE_TIME = ((Long) TimeUnit.DAYS.toSeconds(1)).intValue();
    @Resource
    private UserMapper userMapper;
    @Resource
    private MailService mailService;
    @Value("${bind.email.content}")
    private String bindEmailContent;

    public int updateLogin(String username, String password) throws BaseException {
        UserExample example = new UserExample();
        example.createCriteria().andNameEqualTo(username).andPasswordEqualTo(password);
        java.util.List<User> users = userMapper.selectByExample(example);
        if (users.isEmpty()) {
            throw new com.zero.exception.BaseException(CodeEnum.LOGIN_FAIL, "用户名或者密码错误!");
        } else {
            Integer userId = users.get(0).getId();
            User tmp = new User();
            tmp.setId(userId);
            tmp.setLastLoginTime(DateHelper.getCurrentDateTime());
            userMapper.updateByPrimaryKeySelective(tmp);
            LOG.info("userId={} login success", userId);
            return userId;
        }
    }

    public User getUserInfo(int userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        user.setHeadImg(MediaHelper.getFullMediaPath(user.getHeadImg()));
        user.setPassword("******");
        return user;
    }

    public int add(UserDto userDto) throws IOException, BaseException {
        User tmp = new User();
        tmp.setAge(userDto.getAge());
        String name = userDto.getName();
        tmp.setName(name);
        tmp.setPassword(userDto.getPassword());
        String phone = userDto.getPhone();
        tmp.setPhone(phone);
        userMapper.insertSelective(tmp);
        LOG.info("name={} phone={} register success", name, phone);
        return tmp.getId();
    }

    public void updateHeadImg(Integer userId, String fileName) {
        User tmp = new User();
        tmp.setId(userId);
        tmp.setHeadImg(fileName);
        userMapper.updateByPrimaryKeySelective(tmp);
        LOG.info("userId={} update headImg={}", userId, fileName);
    }

    public void bindEmail(Integer userId, String email) throws Exception {
        if (!RegexUtil.checkEmail(email)) {
            throw new BaseException(CodeEnum.EMAIL_UN_CHECK, "邮件格式错误!");
        }
        BindEmailVo tmp = new BindEmailVo();
        tmp.setUserId(userId);
        tmp.setEmail(email);
        String key = UUID.randomUUID().toString();
        String redisKey = RedisHelper.emailKeyWrapper(key);
        RedisHelper.set(redisKey, JsonHelper.toJSon(tmp));
        RedisHelper.expire(redisKey, EMAIL_EXPIRE_TIME);
        mailService.sendMail(email, "绑定邮箱", String.format(bindEmailContent, key));
    }

    public int updateBindEmail(String key) throws Exception {
        String redisKey = RedisHelper.emailKeyWrapper(key);
        BindEmailVo bindEmailVo = JsonHelper.readValue(RedisHelper.get(redisKey), BindEmailVo.class);
        if (bindEmailVo != null) {
            User tmp = new User();
            int userId = bindEmailVo.getUserId();
            tmp.setId(userId);
            tmp.setEmail(bindEmailVo.getEmail());
            userMapper.updateByPrimaryKeySelective(tmp);
            LOG.info("userId={} bind email success", userId);
            return userId;
        } else {
            throw new BaseException(CodeEnum.BIND_EMAIL_FAIL, "绑定失败!");
        }
    }

    public XSSFWorkbook generateExcel() {

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet1 = wb.createSheet("user表");
        XSSFRow row1 = sheet1.createRow(0);

        XSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);

        XSSFCell cell1 = row1.createCell(0);
        cell1.setCellValue("姓名");
        cell1.setCellStyle(style);
        cell1 = row1.createCell(1);
        cell1.setCellValue("手机号");
        cell1.setCellStyle(style);
        cell1 = row1.createCell(2);
        cell1.setCellValue("邮箱");
        cell1.setCellStyle(style);
        cell1 = row1.createCell(3);
        cell1.setCellValue("登陆时间");
        cell1.setCellStyle(style);

        XSSFCellStyle style1 = wb.createCellStyle();
        style1.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style1.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style1.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style1.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style1.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        List<User> users = list();
        // 写入数据
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            row1 = sheet1.createRow(i + 1);
            cell1 = row1.createCell(0);
            cell1.setCellValue(user.getName());
            cell1.setCellStyle(style1);
            cell1 = row1.createCell(1);
            cell1.setCellStyle(style1);
            cell1.setCellValue(user.getPhone());
            cell1 = row1.createCell(2);
            cell1.setCellValue(user.getEmail());
            cell1.setCellStyle(style1);
            cell1 = row1.createCell(3);
            cell1.setCellValue(DATE_FORMAT.get().format(user.getLastLoginTime()));
            cell1.setCellStyle(style1);
        }
        sheet1.setColumnWidth(0, 100 * 30);
        sheet1.setColumnWidth(1, 100 * 40);
        sheet1.setColumnWidth(2, 100 * 60);
        sheet1.setColumnWidth(3, 100 * 60);
        return wb;
    }

    private List<User> list() {
        UserExample example = new UserExample();
        List<User> users = userMapper.selectByExample(example);
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                Integer o1Age = o1.getAge();
                Integer o2Age = o2.getAge();
                return o1Age.equals(o2Age) ? 0 : o1Age > o2Age ? 1 : -1;
            }
        });
        return users;
    }
}
