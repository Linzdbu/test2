package com.duansq.demo.pbft.verify;/*
package com.duansq.demo.pbft.crypto;

import com.niudong.demo.util.DeEnCoderCipherUtil;

public class cipherExample {

    public final static String CIPHER_MODE1 = "DES";
    public final static String CIPHER_MODE2 = "CBC";
    public final static String CIPHER_MODE3 = "PDCS5padding";


    */
/*public static String DEFAULT_DES_KEY =
            "这是一个DES的默认密钥";
    public static String DEFAULT_CBC_KEY =
            "这是一个CBC的默认密钥";
    public static String DEFAULT_PDCS_KEY =
            "这是一个PDCS的默认密钥";


    //随机生成密码
    Random r = new Random();
    int n1 = r.nextInt(4);//生成[0,4)之间的随机整数
    String[] cryptRandom = {DEFAULT_DES_KEY,DEFAULT_CBC_KEY,DEFAULT_PDCS_KEY};
    String usedCrypto  = cryptRandom[n1];*//*




    public static void main(String[] args) {

        String testString = "这是一个加密的测试";
        randomCrypto rc = new randomCrypto();
        String usedCrpto = rc.createRandomCrpyto();

        String aftegerCrpto = DeEnCoderCipherUtil.encrypt(testString,usedCrpto);
        System.out.println(aftegerCrpto);
        String deCrpto = DeEnCoderCipherUtil.decrypt(aftegerCrpto,usedCrpto);
        System.out.println(deCrpto);

    }


}
*/
