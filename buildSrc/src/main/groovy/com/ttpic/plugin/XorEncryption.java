package com.ttpic.plugin;

import java.nio.charset.Charset;

/**
 * Xor加解密，原理参考：http://www.ruanyifeng.com/blog/2017/05/xor.html
 *
 * Created by panda on 2018/12/25
 **/
public class XorEncryption implements IEncryption {
    private static final Charset charSet = Charset.forName("utf-8");

    @Override
    public String encrypt(String data, String key) {
        return xor(data, key);
    }

    @Override
    public String decrypt(String data, byte[] keyBytes) {
        return xor(data, keyBytes);
    }

    private static String xor(String data, byte[] keyBytes) {
        byte[] inputBytes = data.getBytes();
        for (int i = 0; i < inputBytes.length; i++) {
            inputBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
        }
        return new String(inputBytes, charSet);
    }

    private static String xor(String data, String key) {
        return xor(data, key.getBytes(charSet));
    }
}

