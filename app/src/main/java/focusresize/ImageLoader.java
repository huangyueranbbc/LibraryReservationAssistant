package focusresize;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;

/**
 * Created by Administrator on 2016/8/16.
 */
public class ImageLoader {

    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService; //线程池 接口
    Handler handler = new Handler(); // 用于在UI主线程异步显示图片


    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5); //获取一个线程池 线程池中至少有5个活动的线程
    }

    // 当进入Listview时默认显示的图片 真实图片异步进行加载
    final int stub_id = R.drawable.stub;

    public void DisplayImage(String url, ImageView imageView) {
        imageViews.put(imageView, url);

        //先从内存中查找
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            //  如果缓存/内存中有
            imageView.setImageBitmap(bitmap); //显示图片
        } else {
            // 没有才去开启新线程异步加载
            queuePhoto(url, imageView); // 队列加载图片
            imageView.setImageResource(stub_id); //加载过程中 显示默认的图片
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad photo = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(photo)); // 将一个线程任务提交线程池执行。
    }


    private class PhotosLoader implements Runnable {
        private PhotoToLoad photoToLoad;

        public PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            // 判断图片是否错位
            if (imageViewReused(photoToLoad)) {
                return;
            }


            try {
                Bitmap bitmap = getBitmap(photoToLoad.getUrl()); //根据URL获取一个BitMap
                memoryCache.put(photoToLoad.getUrl(), bitmap);// 存入缓存
                if (imageViewReused(photoToLoad)) {
                    return;
                }
                BitmapDisplayer bp = new BitmapDisplayer(bitmap, photoToLoad);
                handler.post(bp); // 将此线程任务发给UI主线程的消息队列中，交给UI主线程执行
            } catch (Throwable e) {
                e.printStackTrace();
            }


        }
    }

    //在UI主线程中显示图片
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
//            Log.i("lazy", "bitmap is null: "+bitmap);
            if (bitmap != null)
                photoToLoad.getImageView().setImageBitmap(bitmap);
            else
                photoToLoad.getImageView().setImageResource(stub_id);

//            Log.i("lazy", "run: "+photoToLoad.getImageView());
        }
    }


    /**
     * 防止图片错位
     *
     * @param photoToLoad
     * @return true=错位
     */
    private boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.getImageView());
        if (tag == null || !tag.equals(photoToLoad.getUrl()))
            return true;
        return false;
    }

    private static final String TAG = "lazy";

    private Bitmap getBitmap(String url) {
        File f = null; // 根据url在目录中获取一个文件(图片)
        try {
            f = fileCache.getFile(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Bitmap b = decodeFile(f); // 将文件解析为图片
        //如果缓存中有,并解析成功

        if (b != null) {
            return b;
        }
        // 否则 从指定的url中下载图片
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setInstanceFollowRedirects(true);
            conn.connect();
            InputStream is = new BufferedInputStream(conn.getInputStream());

            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            Log.i(TAG, "读取完毕! ");
            os.close();
            conn.disconnect();

            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable e) {
            e.printStackTrace();
            if (e instanceof OutOfMemoryError) {
                memoryCache.clear();
            }
            return null;
        }

    }

    /**
     * 把文件解析为图片，并压缩
     *
     * @param f
     * @return
     */
    private Bitmap decodeFile(File f) {
        try {
            //
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            //动态制定图片压缩的值
            final int REQUIRED_SIZE = 600;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //把文件解析为图片,根据动态指定的压缩值，将图片进行压缩
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clear() {
        memoryCache.clear();
        fileCache.clear();

    }

}
