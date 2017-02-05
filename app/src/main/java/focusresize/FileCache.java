package focusresize;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * 文件缓存 在SD卡上创建一个目录 存放图片文件
 */
public class FileCache {
    private File cacheDir;
    private static final String TAG = "lazy";

    public FileCache(Context context) {

        //Find the dir to save cached images
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(), "MyLazyLisy");
        } else {
            cacheDir = context.getCacheDir();
        }
        if (!cacheDir.exists()) {
            boolean mkdirs = cacheDir.mkdirs();
        }
    }

    public File getFile(String url) throws UnsupportedEncodingException {
        String filename = String.valueOf(url.hashCode()); //文件名 保证不冲突 不重复
//        filename = URLEncoder.encode(url, "UTF-8");
        File f = new File(cacheDir, filename); //创建一个文件
        return f;
    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }
}
