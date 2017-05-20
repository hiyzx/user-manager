package com.zero.user.controller;

import com.zero.enums.CodeEnum;
import com.zero.exception.BaseException;
import com.zero.po.User;
import com.zero.user.annotation.AopCutAnnotation;
import com.zero.user.vo.dto.UserDto;
import com.zero.user.service.UserService;
import com.zero.user.util.SessionHelper;
import com.zero.util.MediaHelper;
import com.zero.util.StringUtil;
import com.zero.vo.BaseReturnVo;
import com.zero.vo.ReturnVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/4/29
 */
@Controller
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;

    @ResponseBody
    @RequestMapping(value = "/register.json", method = RequestMethod.POST)
    @ApiOperation("注册")
    public ReturnVo<String> register(HttpServletRequest request, @RequestBody UserDto userDto) throws Exception {
        int userId = userService.add(userDto);
        String sessionId = request.getSession().getId();
        SessionHelper.pushUserId(sessionId, userId);
        return ReturnVo.success(sessionId);
    }

    @AopCutAnnotation
    @ResponseBody
    @RequestMapping(value = "/login.json", method = RequestMethod.POST)
    @ApiOperation("登陆")
    public ReturnVo<String> login(HttpServletRequest request, @RequestParam String username,
            @RequestParam String password) throws Exception {
        int userId = userService.login(username, password);
        String sessionId = request.getSession().getId();
        SessionHelper.pushUserId(sessionId, userId);
        return ReturnVo.success(sessionId);
    }

    @ResponseBody
    @RequestMapping(value = "/getUserInfo.json", method = RequestMethod.GET)
    @ApiOperation("获取用户信息")
    public ReturnVo<User> getUserInfo(@RequestParam String sessionId,
            @ApiParam(value = "用户id", required = true) @RequestParam int userId) throws Exception {
        if (SessionHelper.getUserId(sessionId) == userId) {
            User userInfo = userService.getUserInfo(userId);
            return ReturnVo.success(userInfo);
        } else {
            throw new BaseException(CodeEnum.PERMISSION_DENIED, "非法sessionId");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/logout.json", method = RequestMethod.POST)
    @ApiOperation("注销")
    public BaseReturnVo logout(HttpServletRequest request) throws Exception {
        SessionHelper.clearSessionId(request.getSession().getId());
        return BaseReturnVo.success();
    }

    @ResponseBody
    @RequestMapping(value = "/bindEmail.json", method = RequestMethod.GET)
    @ApiOperation("发送邮件-绑定")
    public BaseReturnVo sendMail(@RequestParam String sessionId,
            @ApiParam(value = "用户邮箱", required = true) @RequestParam String email) throws Exception {
        Integer userId = SessionHelper.getUserId(sessionId);
        userService.bindEmail(userId, email);
        return BaseReturnVo.success();

    }

    @ResponseBody
    @RequestMapping(value = "/validateEmail.json", method = RequestMethod.GET)
    @ApiOperation("校验")
    public ReturnVo<String> validateEmail(HttpServletRequest request,
            @ApiParam(value = "key", required = true) @RequestParam String key) throws Exception {
        int userId = userService.updateBindEmail(key);
        String sessionId = request.getSession().getId();
        SessionHelper.pushUserId(sessionId, userId);
        return ReturnVo.success(sessionId);

    }

    @ResponseBody
    @RequestMapping(value = "/uploadHeadImg.json", method = POST)
    @ApiOperation(value = "上传头像")
    public BaseReturnVo uploadMedia(@RequestParam String sessionId,
            @RequestParam(value = "file", required = true) MultipartFile file) throws Exception {
        String originalFileName = file.getOriginalFilename().replaceAll("\\s+", "");
        Integer userId = SessionHelper.getUserId(sessionId);
        String suffix = StringUtil.getSuffix(originalFileName);
        String fileName = String.format("%s/%s-%s%s%s", "headImg", System.currentTimeMillis(), userId,
                UUID.randomUUID().toString().substring(0, 5), suffix);
        File targetFile = new File(MediaHelper.getMEDIA_PATH(), fileName);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        file.transferTo(targetFile);
        LOG.info("teacherId={} upload {} to {}", userId, originalFileName, targetFile.getAbsolutePath());
        userService.updateHeadImg(userId, fileName);
        return BaseReturnVo.success();
    }
}
