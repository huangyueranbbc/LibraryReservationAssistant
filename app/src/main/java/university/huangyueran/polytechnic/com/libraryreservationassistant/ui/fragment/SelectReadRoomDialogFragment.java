package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.labo.kaji.swipeawaydialog.support.v4.SwipeAwayDialogFragment;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbReadroom;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbSeat;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.activity.SelectSeatActivity;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.adapter.yuyueadapter.ReadRoomAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.TimestampTypeAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

/**
 * 选择阅览室
 */
public class SelectReadRoomDialogFragment extends SwipeAwayDialogFragment {

    private static List<TbReadroom> readrooms;

    private interface DialogBuilder {
        @NonNull
        Dialog create(Context context, SelectReadRoomDialogFragment fragment, ArrayList<TbReadroom> readrooms);
    }

    public enum Type implements DialogBuilder {
        APPCOMPAT() {
            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReadRoomDialogFragment fragment, ArrayList<TbReadroom> readrooms) {
                return new android.support.v7.app.AlertDialog.Builder(context)
                        .setTitle("Title")
                        .setMessage("Message")
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("OK", null)
                        .setNegativeButton("Cancel", null)
                        .create();
            }
        },
        APPCOMPAT_LIST() {
            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReadRoomDialogFragment fragment, ArrayList<TbReadroom> readrooms) {
                return new android.support.v7.app.AlertDialog.Builder(context)
                        .setTitle("Title")
                        .setIcon(R.mipmap.ic_launcher)
                        .setItems(new String[]{
                                "Item 1",
                                "Item 2",
                                "Item 3",
                                "Item 4",
                                "Item 5",
                                "Item 6",
                                "Item 7",
                                "Item 8",
                                "Item 9",
                                "Item 10",
                        }, null)
                        .setPositiveButton("OK", null)
                        .setNegativeButton("Cancel", null)
                        .create();
            }
        },
        DEFAULT() {
            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReadRoomDialogFragment fragment, ArrayList<TbReadroom> readrooms) {
                return new AlertDialog.Builder(context)
                        .setTitle("请选择座位")
                        .setMessage("不能不选择座位哦~")
                        .setIcon(R.mipmap.ic_launcher) // TODO 添加提示图标
                        .create();
            }
        },
        DEFAULT_LIST() {
            @Override
            public
            @NonNull
            Dialog create(final Context context, SelectReadRoomDialogFragment fragment, final ArrayList<TbReadroom> readrooms) {
                // 网络请求数据
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                ReadRoomAdapter readRoomAdapter = new ReadRoomAdapter(context, R.layout.list_item, readrooms);
                builder.setAdapter(readRoomAdapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        // TODO 点击后，创建新的activity 显示座位预定界面
                        // 网络获取数据 在成功后 跳转activity 携带数据 实时展示 不能写入缓存
                        final String url = GlobalValue.BASE_URL + "/seat/list";
                        Long readroom_id = readrooms.get(which).getId();
                        RequestParams params = new RequestParams();
                        params.addQueryStringParameter("id", String.valueOf(readroom_id));
                        params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

                        HttpUtils http = new HttpUtils();
                        http.send(HttpRequest.HttpMethod.GET,
                                url, params,
                                new RequestCallBack<String>() {
                                    private ProgressDialog dialog = new ProgressDialog(context);

                                    @Override
                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                        // 得到图书馆list集合
                                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                        ArrayList<TbSeat> seats = gson.fromJson(responseInfo.result, new TypeToken<ArrayList<TbSeat>>() {
                                        }.getType());

                                        dialog.dismiss();
                                        // 跳转Activity
                                        Intent intent = new Intent();
                                        intent.setClass(context, SelectSeatActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("seats", seats); //座位数据
                                        bundle.putSerializable("readroom", readrooms.get(which)); //阅览室数据
                                        intent.putExtras(bundle);
                                        context.startActivity(intent);
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
                        // Toast.makeText(context, readrooms.get(which).toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                return builder.setTitle("请选择阅览室").create();
            }

        },
        PROGRESS() {
            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReadRoomDialogFragment fragment, ArrayList<TbReadroom> readrooms) {
                ProgressDialog dialog = new ProgressDialog(context);
                dialog.setTitle("Title");
                dialog.setMessage("Message");
                return dialog;
            }
        },
        DATE() {
            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReadRoomDialogFragment fragment, ArrayList<TbReadroom> readrooms) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                return new DatePickerDialog(context, null, year, month, day);
            }
        },
        TIME() {
            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReadRoomDialogFragment fragment, ArrayList<TbReadroom> readrooms) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                return new TimePickerDialog(context, null, hour, minute, true);
            }
        },
        // 自定义的弹出框 可以设置图标
        CUSTOM() {
            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReadRoomDialogFragment fragment, ArrayList<TbReadroom> readrooms) {
//                String[] urls = context.getResources().getStringArray(R.array.octocat_urls);
                AppCompatDialog dialog = new AppCompatDialog(context);
                dialog.setContentView(R.layout.reservation_info_layout);
//                Glide.with(fragment);
//                        .load(urls[sRandom.nextInt(urls.length)])
//                        .into((ImageView) dialog.findViewById(R.id.customdialog_image));
                return dialog;
            }
        },
    }

    public static SelectReadRoomDialogFragment newInstance(Type type, ArrayList<TbReadroom> readrooms) {
        SelectReadRoomDialogFragment f = new SelectReadRoomDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        args.putSerializable("readrooms", readrooms);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Type type = (Type) getArguments().getSerializable("type");
        ArrayList<TbReadroom> readrooms = (ArrayList<TbReadroom>) getArguments().getSerializable("readrooms"); // 选着图书馆的id
        return type.create(getActivity(), this, readrooms);
    }

}
