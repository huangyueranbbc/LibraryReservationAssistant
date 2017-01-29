package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

public class AFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(UIUtils.getContext());
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER);
        textView.setText("测试页面\n\n");
        textView.setBackgroundColor(0xFFececec);
        return textView;
    }
}
