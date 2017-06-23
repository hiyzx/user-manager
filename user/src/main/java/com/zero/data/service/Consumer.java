package com.zero.data.service;

import com.alibaba.dubbo.rpc.service.EchoService;
import com.zero.data.po.CityInfo;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class Consumer {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "spring/applicationContext-dubbo.xml" });
        context.start();

        // 回升测试===可以用来检查服务是否正常
        final String OK = "OK";
        EchoService echoService = (EchoService) context.getBean("cityInfoService");
        String status = (String) echoService.$echo(OK);
        assert (status.equals(OK));

        ICityInfoService cityInfoService = (ICityInfoService) context.getBean("cityInfoService"); // 获取远程服务代理
        List<CityInfo> cityInfos = cityInfoService.listByParentId(100000);
        for (CityInfo cityInfo : cityInfos) {
            System.out.println(cityInfo.getName());
        }
    }

}