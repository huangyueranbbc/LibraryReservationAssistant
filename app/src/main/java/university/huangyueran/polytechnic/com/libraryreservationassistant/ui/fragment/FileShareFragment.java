package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment.download.BaseDownloadFragment;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment.download.DownloadFragmentFactory;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.view.PagerTab;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

public class FileShareFragment extends BaseFragment {

    private PagerTab mPagerTab;

    private ViewPager mViewPager;

    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.layout_main_download, null);

        mPagerTab = (PagerTab) view.findViewById(R.id.pager_tab);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

        MyPagerAdapter myAdapter = new MyPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(myAdapter);

        // 将指针和viewpager绑定
        mPagerTab.setViewPager(mViewPager);

        // 绑定页面切换事件监听
        mPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                BaseDownloadFragment fragment = DownloadFragmentFactory.createFragment(position);
                // 开始加载数据
                fragment.loadData();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private String[] mTabNames; //标题名称的数组

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            mTabNames = UIUtils.getStringArray(R.array.tabs_file_share);
        }

        /**
         * 返回页签标题
         *
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabNames[position];
        }

        /**
         * 返回当前位置的Fragment对象
         *
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            BaseDownloadFragment fragment = DownloadFragmentFactory.createFragment(position);
            return fragment;
        }


        /**
         * 返回Fragment个数
         *
         * @return
         */
        @Override
        public int getCount() {
            return mTabNames.length;
        }
    }
}
