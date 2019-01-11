package com.ttpic.plugin

/**
 * Created by panda on 2018/12/24
 **/
public class TtpicAppConfigExtension {
    /**
     * 敏感数据的key，目前仅支持对称加解密
     */
    String key = ""

    /**
     * 敏感数据，格式为：[name1,value1,name2,value2,...]
     */
    String[] sensitives = []

    /**
     * 加解密的实现类，为了可扩展，这里可以传入实现类
     */
    String implementation
}
