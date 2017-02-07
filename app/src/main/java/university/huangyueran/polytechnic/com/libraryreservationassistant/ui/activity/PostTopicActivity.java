package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.LMSResult;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.PicUploadResult;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbTopic;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbUser;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.AndroidUploadFile;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.CacheUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.ServerUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.StringUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.TimestampTypeAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

public class PostTopicActivity extends BaseActivity {
    private static final String TAG = "PostTopicActivity";

    private ImageView ivGetPhoto, ivCode;
    private LinearLayout lin;
    private boolean isKeyBoardShow = false, isBQViewShow = false;
    private boolean isADJUST_PAN = false, isADJUST_RESIZE = false;
    private EditText etContent;
    private ImageView ivPic;
    RelativeLayout relPic;

    private int SELECT_PICTURE = 1; // 从图库中选择图片
    private int SELECT_CAMER = 2; // 用相机拍摄照片
    private String filename;
    private File filetemp; // 要上传的图片
    private Button btnSubmit;
    private String fileName;
    private Spinner mspinYear;
    private ArrayAdapter<CharSequence> mAdapterYear;
    private Long mCurrentHobbyId = Long.valueOf(1); // 当前选择的兴趣id 默认为1 文学

    private boolean isFBLoading = false; // 是否在发表中 控制网络单一请求 默认为false 没有请求网络

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setADJUST_RESIZE();
        setContentView(R.layout.activity_post_topic);

