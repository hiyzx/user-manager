package com.zero.base;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

/**
 * @Description: 单元测试基类
 * @author Super.Li
 * @date Apr 27, 2017
 */
@ContextConfiguration(locations = { "classpath*:spring/*.xml" })
public abstract class BaseTest {
    protected final String SRC_ASCIIDOC = "src/docs/asciidoc";
    protected final String TARGET_ASCIIDOC = "target/asciidoc";
    protected final String TARGET_ASCIIDOC_GENERATED = TARGET_ASCIIDOC + "/generated";
    @Resource
    protected WebApplicationContext wac;
}
