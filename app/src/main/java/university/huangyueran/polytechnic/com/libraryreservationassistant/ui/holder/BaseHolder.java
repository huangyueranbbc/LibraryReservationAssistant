package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.holder;

import android.view.View;

/**
 * Created by huangyueran on 2017/1/15.
 */
public abstract class BaseHolder<T> {

    private View mRootView; // 一个item的根布局
    private T data;

    public BaseHolder() {
        mRootView = initView();
        mRootView.setTag(this);
    }

    /**
     * 初始化布局
     * 1.加载布局文件
     * 2.初始化控件 findViewById
     *
     * @return
     */
    public abstract View initView();

    /**
     * 获取数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置数据
     */
    public void setData(T data) {
        this.data = data;
        refreshView(data);
    }

    /**
     * 4.根据数据刷新界面
     *
     * @param data
     */
    public abstract void refreshView(T data);

    /**
     * 返回Item的布局对象
     *
     * @return
     */
    public View getRootView() {
        return mRootView;
    }

}
