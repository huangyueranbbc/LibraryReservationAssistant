package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import focusresize.ImageLoader;
import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.LMSResult;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbTopic;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbTopicReply;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbUser;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.CacheUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.ServerUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.StringUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.TimestampTypeAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

public class CommentActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "CommentActivity";

    private ListView mListView;

    List mList = new ArrayList();
    private TbTopic topic;
    private List<TbTopicReply> replyList;
    private EditText etReply;
    private ImageButton imSend;

    private boolean isFBLoading = false; // 是否在发表中 控制网络单一请求 默认为false 没有请求网络
    private ReplyAdapter replyAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView(savedInstanceState);
        closeInputMethod(); // 关闭输入法
        loadDate(); // 加载评论数据

        initActionBar();
    }

    /**
     * 初始化actionBar
     */
    private void initActionBar() {
        ActionBar actionbar = getSupportActionBar();
        Log.i(TAG, "actionbar: " + actionbar);
        actionbar.setTitle(topic.getHobbyName());
        actionbar.setHomeButtonEnabled(true);// home处可以点击
        actionbar.setDisplayHomeAsUpEnabled(true);// 显示左上角返回键,当和侧边栏结合时展示三个杠图片
    }

    /**
     * 拦截返回键点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 切换抽屉
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 加载评论数据
     */
    private void loadDate() {

        final String url = GlobalValue.BASE_URL + "/reply/list";
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("topic_id", String.valueOf(topic.getId()));
        params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        // 得到图书馆list集合
                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        replyList = gson.fromJson(responseInfo.result, new TypeToken<List<TbTopicReply>>() {
                        }.getType());

                        // 设置数据
                        replyAdapter = new ReplyAdapter();
                        mListView.setAdapter(replyAdapter);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(UIUtils.getContext(), "数据加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    protected void initView(Bundle savedInstanceState) {
        mListView = (ListView) findViewById(R.id.lv_post);
        imSend = (ImageButton) findViewById(R.id.im_send); // 回复按钮
        etReply = (EditText) findViewById(R.id.et_reply);  // 回复文本框

        // 根据传递的参数 显示头布局--主题
        Intent intent = this.getIntent();
        topic = (TbTopic) intent.getSerializableExtra("topicInfo");

//        View v = View.inflate(this, R.layout.item_topic, null);
        View v = UIUtils.inflate(R.layout.item_topic_info);
        // 作者
        TextView tvAuthor = (TextView) v.findViewById(R.id.tv_author);
        // 发表时间
        TextView tvPosttime = (TextView) v.findViewById(R.id.tv_posttime);
        // 回复数
        final TextView tvReplys = (TextView) v.findViewById(R.id.tv_reply_count);
        // 内容
        TextView tvContent = (TextView) v.findViewById(R.id.tv_content);
        // 图片
        final ImageView topicImage = (ImageView) v.findViewById(R.id.topic_image);

        tvAuthor.setText(topic.getAuthorName());
        tvContent.setText("【" + topic.getHobbyName() + "】" + topic.getContent());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tvPosttime.setText(format.format(topic.getCreated()));
        tvReplys.setText(topic.getReplyCount() + "");
        ImageLoader imageLoader = new ImageLoader(getApplicationContext());
        imageLoader.DisplayImage(topic.getTopicPic(), topicImage);

        mListView.addHeaderView(v);

        imSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View v) {
                if (!isFBLoading) { // 不是发表中
                    isFBLoading = true;

                    if (StringUtils.isEmpty(etReply.getText().toString())) {
                        Toast.makeText(CommentActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                        isFBLoading = false;
                    } else {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        String userLoginInfo = CacheUtils.getCacheNotiming(GlobalValue.LOGININFO);
                        TbUser user = gson.fromJson(userLoginInfo, TbUser.class);
                        // 评论数据
                        TbTopicReply reply = new TbTopicReply();
                        reply.setIsdel(0);
                        reply.setAuthorId(user.getUserId());
                        reply.setAuthorName(user.getNickname());
                        reply.setIpaddr(ServerUtils.getHostIP());
                        reply.setCreated(new Date());
                        reply.setUpdated(new Date());
                        reply.setContent(etReply.getText().toString());
                        reply.setTopicId(topic.getId());
                        String replyJson = gson.toJson(reply);

                        final String url = GlobalValue.BASE_URL + "/reply/create";
                        RequestParams params = new RequestParams("UTF-8");
                        try {
                            params.setBodyEntity(new StringEntity(replyJson, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            isFBLoading = false;
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "发送失败 请重试......", Toast.LENGTH_SHORT).show();
                        }
                        params.setContentType("application/json");

                        Log.i(TAG, "replyJson: " + replyJson);

                        HttpUtils http = new HttpUtils();
                        http.send(HttpRequest.HttpMethod.POST,
                                url, params,
                                new RequestCallBack<String>() {

                                    @Override
                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                        // 得到图书馆list集合
                                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                        LMSResult lmsResult = gson.fromJson(responseInfo.result, new TypeToken<LMSResult>() {
                                        }.getType());
                                        if (lmsResult.getStatus() == 200) { // 成功
                                            Toast.makeText(CommentActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                            loadDate();
                                            replyAdapter.notifyDataSetChanged(); // 刷新界面
                                            closeInputMethod(); //关闭输入法
                                            etReply.setText(""); // 清空编辑框数据
                                            // 同时刷新主题 这里偷懒了~ 直接加一
                                            int replyCounts = Integer.parseInt(tvReplys.getText().toString()) + 1;
                                            tvReplys.setText(replyCounts + "");
                                        } else { // 失败
                                            Toast.makeText(CommentActivity.this, "服务异常 请重试...", Toast.LENGTH_SHORT).show();
                                        }
                                        isFBLoading = false;
                                    }

                                    @Override
                                    public void onFailure(HttpException e, String s) {
                                        Log.i(TAG, "onFailure: " + e.getMessage());
                                        Log.i(TAG, "onFailure: " + s);
                                        Log.i(TAG, "onFailure: " + url);
                                        e.printStackTrace();
                                        Toast.makeText(CommentActivity.this, "发送失败 请重试...", Toast.LENGTH_SHORT).show();
                                        isFBLoading = false;
                                    }
                                });
                    }
                }

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 评论lisrview 适配器
     */
    class ReplyAdapter extends BaseAdapter {

        public ReplyAdapter() {
        }

        @Override
        public int getCount() {
            return replyList.size();
        }

        @Override
        public Object getItem(int position) {
            return replyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_comment, null);
                holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_author);
                holder.tvPosttime = (TextView) convertView.findViewById(R.id.tv_posttime);
                holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(replyList.get(position).getAuthorName());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            holder.tvPosttime.setText(format.format(replyList.get(position).getCreated()));
            holder.tvContent.setText(replyList.get(position).getContent());

            return convertView;
        }
    }

    public class ViewHolder {
        public TextView tvName, tvContent, tvPosttime;

        public ViewHolder() {
        }
    }

    /**
     * 关闭输入法
     */
    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            imm.hideSoftInputFromWindow(etReply.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


}