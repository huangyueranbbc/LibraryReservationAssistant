package university.huangyueran.polytechnic.com.libraryreservationassistant.http.protocol;

import android.util.Log;

import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.http.HttpHelper;

/**
 * Created by huangyueran on 2017/1/15.
 * 访问网络基本协议
 */
public abstract class BaseProtocol<T> {

    /**
     * 获取数据
     *
     * @param index 从哪个位置开始获取数据，实现分页
     */
    public T getData(Integer index) {
        // 先判断是否有缓存
        String result = getDataFromServer(index);

        // 解析数据
        if (result != null) {
            T data = parseData(result);
            return data;
        }

        return null;
    }

    /**
     * 从网络获取数据
     *
     * @param index 从哪个位置开始获取数据，实现分页
     */
    public String getDataFromServer(Integer index) {
        HttpHelper.HttpResult httpResult = HttpHelper.get(GlobalValue.BASE_URL + getKey() + "?page=" + index + 1 + getParams());
        Log.i("urls", "请求的URL: " + GlobalValue.BASE_URL + getKey() + "?page=" + index + getParams());

        if (httpResult != null) {
            String result = httpResult.getString();
            System.out.println("访问结果:" + result);
            return result;
        }
        return null;
    }

    /**
     * 获取网络链接关键url，子类必须实现
     *
     * @return
     */
    public abstract String getKey();

    /**
     * 获取网络链接参数，子类必须实现
     *
     * @return
     */
    public abstract String getParams();


    /**
     * 解析数据
     *
     * @param result
     */
    public abstract T parseData(String result);

}
