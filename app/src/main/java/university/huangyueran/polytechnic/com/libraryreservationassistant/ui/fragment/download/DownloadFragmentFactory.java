package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment.download;

import java.util.HashMap;

/**
 * Created by huangyueran on 2017/1/13.
 * 生产Fragment的工厂
 */
public class DownloadFragmentFactory {

    private static HashMap<Integer, BaseDownloadFragment> mFragmentMap = new HashMap<Integer, BaseDownloadFragment>();

    public static BaseDownloadFragment createFragment(int pos) {

        BaseDownloadFragment fragment = mFragmentMap.get(pos);

        if (fragment == null) {
            switch (pos) {
                case 0:
                    fragment = new HomeFragment();
                    break;
                case 1:
                    fragment = new BFragment();
                    break;
                case 2:
                    fragment = new BFragment();
                    break;
                default:
                    break;
            }
        }


        mFragmentMap.put(pos, fragment); // 保存在集合中
        return fragment;
    }

}
