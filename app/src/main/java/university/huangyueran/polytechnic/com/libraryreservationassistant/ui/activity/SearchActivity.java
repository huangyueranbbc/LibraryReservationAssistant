package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.czp.searchmlist.mSearchLayout;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbFileShare;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.CacheUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.StringUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.TimestampTypeAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

/**
 * Created by liuyunming on 2016/11/24.
 */

public class SearchActivity extends Activity {

    private static final String TAG = "SearchActivity";

    private mSearchLayout msearchLy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        msearchLy = (mSearchLayout) findViewById(R.id.msearchlayout);
        initData();
    }

    protected void initData() {
        Log.i(TAG, "msearchLy: " + msearchLy);

        String shareData = CacheUtils.getCacheNotiming(GlobalValue.SEARCH_HISTORY); // 从缓存中获取搜索历史记录
        List<String> skills;
        if (!StringUtils.isEmpty(shareData)) {
            skills = Arrays.asList(shareData.split(","));
        } else {
            skills = null;
        }

        String shareHotData = "三体,JAVA编程思想,英雄联盟爬坑指南"; // TODO 推荐功能 获取热门搜索数据 进行显示
        List<String> skillHots = Arrays.asList(shareHotData.split(","));

        msearchLy.initData(skills, skillHots, new mSearchLayout.setSearchCallBackListener() {
            @Override
            public void Search(final String str) {
                //进行或联网搜索
                final String url = GlobalValue.SEARCH_REST_URL + "/search/fileshare/query";

                RequestParams params = new RequestParams();
                params.addQueryStringParameter("q", str);
                params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

                HttpUtils http = new HttpUtils();
                http.send(HttpRequest.HttpMethod.GET,
                        url, params,
                        new RequestCallBack<String>() {

                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                // 得到图书馆list集合
                                Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                ArrayList<TbFileShare> fileShares = gson.fromJson(responseInfo.result, new TypeToken<ArrayList<TbFileShare>>() {
                                }.getType());
                                Log.i(TAG, "搜索 onSuccess: " + fileShares.toString());
                                Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                                intent.putExtra("fileShares", fileShares);
                                intent.putExtra("search_query", str);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                Toast.makeText(UIUtils.getContext(), "网络异常 搜索失败", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void Back() {
                finish();
            }

            @Override
            public void ClearOldData() {
                CacheUtils.setCacheNotiming(GlobalValue.SEARCH_HISTORY, "");
            }

            @Override
            public void SaveOldData(ArrayList<String> AlloldDataList) {
                // 去除重复项
                HashSet<String> hs = new HashSet<String>(AlloldDataList);
                //保存所有的搜索记录
                StringBuilder sb = new StringBuilder();
                for (String s : hs) {
                    sb.append(s + ",");
                }
                CacheUtils.setCacheNotiming(GlobalValue.SEARCH_HISTORY, sb.toString());
            }
        });
    }

}
