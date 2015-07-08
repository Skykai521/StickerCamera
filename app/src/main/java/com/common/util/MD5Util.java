
package com.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    /**
     * MD5加密
     * 
     * @param val
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5(String val) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(val.getBytes());
            byte[] m = md5.digest();// 加密
            return getString(m);
        } catch (NoSuchAlgorithmException e) {
            return val;
        }

    }

    private static String getString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            sb.append(b[i]);
        }
        return sb.toString();
    }

}
