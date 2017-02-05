package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import me.majiajie.pagerbottomtabstrip.Controller;
import me.majiajie.pagerbottomtabstrip.PagerBottomTabLayout;
import me.majiajie.pagerbottomtabstrip.TabItemBuilder;
import me.majiajie.pagerbottomtabstrip.TabLayoutMode;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectListener;
import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbLibrary;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbUser;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbUserInfo;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment.FragmentFactory;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.CacheUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.StringUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.TimestampTypeAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    // =========== 底部导航
    //    int[] testColors = {0xFF7BA3A8, 0xFFF4F3DE, 0xFFBEAD92, 0xFFF35A4A, 0xFF5B4947};
    int[] testColors = {0xFF00796B, 0xFF8D6E63, 0xFF2196F3, 0xFF607D8B, 0xFFF57C00};

    int mCurrentPosition = 0; // 当前指示器坐标

    Map<Integer, Integer> mFragmentMap = new HashMap<Integer, Integer>();

    Controller controller;

    //  ========== 侧滑菜单
    private static final long RIPPLE_DURATION = 250;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.root)
    FrameLayout root;
    @BindView(R.id.content_hamburger)
    View contentHamburger;
    private List<TbLibrary> libraries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 添加权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<String>();

            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.CHANGE_WIFI_STATE);
            permissions.add(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 1);
            }
        }

        loadData(); // 加载应用初始化必备数据 避免加载UI卡住

        // TODO === 设置登录用户的静态数据 后期改为登录获取 ===
        initUser();

        //=== 设置菜单 ===
        ButterKnife.bind(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

        // === 底部导航条 ===
        //这里这样使用Fragment仅用于测试，请勿模仿！
        initFragment();

        bottomTabTest();
    }

    /**
     * 加载主界面需要的数据
     */
    private void loadData() {
        final String url = GlobalValue.BASE_URL + "/library/list";
        // 学校id
        // TODO 这个需要动态传入
        final int schoole_id = 1;
        String cache = CacheUtils.getCacheNotiming("library-list" + schoole_id);
        if (StringUtils.isEmpty(cache)) { //如果缓存没有 去网络加载 否则不加载

            RequestParams params = new RequestParams();
            params.addQueryStringParameter("id", String.valueOf(schoole_id));
            params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

            HttpUtils http = new HttpUtils();
            http.send(HttpRequest.HttpMethod.GET,
                    url, params,
                    new RequestCallBack<String>() {

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            // 得到图书馆list集合
                            Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                            libraries = gson.fromJson(responseInfo.result, new TypeToken<List<TbLibrary>>() {
                            }.getType());
                            CacheUtils.setCacheNotiming("library-list" + schoole_id, responseInfo.result);
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Toast.makeText(UIUtils.getContext(), "数据加载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * 假装初始化用户静态数据
     */
    private void initUser() {
        TbUser user = new TbUser();
        // user.setPassword("666666");
        // 防止密码泄露 设为null
        user.setPassword(null);
        user.setBirthday(new Date());
        user.setClasses("16计算机科学与技术");
        user.setCollegeId("1");
        user.setCreated(new Date());
        user.setEmail("492736173@qq.com");
        user.setIsban(0);
        user.setLastLoginIp("192.168.68.127");
        user.setNickname("卓依婷");
        user.setPhone("19256462354");
        user.setReputation((long) 100);
        user.setUpdated(new Date());
        user.setSex("女");
        user.setUserId((long) 1);
        user.setUsername("huangyueranbbc");
        user.setUserPic(null);
        user.setIsban(0);
        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        String userInfo = gson.toJson(user); // 将用户登录信息存入缓存
        CacheUtils.setCacheNotiming(GlobalValue.LOGININFO, userInfo);

        TbUserInfo tbUserInfo = new TbUserInfo();
        tbUserInfo.setUserId(user.getUserId());
        tbUserInfo.setToken(user.getUsername()); // 推送消息别名
        String tbUserInfoJson = gson.toJson(tbUserInfo); // 将用户登录信息存入缓存
        CacheUtils.setCacheNotiming(GlobalValue.TBUSERINFO, tbUserInfoJson);

        setTag(tbUserInfo.getToken()); // 设置别名 根据每个用户登录的信息来独立设置
    }

    private void initFragment() {
        // 创建5个Fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // transaction.setCustomAnimations(R.anim.push_up_in,R.anim.push_up_out);
        transaction.add(R.id.frameLayout, FragmentFactory.createFragment(0));
        mFragmentMap.put(0, 0);
        // TODO 提前初始化 避免卡住 BUG 无解
        transaction.add(R.id.frameLayout, FragmentFactory.createFragment(1));
        mFragmentMap.put(1, 1);
        transaction.hide(FragmentFactory.createFragment(1));

        transaction.commit();
    }

    private void bottomTabTest() {
        PagerBottomTabLayout pagerBottomTabLayout = (PagerBottomTabLayout) findViewById(R.id.tab);

        //用TabItemBuilder构建一个导航按钮
        TabItemBuilder tabItemBuilder = new TabItemBuilder(this).create()
                .setDefaultIcon(android.R.drawable.ic_menu_call)
                .setText("空白空白")
                .setSelectedColor(testColors[0])
                .setTag("A")
                .build();

        //构建导航栏,得到Controller进行后续控制
        controller = pagerBottomTabLayout.builder()
                .addTabItem(tabItemBuilder)
                .addTabItem(android.R.drawable.ic_menu_agenda, "座位预约", testColors[1])
                .addTabItem(android.R.drawable.sym_action_chat, "联系人", testColors[2])
                .addTabItem(android.R.drawable.star_big_on, "兴趣圈", testColors[3])
                .addTabItem(android.R.drawable.ic_menu_share, "分享", testColors[4])
//                .setMode(TabLayoutMode.HIDE_TEXT)
//                .setMode(TabLayoutMode.CHANGE_BACKGROUND_COLOR)
                .setMode(TabLayoutMode.HIDE_TEXT | TabLayoutMode.CHANGE_BACKGROUND_COLOR)
                .build();

//        controller.setMessageNumber("A",2);
//        controller.setDisplayOval(0,true);

        controller.addTabItemClickListener(listener);
    }


    OnTabItemSelectListener listener = new OnTabItemSelectListener() {
        @Override
        public void onSelected(int index, Object tag) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // 进行创建
            if (mFragmentMap.get(index) == null) {
                // 进行添加
                transaction.add(R.id.frameLayout, FragmentFactory.createFragment(index));
                mFragmentMap.put(index, index);
            }
            Log.i("asd", "onSelected:" + index + "   TAG: " + tag.toString());
//            transaction.replace(R.id.frameLayout, FragmentFactory.createFragment(index));
//            transaction.commit();
            transaction.hide(FragmentFactory.createFragment(mCurrentPosition));
            transaction.show(FragmentFactory.createFragment(index));
            mCurrentPosition = index;
            transaction.commit();
        }

        @Override
        public void onRepeatClick(int index, Object tag) {
            Log.i("asd", "onRepeatClick:" + index + "   TAG: " + tag.toString());
        }
    };

    private void setTag(String tag) {
        // 检查 tag 的有效性
        if (TextUtils.isEmpty(tag)) {
            // 为空处理
            Toast.makeText(MainActivity.this, "Jpush Tags 初始化失败 请稍后重试...", Toast.LENGTH_SHORT).show();
            return;
        }

        // ","隔开的多个 转换成 Set
        String[] sArray = tag.split(",");
        Set<String> tagSet = new LinkedHashSet<String>();
        for (String sTagItme : sArray) {
            tagSet.add(sTagItme);
            Log.i(TAG, "setTag: " + sTagItme);
        }

        //调用JPush API设置Tag
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, tagSet));

    }

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    /**
     * 设置极光推送tags的handler
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {
                            Log.i(TAG, "Tag 设置状态: " + i);
                        }
                    });
                    break;

                case MSG_SET_TAGS:
                    Log.d(TAG, "Set tags in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {
                            Log.i(TAG, "别名 设置状态: " + i);
                        }
                    });
                    break;

                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };

}
