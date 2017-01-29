package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.infinitecycleviewpager.VerticalInfiniteCycleViewPager;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.Utils;

/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class HorizontalPagerAdapter extends PagerAdapter {
    private static final String TAG = "HorizontalPagerAdapter";

    private final Utils.LibraryObject[] LIBRARIES = new Utils.LibraryObject[]{
            new Utils.LibraryObject(
                    R.drawable.ic_strategy,
                    "Strategy"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_design,
                    "Design"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_development,
                    "Development"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_qa,
                    "Quality Assurance"
            )
    };

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private boolean mIsTwoWay;

    public HorizontalPagerAdapter(final Context context, final boolean isTwoWay) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mIsTwoWay = isTwoWay;
    }

    @Override
    public int getCount() {
        return mIsTwoWay ? 3 : LIBRARIES.length; // 返回三个垂直viewpager
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        Log.i(TAG, "instantiateItem: " + position);
        final View view;
        if (mIsTwoWay) {
            view = mLayoutInflater.inflate(R.layout.two_way_item, container, false);
            final VerticalInfiniteCycleViewPager verticalInfiniteCycleViewPager =
                    (VerticalInfiniteCycleViewPager) view.findViewById(R.id.vicvp);
            // 进行三个viewpager的判断
            switch (position) {

                case 0: //选择图书馆
                    verticalInfiniteCycleViewPager.setAdapter(new SelectReadRoomVerticalPagerAdapter(mContext));
                    break;
                case 1: //预约管理
                    verticalInfiniteCycleViewPager.setAdapter(new YYManagerVerticalPagerAdapter(mContext));
                    break;
                case 2: //馆内详情
                    verticalInfiniteCycleViewPager.setAdapter(new LibraryInfoVerticalPagerAdapter(mContext));
                    break;
                default:
                    break;
            }

            verticalInfiniteCycleViewPager.setCurrentItem(position);
        } else {
            view = mLayoutInflater.inflate(R.layout.item, container, false);
            Utils.setupItem(view, LIBRARIES[position]);
        }

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }
}
