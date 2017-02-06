package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.library.FocusResizeScrollListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import focusresize.EndlessRecyclerOnScrollListener;
import focusresize.PicConstanct;
import focusresize.TopicListAdapter;
import focusresize.domain.CustomObject;
import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbTopic;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.manager.BuilderManager;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.TimestampTypeAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

/**
 * 兴趣圈Fragment
 * Created by huangyueran on 2017/2/5.
 */
public class InterestCircleFragment extends BaseFragment {
    private static final String TAG = "InterestCircleFragment";
    private RecyclerView recyclerView;
    private TopicListAdapter topicListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<TbTopic> mTopicList;

    boolean isLoadMore = false; // 防止重复加载N页
    private String[] hobby_title = {"综合", "文学", "理学", "医学", "军事学", "哲学", "经济学", "教育学", "管理学"};

    private Integer mHobbyid = 0; //当前兴趣id 默认0 综合

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("llll", "兴趣圈");
        View view = UIUtils.inflate(R.layout.activity_listview);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(UIUtils.getContext());

        // 创建爆炸按钮
        BoomMenuButton bmb1 = (BoomMenuButton) view.findViewById(R.id.bmb1);
        bmb1.setButtonEnum(ButtonEnum.TextInsideCircle); // 设置按钮类型 为文字内嵌类型
        bmb1.bringToFront(); // 放到最上层 不被遮挡
        for (int i = 0; i < bmb1.getPiecePlaceEnum().pieceNumber(); i++) {
            TextInsideCircleButton.Builder textInsideCircleButtonBuilder = BuilderManager.getTextInsideCircleButtonBuilder();
            textInsideCircleButtonBuilder.normalText(hobby_title[i]);
            // 点击事件监听处理
            textInsideCircleButtonBuilder.listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
//                    Log.i(TAG, "当前按钮的坐标: " + index);
                    // 兴趣圈id= index 0~8 0为综合
                    // 销毁原先的数据
                    topicListAdapter = null;
//                    topicListAdapter=new
                    // 重新从网络加载数据
                    loadDate(index);
                }
            });
            bmb1.addBuilder(textInsideCircleButtonBuilder);
        }

        loadDate(mHobbyid); // 默认加载综合

//        createCustomAdapter(recyclerView, linearLayoutManager);

        return view;
    }

    // 加载第一页数据
    private void loadDate(int hobby_id) {
        final String url = GlobalValue.BASE_URL + "/topic/list/alltopic";
        final int page = 1; // 页数 首次加载第一页

        RequestParams params = new RequestParams();
        params.addQueryStringParameter("page", String.valueOf(page));
        params.addQueryStringParameter("hobby_id", String.valueOf(hobby_id));  // 兴趣圈id
        params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                url, params,
                new RequestCallBack<String>() {
                    private ProgressDialog dialog = new ProgressDialog(getContext());

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        // 得到图书馆list集合
                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        if (mTopicList != null) {
                            mTopicList.clear();
                        }
                        mTopicList = gson.fromJson(responseInfo.result, new TypeToken<List<TbTopic>>() {
                        }.getType());
                        createCustomAdapter(recyclerView, linearLayoutManager);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(UIUtils.getContext(), "数据加载失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        dialog.setMessage("正在加载数据");
                        dialog.show();
                    }
                });
    }

    private void createCustomAdapter(RecyclerView recyclerView, final LinearLayoutManager linearLayoutManager) {
//        CustomAdapter customAdapter = new CustomAdapter(UIUtils.getContext(), (int) UIUtils.getContext().getResources().getDimension(R.dimen.custom_item_height));
//        customAdapter.addItems(addItems());
        topicListAdapter = new TopicListAdapter(getContext(), (int) getContext().getResources().getDimension(R.dimen.custom_item_height));
        topicListAdapter.addItems(mTopicList);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            if (recyclerView.getAdapter() == null) { // 如果没有设置adapter 则设置
                recyclerView.setAdapter(topicListAdapter);
                recyclerView.addOnScrollListener(new FocusResizeScrollListener<>(topicListAdapter, linearLayoutManager));
                recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
                    @Override
                    public synchronized void onLoadMore(int currentPage) {
                        if (!isLoadMore) { // false 不是加载状态 才进行加载
                            isLoadMore = true;
                            Toast.makeText(getContext(), "加载完毕！", Toast.LENGTH_SHORT).show();
                            loadDateMore(currentPage, mHobbyid); //　mHobbyid当前兴趣id
                        }
                    }
                });
            } else { //如果已有adapter 则替换
                recyclerView.swapAdapter(topicListAdapter, true);
            }
        }
    }

    private synchronized void loadDateMore(int currentPage, Integer mHobbyid) {
        final String url = GlobalValue.BASE_URL + "/topic/list/alltopic";
//        Log.i(TAG, "currentPage: " + currentPage);

        RequestParams params = new RequestParams();
        params.addQueryStringParameter("page", String.valueOf(currentPage));
        params.addQueryStringParameter("hobby_id", String.valueOf(mHobbyid));
        params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                url, params,
                new RequestCallBack<String>() {
                    private ProgressDialog dialog = new ProgressDialog(getContext());

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        // 得到图书馆list集合
                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        List<TbTopic> topics = gson.fromJson(responseInfo.result, new TypeToken<List<TbTopic>>() {
                        }.getType());
                        topicListAdapter.addItems(topics);
                        dialog.dismiss();
                        isLoadMore = false; // 加载完毕 才设为true
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(UIUtils.getContext(), "数据加载失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        dialog.setMessage("正在加载数据");
                        dialog.show();
                    }
                });
    }


    private List<CustomObject> addItems() {
        List<String> picList = PicConstanct.getPicUrl();
        // 创建对象
        List<CustomObject> items = new ArrayList<>();
        for (int i = 0; i < picList.size(); i++) {
            CustomObject object = new CustomObject();
            object.setAuthor("习近平" + i + "号");
            object.setContent("这是习近平发表第" + i + "篇文章");
            object.setPosttime(new Date().toString());
            object.setReplys(i + "");
            object.setUrl(picList.get(i));
            items.add(object);
        }
        return items;
    }

}
