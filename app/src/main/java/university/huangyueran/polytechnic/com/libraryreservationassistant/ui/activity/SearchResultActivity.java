package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbFileShare;
import university.huangyueran.polytechnic.com.libraryreservationassistant.http.protocol.HomeProtocol;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.adapter.MyBaseAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.holder.BaseHolder;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.holder.HomeHolder;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.view.MyListView;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

public class SearchResultActivity extends BaseActivity {

    private ArrayList<TbFileShare> data; // 加载到的网络数据
    private ListView listView;
    private String query="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout mRootView = (FrameLayout) UIUtils.inflate(R.layout.activity_search_result);
        setContentView(mRootView);

        // 取数据
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("fileShares")) {
            data = (ArrayList<TbFileShare>) intent.getSerializableExtra("fileShares");
            query = intent.getStringExtra("search_query");
        }

        listView = new MyListView(mRootView.getContext());

        listView.setAdapter(new HomeAdapter(data));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TbFileShare appInfo = data.get(position);

                if (appInfo != null) { // TODO 详情页展示
                    Intent intent = new Intent(SearchResultActivity.this, FileSharedetailActivity.class);
                    intent.putExtra("fileshareinfo", appInfo); // 传递包名数据
                    startActivity(intent);
                }

            }
        });

        mRootView.addView(listView);

        initActionBar();
    }

    /**
     * 初始化actionBar
     */
    private void initActionBar() {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(query);
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

    class HomeAdapter extends MyBaseAdapter<TbFileShare> {

        public HomeAdapter(ArrayList<TbFileShare> data) {
            super(data);
        }

        /**
         * 获取当前页面的holder对象
         *
         * @return
         */
        @Override
        public BaseHolder<TbFileShare> getHolder(int position) {
            return new HomeHolder();
        }

        /**
         * 网络加载更多数据
         *
         * @return
         */
        @Override
        public ArrayList<TbFileShare> onLoadMore() {
            HomeProtocol protocol = new HomeProtocol();
            // 下一页数据开始位置=当前集合大小
            ArrayList<TbFileShare> moreData = protocol.getData(getListSize());

            return moreData;
        }

    }
}
