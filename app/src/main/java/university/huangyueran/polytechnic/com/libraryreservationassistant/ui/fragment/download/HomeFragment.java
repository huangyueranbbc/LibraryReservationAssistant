package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment.download;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbFileShare;
import university.huangyueran.polytechnic.com.libraryreservationassistant.http.protocol.HomeProtocol;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.activity.FileSharedetailActivity;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.activity.SearchActivity;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.adapter.MyBaseAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.holder.BaseHolder;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.holder.HomeHolder;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.view.LoadingPage;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.view.MyListView;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

/**
 * Created by huangyueran on 2017/2/9.
 */
public class HomeFragment extends BaseDownloadFragment {
    private static final String TAG = "HomeFragment";

    private ArrayList<TbFileShare> data; // 加载到的网络数据
    private ListView listView;

    @Override
    public View onCreateSuccessView() {
        listView = new MyListView(UIUtils.getContext());

        listView.setAdapter(new HomeAdapter(data));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TbFileShare appInfo = data.get(position);

                if (appInfo != null) { // TODO 详情页展示
                    Intent intent = new Intent(getContext(), FileSharedetailActivity.class);
                    intent.putExtra("fileshareinfo", appInfo); // 传递包名数据
                    startActivity(intent);
                }

            }
        });

        setHasOptionsMenu(true);

        return listView;
    }

    /**
     * 添加左侧 搜索菜单
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add("").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.getItem(0).setIcon(R.drawable.abs__ic_search);//替换图标
    }

    /**
     * 左侧菜单点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO 带结果 跳转到搜索Activity 换回结果为搜索的数据
        Intent intent = new Intent(getContext(), SearchActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (data != null && data.hasExtra("fileShares")) {
//            ArrayList<TbFileShare> fileShares = (ArrayList<TbFileShare>) data.getSerializableExtra("fileShares");
//            if (fileShares != null) {
//                // 更新数据 刷新ListView
//                this.data.clear();
//                this.data = fileShares;
//                listView.setAdapter(new HomeAdapter(fileShares));
//            }
//        }
//
//    }

    @Override
    public LoadingPage.ResultState onLoad() {
        // 请求网络
        HomeProtocol protocol = new HomeProtocol();
        data = protocol.getData(0);// 加载第一页数据

        return check(data); // 校验数据并返回状态
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
