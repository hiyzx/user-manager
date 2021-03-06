package com.zero.vo;

import com.zero.enums.*;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/4/29
 */
public class BaseReturnVo {

    final static String SUCCESS_DEFAULT_DESC = "success";
    @io.swagger.annotations.ApiModelProperty("返回码")
    private String resCode;
    @io.swagger.annotations.ApiModelProperty("返回说明")
    private String resDes;

    public BaseReturnVo() {
    }

    public BaseReturnVo(CodeEnum codeEnum, String msg) {
        this.resCode = codeEnum.getCodeEnum();
        this.resDes = msg;
    }

    public static BaseReturnVo success() {
        return new BaseReturnVo(CodeEnum.SUCCESS, SUCCESS_DEFAULT_DESC);
    }

    public String getResCode() {
        return resCode;
    }

    public String getResDes() {
        return resDes;
    }

}
