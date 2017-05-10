package com.zero.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zero.service.MailService;
import com.zero.vo.BaseReturnVo;

import io.swagger.annotations.ApiOperation;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/10
 */
@Controller
public class MailController {

    @javax.annotation.Resource
    private MailService mailService;

    @ResponseBody
    @RequestMapping(value = "/sendMail.json", method = RequestMethod.POST)
    @ApiOperation("send")
    public BaseReturnVo sendMail(@RequestParam String receiver, @RequestParam String title,
            @RequestParam String content) {
        mailService.sendMail(receiver, title, content);
        return BaseReturnVo.success();
    }
}
