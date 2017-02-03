package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbLibrary;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbReadroom;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment.SelectReadRoomDialogFragment;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.CacheUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.StringUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.TimestampTypeAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.Utils;


/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class SelectLibraryVerticalPagerAdapter extends PagerAdapter {

    private static final String TAG = "tttt";

    private List<TbLibrary> libraries;
    private static Utils.LibraryObject[] TWO_WAY_LIBRARIES;

    private LayoutInflater mLayoutInflater;

    private FragmentManager mFragmentManager;
    private ArrayList<TbReadroom> readrooms;
    private Context mContext;

    public SelectLibraryVerticalPagerAdapter(final Context context, Utils.LibraryObject[] two_way_libraries, ArrayList<TbLibrary> libraries, FragmentManager fragmentManager) {
        Toast.makeText(context, "请选择图书馆", Toast.LENGTH_LONG).show();
        this.mContext = context;
        this.libraries = libraries;
        this.TWO_WAY_LIBRARIES = two_way_libraries;
        this.mFragmentManager = fragmentManager;
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
        final View view = mLayoutInflater.inflate(R.layout.item, container, false);

        // TODO 点击事件监听
        LinearLayout llItem = (LinearLayout) view.findViewById(R.id.ll_item);
        final int showPosition;
        if (position < TWO_WAY_LIBRARIES.length) {
            showPosition = position;
        } else {
            showPosition = position - TWO_WAY_LIBRARIES.length;
        }

        llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 创建选择阅览室的ListView
                final Long library_id = libraries.get(showPosition).getId(); // 当前点击item的图书馆id

                // 网络请求数据
                final String url = GlobalValue.BASE_URL + "/readroom/list";
                String cache = CacheUtils.getCache("readroom-list" + library_id);

                if (!StringUtils.isEmpty(cache)) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    readrooms = gson.fromJson(cache, new TypeToken<ArrayList<TbReadroom>>() {
                    }.getType());
                    // 创建弹出框 listview
                    SelectReadRoomDialogFragment.newInstance(SelectReadRoomDialogFragment.Type.DEFAULT_LIST, readrooms).show(mFragmentManager, "list");
                } else {
                    // 缓存没有 去网络中取
                    RequestParams params = new RequestParams();
                    params.addQueryStringParameter("id", String.valueOf(library_id));

                    HttpUtils http = new HttpUtils();
                    http.send(HttpRequest.HttpMethod.GET,
                            url, params,
                            new RequestCallBack<String>() {
                                private ProgressDialog dialog = new ProgressDialog(mContext);

                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    // 得到图书馆list集合
                                    Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                    readrooms = gson.fromJson(responseInfo.result, new TypeToken<ArrayList<TbReadroom>>() {
                                    }.getType());
                                    // 存入缓存
                                    CacheUtils.setCache("readroom-list" + library_id, responseInfo.result);

                                    dialog.dismiss();
                                    // 创建弹出框 listview
                                    SelectReadRoomDialogFragment.newInstance(SelectReadRoomDialogFragment.Type.DEFAULT_LIST, readrooms).show(mFragmentManager, "list");
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    Toast.makeText(UIUtils.getContext(), "数据加载失败", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }

                                @Override
                                public void onStart() {
                                    super.onStart();
                                }

                                @Override
                                public void onLoading(long total, long current, boolean isUploading) {
                                    super.onLoading(total, current, isUploading);
                                    dialog.setTitle("正在加载中");
                                    dialog.show();
                                }
                            });
                }

            }
        });

        Utils.setupItem(view, TWO_WAY_LIBRARIES[showPosition]);
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
