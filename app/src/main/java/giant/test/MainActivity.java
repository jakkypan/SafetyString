package giant.test;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    public static final String TWO = "love youiiiii2121!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Field[] fields = BuildConfig.class.getDeclaredFields();
        for(Field field : fields ) {
            try {
                Log.e("111", field.get(null).toString());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int i = 10;
            }
        }, 2000);

        HandlerThread hd = new HandlerThread("_test_name_");hd.start();
        new Handler(hd.getLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Log.e("111", "======" + BuildConfig.ONE);
    }
}
