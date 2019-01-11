package giant.test;

import java.nio.charset.Charset;

/**
 * Xor加解密，原理参考：http://www.ruanyifeng.com/blog/2017/05/xor.html
 *
 * Created by panda on 2018/12/25
 **/
public class XorEncryption {

    public static String encrypt(String data, String key) {
        return xor(data, key);
    }


    public static String decrypt(String data, String key) {
        return xor(data, key);
    }

    private static String xor(String data, String key) {
        Charset charSet = Charset.forName("utf-8");
        byte[] inputBytes = data.getBytes(charSet);
        byte[] keyBytes = key.getBytes(charSet);
        for (int i = 0; i < inputBytes.length; i++) {
            inputBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
        }
        return new String(inputBytes, charSet);
    }
}

