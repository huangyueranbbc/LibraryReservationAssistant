package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import university.huangyueran.polytechnic.com.libraryreservationassistant.manager.ThreadManager;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.holder.BaseHolder;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.holder.MoreHolder;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;


/**
 * 对Adapter的封装
 * Created by huangyueran on 2017/1/15.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {

    // 必须从0开始声明
    private static final int TYPE_MORE = 0; //加载更多布局类型
    private static final int TYPE_NORMAL = 1; // 正常布局类型


    private ArrayList<T> data; //全局的list
    private boolean isLoadMore = false; //标记是否正在加载更多

    public MyBaseAdapter(ArrayList<T> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size() + 1; // 增加加载更多布局的数量
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder holder;
        if (convertView == null) {
            // 1.加载布局文件
            // 2.初始化控件 findViewById
            // 3.打一个标记tag
            // 判断是否是加载更多的类型
            if (getItemViewType(position) == TYPE_MORE) {
                // 加载更多布局对象
                holder = new MoreHolder(hasMore());
            } else {
                holder = getHolder(position); //子类返回具体对象
            }
        } else {
            holder = (BaseHolder) convertView.getTag();
        }

        // 4.刷新数据 设置数据并直接刷新
        if (getItemViewType(position) != TYPE_MORE) {
            holder.setData(getItem(position));
        } else {
            // 加载更多布局
            // 一旦加载更对布局展示出来，就开始加载更多数据
            // 只有在有更多数据的状态下才加载更多
            MoreHolder moreHolder = (MoreHolder) holder;
            if (moreHolder.getData() == MoreHolder.STATE_MORE_MORE) {
                loadMore(moreHolder);
            }
        }

        return holder.getRootView();
    }

    /**
     * 是否由更多 子类重写 改变数据
     * 默认有更多数据
     *
     * @return
     */
    public boolean hasMore() {
        return true;
    }

    /**
     * 返回布局类型的个数
     *
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 2; // 返回两种布局，普通布局+加载更多布局
    }

    /**
     * 返回当前位置应该展示布局的类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == getCount() - 1) {//最后一个布局
            return TYPE_MORE; // 加载更多
        } else {
            return getInnerType(position); // 普通布局
        }
    }

    /**
     * 返回默认类型 子类可以重写此方法修改返回的类型
     */
    public int getInnerType(int position) {
        return TYPE_NORMAL;
    }

    /**
     * 获取当前页面的holder对象 必须子类实现
     *
     * @return
     */
    public abstract BaseHolder<T> getHolder(int position);

    /**
     * 加载更多数据
     */
    public void loadMore(final MoreHolder moreHolder) {

        if (!isLoadMore) {
            isLoadMore = true;

//            new Thread() {
//                @Override
//                public void run() {
//                    final ArrayList<T> moreData = onLoadMore();
//
//                    // 根据返回数据修改UI界面,需要在主线程中运行
//                    UIUtils.runOnUIThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (moreData != null) {
//                                if (moreData.size() < 20) {
//                                    // 如果返回数据数量小于每页数据数量，则没有更多数据，是最后一页
//                                    moreHolder.setData(MoreHolder.STATE_MORE_NONE);
//                                    Toast.makeText(UIUtils.getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    // 有更多数据
//                                    moreHolder.setData(MoreHolder.STATE_MORE_MORE);
//                                }
//
//                                // 将更多数据追加到当前集合中
//                                data.addAll(moreData);
//                                // 刷新界面
//                                MyBaseAdapter.this.notifyDataSetChanged();
//
//                            } else {
//                                // 没有数据,则加载数据失败
//                                moreHolder.setData(MoreHolder.STATE_MORE_ERROR);
//                            }
//                            isLoadMore = false;
//                        }
//                    });
//
//
//                }
//            }.start();

            ThreadManager.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<T> moreData = onLoadMore();

                    // 根据返回数据修改UI界面,需要在主线程中运行
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (moreData != null) {
                                if (moreData.size() < 20) {
                                    // 如果返回数据数量小于每页数据数量，则没有更多数据，是最后一页
                                    moreHolder.setData(MoreHolder.STATE_MORE_NONE);
                                    Toast.makeText(UIUtils.getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                                } else {
                                    // 有更多数据
                                    moreHolder.setData(MoreHolder.STATE_MORE_MORE);
                                }

                                // 将更多数据追加到当前集合中
                                data.addAll(moreData);
                                // 刷新界面
                                MyBaseAdapter.this.notifyDataSetChanged();

                            } else {
                                // 没有数据,则加载数据失败
                                moreHolder.setData(MoreHolder.STATE_MORE_ERROR);
                            }
                            isLoadMore = false;
                        }
                    });
                }
            });

        }

    }

    /**
     * 网络加载更多数据，必须由子类实现
     */
    public abstract ArrayList<T> onLoadMore();

    /**
     * 返回当前集合大小
     *
     * @return
     */
    public int getListSize() {
        return data.size();
    }
}
