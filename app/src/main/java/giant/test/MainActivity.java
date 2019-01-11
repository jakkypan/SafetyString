package giant.test;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    public static final String ONE = "love you!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Field[] fields = BuildConfig.class.getDeclaredFields();
        for(Field field : fields ) {
            try {
                Log.e("111", field.get(null).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

//        Log.e("111", "======" + BuildConfig.ONE);


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "giant.test",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String s = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("111", s);
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
