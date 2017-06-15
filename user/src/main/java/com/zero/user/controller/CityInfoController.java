package com.zero.user.controller;

import com.zero.data.service.ICityInfoService;
import com.zero.data.po.CityInfo;
import com.zero.vo.ReturnVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/6/15
 */
@Controller
@RequestMapping(value = "/city")
public class CityInfoController {

    @Resource
    private ICityInfoService cityInfoService;

    @ResponseBody
    @RequestMapping(value = "/getByParentId.json", method = RequestMethod.GET)
    @ApiOperation("获取下级所有城市列表")
    public ReturnVo<List<CityInfo>> getByParentId(@RequestParam String sessionId, @RequestParam Integer parentId)
            throws Exception {
        return ReturnVo.success(cityInfoService.listByParentId(parentId));
    }
}
