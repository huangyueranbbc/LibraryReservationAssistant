package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.view.LoadingPage;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;


/**
 * Created by huangyueran on 2017/1/13.
 */
public abstract class MyBaseFragment extends Fragment {

    private LoadingPage mLoadingPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TextView textView = new TextView(UIUtils.getContext());
        // textView.setText(getClass().getSimpleName());
        // textView.setTextColor(Color.BLUE);
        mLoadingPage = new LoadingPage(UIUtils.getContext()) {
            @Override
            public View onCreateSuccessView() {
                return MyBaseFragment.this.onCreateSuccessView();
            }

            @Override
            public ResultState onLoad() {
                return MyBaseFragment.this.onLoad();
            }
        };
        return mLoadingPage;
    }

    /**
     * 加载成功的布局 必须由子类实现
     *
     * @return
     */
    public abstract View onCreateSuccessView();

    /**
     * 加载网络数据 必须由子类实现
     *
     * @return
     */
    public abstract LoadingPage.ResultState onLoad();

    /**
     * 开始加载数据
     */
    public void loadData() {
        if (mLoadingPage != null) {
            mLoadingPage.loadData();
        }
    }

    /**
     * 校验结果数据是否正确，并返回状态值
     *
     * @param o
     * @return
     */
    public LoadingPage.ResultState check(Object o) {
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
}
