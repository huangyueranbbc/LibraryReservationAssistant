package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.TextView;

import com.labo.kaji.swipeawaydialog.support.v4.SwipeAwayDialogFragment;

import java.util.Calendar;
import java.util.List;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbReadroom;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbReservation;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

/**
 * 选择阅览室
 */
public class SelectReservationDialogFragment extends SwipeAwayDialogFragment {

    private static List<TbReadroom> readrooms;

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
        args.putSerializable("readrooms", reservation);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Type type = (Type) getArguments().getSerializable("type");
        TbReservation reservation = (TbReservation) getArguments().getSerializable("readrooms"); // 选着图书馆的id
        return type.create(getActivity(), this, reservation);
    }

}
