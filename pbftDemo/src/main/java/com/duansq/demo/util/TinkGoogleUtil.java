package com.duansq.demo.util;

import com.google.crypto.tink.*;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AeadFactory;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.integration.awskms.AwsKmsClient;
import com.google.crypto.tink.integration.gcpkms.GcpKmsClient;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.signature.PublicKeySignFactory;
import com.google.crypto.tink.signature.PublicKeyVerifyFactory;
import com.google.crypto.tink.signature.SignatureKeyTemplates;

import java.io.File;


/**
 * 基于Tink（由谷歌的一群密码学家和安全工程师编写的密码库。）的加密解密
 *
 * @author 牛冬
 */
public class TinkGoogleUtil {

    // Tink的初始化
    public void register() {
        try {
            AeadConfig.register();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 使用Java中的AEAD原语加密或解密时的代码示例
    public void aead(byte[] plaintext, byte[] aad) {
        try {
            // 1. Generate the key material.
            KeysetHandle keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM);

            // 2. Get the primitive.
            Aead aead = AeadFactory.getPrimitive(keysetHandle);

            // 3. Use the primitive.
            byte[] ciphertext = aead.encrypt(plaintext, aad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 生成新密钥(组)
    public void createKeySet() {
        try {
            KeyTemplate keyTemplate = AeadKeyTemplates.AES128_GCM;
            KeysetHandle keysetHandle = KeysetHandle.generateNew(keyTemplate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 存储秘钥
    public void save2File() {
        try {
            // Generate the key material...
            KeysetHandle keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM);

            // and write it to a file.
            String keysetFilename = "my_keyset.json";
            CleartextKeysetHandle.write(keysetHandle,
                    JsonKeysetWriter.withFile(new File(keysetFilename)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 存储秘钥
    public void save2FileBaseKMS() {
        try {
            // Generate the key material...
            KeysetHandle keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM);

            // and write it to a file...
            String keysetFilename = "my_keyset.json";
            // encrypted with the this key in GCP KMS
            String masterKeyUri =
                    "gcp-kms://projects/tink-examples/locations/global/keyRings/foo/cryptoKeys/bar";
            keysetHandle.write(JsonKeysetWriter.withFile(new File(keysetFilename)),
                    new GcpKmsClient().getAead(masterKeyUri));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 加载秘钥
    public void loadKeySet() {
        try {
            String keysetFilename = "my_keyset.json";
            // The keyset is encrypted with the this key in AWS KMS.
            String masterKeyUri =
                    "aws-kms://arn:aws:kms:us-east-1:007084425826:key/84a65985-f868-4bfc-83c2-366618acf147";
            KeysetHandle keysetHandle =
                    KeysetHandle.read(JsonKeysetReader.withFile(new File(keysetFilename)),
                            new AwsKmsClient().getAead(masterKeyUri));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 加载秘钥
    public void loadCleartextKeySet() {
        try {
            String keysetFilename = "my_keyset.json";
            KeysetHandle keysetHandle =
                    CleartextKeysetHandle.read(JsonKeysetReader.withFile(new File(keysetFilename)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 对称密钥加密
    public void aeadAES(byte[] plaintext, byte[] aad) {
        try {
            // 1. Generate the key material.
            KeysetHandle keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM);

            // 2. Get the primitive.
            Aead aead = AeadFactory.getPrimitive(keysetHandle);

            // 3. Use the primitive to encrypt a plaintext,
            byte[] ciphertext = aead.encrypt(plaintext, aad);

            // ... or to decrypt a ciphertext.
            byte[] decrypted = aead.decrypt(ciphertext, aad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 数字签名
    public void signatures(byte[] data) {
        try {
            // 1. Generate the private key material.
            KeysetHandle privateKeysetHandle = KeysetHandle.generateNew(SignatureKeyTemplates.ECDSA_P256);

            // 2. Get the primitive.
            PublicKeySign signer = PublicKeySignFactory.getPrimitive(privateKeysetHandle);

            // 3. Use the primitive to sign.
            byte[] signature = signer.sign(data);

            // VERIFYING

            // 1. Obtain a handle for the public key material.
            KeysetHandle publicKeysetHandle = privateKeysetHandle.getPublicKeysetHandle();

            // 2. Get the primitive.
            PublicKeyVerify verifier = PublicKeyVerifyFactory.getPrimitive(publicKeysetHandle);

            // 4. Use the primitive to verify.
            verifier.verify(signature, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
