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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.adapter.SampleAdapter;

/**
 * @author kakajika
 * @since 15/08/15.
 */
public class ExampleDialogFragment extends SwipeAwayDialogFragment {

    private static final Random sRandom = new Random();

    private interface DialogBuilder {
        @NonNull
        Dialog create(Context context, ExampleDialogFragment fragment);
    }

    public enum Type implements DialogBuilder {
        APPCOMPAT() {
            @Override
            public
            @NonNull
            Dialog create(Context context, ExampleDialogFragment fragment) {
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
            Dialog create(Context context, ExampleDialogFragment fragment) {
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
            Dialog create(Context context, ExampleDialogFragment fragment) {
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
            Dialog create(final Context context, ExampleDialogFragment fragment) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final String[] dialogtxt = {"Samsung", "LG", "Google", "Oneplus"};

//                ArrayAdapter<String> dialogadapter = new ArrayAdapter<>(context, android.R.layout.list_item, dialogtxt);

                String KEY_ICON = "icon";
                String KEY_COLOR = "color";
                Map<String, Integer> map;
                List<Map<String, Integer>> mSampleList = new ArrayList<>();
                int[] icons = {
                        R.drawable.icon_1,
                        R.drawable.icon_2,
                        R.drawable.icon_3};
                int[] colors = {
                        R.color.saffron,
                        R.color.eggplant,
                        R.color.sienna};
                for (int i = 0; i < icons.length; i++) {
                    map = new HashMap<>();
                    map.put(KEY_ICON, icons[i]);
                    map.put(KEY_COLOR, colors[i]);
                    mSampleList.add(map);
                }

                SampleAdapter sampleAdapter = new SampleAdapter(context, R.layout.list_item, mSampleList);

                builder.setAdapter(sampleAdapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialogtxt[which] == "Samsung") {
                            Toast.makeText(context, "galaxy s5", Toast.LENGTH_SHORT).show();
                        }
                        if (dialogtxt[which] == "LG") {
                            Toast.makeText(context, "G3", Toast.LENGTH_SHORT).show();
                        }
                        if (dialogtxt[which] == "Google") {
                            Toast.makeText(context, "nexus 5", Toast.LENGTH_SHORT).show();
                        }

                        if (dialogtxt[which] == "Oneplus") {
                            Toast.makeText(context, "one", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                return builder.setTitle("请选择阅览室").create();

            }
        },
        PROGRESS() {
            @Override
            public
            @NonNull
            Dialog create(Context context, ExampleDialogFragment fragment) {
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
            Dialog create(Context context, ExampleDialogFragment fragment) {
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
            Dialog create(Context context, ExampleDialogFragment fragment) {
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

    public static ExampleDialogFragment newInstance(Type type) {
        ExampleDialogFragment f = new ExampleDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Type type = (Type) getArguments().getSerializable("type");
        return type.create(getActivity(), this);
    }

}
