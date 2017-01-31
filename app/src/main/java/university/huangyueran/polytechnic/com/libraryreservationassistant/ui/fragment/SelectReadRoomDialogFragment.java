package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.labo.kaji.swipeawaydialog.support.v4.SwipeAwayDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbReadroom;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.adapter.yuyueadapter.ReadRoomAdapter;

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
                        .setTitle("Title")
                        .setMessage("Message")
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("OK", null)
                        .setNegativeButton("Cancel", null)
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
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, readrooms.get(which).toString(), Toast.LENGTH_SHORT).show();
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
//        CUSTOM() {
//            @Override
//            public
//            @NonNull
//            Dialog create(Context context, ExampleDialogFragment fragment) {
//                String[] urls = context.getResources().getStringArray(R.array.octocat_urls);
//                AppCompatDialog dialog = new AppCompatDialog(context);
//                dialog.setContentView(R.layout.dialog_custom);
//                Glide.with(fragment)
//                        .load(urls[sRandom.nextInt(urls.length)])
//                        .into((ImageView) dialog.findViewById(R.id.customdialog_image));
//                return dialog;
//            }
//        },
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
