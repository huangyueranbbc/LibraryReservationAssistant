package university.huangyueran.polytechnic.com.libraryreservationassistant.http.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbFileShare;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.TimestampTypeAdapter;

/**
 * 首页网络数据解析
 * Created by huangyueran on 2017/1/15.
 */
public class HomeProtocol extends BaseProtocol<ArrayList<TbFileShare>> {


    private ArrayList<String> pictures; // TODO 轮播数据 没有 后期删除

    @Override
    public String getKey() {
        return "/fileshare/homepage";
    }

    @Override
    public String getParams() {
        return ""; //如果没有参数，就传递空字符串
    }

    @Override
    public ArrayList<TbFileShare> parseData(String result) {
        //Gson,JsonObject
        try {
            // TODO 解析应用列表数据
            Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            ArrayList<TbFileShare> fileShares = gson.fromJson(result, new TypeToken<List<TbFileShare>>() {
            }.getType());

//            JSONArray jsonArray = jsonObject.getJSONArray("list");
//            ArrayList<TbFileShare> appInfoList = new ArrayList<TbFileShare>();
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jo1 = jsonArray.getJSONObject(i);
//
//                TbFileShare info = new TbFileShare();
//                info.des = jo1.getString("des");
//                info.downloadUrl = jo1.getString("downloadUrl");
//                info.iconUrl = jo1.getString("iconUrl");
//                info.id = jo1.getString("id");
//                info.name = jo1.getString("name");
//                info.packageName = jo1.getString("packageName");
//                info.size = jo1.getLong("size");
//                info.stars = (float) jo1.getDouble("stars");
//
//                appInfoList.add(info);
//            }

            // 初始化轮播条的数据
            pictures = new ArrayList<String>();
            for (int i = 0; i < fileShares.size(); i++) {
                pictures.add("http://img.mukewang.com/5667d2e3000100f304720609-200-200.jpg");
            }

            return fileShares;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 返回轮播条图片数据
     *
     * @return
     */
    public ArrayList<String> getPictureList() {
        return pictures;
    }
}
