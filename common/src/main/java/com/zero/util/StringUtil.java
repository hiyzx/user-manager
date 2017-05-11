package com.zero.util;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/11
 */
public class StringUtil {

    public static String getSuffix(String originalFileName) {
        final String SEPARATOR = ".";
        String suffix = "";
        if (originalFileName.lastIndexOf(SEPARATOR) > 0) {
            suffix = originalFileName.substring(originalFileName.lastIndexOf(SEPARATOR));
        }
        return suffix;
    }
}
