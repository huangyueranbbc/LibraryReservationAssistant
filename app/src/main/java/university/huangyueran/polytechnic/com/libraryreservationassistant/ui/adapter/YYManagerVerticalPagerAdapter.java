package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.Utils;

/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class YYManagerVerticalPagerAdapter extends PagerAdapter {

    private final Utils.LibraryObject[] TWO_WAY_LIBRARIES = new Utils.LibraryObject[]{
            new Utils.LibraryObject(
                    R.drawable.ic_fintech,
                    "我的预约"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_delivery,
                    "提前离开"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_social,
                    "一键预约"
            ),
    };

    private LayoutInflater mLayoutInflater;

    public YYManagerVerticalPagerAdapter(final Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return TWO_WAY_LIBRARIES.length;
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view = mLayoutInflater.inflate(R.layout.item, container, false);
        LinearLayout llItem = (LinearLayout) view.findViewById(R.id.ll_item);
        llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 每个item被点击 触发事件
                Toast.makeText(UIUtils.getContext(), "当前" + TWO_WAY_LIBRARIES[position] + position + "被点击了", Toast.LENGTH_SHORT).show();
            }
        });
        Utils.setupItem(view, TWO_WAY_LIBRARIES[position]);

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
