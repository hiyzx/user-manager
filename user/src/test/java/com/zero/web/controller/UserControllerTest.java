package com.zero.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zero.po.User;
import com.zero.user.vo.dto.UserDto;
import com.zero.user.service.UserService;
import com.zero.user.util.SessionHelper;
import com.zero.util.JsonHelper;
import com.zero.vo.ReturnVo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/*.xml" })
@Transactional
@WebAppConfiguration
public class UserControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(UserControllerTest.class);
    @Resource
    protected WebApplicationContext wac;
    protected MockMvc mockMvc;
    private static final String NAME = "zero";
    private static final String PHONE = "188";
    private String SESSION_ID;
    private Integer userId;
    @Resource
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        UserDto tmp = new UserDto();
        tmp.setPhone(PHONE);
        tmp.setName(NAME);
        tmp.setPassword("abc");
        tmp.setAge(12);
        userId = userService.add(tmp);
        SESSION_ID = SessionHelper.createSessionId(userId);
    }

    @Test
    public void testGetUserInfo() throws Exception {
        String responseString = mockMvc
                .perform(get("/getUserInfo.json").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("sessionId", SESSION_ID).param("userId", String.valueOf(userId)))
                .andExpect(status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString();
        final TypeReference<ReturnVo<User>> REFERENCE = new TypeReference<ReturnVo<User>>() {
        };
        ReturnVo<User> userReturnVo = JsonHelper.readValue(responseString, REFERENCE);
        Assert.assertEquals(userReturnVo.getData().getName(), NAME);
        Assert.assertEquals(userReturnVo.getData().getId(), userId);
    }
}