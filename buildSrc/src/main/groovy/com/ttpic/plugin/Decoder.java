package com.ttpic.plugin;

/**
 * Created by panda on 2019/1/9
 **/
public class Decoder {
    private static XorEncryption IMPL = new XorEncryption();

    public static String decrypt(String data) {
        return IMPL.decrypt(data, new byte[]{});
    }
}
