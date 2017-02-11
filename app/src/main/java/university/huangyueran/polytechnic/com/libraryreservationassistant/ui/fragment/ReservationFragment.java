package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.adapter.MainPagerAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

/**
 * 座位预约Fragment
 * Created by huangyueran on 2017/1/29.
 */
public class ReservationFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = UIUtils.inflate(R.layout.layout_fragment_yuyue);
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.vp_main);
        viewPager.setAdapter(new MainPagerAdapter(getChildFragmentManager()));

        return view;
    }
}
