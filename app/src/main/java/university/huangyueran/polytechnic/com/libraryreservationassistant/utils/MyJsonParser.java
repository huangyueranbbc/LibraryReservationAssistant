package university.huangyueran.polytechnic.com.libraryreservationassistant.utils;

import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.socks.okhttp.plus.parser.OkBaseJsonParser;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.security.Timestamp;

import okhttp3.Response;

/**
 * Created by huangyueran on 2017/1/31.
 * 自定义 可以转换时间类型适配器
 */
public class MyJsonParser<T> extends OkBaseJsonParser<T> {
    //new GsonBuilder().registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    protected Gson mGson;

    public MyJsonParser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GsonBuilder gsonBuilder = new GsonBuilder()
                    .excludeFieldsWithModifiers(
                            Modifier.FINAL,
                            Modifier.TRANSIENT,
                            Modifier.STATIC);
            // 自定义时间Date类型适配器
            gsonBuilder.registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            mGson = gsonBuilder.create();
        } else {
            mGson = new Gson();
        }
    }

    @Override
    public T parse(Response response) throws IOException {
        String body = response.body().string();
        return mGson.fromJson(body, mType);
    }
}
