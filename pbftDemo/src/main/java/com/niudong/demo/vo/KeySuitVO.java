package com.niudong.demo.vo;

/**
 * 公私钥对
 *
 * @author 牛冬
 */
public class KeySuitVO {
    // 私钥
    private String privateKey;
    // 公钥
    private String publicKey;

    public KeySuitVO() {

    }

    public KeySuitVO(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

}
