package com.duansq.demo.util;


import org.testng.util.Strings;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * 基于Cipher实现的加密解密工具类
 *
 * @author 牛冬
 */
public class DeEnCoderCipherUtil {
    // 加密解密模式
    private final static String CIPHER_MODE = "DES";
    private final static String CIPHER_MODE2 = "CBC";
    private final static String CIPHER_MODE3 = "PDCS5padding";

    // DES密钥
    public static String DEFAULT_DES_KEY =
            "好未来是一个以智慧教育和开放平台为主体，以素质教育和学科培训为载体，在全球范围内服务公办教育，助力民办教育，探索未来教育新模式的科技教育型公司。";

    /**
     * function 加密通用方法
     *
     * @param originalContent:明文
     * @param key                加密秘钥
     * @return 密文
     */
    public static String encrypt(String originalContent, String key) {
        // 明文或加密秘钥为空时
        if (Strings.isNullOrEmpty(originalContent) || Strings.isNullOrEmpty(key)) {
            return null;
        }

        // 明文或加密秘钥不为空时
        try {
            byte[] byteContent = encrypt(originalContent.getBytes(), key.getBytes());
            return new BASE64Encoder().encode(byteContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * function 解密通用方法
     *
     * @param ciphertext 密文
     * @param key        DES解密秘钥（同加密秘钥）
     * @return 明文
     */
    public static String decrypt(String ciphertext, String key) {
        // 密文或加密秘钥为空时
        if (Strings.isNullOrEmpty(ciphertext) || Strings.isNullOrEmpty(key)) {
            return null;
        }

        // 密文或加密秘钥不为空时
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bufCiphertext = decoder.decodeBuffer(ciphertext);
            byte[] contentByte = decrypt(bufCiphertext, key.getBytes());
            return new String(contentByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * function 字节加密方法
     *
     * @param originalContent：明文
     * @param key                加密秘钥的byte数组
     * @return 密文的byte数组
     */
    private static byte[] encrypt(byte[] originalContent, byte[] key) throws Exception {
        // 步骤1：生成可信任的随机数源
        SecureRandom secureRandom = new SecureRandom();

        // 步骤2：基于密钥数据创建DESKeySpec对象
        DESKeySpec desKeySpec = new DESKeySpec(key);

        // 步骤3：创建密钥工厂，将DESKeySpec转换成SecretKey对象来保存对称密钥
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(CIPHER_MODE);
        SecretKey securekey = keyFactory.generateSecret(desKeySpec);

        // 步骤4：Cipher对象实际完成加密操作,指定其支持指定的加密解密算法
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);

        // 步骤5：用密钥初始化Cipher对象,ENCRYPT_MODE表示加密模式
        cipher.init(Cipher.ENCRYPT_MODE, securekey, secureRandom);

        // 返回密文
        return cipher.doFinal(originalContent);
    }

    /**
     * function 字节解密方法
     *
     * @param ciphertextByte:字节密文
     * @param key                 解密秘钥（同加密秘钥）byte数组
     * @return 明文byte数组
     */
    private static byte[] decrypt(byte[] ciphertextByte, byte[] key) throws Exception {
        // 步骤1：生成可信任的随机数源
        SecureRandom secureRandom = new SecureRandom();

        // 步骤2：从原始密钥数据创建DESKeySpec对象
        DESKeySpec desKeySpec = new DESKeySpec(key);

        // 步骤3：创建密钥工厂，将DESKeySpec转换成SecretKey对象来保存对称密钥
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(CIPHER_MODE);
        SecretKey securekey = keyFactory.generateSecret(desKeySpec);

        // 步骤4：Cipher对象实际完成解密操作,指定其支持响应的加密解密算法
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);

        // 步骤5：用密钥初始化Cipher对象，DECRYPT_MODE表示解密模式
        cipher.init(Cipher.DECRYPT_MODE, securekey, secureRandom);

        // 返回明文
        return cipher.doFinal(ciphertextByte);
    }


}
