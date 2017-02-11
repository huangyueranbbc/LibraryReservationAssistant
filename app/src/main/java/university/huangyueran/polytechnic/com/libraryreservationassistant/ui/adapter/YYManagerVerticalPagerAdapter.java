package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
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
import java.util.Random;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbReservation;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbUser;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment.SelectReservationDialogFragment;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.CacheUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.StringUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.TimestampTypeAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.Utils;

/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class YYManagerVerticalPagerAdapter extends PagerAdapter {

    FragmentManager mFragmentManager;

    Context mContext;

    private static boolean isYYLoading = false; // 防止重复请求 false==不在请求状态中

    private final Utils.LibraryObject[] TWO_WAY_LIBRARIES = new Utils.LibraryObject[]{
            new Utils.LibraryObject(
                    R.drawable.ic_fintech,
                    "我的预约"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_delivery,
                    "一键预约"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_social,
                    "提前离开"
            ),
    };

    private LayoutInflater mLayoutInflater;

    public YYManagerVerticalPagerAdapter(final Context context, FragmentManager fragmentManager) {
        mLayoutInflater = LayoutInflater.from(context);
        this.mFragmentManager = fragmentManager;
        this.mContext = context;
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
        LinearLayout llItem = (LinearLayout) view.findViewById(R.id.ll_item);

        final int showPosition;
        if (position < TWO_WAY_LIBRARIES.length) {
            showPosition = position;
        } else {
            showPosition = position - TWO_WAY_LIBRARIES.length;
        }

        // TODO 点击事件监听
        llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View v) {
                // 获取用户id
                String userLoginJson = CacheUtils.getCacheNotiming(GlobalValue.LOGININFO);
                Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                final TbUser user = gson.fromJson(userLoginJson, new TypeToken<TbUser>() {
                }.getType());
                final ProgressDialog dialog = new ProgressDialog(mContext);

                // 每个item被点击 触发事件
                switch (showPosition) {

                    case 0: // 我的预约
                        // 先从缓存中取
                        // String cache = CacheUtils.getCache(GlobalValue.RESERVATION_CACHE_INFO + user.getUserId());
                        if (!isYYLoading) {
                            isYYLoading = true;
                            String cache = "";// 不读缓存
                            if (!StringUtils.isEmpty(cache)) { // 缓存不为空 去缓存
                                TbReservation reservation = gson.fromJson(cache, new TypeToken<TbReservation>() {
                                }.getType());
                                SelectReservationDialogFragment.newInstance(SelectReservationDialogFragment.Type.CUSTOM, reservation).show(mFragmentManager, "alert");
                                SystemClock.sleep(500);
                                isYYLoading = false;
                            } else { // 缓存为空 取网络中取
                                // 网络请求数据
                                final String url = GlobalValue.BASE_URL + "/reservation/show/used";
                                RequestParams params = new RequestParams();
                                params.addQueryStringParameter("id", String.valueOf(user.getUserId()));
                                params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

                                HttpUtils http = new HttpUtils();
                                http.send(HttpRequest.HttpMethod.GET,
                                        url, params,
                                        new RequestCallBack<String>() {

                                            @Override
                                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                                if (StringUtils.isEmpty(responseInfo.result)) {// 如果为Null 表示还没有预订记录
                                                    SelectReservationDialogFragment.newInstance(SelectReservationDialogFragment.Type.NOSEATRECORDINFO, null).show(mFragmentManager, "alert");
                                                } else {
                                                    Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                                    TbReservation reservation = gson.fromJson(responseInfo.result, new TypeToken<TbReservation>() {
                                                    }.getType());
                                                    CacheUtils.setCache(GlobalValue.RESERVATION_CACHE_INFO + user.getUserId(), responseInfo.result, (long) (reservation.getTimeSpan() * 3600000));
                                                    SelectReservationDialogFragment.newInstance(SelectReservationDialogFragment.Type.CUSTOM, reservation).show(mFragmentManager, "alert");
                                                }
                                                dialog.dismiss();
                                                SystemClock.sleep(500);
                                                isYYLoading = false;
                                            }

                                            @Override
                                            public void onFailure(HttpException e, String s) {
                                                Toast.makeText(UIUtils.getContext(), "数据加载失败", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                SystemClock.sleep(500);
                                                isYYLoading = false;
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
                        break;
                    case 1: //一键预约
                        if (!isYYLoading) {
                            isYYLoading = true;
                            String url = GlobalValue.BASE_URL + "/reservation/random/create";
                            RequestParams params = new RequestParams();
                            params.addQueryStringParameter("user_id", String.valueOf(user.getUserId()));
                            params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交
                            HttpUtils http = new HttpUtils();
                            http.send(HttpRequest.HttpMethod.GET,
                                    url, params,
                                    new RequestCallBack<String>() {

                                        @Override
                                        public void onSuccess(ResponseInfo<String> responseInfo) {
                                            Log.i("intent", "网络网络: ");
                                            if (StringUtils.isEmpty(responseInfo.result)) { // 如果结果为空 图书馆作为已满
                                                SelectReservationDialogFragment.newInstance(SelectReservationDialogFragment.Type.RANDOMCREATESEAT, null).show(mFragmentManager, "alert");
                                                SystemClock.sleep(500);
                                                isYYLoading = false;
                                            } else {
                                                Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                                TbReservation reservation = gson.fromJson(responseInfo.result, new TypeToken<TbReservation>() {
                                                }.getType());
                                                // CacheUtils.setCache(GlobalValue.RESERVATION_CACHE_INFO + user.getUserId(), responseInfo.result, (long) (reservation.getTimeSpan() * 3600000));
                                                SelectReservationDialogFragment.newInstance(SelectReservationDialogFragment.Type.CUSTOM, reservation).show(mFragmentManager, "info");
                                            }
                                            dialog.dismiss();
                                            SystemClock.sleep(500);
                                            isYYLoading = false;
                                        }

                                        @Override
                                        public void onFailure(HttpException e, String s) {
                                            Toast.makeText(UIUtils.getContext(), "数据加载失败", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            SystemClock.sleep(500);
                                            isYYLoading = false;
                                        }

                                        @Override
                                        public void onLoading(long total, long current, boolean isUploading) {
                                            super.onLoading(total, current, isUploading);
                                            dialog.setTitle("正在加载中");
                                            dialog.show();
                                        }
                                    });
                        }

                        break;
                    case 2: // 提前离开
                        if (!isYYLoading) {
                            isYYLoading = true;
                            TbReservation reservation = new TbReservation();
                            reservation.setUserId(user.getUserId());
                            SelectReservationDialogFragment.newInstance(SelectReservationDialogFragment.Type.ISLEAVE, reservation, mFragmentManager).show(mFragmentManager, "info");
                            SystemClock.sleep(400);
                            isYYLoading = false;
                        }
                        break;
                    default:
                        break;
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
