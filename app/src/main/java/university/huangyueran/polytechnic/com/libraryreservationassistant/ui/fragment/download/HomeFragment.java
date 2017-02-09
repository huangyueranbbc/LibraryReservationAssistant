package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment.download;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbFileShare;
import university.huangyueran.polytechnic.com.libraryreservationassistant.http.protocol.HomeProtocol;
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

    private ArrayList<TbFileShare> data; // 加载到的网络数据

    @Override
    public View onCreateSuccessView() {
        ListView listView = new MyListView(UIUtils.getContext());

        listView.setAdapter(new HomeAdapter(data));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TbFileShare appInfo = data.get(position);

                if (appInfo != null) { // TODO 详情页展示
//                    Intent intent = new Intent(UIUtils.getContext(), HomedetailActivity.class);
//                    intent.putExtra("packageName", appInfo.getPackageName()); // 传递包名数据
//                    startActivity(intent);
                }

            }
        });

        return listView;
    }

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
