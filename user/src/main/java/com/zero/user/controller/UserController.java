package com.zero.user.controller;

import com.zero.enums.CodeEnum;
import com.zero.exception.BaseException;
import com.zero.po.User;
import com.zero.user.service.UserService;
import com.zero.user.dto.UserDto;
import com.zero.util.SessionHelper;
import com.zero.vo.BaseReturnVo;
import com.zero.vo.ReturnVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/4/29
 */
@Controller
public class UserController {

    @Resource
    private UserService userService;

    @ResponseBody
    @RequestMapping(value = "/register.json", method = RequestMethod.POST)
    @ApiOperation("注册")
    private ReturnVo<String> register(HttpServletRequest request, @RequestBody UserDto userDto) throws Exception {
        int userId = userService.add(userDto);
        String sessionId = request.getSession().getId();
        SessionHelper.pushUserId(sessionId, userId);
        return ReturnVo.success(sessionId);
    }

    @ResponseBody
    @RequestMapping(value = "/login.json", method = RequestMethod.POST)
    @ApiOperation("登陆")
    private ReturnVo<String> login(HttpServletRequest request, @RequestParam String username,
            @RequestParam String password) throws Exception {
        int userId = userService.login(username, password);
        String sessionId = request.getSession().getId();
        SessionHelper.pushUserId(sessionId, userId);
        return ReturnVo.success(sessionId);
    }

    @ResponseBody
    @RequestMapping(value = "/getUserInfo.json", method = RequestMethod.GET)
    @ApiOperation("获取用户信息")
    private ReturnVo<User> getUserInfo(@RequestParam String sessionId,
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
    private BaseReturnVo logout(HttpServletRequest request) throws Exception {
        SessionHelper.clearSessionId(request.getSession().getId());
        return BaseReturnVo.success();
    }

}