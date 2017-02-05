package focusresize;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Administrator on 2016/8/16.
 */
public class MemoryCache {

    private static final String TAG = "MemoryCache";
    private Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true)); // 可线程同步的集合
    private long size = 0;//current allocated size
    private long limit = 1000000;//max memory in bytes

    public MemoryCache() {
        setLimit(Runtime.getRuntime().maxMemory() / 4); //设置缓存大小 limit
    }

    private void setLimit(long new_limit) {
        limit = new_limit;
        Log.i(TAG, "MemoryCache will use up to " + limit / 1024 / 1024 + "MB");
    }

    public void put(String id, Bitmap bitmap) {
        // 存入的都是不重复的
        try {
            if (cache.containsKey(id))
                size -= getSizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            size += getSizeInBytes(bitmap);
            checkSize();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public Bitmap get(String url) {

        try {
            if (!cache.containsKey(url)) {
                // 缓存中没有
                return null;
            } else {
                return cache.get(url);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }

    }


    private void checkSize() {
        Log.i(TAG, "cache size=" + size + " length=" + cache.size());
        if (size > limit) {
            // 使用缓存算法 清理缓存
            Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();//LFU 最近很少被使用(命中率低)
            while (iter.hasNext()) {
                Entry<String, Bitmap> entry = iter.next();
                size -= getSizeInBytes(entry.getValue());
                iter.remove();
                if (size <= limit)
                    break;
            }
            Log.i(TAG, "Clean cache. New size " + cache.size());
        }
    }

    public void clear() {
        try {
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
            cache.clear();
            size = 0;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
