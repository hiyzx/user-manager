package com.zero.exception;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/4/29
 */
public class BaseException extends Exception {

    private com.zero.enums.CodeEnum codeEnum;

    private String msg;

    public BaseException(com.zero.enums.CodeEnum codeEnum, String msg) {
        super(msg);
        this.codeEnum = codeEnum;
        this.msg = msg;
    }

    public com.zero.enums.CodeEnum getCodeEnum() {
        return codeEnum;
    }

    public void setCodeEnum(com.zero.enums.CodeEnum codeEnum) {
        this.codeEnum = codeEnum;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
