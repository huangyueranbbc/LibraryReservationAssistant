package university.huangyueran.polytechnic.com.libraryreservationassistant.http.protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import university.huangyueran.polytechnic.com.libraryreservationassistant.http.HttpHelper;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.IOUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.StringUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

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
        String result = getCache(index);
        if (StringUtils.isEmpty(result)) {
            // 如果没有缓存,获取缓存失效 则访问网络读取数据
            // 请求服务器
            result = getDataFromServer(index);
        }

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
        HttpHelper.HttpResult httpResult = HttpHelper.get(HttpHelper.URL + getKey() + "?index=" + index + getParams());

        if (httpResult != null) {
            String result = httpResult.getString();
            System.out.println("访问结果:" + result);
            // 写缓存
            if (!StringUtils.isEmpty(result)) {
                setCache(index, result);
            }
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


    // 写缓存
    // 以url为key, 以json为value
    public void setCache(int index, String json) {
        // 以url为文件名, 以json为文件内容,保存在本地
        File cacheDir = UIUtils.getContext().getCacheDir();// 本应用的缓存文件夹
        // 生成缓存文件
        File cacheFile = new File(cacheDir, getKey() + "?index=" + index
                + getParams());

        FileWriter writer = null;
        try {
            writer = new FileWriter(cacheFile);
            // 缓存失效的截止时间
            long deadline = System.currentTimeMillis() + 30 * 60 * 1000;// 半个小时有效期
            writer.write(deadline + "\n");// 在第一行写入缓存时间, 换行
            writer.write(json);// 写入json
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(writer);
        }
    }

    // 读缓存
    public String getCache(int index) {
        // 以url为文件名, 以json为文件内容,保存在本地
        File cacheDir = UIUtils.getContext().getCacheDir();// 本应用的缓存文件夹
        // 生成缓存文件
        File cacheFile = new File(cacheDir, getKey() + "?index=" + index
                + getParams());

        // 判断缓存是否存在
        if (cacheFile.exists()) {
            // 判断缓存是否有效
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(cacheFile));
                String deadline = reader.readLine();// 读取第一行的有效期
                long deadtime = Long.parseLong(deadline);

                if (System.currentTimeMillis() < deadtime) {// 当前时间小于截止时间,
                    // 说明缓存有效
                    // 缓存有效
                    StringBuffer sb = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    return sb.toString();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(reader);
            }

        }

        return null;
    }

    /**
     * 解析数据
     *
     * @param result
     */
    public abstract T parseData(String result);

}
