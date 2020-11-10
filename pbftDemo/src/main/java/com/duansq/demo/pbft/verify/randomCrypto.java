package com.duansq.demo.pbft.verify;

import java.security.SecureRandom;

public class randomCrypto {

    public static String DEFAULT_DES_KEY =
            "这是一个DES的默认密钥";
    public static String DEFAULT_CBC_KEY =
            "这是一个CBC的默认密钥";
    public static String DEFAULT_PDCS_KEY =
            "这是一个PDCS的默认密钥";
    //茫茫多的冗余密钥，动态冗余架构
    //随机选取某密钥


    public String createRandomCrpyto() {
        String res = "";
        SecureRandom random = new SecureRandom();
        int n1 = random.nextInt(3);
        String[] randomCrpyto = {DEFAULT_CBC_KEY, DEFAULT_DES_KEY, DEFAULT_PDCS_KEY};
        String usedCrpyto = randomCrpyto[n1];
        return usedCrpyto;
    }


}
