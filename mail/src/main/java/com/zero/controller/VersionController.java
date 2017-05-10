package com.zero.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class VersionController {
    private static final ThreadLocal<java.text.DateFormat> DATE_FORMAT = new ThreadLocal<java.text.DateFormat>() {
        public java.text.DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        };
    };
    @Value("${project.version}")
    private String version;
    @Value("${project.buildTime}")
    private String builtAt;
    @org.springframework.beans.factory.annotation.Value("${project.format}")
    private String format;

    @ResponseBody
    @RequestMapping(value = "/version", method = GET)
    @ApiOperation(value = "查看版本信息")
    public Map<String, String> version() throws ParseException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("version", version);
        map.put("builtAt", builtAt);
        return map;
    }
}
