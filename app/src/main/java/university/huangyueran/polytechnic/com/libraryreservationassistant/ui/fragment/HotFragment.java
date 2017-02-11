package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
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
import java.util.Random;

import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbFileShare;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.activity.FileSharedetailActivity;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment.download.BaseDownloadFragment;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.view.FlowLayout;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.view.LoadingPage;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.DrawableUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.TimestampTypeAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;


/**
 * Created by huangyueran on 2017/1/13.
 * 排行Fragment
 */
public class HotFragment extends BaseDownloadFragment {

    private ArrayList<TbFileShare> fileShares;

    @Override
    public View onCreateSuccessView() {
        // 支持上下滑动
        ScrollView scrollView = new ScrollView(UIUtils.getContext());
        FlowLayout flowLayout = new FlowLayout(UIUtils.getContext());

        int padding = UIUtils.dip2px(10);
        flowLayout.setPadding(padding, padding, padding, padding); // 设置内边距
        flowLayout.setHorizontalSpacing(UIUtils.dip2px(6)); // 水平间距
        flowLayout.setVerticalSpacing(UIUtils.dip2px(8)); // 垂直间距

        for (int i = 0; i < fileShares.size(); i++) {
            String fileNameNoEx = getFileNameNoEx(fileShares.get(i).getRealName());
            final String keyword = fileNameNoEx;
            TextView textView = new TextView(UIUtils.getContext());
            textView.setText(keyword);

            textView.setTextColor(Color.WHITE); //文字颜色 白色
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);// 18sp
            textView.setPadding(padding, padding, padding, padding);
            textView.setGravity(Gravity.CENTER);

            // 生成随机颜色
            Random random = new Random();
            int r = 30 + random.nextInt(170);
            int g = 30 + random.nextInt(170);
            int b = 30 + random.nextInt(170);

            int color = 0xffcecece;// 按下后偏白的背景色

            // GradientDrawable bgNormal = DrawableUtils.getGradientDrawable(Color.rgb(r, g, b), UIUtils.dip2px(6));
            // GradientDrawable bgPress = DrawableUtils.getGradientDrawable(color, UIUtils.dip2px(6));
            // StateListDrawable selector = DrawableUtils.getSelector(bgNormal, bgPress);

            StateListDrawable selector = DrawableUtils.getSelector(Color.rgb(r, g, b), color, UIUtils.dip2px(6));
            textView.setBackgroundDrawable(selector);

            flowLayout.addView(textView);

            // 只有设置点击事件, 状态选择器才起作用
            final int position = i;
            textView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(), keyword, Toast.LENGTH_SHORT).show();
                    if (fileShares.get(position) != null) { // TODO 详情页展示
                        Intent intent = new Intent(getContext(), FileSharedetailActivity.class);
                        intent.putExtra("fileshareinfo", fileShares.get(position)); // 传递包名数据
                        startActivity(intent);
                    }
                    // activity跳转
                }
            });
        }

        scrollView.addView(flowLayout);
        return scrollView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String url = GlobalValue.BASE_URL + "/fileshare/hot";
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        // 得到图书馆list集合
                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        fileShares = gson.fromJson(responseInfo.result, new TypeToken<ArrayList<TbFileShare>>() {
                        }.getType());
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(UIUtils.getContext(), "数据加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        // TODO 直接使用http请求网络数据 并进行返回
        final String url = GlobalValue.BASE_URL + "/fileshare/hot";
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        // 得到图书馆list集合
                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        fileShares = gson.fromJson(responseInfo.result, new TypeToken<ArrayList<TbFileShare>>() {
                        }.getType());
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(UIUtils.getContext(), "数据加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
        return check(fileShares);
    }

    /**
     * 校验结果数据是否正确，并返回状态值
     *
     * @param o
     * @return
     */
    public LoadingPage.ResultState check(List<TbFileShare> o) {
        if (o != null) {
            if (o instanceof ArrayList) { // 判断是否是集合
                ArrayList list = (ArrayList) o;
                if (list.isEmpty()) {
                    return LoadingPage.ResultState.STATE_EMPTY;
                } else {
                    return LoadingPage.ResultState.STATE_SUCCESS;
                }
            }
        }

        return LoadingPage.ResultState.STATE_ERROR;
    }

    /**
     * Java文件操作 获取不带扩展名的文件名
     * <p/>
     * Created on: 2011-8-2
     * Author: blueeagle
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

}
