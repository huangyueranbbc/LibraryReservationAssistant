package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.socks.okhttp.plus.OkHttpProxy;
import com.socks.okhttp.plus.parser.OkJsonParser;

import java.io.IOException;

import okhttp3.Response;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.User;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.test.TestUrls;

/**
 * 座位预约Fragment
 * Created by huangyueran on 2017/1/29.
 */
public class ReservationFragment extends BaseFragment  implements TestUrls {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final TextView textView = new TextView(UIUtils.getContext());
        textView.setTextSize(30);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundColor(0xFFececec);

        // 从网络获取数据
//        OkHttpProxy.get()
//                .url("https://raw.githubusercontent.com/ZhaoKaiQiang/OkHttpPlus/master/server/user")
//                .tag(this)
//                .enqueue(new OkCallback<User>(new OkJsonParser<User>() {
//                }) {
//                    @Override
//                    public void onSuccess(int code, User user) {
//                        Log.i("aaa", "onSuccess: ");
//                        textView.setText(user.toString());
//                    }
//
//                    @Override
//                    public void onFailure(Throwable e) {
//                        Log.i("aaa", "error: ");
//                        textView.setText(e.getMessage());
//                    }
//                });

        // 异步
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = OkHttpProxy.get()
                            .url(URL_USER)
                            .tag(this)
                            .execute();

                    final User user = new OkJsonParser<User>() {
                    }.parse(response);

                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(user.toString());
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        return textView;
    }
}
