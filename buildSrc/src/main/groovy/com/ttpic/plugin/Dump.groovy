package com.ttpic.plugin

import com.android.utils.FileUtils;

/**
 * Created by panda on 2018/12/25
 **/
public abstract class Dump {
    private static final PACKAGE = "/com/ttpic/plugin/"

    /**
     * dump出字节码code
     *
     * @param dir
     */
    public abstract void dump(String dir)

    /**
     * 将字节码写入到class文件中
     */
    public final void writeCwIntoFile(String dir, String fileName, byte[] code) {
        dir += PACKAGE
        FileUtils.mkdirs(new File(dir))
        FileOutputStream fos = new FileOutputStream(new File(dir+fileName))
        fos.write(code,0, code.length)
        fos.flush()
        fos.close()
    }
}
