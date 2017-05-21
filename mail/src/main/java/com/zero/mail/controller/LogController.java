package com.zero.mail.controller;

import com.zero.util.JsonHelper;
import com.zero.vo.BaseReturnVo;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/19
 */
@Controller
public class LogController {

    private static final Logger LOG = LoggerFactory.getLogger(LogController.class);

    @ResponseBody
    @RequestMapping(value = "/requestData.json", method = RequestMethod.POST)
    @ApiOperation("获取请求数据")
    public BaseReturnVo sendMail(@RequestBody Map<String, String> requestData) throws InterruptedException {
	    Thread.sleep(10000);
        LOG.info(JsonHelper.toJSon(requestData));
        return BaseReturnVo.success();
    }
}
