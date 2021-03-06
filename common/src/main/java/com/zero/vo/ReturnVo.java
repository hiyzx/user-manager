package com.zero.vo;

import com.zero.enums.*;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/4/29
 */
public class ReturnVo<T> extends BaseReturnVo {

    @io.swagger.annotations.ApiModelProperty("返回结果")
    private T data;

    public ReturnVo() {
    }

    public ReturnVo(CodeEnum codeEnum, String msg) {
        super(codeEnum, msg);
    }

    public static <T> ReturnVo<T> success(T data) {
        ReturnVo<T> returnVo = new ReturnVo<>(CodeEnum.SUCCESS, SUCCESS_DEFAULT_DESC);
        returnVo.setData(data);
        return returnVo;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
