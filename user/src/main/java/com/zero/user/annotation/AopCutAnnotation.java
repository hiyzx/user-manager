package com.zero.user.annotation;

import java.lang.annotation.*;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/19
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AopCutAnnotation {

	String appName() default "user";
}
