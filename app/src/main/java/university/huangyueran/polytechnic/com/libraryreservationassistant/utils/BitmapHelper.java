package university.huangyueran.polytechnic.com.libraryreservationassistant.utils;

import com.lidroid.xutils.BitmapUtils;

public class BitmapHelper {

    private static BitmapUtils mBitmapUtils = null;

    // 单例, 懒汉模式
    public static BitmapUtils getBitmapUtils() {
        if (mBitmapUtils == null) {
            synchronized (BitmapHelper.class) {
                if (mBitmapUtils == null) {
                    mBitmapUtils = new BitmapUtils(UIUtils.getContext());
                }
            }
        }

        return mBitmapUtils;
    }
}
