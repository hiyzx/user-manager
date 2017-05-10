package com.zero.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
public class VersionController {
    private static final ThreadLocal<java.text.DateFormat> DATE_FORMAT = new ThreadLocal<java.text.DateFormat>() {
        public java.text.DateFormat initialValue() {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        };
    };
    @Value("${project.version}")
    private String version;
    @Value("${project.buildTime}")
    private String builtAt;
    @Value("${project.format}")
    private String format;

    @ResponseBody
    @RequestMapping(value = "/version", method = GET)
    @ApiOperation(value = "查看版本信息")
    public Map<String, String> version() throws java.text.ParseException {
        java.util.Map<String, String> map = new java.util.HashMap<String, String>();
        map.put("version", version);
        map.put("builtAt", DATE_FORMAT.get().format(new java.text.SimpleDateFormat(format).parse(builtAt)));
        return map;
    }
}
