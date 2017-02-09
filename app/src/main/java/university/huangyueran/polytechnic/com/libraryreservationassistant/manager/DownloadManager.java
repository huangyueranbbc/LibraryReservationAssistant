package university.huangyueran.polytechnic.com.libraryreservationassistant.manager;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.DownloadInfo;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbFileShare;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.http.HttpHelper;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.IOUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

/**
 * 下载管理器
 * - 未下载 - 等待下载 - 正在下载 - 暂停下载 - 下载失败 - 下载成功
 * DownloadManager: 被观察者, 有责任通知所有观察者状态和进度发生变化
 * Created by huangyueran on 2017/1/22.
 */
public class DownloadManager {

    public static final int STATE_UNDO = 1; // 未下载
    public static final int STATE_WAITING = 2; // 等待下载
    public static final int STATE_DOWNLOADING = 3; // 正在下载
    public static final int STATE_PAUSE = 4; //暂停下载
    public static final int STATE_ERROR = 5;//下载失败
    public static final int STATE_SUCCESS = 6;// 下载成功
    private static final String TAG = "DownloadManager";
    private static DownloadManager mDownloadManager = new DownloadManager();

    private ArrayList<DownloadObserver> mObservers = new ArrayList<DownloadObserver>(); // 观察者集合

    // 下载对象的集合, 使用线程安全的HashMap
    // private HashMap<String, DownloadInfo> mDownloadInfoMap = newHashMap<String, DownloadInfo>();
    private ConcurrentHashMap<String, DownloadInfo> mDownloadInfoMap = new ConcurrentHashMap<String, DownloadInfo>();

    // 下载任务的集合
    private ConcurrentHashMap<String, DownloadTask> mDownloadTaskMap = new ConcurrentHashMap<String, DownloadTask>();

    private DownloadManager() {

    }

    public static DownloadManager getInstance() {
        return mDownloadManager;
    }

    // 注册观察者
    public synchronized void registerObserver(DownloadObserver observer) {
        if (observer != null && !mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    // 注销观察者
    public synchronized void unregisterObserver(DownloadObserver observer) {
        if (observer != null && mObservers.contains(observer)) {
            mObservers.remove(observer);
        }
    }

    // 通知下载状态发生变化
    public synchronized void notifyDownloadStateChanged(DownloadInfo downloadInfo) {
        for (DownloadObserver observer : mObservers) {
            observer.onDownloadStateChanged(downloadInfo);
        }
    }

    // 通知下载进度发生变化
    public synchronized void notifyDownloadProgressChanged(DownloadInfo downloadInfo) {
        for (DownloadObserver observer : mObservers)
            observer.onDownloadProgressChanged(downloadInfo);
    }

    // 开始下载
    public synchronized void download(TbFileShare share) {
        // 如果对象是第一次下载，需要创建一个新的DownloadInfo对象,从头下载。
        // 如果之前已经下载过,需要接着下载，断点续传
        DownloadInfo downloadInfo = mDownloadInfoMap.get(share.getId());
        if (downloadInfo == null) {
            downloadInfo = DownloadInfo.copy(share); // 根据appinfo拷贝一个下载对象
        }

        downloadInfo.currentState = STATE_WAITING; // 状态切换到等待下载
        // 通知所有的观察者,下载状态发生变化
        notifyDownloadStateChanged(downloadInfo);
        System.out.println(downloadInfo.name + "等待下载ing......");

        //将下载对象放入集合中
        mDownloadInfoMap.put(downloadInfo.id, downloadInfo);

        // 开始下载,初始化下载任务，并放入线程池中运行
        DownloadTask task = new DownloadTask(downloadInfo);
        ThreadManager.getThreadPool().execute(task);

        // 将下载任务放入任务集合中
        mDownloadTaskMap.put(downloadInfo.id, task);
    }

    // 下载暂停
    public synchronized void pause(TbFileShare share) {
        DownloadInfo downloadInfo = mDownloadInfoMap.get(share.getId().toString()); // 取出下载对象
        if (downloadInfo != null) {
            // 只有在正在下载和等待下载时可以暂停
            if (downloadInfo.currentState == STATE_DOWNLOADING || downloadInfo.currentState == STATE_WAITING) {
                // 下载状态切换到暂停
                downloadInfo.currentState = STATE_PAUSE;
                notifyDownloadStateChanged(downloadInfo); // 通知观察者
                DownloadTask task = mDownloadTaskMap.get(downloadInfo.id);
                if (task != null) {
                    // 如果任务还没开始,正在等待,则可以通过此方法移除
                    // 如果任务已经开始运行,则需要在run方法里面进行中断
                    ThreadManager.getThreadPool().cancel(task); // 移除下载任务
                }
            }
        }
    }

    // 安装
    public synchronized void install(TbFileShare share) {
        DownloadInfo downloadInfo = mDownloadInfoMap.get(share.getId().toString());
        if (downloadInfo != null) {
            // 跳到系统的安装页面进行安装
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + downloadInfo.path),
                    "application/vnd.android.package-archive");
            UIUtils.getContext().startActivity(intent);
        }
    }

