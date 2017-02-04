package university.huangyueran.polytechnic.com.libraryreservationassistant.global;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Process;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;

import cn.jpush.android.api.JPushInterface;

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

    public static LocationClient mLocClient;
    private static LatLng mUserLocation;

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化百度定位
        SDKInitializer.initialize(this);
        // 初始化 JPush SDK
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        initBaiduSDK(); // 获取图书馆信息

        initTypeface();
        context = getApplicationContext();
        handler = new Handler();
        //进程ID
        myPid = Process.myPid();
        //线程ID 此处是主线程id
        mainThreadId = Process.myTid();

    }

//    private void initLibraryInfo() {
//        // 获取图书馆信息
//        final String url = GlobalValue.BASE_URL + "/library/show";
//        RequestParams params = new RequestParams();
//        params.addQueryStringParameter("id", String.valueOf(readroom.getLibraryId()));
//        params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交
//
//        HttpUtils http = new HttpUtils();
//        http.send(HttpRequest.HttpMethod.GET,
//                url, params,
//                new RequestCallBack<String>() {
//
//                    @Override
//                    public void onSuccess(ResponseInfo<String> responseInfo) {
//                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
//                        library = gson.fromJson(responseInfo.result, new TypeToken<TbLibrary>() {
//                        }.getType());
//                        initBaiduSDK();
//                    }
//
//                    @Override
//                    public void onFailure(HttpException e, String s) {
//                        Toast.makeText(SelectSeatActivity.this, "图书馆数据请求失败", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    /**
     * 初始化百度定位
     */
    private void initBaiduSDK() {
        mLocClient = new LocationClient(this);
        //设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setProdName("LocationDemo"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(1000); // 设置定位时间间隔 1秒
        mLocClient.setLocOption(option);

        //注册位置监听器
        mLocClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                // 用户坐标
                mUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        });

        mLocClient.start(); // 开始定位
        mLocClient.requestLocation();
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

    public static LatLng getmUserLocation() {
        return mUserLocation;
    }

}