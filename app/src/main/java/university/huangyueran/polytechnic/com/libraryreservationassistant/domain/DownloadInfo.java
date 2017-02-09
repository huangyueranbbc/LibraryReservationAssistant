package university.huangyueran.polytechnic.com.libraryreservationassistant.domain;

import android.os.Environment;
import android.util.Log;

import java.io.File;

import university.huangyueran.polytechnic.com.libraryreservationassistant.manager.DownloadManager;

/**
 * 下载对象
 * <p/>
 * 注意: 一定要有读写sdcard的权限!!!!
 * <p/>
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 */
public class DownloadInfo {

    public static final String GOOGLE_MARKET = "LIBRARY";// sdcard根目录文件夹名称
    public static final String DONWLOAD = "download";// 子文件夹名称, 存放下载的文件
    public String id;
    public String name;
    public String downloadUrl;
    public long size;
    public String packageName;
    public long currentPos;// 当前下载位置
    public int currentState;// 当前下载状态
    public String path;// 下载到本地文件的路径

    // 拷贝对象, 从AppInfo中拷贝出一个DownloadInfo
    public static DownloadInfo copy(TbFileShare info) {
        DownloadInfo downloadInfo = new DownloadInfo();

        // TODO
        downloadInfo.id = String.valueOf(info.getId());
        downloadInfo.name = info.getTitle();
        downloadInfo.downloadUrl = info.getDownloadUrl();
//        downloadInfo.packageName = info.packageName; // 没有包名
        downloadInfo.size = info.getSize();

        downloadInfo.currentPos = 0;
        downloadInfo.currentState = DownloadManager.STATE_UNDO;// 默认状态是未下载
        downloadInfo.path = downloadInfo.getFilePath(info);

        return downloadInfo;
    }

    // 获取下载进度(0-1)
    public float getProgress() {
        if (size == 0) {
            return 0;
        }

        float progress = currentPos / (float) size;
        return progress;
    }

    // 获取文件下载路径
    public String getFilePath(TbFileShare info) {
        StringBuffer sb = new StringBuffer();
        String sdcard = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        sb.append(sdcard);
        // sb.append("/");
        sb.append(File.separator);
        sb.append(GOOGLE_MARKET);
        sb.append(File.separator);
        sb.append(DONWLOAD);

        if (createDir(sb.toString())) {
            // 文件夹存在或者已经创建完成
            Log.i("downloadURL", "getFilePath: " + sb.toString() + File.separator + info.getRealName());
            return sb.toString() + File.separator + name + ".apk";// 返回文件路径
        }

        return null;
    }

    private boolean createDir(String dir) {
        File dirFile = new File(dir);

        // 文件夹不存在或者不是一个文件夹
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return dirFile.mkdirs();
        }

        return true;// 文件夹存在
    }

}
