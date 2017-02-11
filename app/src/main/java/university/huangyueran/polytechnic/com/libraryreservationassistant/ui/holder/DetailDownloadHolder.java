package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.holder;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.DownloadInfo;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbFileShare;
import university.huangyueran.polytechnic.com.libraryreservationassistant.manager.DownloadManager;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.view.ProgressHorizontal;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;


/**
 * 详情页--下载模块
 * Created by huangyueran on 2017/1/22.
 */
public class DetailDownloadHolder extends BaseHolder<TbFileShare> implements DownloadManager.DownloadObserver, View.OnClickListener {

    private static final String TAG = "DownloadManager";

    private DownloadManager mDownloadManager;
    private int mCurrentState;
    private float mProgress;
    private FrameLayout flProgress;
    private Button btnDownload;
    private ProgressHorizontal pbProgress;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.layout_detail_download);

        // 下载 暂停 安装 点击事件
        btnDownload = (Button) view.findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(this);

        // 初始化自定义进度条
        flProgress = (FrameLayout) view.findViewById(R.id.fl_progress);
        flProgress.setOnClickListener(this); // 进度条点击事件

        pbProgress = new ProgressHorizontal(UIUtils.getContext());
        pbProgress.setProgressBackgroundResource(R.drawable.progress_bg);// 进度条背景图片
        pbProgress.setProgressResource(R.drawable.progress_normal);// 进度条图片
        pbProgress.setProgressTextColor(Color.WHITE);// 进度文字颜色
        pbProgress.setProgressTextSize(UIUtils.dip2px(18));// 进度文字大小

        // 宽高填充父窗体
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        // 给帧布局添加自定义进度条
        flProgress.addView(pbProgress, params);

        mDownloadManager = DownloadManager.getInstance();
        mDownloadManager.registerObserver(this); // 注册观察者,监听状态和进度变化

        return view;
    }

    /**
     * 刷新界面
     *
     * @param appInfo
     */
    @Override
    public void refreshView(TbFileShare appInfo) {
        // 判断当前应用是否下载中
        DownloadInfo downloadInfo = mDownloadManager.getDownloadInfo(appInfo);

        if (downloadInfo != null) {
            // 之前下载过 获取进度和状态
            mCurrentState = downloadInfo.currentState;
            mProgress = downloadInfo.getProgress();
        } else {
            // 之前没有下载过
            mCurrentState = DownloadManager.STATE_UNDO;
            mProgress = 0;
        }

        refreshUI(mCurrentState, mProgress);
    }

    // 根据当前的下载进度和状态来更新界面
    private void refreshUI(int currentState, float progress) {

        //System.out.println("刷新ui了:" + currentState);

        mCurrentState = currentState;
        mProgress = progress;

        switch (currentState) {
            case DownloadManager.STATE_UNDO:// 未下载
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("下载");
                break;

            case DownloadManager.STATE_WAITING:// 等待下载
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("等待中..");
                break;

            case DownloadManager.STATE_DOWNLOADING:// 正在下载
                flProgress.setVisibility(View.VISIBLE);
                btnDownload.setVisibility(View.GONE);
                pbProgress.setCenterText("");
                pbProgress.setProgress(mProgress);// 设置下载进度
                break;

            case DownloadManager.STATE_PAUSE:// 下载暂停
                flProgress.setVisibility(View.VISIBLE);
                btnDownload.setVisibility(View.GONE);
                pbProgress.setCenterText("暂停");
                pbProgress.setProgress(mProgress);

                System.out.println("暂停界面更新:" + mCurrentState);
                break;

            case DownloadManager.STATE_ERROR:// 下载失败
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("下载失败");
                break;

            case DownloadManager.STATE_SUCCESS:// 下载成功
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("打开");
                break;

            default:
                break;
        }

    }

    // 主线程更新UI界面
    private void refreshUIOnMainThread(final DownloadInfo info) {
        // 判断下载对象是否是当前应用
        TbFileShare appInfo = getData();
        if (appInfo.getId().toString().equals(info.id)) {
            UIUtils.runOnUIThread(new Runnable() {

                @Override
                public void run() {
                    refreshUI(info.currentState, info.getProgress());
                }
            });
        }
    }

    /**
     * 状态更新
     *
     * @param downloadInfo
     */
    @Override
    public void onDownloadStateChanged(DownloadInfo downloadInfo) {
        refreshUIOnMainThread(downloadInfo);
    }

    /**
     * 进度条更新 子线程
     *
     * @param downloadInfo
     */
    @Override
    public void onDownloadProgressChanged(DownloadInfo downloadInfo) {
        refreshUIOnMainThread(downloadInfo);
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download:
            case R.id.fl_progress:
                // 根据当前状态来决定下一步操作
                if (mCurrentState == DownloadManager.STATE_UNDO
                        || mCurrentState == DownloadManager.STATE_ERROR
                        || mCurrentState == DownloadManager.STATE_PAUSE) {
                    mDownloadManager.download(getData());// 开始下载
                } else if (mCurrentState == DownloadManager.STATE_DOWNLOADING
                        || mCurrentState == DownloadManager.STATE_WAITING) {
                    mDownloadManager.pause(getData());// 暂停下载
                } else if (mCurrentState == DownloadManager.STATE_SUCCESS) {
                    mDownloadManager.install(getData());// 开始安装
                }

                break;

            default:
                break;
        }
    }
}
