package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Timestamp;
import java.util.Date;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.FileUploadResult;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.LMSResult;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbFileShare;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbUser;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.AndroidUploadFile;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.CacheUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.StringUtils;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.TimestampTypeAdapter;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

public class FileUploadActivity extends Activity implements OnClickListener {

    private static final String TAG = "FileUploadActivity";

    protected static final int SUCCESS = 2;
    protected static final int FAILD = 3;
    protected static int RESULT_LOAD_FILE = 1;
    private TextView cancel;
    private TextView upload;
    private EditText pathView;
    private Button buttonLoadImage;
    private String filePath;
    private View show;

    private Long mCurrentHobbyId = Long.valueOf(1); // 当前选择的兴趣id 默认为1 文学
    private EditText mETDesc;
    private EditText mEtTitle;

    private boolean isShareLoading = false; // 是否在网络请求中 控制网络单一请求 默认为false 没有请求网络

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);
        initView();
        initData();
    }

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {

                case SUCCESS:
                    show.setVisibility(View.INVISIBLE);
                    filePath = "";
                    pathView.setText(filePath);
                    Toast.makeText(getApplicationContext(), "上传成功！",
                            Toast.LENGTH_LONG).show();
                    break;
                case FAILD:
                    show.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "上传失败！",
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
            return false;
        }

    });

    private void initView() {
        cancel = (TextView) findViewById(R.id.cancel);
        upload = (TextView) findViewById(R.id.upload);
        buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
        cancel.setOnClickListener(this);
        upload.setOnClickListener(this);
        buttonLoadImage.setOnClickListener(this);
        show = findViewById(R.id.show);
        pathView = (EditText) findViewById(R.id.file_path);
        pathView.setKeyListener(null);

        mEtTitle = (EditText) findViewById(R.id.et_title); // 标题
        mETDesc = (EditText) findViewById(R.id.et_desc); // 描述

        // 选择兴趣圈
        Spinner mSpHobbySelect = (Spinner) findViewById(R.id.sp_hobby_select);
        ArrayAdapter<CharSequence> mSpHobbyAdapter = ArrayAdapter.createFromResource(this, R.array.years, android.R.layout.simple_list_item_1);
        mSpHobbySelect.setAdapter(mSpHobbyAdapter);
        mSpHobbySelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentHobbyId = Long.valueOf(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initData() {
        filePath = "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.buttonLoadPicture:
                Intent intent = new Intent(getApplicationContext(),
                        FileSelectActivity.class);

                startActivityForResult(intent, RESULT_LOAD_FILE);
                break;
            case R.id.upload:
                uploadFile();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_FILE && resultCode == RESULT_LOAD_FILE
                && data != null) {
            filePath = data.getStringExtra("path");
            String endfix = filePath.substring(filePath.lastIndexOf(".") + 1); // 文件后缀名
            System.out.println("后缀名:" + endfix);
            if ("txt".equals(endfix.toLowerCase())) { // 1

            } else if ("pdf".equals(endfix.toLowerCase())) { //2

            } else if ("doc".equals(endfix.toLowerCase()) || "docx".equals(endfix.toLowerCase())) { // 3

            } else if ("xlsx".equals(endfix.toLowerCase()) || "xls".equals(endfix.toLowerCase())) { //4

            } else if ("ppt".equals(endfix.toLowerCase())) { //5

            } else {
                filePath = "";
                Toast.makeText(this, "不支持的上传类型", Toast.LENGTH_SHORT).show();
            }
            pathView.setText(filePath);
        }
    }

    /**
     * 上传分享文件
     */
    private void uploadFile() {
        new Thread() {

            private FileUploadResult uploadResult;

            @Override
            public synchronized void run() {
                String title = mEtTitle.getText().toString(); // 标题内容
                String desc = mETDesc.getText().toString(); // 描述内容

                if (!isShareLoading) { // 不在分享中
                    isShareLoading = true;
                    if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(desc)) {// 标题和描述都不为空
                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        String result = null;
                        try {
                            if (filePath != null) {
                                result = AndroidUploadFile.uploadFile(filePath, GlobalValue.BASE_URL + "/fileshare/upload");
                            } else {
                                result = null;
                            }
                            Log.i(TAG, "result: " + result);
                            Log.i(TAG, "filePath: " + filePath);
                            Log.i(TAG, "url: " + GlobalValue.BASE_URL + "/fileshare/upload");

                        } catch (IOException e) {
                            e.printStackTrace();
                            UIUtils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FileUploadActivity.this, "上传失败 请稍后重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                            isShareLoading = false;
                        }
                        if (!StringUtils.isEmpty(result)) {
                            // 上传结果
                            uploadResult = gson.fromJson(result, FileUploadResult.class);
                        } else {
                            isShareLoading = false;
                            uploadResult = new FileUploadResult();
                            uploadResult.setError("1");
                        }

                        if ("0".equals(uploadResult.getError())) { //上传成功
                            // 创建文件分享记录
                            TbFileShare fileShare = new TbFileShare();
                            fileShare.setCreated(new Date());
                            String userLoginInfo = CacheUtils.getCacheNotiming(GlobalValue.LOGININFO);
                            TbUser user = gson.fromJson(userLoginInfo, TbUser.class);
                            fileShare.setAuthorName(user.getNickname());
                            fileShare.setDownloads((long) 0);
                            fileShare.setFavoriteCount(0);
                            fileShare.setFileDesc(desc);
                            fileShare.setHobbyId(String.valueOf(mCurrentHobbyId));
                            fileShare.setLaudCount(0);
                            fileShare.setUserId(user.getUserId());
                            fileShare.setTitle(title);
                            fileShare.setRealName(uploadResult.getRealName());
                            fileShare.setFielPath(uploadResult.getPath());
                            fileShare.setSize(uploadResult.getFilesize());

                            // 发送请求
                            String fileShareJson = gson.toJson(fileShare);

                            final String url = GlobalValue.BASE_URL + "/fileshare/create";
                            RequestParams params = new RequestParams("UTF-8");
                            try {
                                params.setBodyEntity(new StringEntity(fileShareJson, "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                UIUtils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FileUploadActivity.this, "上传失败 请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                isShareLoading = false;
                            }
                            params.setContentType("application/json");

                            Log.i(TAG, "fileShareJson: " + fileShareJson);

                            HttpUtils http = new HttpUtils();
                            http.send(HttpRequest.HttpMethod.POST,
                                    url, params,
                                    new RequestCallBack<String>() {

                                        @Override
                                        public void onSuccess(ResponseInfo<String> responseInfo) {
                                            Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                            LMSResult lmsResult = gson.fromJson(responseInfo.result, new TypeToken<LMSResult>() {
                                            }.getType());
                                            if (lmsResult.getStatus() == 200) { // 成功
                                                Toast.makeText(FileUploadActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                                                setResult(RESULT_OK, null);
                                                finish();
                                            } else { // 失败
                                                Toast.makeText(FileUploadActivity.this, "服务异常 请重试...", Toast.LENGTH_SHORT).show();
                                            }
                                            isShareLoading = false;
                                            show.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onFailure(HttpException e, String s) {
                                            Log.i(TAG, "onFailure: " + e.getMessage());
                                            Log.i(TAG, "onFailure: " + s);
                                            Log.i(TAG, "onFailure: " + url);
                                            e.printStackTrace();
                                            Toast.makeText(FileUploadActivity.this, "发表失败 请重试...", Toast.LENGTH_SHORT).show();
                                            show.setVisibility(View.INVISIBLE);
                                            isShareLoading = false;
                                        }

                                        @Override
                                        public void onStart() {
                                            super.onStart();
                                            show.setVisibility(View.VISIBLE);
                                        }
                                    });

                        } else { //上传失败
                            UIUtils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FileUploadActivity.this, "上传失败 请稍后重试...", Toast.LENGTH_SHORT).show();
                                }
                            });
                            isShareLoading = false;
                        }

                    } else {
                        UIUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FileUploadActivity.this, "标题和描述都不能为空", Toast.LENGTH_SHORT).show();
                            }
                        });
                        isShareLoading = false;
                    }
                }

            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        show.setVisibility(View.INVISIBLE);
        super.onDestroy();
    }
}