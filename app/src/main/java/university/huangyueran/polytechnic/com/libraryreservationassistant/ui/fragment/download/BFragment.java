package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment.download;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment.download.BaseDownloadFragment;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.view.LoadingPage;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

public class BFragment extends BaseDownloadFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(UIUtils.getContext());
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER);
        textView.setText("测试页面\n\n" + new Date());
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundColor(0xFFececec);
        return textView;
    }

    @Override
    public View onCreateSuccessView() {
        return null;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        return null;
    }
}
