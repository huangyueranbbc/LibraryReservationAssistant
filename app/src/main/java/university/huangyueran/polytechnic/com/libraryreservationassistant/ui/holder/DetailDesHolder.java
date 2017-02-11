package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.holder;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbFileShare;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

/**
 * Created by huangyueran on 2017/1/20.
 * 详情页--应用描述
 */
public class DetailDesHolder extends BaseHolder<TbFileShare> {

    private TextView tvDes;
    private TextView tvAuthor;
    private ImageView ivArrow;
    private LinearLayout.LayoutParams mParams;
    private boolean isOpen = false;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.layout_detail_desinfo);

        tvDes = (TextView) view.findViewById(R.id.tv_detail_des);
        tvAuthor = (TextView) view.findViewById(R.id.tv_detail_author);
        ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);

        RelativeLayout rlToggle = (RelativeLayout) view.findViewById(R.id.rl_detail_toggle);
        rlToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        return view;
    }

    @Override
    public void refreshView(TbFileShare data) {
        tvDes.setText(data.getFileDesc());
        tvAuthor.setText("上传者: " + data.getAuthorName());

        // 放在消息队列中运行, 解决当只有三行描述时也是7行高度的bug
        tvDes.post(new Runnable() {

            @Override
            public void run() {
                //  设置默认展示7行高度
                int shortHeight = getShortHeight();
                mParams = (LinearLayout.LayoutParams) tvDes.getLayoutParams();
                mParams.height = shortHeight;

                tvDes.setLayoutParams(mParams);
            }
        });

    }

    /**
     * 打开/关闭详情介绍
     */
    private void toggle() {
        int shortHeight = getShortHeight();
        int longHeight = getLongHeight();

        ValueAnimator animator = null;
        if (isOpen) {
            // 关闭
            isOpen = false;
            if (longHeight > shortHeight) { // 只有描述信息大于7行,才启动动画
                animator = ValueAnimator.ofInt(longHeight, shortHeight);
            }

        } else {
            // 打开
            isOpen = true;
            if (longHeight > shortHeight) { // 只有描述信息大于7行,才启动动画
                animator = ValueAnimator.ofInt(shortHeight, longHeight);
            }
        }
        if (animator != null) {
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer height = (Integer) animation.getAnimatedValue();

                    mParams.height = height;
                    tvDes.setLayoutParams(mParams);
                }
            });

            animator.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    // ScrollView要滑动到最底部
                    final ScrollView scrollView = getScrollView();

                    // 为了运行更加安全和稳定, 可以讲滑动到底部方法放在消息队列中执行
                    scrollView.post(new Runnable() {

                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);// 滚动到底部
                        }
                    });

                    if (isOpen) {
                        ivArrow.setImageResource(R.drawable.arrow_up);
                    } else {
                        ivArrow.setImageResource(R.drawable.arrow_down);
                    }

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }
            });

            // 启动动画
            animator.setDuration(200);
            animator.start();
        }

    }

    /**
     * 获取7行textview的高度
     */
    private int getShortHeight() {
        // 模拟一个textview,设置最大行数为7Hang,计算该虚拟textview的高度,从而知道tvDes在展示7行时该多高
        int width = tvDes.getMeasuredWidth(); // 宽度
        TextView textView = new TextView(UIUtils.getContext());
        textView.setText(getData().getFileDesc()); // 设置文字
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14); // 文字大小一致
        textView.setMaxLines(8); // 最大行数7行

        //EXACTLY = match_parent 宽不变,确定值。
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        //AT_MOST = wrap_content 包裹内容
        // 参1表示尺寸最大值,暂写2000, 也可以是屏幕高度
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.AT_MOST);

        // 开始测量高度
        textView.measure(widthMeasureSpec, heightMeasureSpec);

        return textView.getMeasuredHeight(); // 返回测量后的高度
    }

    /**
     * 获取完整textview的高度
     */
    private int getLongHeight() {
        // 模拟一个textview,设置最大行数为7行, 计算该虚拟textview的高度, 从而知道tvDes在展示7行时应该多高
        int width = tvDes.getMeasuredWidth();// 宽度

        TextView view = new TextView(UIUtils.getContext());
        view.setText(getData().getFileDesc());// 设置文字
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);// 文字大小一致
        // view.setMaxLines(7);// 最大行数为7行

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width,
                View.MeasureSpec.EXACTLY);// 宽不变, 确定值, match_parent
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(2000,
                View.MeasureSpec.AT_MOST);// 高度包裹内容, wrap_content;当包裹内容时,
        // 参1表示尺寸最大值,暂写2000, 也可以是屏幕高度

        // 开始测量
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight();// 返回测量后的高度
    }

    // 获取ScrollView, 一层一层往上找,
    // 知道找到ScrollView后才返回;注意:一定要保证父控件或祖宗控件有ScrollView,否则死循环
    private ScrollView getScrollView() {
        ViewParent parent = tvDes.getParent();

        while (!(parent instanceof ScrollView)) {
            parent = parent.getParent();
        }

        return (ScrollView) parent;
    }
}
