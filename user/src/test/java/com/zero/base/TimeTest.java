package com.zero.base;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/11
 */
public class TimeTest {

    private static final int EXPIRE_TIME = ((Long) TimeUnit.DAYS.toSeconds(1)).intValue();

    @Test
    public void testTime() {
        System.out.println(EXPIRE_TIME);
    }
}
