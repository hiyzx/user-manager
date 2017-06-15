package com.zero.data.service;

import com.zero.data.po.CityInfo;

import java.util.List;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/6/15
 */
public interface ICityInfoService {

    void save(CityInfo cityInfo);

    List<CityInfo> listByParentId(Integer parentId);
}
