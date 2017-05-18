package com.zero.user.controller;

import com.zero.user.service.HealthCheckService;
import com.zero.user.util.HttpClient;
import com.zero.util.RedisHelper;
import com.zero.vo.HealthCheckVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class HealthCheckController {
    private static final ThreadLocal<java.text.DateFormat> DATE_FORMAT = new ThreadLocal<java.text.DateFormat>() {
        public java.text.DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        };
    };
    @Value("${project.version}")
    private String version;
    @Value("${project.buildTime}")
    private String builtAt;
    @Value("${project.format}")
    private String format;

    @Resource
    private HttpClient mailHttpClient;
    @Resource
    private HealthCheckService healthCheckService;

    @ResponseBody
    @RequestMapping(value = "/version", method = GET)
    @ApiOperation(value = "查看版本信息")
    public Map<String, String> version() throws ParseException {
        Map<String, String> map = new HashMap<>();
        map.put("version", version);
        map.put("builtAt", DATE_FORMAT.get().format(new SimpleDateFormat(format).parse(builtAt)));
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/healthCheck", method = GET)
    public List<HealthCheckVo> healthCheck() throws ParseException {
        List<HealthCheckVo> healthCheckVos = new ArrayList<>();
        healthCheckVos.add(mailHttpClient.healthCheck());
        healthCheckVos.add(RedisHelper.checkRedisConnection());
        healthCheckVos.add(healthCheckService.checkDBConnection());
        return healthCheckVos;
    }

}
