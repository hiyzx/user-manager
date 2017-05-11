package com.zero.user.controller;

import com.zero.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/11
 */
@Controller
public class ExcelController {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelController.class);

    @Resource
    private UserService userService;

    @RequestMapping(value = "/exportExcel.json", method = RequestMethod.GET)
    @ApiOperation("导出excel")
    public void exportExcel(HttpServletResponse response) throws Exception {
        XSSFWorkbook wb = userService.generateExcel();
        LOG.info("export excel success");
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", String.format("attachment; filename=%s.xlsx", "user"));
        OutputStream os = response.getOutputStream();
        wb.write(os);
        os.flush();
        os.close();
    }
}