        // 添加权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<String>();

            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.CHANGE_WIFI_STATE);
            permissions.add(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
            permissions.add(Manifest.permission.CAMERA);
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 1);
            }
        }

        // 选择兴趣圈
        mspinYear = (Spinner) findViewById(R.id.spinYear);
        mAdapterYear = ArrayAdapter.createFromResource(this, R.array.years,
                android.R.layout.simple_list_item_1);
        mspinYear.setAdapter(mAdapterYear);
        mspinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentHobbyId = Long.valueOf(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etContent = (EditText) findViewById(R.id.wpost_et);
        ivPic = (ImageView) findViewById(R.id.wpost_img);
        relPic = (RelativeLayout) findViewById(R.id.wpost_imglayout);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public synchronized void onClick(View v) {
                if (!isFBLoading) {// 只有不是发表中状态 才可以发表
                    isFBLoading = true;
                    Log.i(TAG, "提交按钮被点击了!: ");
                    String context = etContent.getText().toString();
                    if (StringUtils.isEmpty(context)) {
                        Toast.makeText(PostTopicActivity.this, "请输入内容!", Toast.LENGTH_SHORT).show();
                        isFBLoading = false;
                    } else {
                        new Thread() {
                            public void run() {
                                try {
                                    PicUploadResult uploadResult = null;
                                    // 解析上传结果
                                    Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                    // 判断内容是否为空
                                    String context = etContent.getText().toString();
                                    Log.i(TAG, "文本内容: " + context);
                                    if (!StringUtils.isEmpty(context)) {

                                        if (filetemp != null) {
                                            String result = AndroidUploadFile.uploadFile(filetemp.getAbsolutePath(), GlobalValue.BASE_URL + "/pic/upload");
                                            uploadResult = gson.fromJson(result, PicUploadResult.class);
                                        } else {
                                            uploadResult = new PicUploadResult();
                                            uploadResult.setError(1 + "");
                                            uploadResult.setMessage("请选择图片");
                                            UIUtils.runOnUIThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(PostTopicActivity.this, "请选择图片!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    }

                                    Log.i(TAG, "PicUploadResult: " + uploadResult.toString());

                                    if ("0".equals(uploadResult.getError())) { // 上传成功 创建主题 都成功销毁此Activity并返回
                                        // 如果图片上传成功 继续创建主题
                                        String content = etContent.getText().toString(); // 主题内容

                                        TbTopic topic = new TbTopic();
                                        String userLoginInfo = CacheUtils.getCacheNotiming(GlobalValue.LOGININFO);
                                        TbUser user = gson.fromJson(userLoginInfo, TbUser.class);
                                        topic.setAuthorId(user.getUserId());// 用户id
                                        Long hobby_id = mCurrentHobbyId; // 哲学
                                        topic.setHobbyId(hobby_id);

                                        topic.setContent(content);
                                        topic.setCreated(new Date());
                                        topic.setAuthorName(user.getNickname());
                                        topic.setLaudCount(0);
                                        topic.setReplyCount(0);
                                        topic.setTopicPic(uploadResult.getUrl());
                                        topic.setIpaddr(ServerUtils.getHostIP());
                                        topic.setIsdel(0);
                                        Log.i(TAG, "topic: " + topic.toString());

                                        // 发送请求
                                        String topicJson = gson.toJson(topic);

                                        final String url = GlobalValue.BASE_URL + "/topic/create";
                                        RequestParams params = new RequestParams("UTF-8");
                                        params.setBodyEntity(new StringEntity(topicJson, "UTF-8"));
                                        params.setContentType("application/json");

                                        Log.i(TAG, "topicJson: " + topicJson);

                                        HttpUtils http = new HttpUtils();
                                        http.send(HttpRequest.HttpMethod.POST,
                                                url, params,
                                                new RequestCallBack<String>() {

                                                    @Override
                                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                                        // 得到图书馆list集合
                                                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                                        LMSResult lmsResult = gson.fromJson(responseInfo.result, new TypeToken<LMSResult>() {
                                                        }.getType());
                                                        if (lmsResult.getStatus() == 200) { // 成功
                                                            Toast.makeText(PostTopicActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
                                                            setResult(RESULT_OK, null);
                                                            finish();
                                                        } else { // 失败
                                                            Toast.makeText(PostTopicActivity.this, "服务异常 请重试...", Toast.LENGTH_SHORT).show();
                                                        }
                                                        isFBLoading = false;
                                                    }

                                                    @Override
                                                    public void onFailure(HttpException e, String s) {
                                                        Log.i(TAG, "onFailure: " + e.getMessage());
                                                        Log.i(TAG, "onFailure: " + s);
                                                        Log.i(TAG, "onFailure: " + url);
                                                        e.printStackTrace();
                                                        Toast.makeText(PostTopicActivity.this, "发表失败 请重试...", Toast.LENGTH_SHORT).show();
                                                        isFBLoading = false;
                                                    }
                                                });

                                    } else { // 上传失败
                                        if (uploadResult != null) {
                                            Toast.makeText(PostTopicActivity.this, uploadResult.getMessage(), Toast.LENGTH_SHORT).show();
                                            isFBLoading = false;
                                        } else {
                                            Toast.makeText(PostTopicActivity.this, "服务异常 请重试...", Toast.LENGTH_SHORT).show();
                                            isFBLoading = false;
                                        }

                                    }


                                } catch (ClientProtocolException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                }

            }
        });
        findViewById(R.id.wpost_remimg).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                relPic.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.wpost_getimg).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isKeyBoardShow) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                showSetHeadimg();
            }
        });

        final TextView tvWordNum = (TextView) findViewById(R.id.wpost_wordnum);
        final int wordnum = 300;
        etContent.addTextChangedListener(new TextWatcher() {
            int con = 0;
            CharSequence c;

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                con = arg0.length();
                c = arg0;
                tvWordNum.setText((wordnum - con) + "");
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (con > wordnum) {
                    c = c.subSequence(0, wordnum);
                    etContent.setText(c.toString());
                    etContent.setSelection(wordnum);
                }
            }
        });
        lin = (LinearLayout) findViewById(R.id.wpos_layout);
        // lin.addView(bqView);
        lin.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int n = lin.getRootView().getHeight() - lin.getHeight();
                if (n > 100) {// 软键盘已弹出
                    if (isBQViewShow) {

                        setADJUST_RESIZE();
                        isBQViewShow = false;
                    }
                    isKeyBoardShow = true;
                } else {// 软键盘未弹出
                    isKeyBoardShow = false;
                    if (isADJUST_PAN) {
                        setADJUST_RESIZE();
                    }
                }

            }
        });

    }

    @Override
    protected void onResume() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                // InputMethodManager inputManager = (InputMethodManager)
                // etContent
                // .getContext().getSystemService(
                // Context.INPUT_METHOD_SERVICE);
                // inputManager.showSoftInput(etContent, 0);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);

                // // InputMethodManager imm = (InputMethodManager)
                // // getSystemService(Context.INPUT_METHOD_SERVICE);
                // // imm.toggleSoftInput(0,
                // InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 300);
        super.onResume();
    }

    private void setADJUST_PAN() {
        isADJUST_RESIZE = false;
        isADJUST_PAN = true;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    private void setADJUST_RESIZE() {
        isADJUST_RESIZE = true;
        isADJUST_PAN = false;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public void cancle(View v) {
        finish();
    }

    public void button(View v) {

    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }

    private void showSetHeadimg() {
        final PopupWindow popupWindow = new PopupWindow(this);
        View v = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_changetx, null);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindow.setContentView(v);
        popupWindow.setAnimationStyle(R.style.AnimationPreview);
        popupWindow.showAtLocation(new View(this), Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.35f);
        popupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        v.findViewById(R.id.tx_camera).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                // 进入相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getExternalCacheDir(), "edtimg.jpg")));
                startActivityForResult(intent, SELECT_CAMER);
            }
        });
        v.findViewById(R.id.tx_photo).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                // 进入图库
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });
        v.findViewById(R.id.tx_cancle).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    Bitmap bitmap = null;
                    if (requestCode == SELECT_CAMER) {
                        bitmap = BitmapFactory.decodeFile(getExternalCacheDir() + "/edtimg.jpg");
                        // 将得到的图片存放在文件夹中
                        filename = UUID.randomUUID() + ".jpg";
                        saveBitmap2file(bitmap, filename);
                    } else if (requestCode == SELECT_PICTURE) {
                        Uri uri = data.getData();
                        ContentResolver cr = PostTopicActivity.this.getContentResolver();

                        try {
                            bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                            // 将得到的图片存放在文件夹中
                            filename = UUID.randomUUID() + ".jpg";
                            saveBitmap2file(bitmap, filename);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    Message msg = new Message();
                    msg.obj = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
                    handler.sendMessage(msg);

//                    BitmapUtil.comp(bitmap);
//                    Message msg2 = new Message();
//                    msg2.obj = bitmap;
//                    handler.sendMessage(msg2);
                }
            });

            thread.start();

        } else {
            Toast.makeText(this, "选择图片失败,请重新选择", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage: ");
            Bitmap bitmap = (Bitmap) msg.obj;
            relPic.setVisibility(View.VISIBLE);
            ivPic.setImageBitmap(bitmap);
        }
    };

    /**
     * 保存图片
     *
     * @param bmp
     * @param filename
     * @return
     */
    boolean saveBitmap2file(Bitmap bmp, String filename) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            File file = getCacheDir();// 本应用的缓存文件夹

            Log.i(TAG, "saveBitmap2file: " + file.getAbsolutePath() + "/" + filename);
            filetemp = new File(file.getAbsolutePath() + "/" + filename);
            if (!file.exists()) {
                filetemp.createNewFile();
                fileName = file.getAbsolutePath() + "/" + filename;
            }
            stream = new FileOutputStream(filetemp);
            Log.i(TAG, "stream: " + stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp.compress(format, quality, stream);
    }


//    private String doPost(String imagePath) {
//        OkHttpClient mOkHttpClient = new OkHttpClient();
//
//        String result = "error";
//        MultipartBody.Builder builder = new MultipartBody.Builder();
//        // 这里演示添加用户ID
////        builder.addFormDataPart("userId", "20160519142605");
//        builder.addFormDataPart("image", imagePath,
//                RequestBody.create(MediaType.parse("image/jpeg"), new File(imagePath)));
//
//        RequestBody requestBody = builder.build();
//        Request.Builder reqBuilder = new Request.Builder();
//        Request request = reqBuilder
//                .url("BASE_URL" + "/uploadimage")
//                .post(requestBody)
//                .build();
//
//        Log.d(TAG, "请求地址 " + "BASE_URL" + "/uploadimage");
//        try {
//            Response response = mOkHttpClient.newCall(request).execute();
//            Log.d(TAG, "响应码 " + response.code());
//            if (response.isSuccessful()) {
//                String resultValue = response.body().string();
//                Log.d(TAG, "响应体 " + resultValue);
//                return resultValue;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

}
