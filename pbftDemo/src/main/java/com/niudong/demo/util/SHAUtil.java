package com.niudong.demo.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

import cn.hutool.crypto.digest.DigestUtil;

public class SHAUtil {

    /***
     * 利用Apache commons-codec的工具类实现SHA-256加密
     *
     * @param originalStr 加密前的报文
     * @return String 加密后的报文
     */
    public static String getSHA256BasedMD(String originalStr) {
        MessageDigest messageDigest;
        String encdeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(originalStr.getBytes("UTF-8"));
            encdeStr = Hex.encodeHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encdeStr;
    }

    /***
     * 利用Hutool的工具类实现SHA-256加密
     *
     * @param originalStr 加密前的报文
     * @return String 加密后的报文
     */
    public static String sha256BasedHutool(String originalStr) {
        return DigestUtil.sha256Hex(originalStr);
    }

}
