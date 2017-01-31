package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.yalantis.guillotine.animation.GuillotineAnimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.majiajie.pagerbottomtabstrip.Controller;
import me.majiajie.pagerbottomtabstrip.PagerBottomTabLayout;
import me.majiajie.pagerbottomtabstrip.TabItemBuilder;
import me.majiajie.pagerbottomtabstrip.TabLayoutMode;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectListener;
import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment.FragmentFactory;

public class MainActivity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 添加权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<String>();

            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 1);
            }
        }
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

    private void initFragment() {
        // 创建5个Fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // transaction.setCustomAnimations(R.anim.push_up_in,R.anim.push_up_out);
        transaction.add(R.id.frameLayout, FragmentFactory.createFragment(0));
        mFragmentMap.put(0, 0);
        transaction.commit();
    }

    private void bottomTabTest() {
        PagerBottomTabLayout pagerBottomTabLayout = (PagerBottomTabLayout) findViewById(R.id.tab);

        //用TabItemBuilder构建一个导航按钮
        TabItemBuilder tabItemBuilder = new TabItemBuilder(this).create()
                .setDefaultIcon(android.R.drawable.ic_menu_call)
                .setText("座位预约")
                .setSelectedColor(testColors[0])
                .setTag("A")
                .build();

        //构建导航栏,得到Controller进行后续控制
        controller = pagerBottomTabLayout.builder()
                .addTabItem(tabItemBuilder)
                .addTabItem(android.R.drawable.ic_menu_agenda, "预约管理", testColors[1])
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

}
