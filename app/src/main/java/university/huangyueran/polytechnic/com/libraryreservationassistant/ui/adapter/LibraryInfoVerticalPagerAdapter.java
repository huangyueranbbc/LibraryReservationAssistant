package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
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
public class LibraryInfoVerticalPagerAdapter extends PagerAdapter {

    private final Utils.LibraryObject[] TWO_WAY_LIBRARIES = new Utils.LibraryObject[]{
            new Utils.LibraryObject(
                    R.drawable.ic_fintech,
                    "当前人数"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_delivery,
                    "在线好友"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_social,
                    "扫码找座"
            ),
    };

    private LayoutInflater mLayoutInflater;

    public LibraryInfoVerticalPagerAdapter(final Context context, FragmentManager mFragmentManager) {
        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return TWO_WAY_LIBRARIES.length * 2;
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        if (position < TWO_WAY_LIBRARIES.length) {
            final View view = mLayoutInflater.inflate(R.layout.item, container, false);

            // TODO 点击事件监听
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
        } else {
            final View view = mLayoutInflater.inflate(R.layout.item, container, false);

            // TODO 点击事件监听
            LinearLayout llItem = (LinearLayout) view.findViewById(R.id.ll_item);
            llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 每个item被点击 触发事件
                    Toast.makeText(UIUtils.getContext(), "当前" + TWO_WAY_LIBRARIES[position - TWO_WAY_LIBRARIES.length] + "======" + position + "被点击了", Toast.LENGTH_SHORT).show();
                }
            });

            Utils.setupItem(view, TWO_WAY_LIBRARIES[position - TWO_WAY_LIBRARIES.length]);

            container.addView(view);
            return view;
        }
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
