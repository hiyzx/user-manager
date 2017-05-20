package com.zero.mail.controller;

import com.zero.mail.service.ActiveMqService;
import com.zero.vo.BaseReturnVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/10
 */
@Controller
public class MailController {

    @Resource
    private ActiveMqService activeMqService;

    @ResponseBody
    @RequestMapping(value = "/sendMail.json", method = RequestMethod.POST)
    @ApiOperation("发送邮件")
    public BaseReturnVo sendMail(@RequestParam String receiver, @RequestParam String title,
            @RequestParam String content) {
        activeMqService.sendToMQ(receiver, title, content);
        return BaseReturnVo.success();
    }
}
