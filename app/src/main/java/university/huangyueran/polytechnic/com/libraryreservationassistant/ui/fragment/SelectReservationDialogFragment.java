package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.LMSResult;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbReadroom;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbReservation;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.TimestampTypeAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

/**
 * 选择阅览室
 */
public class SelectReservationDialogFragment extends SwipeAwayDialogFragment {
    private static final String TAG = "SelectReservationDialogFragment";

    private static List<TbReadroom> readrooms;

    private static FragmentManager mFragmentManager;

    private static boolean isSelectRevSeatLoading = false; // 防止重复加载 false==不在加载中

    private interface DialogBuilder {
        @NonNull
        Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation readrooms);
    }

    public enum Type implements DialogBuilder {
        APPCOMPAT() {
            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation readrooms) {
                return new android.support.v7.app.AlertDialog.Builder(context)
                        .setTitle("Title")
                        .setMessage("Message")
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("OK", null)
                        .setNegativeButton("Cancel", null)
                        .create();
            }
        },
        ISLEAVE() { // 是否离开

            @Override
            public
            @NonNull
            Dialog create(final Context context, final SelectReservationDialogFragment fragment, final TbReservation reservation) {
                return new android.support.v7.app.AlertDialog.Builder(context)
                        .setTitle("提前离开?")
                        .setMessage("确定提前离开吗？")
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                if (!isSelectRevSeatLoading) {
                                    isSelectRevSeatLoading = true;
                                    final ProgressDialog dialogProgress = new ProgressDialog(context);
                                    // 请求网络服务 提前离开
                                    String url = GlobalValue.BASE_URL + "/reservation/leave";
                                    RequestParams params = new RequestParams();
                                    params.addQueryStringParameter("user_id", String.valueOf(reservation.getUserId()));
                                    params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交
                                    HttpUtils http = new HttpUtils();
                                    http.send(HttpRequest.HttpMethod.GET,
                                            url, params,
                                            new RequestCallBack<String>() {

                                                @Override
                                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                                    Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                                    LMSResult result = gson.fromJson(responseInfo.result, new TypeToken<LMSResult>() {
                                                    }.getType());
                                                    Log.i("yjyy", "onSuccess: " + result.getStatus());
                                                    if (result.getStatus() == 200) { // 成功
                                                        SelectReservationDialogFragment.newInstance(Type.LEAVESUCCESS, null).show(mFragmentManager, "取消成功");
                                                    } else if (result.getStatus() == 402) { // 没有预订记录
                                                        SelectReservationDialogFragment.newInstance(Type.NORESERVATIONRECORD, null).show(mFragmentManager, "没有预约记录");
                                                    } else { // 服务错误
                                                        SelectReservationDialogFragment.newInstance(Type.SERVERERROR, null).show(mFragmentManager, "服务错误");
                                                    }
                                                    dialogProgress.dismiss();
                                                    isSelectRevSeatLoading = false;
                                                }

                                                @Override
                                                public void onFailure(HttpException e, String s) {
                                                    Toast.makeText(UIUtils.getContext(), "数据加载失败", Toast.LENGTH_SHORT).show();
                                                    dialogProgress.dismiss();
                                                    isSelectRevSeatLoading = false;
                                                }

                                                @Override
                                                public void onLoading(long total, long current, boolean isUploading) {
                                                    super.onLoading(total, current, isUploading);
                                                    dialogProgress.setTitle("正在加载中");
                                                    dialogProgress.show();
                                                }
                                            });
                                }

                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
            }
        },
        APPCOMPAT_LIST() {
            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation readrooms) {
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
            Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation reservation) {
                return new AlertDialog.Builder(context)
                        .setTitle("请前往图书馆中预订")
                        .setMessage("不能在图书馆以外的位置预约座位哦~")
                        .setIcon(R.mipmap.ic_launcher) // TODO 添加提示图标
                        .create();
            }
        },
        RANDOMCREATESEAT() {
            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation reservation) {
                return new AlertDialog.Builder(context)
                        .setTitle("图书馆人数已满或者您已有预约记录")
                        .setMessage("抱歉~当前图书馆人数已满或者您已有预约记录了哦~~请稍后重试呢~亲")
                        .setIcon(R.mipmap.ic_launcher) // TODO 添加提示图标
                        .create();
            }
        },
        CREATEHASRECORD() { // 已有预订记录

            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation reservation) {
                return new AlertDialog.Builder(context)
                        .setTitle("您已有座位预约记录")
                        .setMessage("抱歉~您已有预约记录了哦~亲")
                        .setIcon(R.mipmap.ic_launcher) // TODO 添加提示图标
                        .create();
            }
        },
        CREATEERROR() { // 预约错误

            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation reservation) {
                return new AlertDialog.Builder(context)
                        .setTitle("预约错误")
                        .setMessage("抱歉~预约系统被外星人可能劫持了呢~请稍后重试~亲")
                        .setIcon(R.mipmap.ic_launcher) // TODO 添加提示图标
                        .create();
            }
        },
        LEAVESUCCESS() { // 离开成功

            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation reservation) {
                return new AlertDialog.Builder(context)
                        .setTitle("取消成功")
                        .setMessage("您的座位预订已经取消~!")
                        .setIcon(R.mipmap.ic_launcher) // TODO 添加提示图标
                        .create();
            }
        },
        NORESERVATIONRECORD() { // 没有预约记录

            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation reservation) {
                return new AlertDialog.Builder(context)
                        .setTitle("您还没有预约记录")
                        .setMessage("抱歉~您还没有进行过预约了哦~亲")
                        .setIcon(R.mipmap.ic_launcher) // TODO 添加提示图标
                        .create();
            }
        },
        SERVERERROR() { // 服务错误

            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation reservation) {
                return new AlertDialog.Builder(context)
                        .setTitle("取消错误")
                        .setMessage("抱歉~预约系统被外星人可能劫持了呢~请稍后重试~亲")
                        .setIcon(R.mipmap.ic_launcher) // TODO 添加提示图标
                        .create();
            }
        },
        NOSEATRECORDINFO() { // 没有座位预订记录信息

            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation reservation) {
                return new AlertDialog.Builder(context)
                        .setTitle("您还没有预定座位")
                        .setMessage("抱歉~您还没有预约座位呢~亲")
                        .setIcon(R.mipmap.ic_launcher) // TODO 添加提示图标
                        .create();
            }
        },
        PROGRESS() {
            @Override
            public
            @NonNull
            Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation reservation) {
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
            Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation reservation) {
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
            Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation reservation) {
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
            Dialog create(Context context, SelectReservationDialogFragment fragment, TbReservation reservation) {
//                String[] urls = context.getResources().getStringArray(R.array.octocat_urls);
                AppCompatDialog dialog = new AppCompatDialog(context);
                View view = UIUtils.inflate(R.layout.reservation_info_layout);
                TextView textView = (TextView) view.findViewById(R.id.textView);
                textView.setText(reservation.toString());
                textView.setTextSize(20);
                textView.setTextColor(Color.BLACK);
                dialog.setContentView(view);
//                Glide.with(fragment);
//                        .load(urls[sRandom.nextInt(urls.length)])
//                        .into((ImageView) dialog.findViewById(R.id.customdialog_image));
                return dialog;
            }
        },
    }

    public static SelectReservationDialogFragment newInstance(Type type, TbReservation reservation) {
        SelectReservationDialogFragment f = new SelectReservationDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        args.putSerializable("reservation", reservation);
        f.setArguments(args);
        return f;
    }

    public static SelectReservationDialogFragment newInstance(Type type, TbReservation reservation, FragmentManager fragmentManager) {
        SelectReservationDialogFragment f = new SelectReservationDialogFragment();
        mFragmentManager = fragmentManager;
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        args.putSerializable("reservation", reservation);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Type type = (Type) getArguments().getSerializable("type");
        TbReservation reservation = (TbReservation) getArguments().getSerializable("reservation"); // 选着图书馆的id
        return type.create(getActivity(), this, reservation);
    }
}
