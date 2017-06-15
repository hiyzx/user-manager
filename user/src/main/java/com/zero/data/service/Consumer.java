package com.zero.data.service;

import com.zero.data.po.CityInfo;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class Consumer {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "spring/applicationContext-dubbo.xml" });
        context.start();

        ICityInfoService cityInfoService = (ICityInfoService) context.getBean("cityInfoService"); // 获取远程服务代理
        List<CityInfo> cityInfos = cityInfoService.listByParentId(100000);
        for (CityInfo cityInfo : cityInfos) {
            System.out.println(cityInfo.getName());
        }
    }

}