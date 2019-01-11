package com.ttpic.plugin;

/**
 * Created by panda on 2018/12/25
 **/
public interface IEncryption {
    /**
     * 加密
     *
     * @param data
     * @param key
     * @return
     */
    String encrypt(String data, String key);

    /**
     * 解密，传入key的byte数组
     *
     * @param data
     * @param key
     * @return
     */
    String decrypt(String data, byte[] key);
}
