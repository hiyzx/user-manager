package com.zero.util;

import com.zero.enums.CodeEnum;
import com.zero.exception.BaseException;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/11
 */
public class StringHelper {

    public static String getSuffix(String originalFileName) {
        final String SEPARATOR = ".";
        String suffix = "";
        if (originalFileName.lastIndexOf(SEPARATOR) > 0) {
            suffix = originalFileName.substring(originalFileName.lastIndexOf(SEPARATOR));
        }
        return suffix;
    }

    public static void checkVerifyCode(String redisKey, String inputCode) throws BaseException {
        if (redisKey == null) {
            throw new BaseException(CodeEnum.VERIFY_CODE_EXPIRE, "验证码已经失效");
        } else {
            if (!redisKey.equals(inputCode)) {
                throw new BaseException(CodeEnum.VERIFY_CODE_WRONG, "验证码错误");
            }
        }
    }
}
