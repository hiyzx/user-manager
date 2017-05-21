package com.zero.user.service;

import com.zero.enums.CodeEnum;
import com.zero.exception.BaseException;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/21
 */
@Service
public class VerifyCodeService {

    public void checkVerifyCode(Object redisKey, String inputCode) throws BaseException {
        if (redisKey == null) {
            throw new BaseException(CodeEnum.VERIFY_CODE_EXPIRE, "验证码已经失效");
        } else {
            String redisCode = redisKey.toString();
            if (!redisCode.equals(inputCode)) {
                throw new BaseException(CodeEnum.VERIFY_CODE_WRONG, "验证码错误");
            }
        }
    }
}