    // 根据应用信息返回下载对象
    public DownloadInfo getDownloadInfo(TbFileShare share) {
        return mDownloadInfoMap.get(share.getId().toString());
    }


    /**
     * 声明观察者接口
     */
    public interface DownloadObserver {
        // 下载状态发生变化
        void onDownloadStateChanged(DownloadInfo downloadInfo);

        // 下载进度发生变化
        void onDownloadProgressChanged(DownloadInfo downloadInfo);
    }

    /**
     * 下载runnable 执行对象
     */
    class DownloadTask implements Runnable {

        private DownloadInfo downloadInfo;

        public DownloadTask(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public synchronized void run() {
            System.out.println(downloadInfo.name + "开始下载!");

            //切换状态为正在下载
            downloadInfo.currentState = STATE_DOWNLOADING;
            notifyDownloadStateChanged(downloadInfo);

            File file = new File(downloadInfo.path);

            HttpHelper.HttpResult httpResult;
            if (!file.exists() || file.length() != downloadInfo.currentPos || downloadInfo.currentPos == 0) { // 文件长度不对||当前进度为0
                // 不存在 从头开始下载
                //删除无效文件
                file.delete();  //不存在也可以删除,不报错
                downloadInfo.currentPos = 0; // 当前下载位置置为0

                // TODO 从头开始下载//
                httpResult = HttpHelper.download(GlobalValue.BASE_URL + "/fileshare/download?" + downloadInfo.downloadUrl);
                //httpResult = HttpHelper.download("http://gdown.baidu.com/data/wisegame/fc328fa3a33efe57/QQ_482.apk");
                //http://gdown.baidu.com/data/wisegame/fc328fa3a33efe57/QQ_482.apk
                //http://192.168.1.102:8080/fileshare/download?fileshare_id=2&range=0
                Log.i("downloadURL", "downloadURL: " + HttpHelper.URL + "download?name=" + downloadInfo.downloadUrl);
            } else {
                // 断点续传,继续下载
                // 断点续传
                // range 表示请求服务器从文件的哪个位置开始返回数据
                httpResult = HttpHelper.download(HttpHelper.URL + "/fileshare/download?" + downloadInfo.downloadUrl + "&range=" + file.length());
            }

            // TODO 下载逻辑处理
            if (httpResult != null && httpResult.getInputStream() != null) {
                InputStream in = httpResult.getInputStream();
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file, true); //参数二:在原有文件上追加数据
                    int len = 0;
                    byte[] buffer = new byte[1024];
                    // 只有状态是正在下载,才继续轮询下载,解决下载过程中暂停的问题
                    while ((len = in.read(buffer)) != -1 && downloadInfo.currentState == STATE_DOWNLOADING) {// 读流
                        out.write(buffer, 0, len);
                        out.flush(); // 把剩余数据刷入本地
                        // 更新下载进度
                        downloadInfo.currentPos += len;
                        // 通知观察者下载进度变化
                        notifyDownloadProgressChanged(downloadInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 关闭资源
                    IOUtils.close(in);
                    IOUtils.close(out);
                }

                Log.i(TAG, "文件下载结束");

                // 文件下载结束
                if (file.length() == downloadInfo.size) {
                    // 文件完整, 表示下载成功
                    downloadInfo.currentState = STATE_SUCCESS;
                    notifyDownloadStateChanged(downloadInfo);
                } else if (downloadInfo.currentState == STATE_PAUSE) {
                    // 中途暂停
                    notifyDownloadStateChanged(downloadInfo);
                } else {
                    Log.i(TAG, "下载失败: " + file.length() + ":" + downloadInfo.size);
                    // 下载失败
                    file.delete();// 删除无效文件
                    downloadInfo.currentState = STATE_ERROR;
                    downloadInfo.currentPos = 0;
                    notifyDownloadStateChanged(downloadInfo);
                }
            } else {
                // 网络异常
                file.delete();// 删除无效文件
                downloadInfo.currentState = STATE_ERROR; // 状态切换为下载失败
                downloadInfo.currentPos = 0;
                notifyDownloadStateChanged(downloadInfo);
            }

            // 下载文件成功后,从集合中移除下载任务
            mDownloadTaskMap.remove(downloadInfo.id);

        }
    }
}
