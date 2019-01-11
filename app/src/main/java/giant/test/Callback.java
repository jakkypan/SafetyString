package giant.test;

import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * Created by panda on 2019/1/7
 **/
public class Callback {
    private WeakReference<T> t;

    public Callback(T t) {
        this.t = new WeakReference<>(t);
    }

    public interface T {
        void print();
    }

    void request() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                t.get().print();
            }
        }, 10000);
    }
}
