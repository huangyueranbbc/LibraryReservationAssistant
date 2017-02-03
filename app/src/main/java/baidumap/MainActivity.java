package baidumap;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapapi.utils.SpatialRelationUtil;

import java.util.ArrayList;
import java.util.List;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;

public class MainActivity extends AppCompatActivity {
    //http://api.map.baidu.com/geocoder/v2/?ak=xSoiv2RBSr1GNbC8BP4GHKefPiF5NW1x&mcode=08:57:B1:39:DE:60:01:32:F8:0A:10:DD:CE:C0:4E:EC:DC:A5:FD:AF;com.manager.oa.huangyueran.baidumap&callback=renderReverse&location=30.20395,115.082496&output=json&pois=1

    private TextView locationInfoTextView = null;
    private Button startButton = null;
    private LocationClient mLocClient = null;
    private static final int UPDATE_TIME = 5000;
    private static int LOCATION_COUTNS = 0;

    public static int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 1;
    private static final String TAG = "map";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.my_baidu_info_localtion);
        locationInfoTextView = (TextView) this.findViewById(R.id.tv_loc_info);
        startButton = (Button) this.findViewById(R.id.btn_start);

        // 6.0中添加权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<String>();
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.CHANGE_WIFI_STATE);

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
            }
        } else {//小于6.0

        }

        mLocClient = new LocationClient(this);

        //设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setProdName("LocationDemo"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(1000); // 设置定位时间间隔 1秒
        mLocClient.setLocOption(option);

        //注册位置监听器
        mLocClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                SpatialRelationUtil spatialRelationUtil = new SpatialRelationUtil();
                // 当前坐标
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                // LatLng latLng = new LatLng( 115.95845796638, 28.696117043877); // 失败测试
                double tsgJingdu = 115.082472; // 图书馆经度
                double tsgWeidu = 30.203991; // 图书馆纬度
                double radius = 50.000000; // 图书馆半径
//                double realWDRadius = tsgWeidu + (radius / 23.6) / 60.00 / 60.00; // 地图上真实半径差 根据距离算出纬度差
//                double realJDRadius = tsgJingdu + (radius / 30.9) / 60.00 / 60.00; // 地图上真实半径差 根据距离算出经度差
                LatLng l1 = new LatLng(tsgWeidu, tsgJingdu); // 图书馆坐标
                LatLng l2 = new LatLng(location.getLatitude(), location.getLongitude()); // 用户坐标
                double distance = DistanceUtil.getDistance(l1, l2); // 用户和图书馆距离

                // TODO 静态数据判断
                List<LatLng> latlngList = new ArrayList<LatLng>();
                double wuchaValue = 0.00001;
                LatLng latLngzs = new LatLng(location.getLatitude() + wuchaValue, location.getLongitude() - wuchaValue); // 左上
                LatLng latLngzx = new LatLng(location.getLatitude() - wuchaValue, location.getLongitude() - wuchaValue); // 左下
                LatLng latLngys = new LatLng(location.getLatitude() + wuchaValue, location.getLongitude() + wuchaValue); // 右上
                LatLng latLngyx = new LatLng(location.getLatitude() - wuchaValue, location.getLongitude() + wuchaValue); // 右下
                latlngList.add(latLngzs);
                latlngList.add(latLngzx);
                latlngList.add(latLngys);
                latlngList.add(latLngyx);
                boolean isInFlag = spatialRelationUtil.isPolygonContainsPoint(latlngList, latLng);
                if (location == null) {
                    return;
                }
                StringBuffer sb = new StringBuffer(256);
                sb.append("Time : ");
                sb.append(location.getTime());
                sb.append("\nError code : ");
                sb.append(location.getLocType());
                sb.append("\n纬度 : ");
                sb.append(location.getLatitude());
                sb.append("\n经度 : ");
                sb.append(location.getLongitude());
                sb.append("\nRadius : ");
                sb.append(location.getRadius());
                sb.append("\n是否在区域内 : " + isInFlag);
                sb.append("\n两点距离 : " + distance);
                sb.append("\n用户是否在图书馆半径内 : " + (radius >= distance));
                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    sb.append("\nSpeed : ");
                    sb.append(location.getSpeed());
                    sb.append("\nSatellite : ");
                    sb.append(location.getSatelliteNumber());
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    sb.append("\nAddress : ");
                    sb.append(location.getAddrStr());
                }
                LOCATION_COUTNS++;
                sb.append("\n检查位置更新次数：");
                sb.append(String.valueOf(LOCATION_COUTNS));
                locationInfoTextView.setText(sb.toString());
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocClient == null) {
                    return;
                }
                if (mLocClient.isStarted()) {
                    startButton.setText("Start");
                    mLocClient.stop();
                } else {
                    startButton.setText("Stop");
                    mLocClient.start();
                    /*
                     *当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。
                     *调用requestLocation( )后，每隔设定的时间，定位SDK就会进行一次定位。
                     *如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
                     *返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
                     *定时定位时，调用一次requestLocation，会定时监听到定位结果。
                     */
                    mLocClient.requestLocation();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocClient != null && mLocClient.isStarted()) {
            mLocClient.stop();
            mLocClient = null;
        }
    }

    @Override
    protected void onResume() {
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        super.onResume();
    }

    @Override
    protected void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        super.onPause();
    }
}
