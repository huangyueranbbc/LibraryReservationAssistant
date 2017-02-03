package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.github.rubensousa.raiflatbutton.RaiflatButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.seatview.seatchoosetest.OnNewSeatClickListener;
import com.seatview.seatchoosetest.model.CH_seatInfo;
import com.seatview.seatchoosetest.model.SeatStatus;
import com.seatview.seatchoosetest.view.SSThumView;
import com.seatview.seatchoosetest.view.SeatView;

import java.security.Timestamp;
import java.util.ArrayList;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.LMSResult;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbLibrary;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbReadroom;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbReservation;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbSeat;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment.SelectReadRoomDialogFragment;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment.SelectReservationDialogFragment;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.CacheUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.TimestampTypeAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;


@SuppressLint("ShowToast")
public class SelectSeatActivity extends BaseActivity implements View.OnClickListener {
    private static final int ROW = 16;
    private static final int column = 16;
    private SeatView mSSView;
    private SSThumView mSSThumView;
    private ArrayList<CH_seatInfo> list_CH_seatInfo = new ArrayList<CH_seatInfo>();
    private TextView chooseSeat;
    private ArrayList<TbSeat> seats;
    private TbReadroom readroom;

    private CH_seatInfo mCurrentSeat;
    private RaiflatButton mBtnSubmitSeat;
    private TbLibrary library;
    private LocationClient mLocClient;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_select_seat);
        // 获取传递的座位数据
        Intent intent = this.getIntent();
        seats = (ArrayList<TbSeat>) intent.getSerializableExtra("seats");
        readroom = (TbReadroom) intent.getSerializableExtra("readroom");
        initLibraryInfo(); // 获取图书馆信息
        initButton();
        init();
    }

    /**
     * 初始化百度定位
     */
    private void initBaiduSDK() {
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
                // 当前坐标
                LatLng l1 = new LatLng(library.getLatitude(), library.getLongitude()); // 图书馆坐标
                LatLng l2 = new LatLng(location.getLatitude(), location.getLongitude()); // 用户坐标
                // 用户和图书馆距离
                distance = DistanceUtil.getDistance(l1, l2);
            }
        });

        mLocClient.start(); // 开始定位
        mLocClient.requestLocation();
    }

    private void initLibraryInfo() {
        // 获取图书馆信息
        final String url = GlobalValue.BASE_URL + "/library/show";
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("id", String.valueOf(readroom.getLibraryId()));

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        library = gson.fromJson(responseInfo.result, new TypeToken<TbLibrary>() {
                        }.getType());
                        initBaiduSDK();
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(SelectSeatActivity.this, "图书馆数据请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initButton() {
        mBtnSubmitSeat = (RaiflatButton) findViewById(R.id.btn_tjzw); // 提交座位
        mBtnSubmitSeat.setOnClickListener(this);
        // 一键选座
    }

    private void init() {
        mSSView = (SeatView) this.findViewById(R.id.mSSView);
        mSSThumView = (SSThumView) this.findViewById(R.id.ss_ssthumview);
        chooseSeat = (TextView) this.findViewById(R.id.textView1);
        SeatiInforData(seats);
        mSSView.setCenterText("湖北理工学院图书馆" + readroom.getRoomName());
        mSSView.init(list_CH_seatInfo, mSSThumView, 10);
        mSSView.setOnSeatClickListener(new OnNewSeatClickListener() {
            @Override
            public boolean unClick(CH_seatInfo seatInfo) {
                int raw = seatInfo.getRaw() + 1;
                int Column = seatInfo.getColumn() + 1;
                chooseSeat.setText("取消:" + raw + "排——" + Column + "座");
                mCurrentSeat = null;
                return false;
            }

            @Override
            public boolean onClick(CH_seatInfo seatInfo) {
                int raw = seatInfo.getRaw() + 1;
                int Column = seatInfo.getColumn() + 1;
                chooseSeat.setText("选中:" + raw + "排——" + Column + "座");
                mCurrentSeat = seatInfo;
                return false;
            }
        });
    }

    private void SeatiInforData(ArrayList<TbSeat> seats) {

        for (TbSeat seat : seats) {
            CH_seatInfo cs = new CH_seatInfo();
            cs.setId(String.valueOf(seat.getId()));
            cs.setPosition(seat.getSeatIndex());
            cs.setRaw(seat.getX());
            cs.setColumn(seat.getY());
            cs.setStatus(seat.getStatus());
            list_CH_seatInfo.add(cs);
        }
    }

    private void SeatiInforData() {
        int index = 0;
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < column; j++) {
                CH_seatInfo cs = new CH_seatInfo();
                cs.setPosition(index);
                cs.setRaw(i);
                cs.setColumn(j);
                cs.setStatus(SeatStatus.CHOOSE_UN);
                list_CH_seatInfo.add(cs);
                index += 1;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_tjzw:
                if (mCurrentSeat != null) {
                    // 判断是否在图书馆内
                    // 在图书馆内
                    if (library != null) {
                        if (distance <= library.getRadius()) { // 如果小于半径,则在图书馆范围内
                            // TODO 假数据 用户ID 后期从缓存中取用户信息
                            final Long user_id = Long.valueOf(1);
                            // 请求网络数据 订座
                            final String url = GlobalValue.BASE_URL + "/reservation/create";
                            RequestParams params = new RequestParams();
                            params.addQueryStringParameter("user_id", String.valueOf(user_id));
                            params.addQueryStringParameter("seat_id", mCurrentSeat.getId());

                            HttpUtils http = new HttpUtils();
                            http.send(HttpRequest.HttpMethod.GET,
                                    url, params,
                                    new RequestCallBack<String>() {
                                        private ProgressDialog dialog = new ProgressDialog(SelectSeatActivity.this);

                                        @Override
                                        public void onSuccess(ResponseInfo<String> responseInfo) {
                                            // 得到图书馆list集合
                                            Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
//                                    LMSResult result = LMSResult.formatToPojo(responseInfo.result, LMSResult.class);
                                            LMSResult result = gson.fromJson(responseInfo.result, new TypeToken<LMSResult>() {
                                            }.getType());

                                            if (result != null && result.getStatus() == 200) {// 创建成功
                                                // 存入缓存
                                                String json = gson.toJson(result.getData());
                                                TbReservation reservation = gson.fromJson(json, TbReservation.class);
                                                CacheUtils.setCache(GlobalValue.RESERVATION_CACHE_INFO + user_id, json, (long) (reservation.getTimeSpan() * 3600000));
                                                // TODO 展示预订信息
                                                dialog.dismiss();
                                                SelectReservationDialogFragment.newInstance(SelectReservationDialogFragment.Type.CUSTOM, reservation).show(getSupportFragmentManager(), "alert");
                                                // 更新Activity
                                                list_CH_seatInfo.get(mCurrentSeat.getPosition()).setStatus(2);
                                                mSSView.invalidate();
                                            }

                                        }

                                        @Override
                                        public void onFailure(HttpException e, String s) {
                                            Toast.makeText(UIUtils.getContext(), "数据加载失败", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onStart() {
                                            super.onStart();
                                        }

                                        @Override
                                        public void onLoading(long total, long current, boolean isUploading) {
                                            super.onLoading(total, current, isUploading);
                                            dialog.setTitle("正在加载中");
                                            dialog.show();
                                        }
                                    });
                        } else {
                            // 不在图书馆内
                            SelectReservationDialogFragment.newInstance(SelectReservationDialogFragment.Type.DEFAULT, null).show(getSupportFragmentManager(), "alert");
                        }
                    } else {
                        // 不在图书馆内
                        SelectReservationDialogFragment.newInstance(SelectReservationDialogFragment.Type.DEFAULT, null).show(getSupportFragmentManager(), "alert");
                    }
                } else {
                    //  为选择座位 提示请选择座位
                    SelectReadRoomDialogFragment.newInstance(SelectReadRoomDialogFragment.Type.DEFAULT, null).show(getSupportFragmentManager(), "alert");
                }

                break;
            default:
                break;
        }
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
