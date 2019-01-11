package giant.test;

/**
 * Created by panda on 2019/1/8
 **/
public class C {
    static final String s;

    static {
        String e = XorEncryption.encrypt("aaa", "xxx");
        s = XorEncryption.decrypt(e, "xxx");
    }

    public void m() throws Exception {
        Thread.sleep(100);



    }
}
