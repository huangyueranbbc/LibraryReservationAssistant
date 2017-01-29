package university.huangyueran.polytechnic.com.libraryreservationassistant.global;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Process;

import com.socks.okhttp.plus.OkHttpProxy;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import okhttp3.OkHttpClient;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.test.MyHostnameVerifier;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.test.MyTrustManager;

/**
 * Created by huangyueran on 2017/1/13.
 * 自定义Appliaction 进行全局初始化
 */
public class MainApplication extends Application {
    // === 顶部菜单 ===
    private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf";
    public static Typeface canaroExtraBold;

    // 定义全局公用变量
    private static Context context;
    private static Handler handler;
    private static int mainThreadId;
    private static int myPid;

    @Override
    public void onCreate() {
        super.onCreate();
        initTypeface();
        context = getApplicationContext();
        handler = new Handler();
        //进程ID
        myPid = Process.myPid();
        //线程ID 此处是主线程id
        mainThreadId = Process.myTid();

        //====  okhttp test
        OkHttpClient.Builder builder = OkHttpProxy.getInstance().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS);

        //ignore HTTPS Authentication
        builder.hostnameVerifier(new MyHostnameVerifier());
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            builder.sslSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        OkHttpProxy.setInstance(builder.build());

    }

    private void initTypeface() {
        canaroExtraBold = Typeface.createFromAsset(getAssets(), CANARO_EXTRA_BOLD_PATH);

    }

    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }

    public static int getMyPid() {
        return myPid;
    }
}
