package university.huangyueran.polytechnic.com.libraryreservationassistant.global;

/**
 * Created by huangyueran on 2017/1/31.
 * 公共的配置属性
 */
public class GlobalValue {
    public static String BASE_URL = "http://192.168.1.102:8080";
    //    public static String BASE_URL = "http://192.168.191.1:8080";
    //    public static String BASE_URL = "http://huangyueranbbc.xicp.net";

    public static String SEARCH_REST_URL = "http://192.168.1.102:8082";

    //    http://huangyueranbbc.xicp.net
    public static String RESERVATION_CACHE_INFO = "reservation-info"; // 座位预订缓存记录

    public static String LOGININFO = "LOGININFO"; // 座位预订缓存记录
    public static String TBUSERINFO = "TBUSERINFO"; // 座位预订缓存记录

    public static String SEARCH_HISTORY = "SEARCH_HISTORY"; // 历史搜索缓存
}
