package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbFileShare;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.holder.DetailAppInfoHolder;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.holder.DetailDesHolder;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.holder.DetailDownloadHolder;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.view.LoadingPage;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

public class FileSharedetailActivity extends AppCompatActivity {

    private static final String TAG = "FileSharedetailActivity";

    private LoadingPage mLoadingPage;
    private TbFileShare data;
    private TbFileShare fileShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoadingPage = new LoadingPage(this) {
            @Override
            public View onCreateSuccessView() {
                return FileSharedetailActivity.this.onCreateSuccessView();
            }

            @Override
            public ResultState onLoad() {
                return FileSharedetailActivity.this.onLoad();
            }
        };

        setContentView(mLoadingPage);// 直接将一个view对象设置给Activity

        // 获取从HomeFragment传递过来的包名参数
        fileShare = (TbFileShare) getIntent().getSerializableExtra("fileshareinfo");

        // 开始加载网络数据
        mLoadingPage.loadData();

        initActionBar();
    }

    /**
     * 初始化actionBar
     */
    private void initActionBar() {
        ActionBar actionbar = getSupportActionBar();
        Log.i(TAG, "actionbar: " + actionbar);

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


    public View onCreateSuccessView() {
        // 初始化成功布局文件
        View view = UIUtils.inflate(R.layout.page_home_detail);

        // 初始化应用信息模块
        FrameLayout flDetailAppInfo = (FrameLayout) view.findViewById(R.id.fl_detail_appinfo);

        // 动态给帧布局填充页面
        DetailAppInfoHolder appInfoHolder = new DetailAppInfoHolder();
        flDetailAppInfo.addView(appInfoHolder.getRootView());
        appInfoHolder.setData(data);

        // 初始化描述
        FrameLayout flDeatilDes = (FrameLayout) view.findViewById(R.id.fl_detail_des);
        DetailDesHolder detailDesHolder = new DetailDesHolder();
        flDeatilDes.addView(detailDesHolder.getRootView());
        detailDesHolder.setData(data);

        // 初始化下载模块
        FrameLayout flDeatilDownload = (FrameLayout) view.findViewById(R.id.fl_detail_download);
        DetailDownloadHolder detailDownloadHolder = new DetailDownloadHolder();
        flDeatilDownload.addView(detailDownloadHolder.getRootView());
        detailDownloadHolder.setData(data);
        return view;
    }

    public LoadingPage.ResultState onLoad() {
        // 请求网络,加载数据
        data = fileShare;

        if (data != null) {
            return LoadingPage.ResultState.STATE_SUCCESS;
        } else {
            return LoadingPage.ResultState.STATE_ERROR;
        }
    }

}
