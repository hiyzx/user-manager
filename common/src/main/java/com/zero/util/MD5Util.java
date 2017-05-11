package com.zero.util;

import java.security.MessageDigest;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/11
 */
public final class MD5Util {

    public static String getMd5String(String plainText) {
        String result = "";

        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            e.update(plainText.getBytes("UTF-8"));
            byte[] b = e.digest();
            StringBuffer buf = new StringBuffer("");

            for (int offset = 0; offset < b.length; ++offset) {
                int i = b[offset];
                if (i < 0) {
                    i += 256;
                }

                if (i < 16) {
                    buf.append("0");
                }

                buf.append(Integer.toHexString(i));
            }

            result = buf.toString();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return result;
    }
}
