package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gigamole.infinitecycleviewpager.VerticalInfiniteCycleViewPager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbLibrary;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.CacheUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.StringUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.TimestampTypeAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.Utils;

/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class HorizontalPagerAdapter extends PagerAdapter {
    private static final String TAG = "HorizontalPagerAdapter";

    private static HashMap<Integer, PagerAdapter> verticalPagerAdapterMap = new HashMap<Integer, PagerAdapter>();

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
    private Utils.LibraryObject[] two_way_libraries;
    private ArrayList<TbLibrary> libraries;

    private FragmentManager mFragmentManager;

    public HorizontalPagerAdapter(final Context context, final boolean isTwoWay, FragmentManager fragmentManager) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mIsTwoWay = isTwoWay;
        this.mFragmentManager = fragmentManager;
    }

    @Override
    public int getCount() {
        return 6; // 返回三个垂直viewpager
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        final View view;
        if (mIsTwoWay) {
            view = mLayoutInflater.inflate(R.layout.two_way_item, container, false);
            final VerticalInfiniteCycleViewPager verticalInfiniteCycleViewPager = (VerticalInfiniteCycleViewPager) view.findViewById(R.id.vicvp);
            // 进行三个viewpager的判断
            switch (position) {
                case 0: //选择图书馆
                case 3:
                    if (null == verticalPagerAdapterMap.get(0)) {
                        final String url = GlobalValue.BASE_URL + "/library/list";
                        // TODO 这个需要动态传入
                        final int schoole_id = 1;
                        // 获取图书馆数据
                        // 取缓存
                        String cache = CacheUtils.getCacheNotiming("library-list" + schoole_id);
                        if (!StringUtils.isEmpty(cache)) {
                            Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                            libraries = gson.fromJson(cache, new TypeToken<List<TbLibrary>>() {
                            }.getType());
                            // 根据集合 初始化数组 在页面展示的内容
                            two_way_libraries = new Utils.LibraryObject[libraries.size()];
                            for (int i = 0; i < libraries.size(); i++) {
                                Utils.LibraryObject object = new Utils.LibraryObject(R.drawable.ic_fintech,
                                        libraries.get(i).getName());
                                two_way_libraries[i] = object;
                            }
                            SelectLibraryVerticalPagerAdapter selectLibraryVerticalPagerAdapter = new SelectLibraryVerticalPagerAdapter(mContext, two_way_libraries, libraries, mFragmentManager);
                            verticalInfiniteCycleViewPager.setAdapter(selectLibraryVerticalPagerAdapter);
                            verticalPagerAdapterMap.put(0, selectLibraryVerticalPagerAdapter);
                        } else {
                            // 缓存没有 去网络中取
                            // 学校id
                            RequestParams params = new RequestParams();
                            params.addQueryStringParameter("id", String.valueOf(schoole_id));

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
                                            // 根据集合 初始化数组 在页面展示的内容
                                            two_way_libraries = new Utils.LibraryObject[libraries.size()];
                                            for (int i = 0; i < libraries.size(); i++) {
                                                Utils.LibraryObject object = new Utils.LibraryObject(R.drawable.ic_fintech,
                                                        libraries.get(i).getName());
                                                two_way_libraries[i] = object;
                                            }
                                            dialog.dismiss();
                                            SelectLibraryVerticalPagerAdapter selectLibraryVerticalPagerAdapter = new SelectLibraryVerticalPagerAdapter(mContext, two_way_libraries, libraries, mFragmentManager);
                                            verticalInfiniteCycleViewPager.setAdapter(selectLibraryVerticalPagerAdapter);
                                            verticalPagerAdapterMap.put(0, selectLibraryVerticalPagerAdapter);
                                            CacheUtils.setCacheNotiming("library-list" + schoole_id, responseInfo.result);
                                        }

                                        @Override
                                        public void onFailure(HttpException e, String s) {
                                            Toast.makeText(UIUtils.getContext(), "数据加载失败", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onStart() {
                                            dialog.setTitle("正在加载中");
                                            dialog.show();
                                        }

                                        @Override
                                        public void onLoading(long total, long current, boolean isUploading) {
                                            dialog.setTitle("正在加载中");
                                            dialog.show();
                                        }
                                    });
                        }

                    } else {
                        verticalInfiniteCycleViewPager.setAdapter(verticalPagerAdapterMap.get(0));
                    }
                    break;
                case 1: //预约管理
                case 4:
                    if (null == verticalPagerAdapterMap.get(1)) {
                        YYManagerVerticalPagerAdapter yyManagerVerticalPagerAdapter = new YYManagerVerticalPagerAdapter(mContext, mFragmentManager);
                        verticalInfiniteCycleViewPager.setAdapter(yyManagerVerticalPagerAdapter);
                        verticalPagerAdapterMap.put(1, yyManagerVerticalPagerAdapter);
                    } else {
                        verticalInfiniteCycleViewPager.setAdapter(verticalPagerAdapterMap.get(1));
                    }
                    break;
                case 2: //馆内详情
                case 5:
                    if (null == verticalPagerAdapterMap.get(2)) {
                        LibraryInfoVerticalPagerAdapter libraryInfoVerticalPagerAdapter = new LibraryInfoVerticalPagerAdapter(mContext, mFragmentManager);
                        verticalInfiniteCycleViewPager.setAdapter(libraryInfoVerticalPagerAdapter);
                        verticalPagerAdapterMap.put(2, libraryInfoVerticalPagerAdapter);
                    } else {
                        verticalInfiniteCycleViewPager.setAdapter(verticalPagerAdapterMap.get(2));
                    }
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
