package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.manager.ThreadManager;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;


/**
 * Created by huangyueran on 2017/1/13.
 * 根据当前状态来显示不同页面的自定义帧布局控件
 * 加载页面 - 正在加载 - 加载失败 - 数据为空 - 访问成功
 */
public abstract class LoadingPage extends FrameLayout {

    private static final int STATE_UNLOAD = 1;// 未加载
    private static final int STATE_LOADING = 2;// 正在加载
    private static final int STATE_LOAD_ERROR = 3;// 加载失败
    private static final int STATE_LOAD_EMPTY = 4;// 数据为空
    private static final int STATE_LOAD_SUCCESS = 5;// 访问成功

    private int mCurrentState = STATE_UNLOAD; // 当前状态 默认未加载
    private View mLoadingPage;
    private View mErrorPage;
    private View mEmptyPage;
    private View mSuccessPage;

    public LoadingPage(Context context) {
        super(context);
        initView();
    }

    public LoadingPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

    }

    public LoadingPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        // 初始化加载中布局
        if (mLoadingPage == null) {
            mLoadingPage = UIUtils.inflate(R.layout.page_loading);
            addView(mLoadingPage); //将加载中的布局添加给帧布局
        }

        // 初始化加载失败布局
        if (mErrorPage == null) {
            mErrorPage = UIUtils.inflate(R.layout.page_error);
            // 点击重试按钮事件
            Button btnRetry = (Button) mErrorPage.findViewById(R.id.btn_retry);
            btnRetry.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 重新加载数据
                    loadData();
                }
            });
            addView(mErrorPage);
        }

        // 初始化加载为空的布局
        if (mEmptyPage == null) {
            mEmptyPage = UIUtils.inflate(R.layout.page_empty);
            addView(mEmptyPage);
        }

        showRightPage();
    }

    /**
     * 根据当前决定显示和隐藏哪个布局
     */
    private void showRightPage() {
        if (mLoadingPage != null) {
            mLoadingPage.setVisibility((mCurrentState == STATE_LOADING || mCurrentState == STATE_UNLOAD) ? View.VISIBLE : View.GONE);
        }

        if (mEmptyPage != null) {
            mEmptyPage.setVisibility(mCurrentState == STATE_LOAD_EMPTY ? View.VISIBLE : View.GONE);
        }

        if (mErrorPage != null) {
            mErrorPage.setVisibility(mCurrentState == STATE_LOAD_ERROR ? View.VISIBLE : View.GONE);
        }

        // 当成功布局为空并且当前状态为成功加载时 才创建成功布局
        if (mSuccessPage == null && mCurrentState == STATE_LOAD_SUCCESS) {
            mSuccessPage = onCreateSuccessView();
            if (mSuccessPage != null) {
                Log.i("aaa", "showRightPage: 4");
                addView(mSuccessPage);
            }
        }

        if (mSuccessPage != null) {
            mSuccessPage.setVisibility(mCurrentState == STATE_LOAD_SUCCESS ? View.VISIBLE : View.GONE);
        }

    }

    /**
     * 开始加载数据
     */
    public void loadData() {

        if (mCurrentState != STATE_LOADING) { // 如果当前没有加载，才开始加载数据
            mCurrentState = STATE_LOADING;

//            new Thread() {
//                @Override
//                public void run() {
//                    final ResultState resultState = onLoad();
//                    //在主线程运行
//                    UIUtils.runOnUIThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (resultState != null) {
//                                mCurrentState = resultState.getState(); // 网络加载结束后，更新网络状态
//                                //根据最新的状态来刷新页面
//                                showRightPage();
//                            }
//                        }
//                    });
//
//                }
//            }.start();

            ThreadManager.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    final ResultState resultState = onLoad();
                    //在主线程运行
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultState != null) {
                                mCurrentState = resultState.getState(); // 网络加载结束后，更新网络状态
                                //根据最新的状态来刷新页面
                                showRightPage();
                            }
                        }
                    });
                }
            });

        }

    }

    /**
     * 加载成功后显示的布局 必须由调用者来实现
     *
     * @return
     */
    public abstract View onCreateSuccessView();

    /**
     * 加载网络数据，返回值来表示请求网络结束后的状态
     */
    public abstract ResultState onLoad();


    public enum ResultState {
        STATE_SUCCESS(STATE_LOAD_SUCCESS), STATE_EMPTY(STATE_LOAD_EMPTY), STATE_ERROR(
                STATE_LOAD_ERROR);

        private int state;

        private ResultState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }

}
