package focusresize;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.library.FocusResizeScrollListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import focusresize.domain.CustomObject;
import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.ui.activity.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        // 权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<String>();
            permissions.add(Manifest.permission.INTERNET);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 1);
            }
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        createCustomAdapter(recyclerView, linearLayoutManager);

    }

    private void createCustomAdapter(RecyclerView recyclerView, LinearLayoutManager linearLayoutManager) {
        CustomAdapter customAdapter = new CustomAdapter(this, (int) getResources().getDimension(R.dimen.custom_item_height));
        customAdapter.addItems(addItems());
        if (recyclerView != null) {
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(customAdapter);
            recyclerView.addOnScrollListener(new FocusResizeScrollListener<>(customAdapter, linearLayoutManager));
        }
    }

    private List<CustomObject> addItems() {

        List<String> picList = PicConstanct.getPicUrl();
        // 创建对象
        List<CustomObject> items = new ArrayList<>();
        for (int i = 0; i < picList.size(); i++) {
            CustomObject object = new CustomObject();
            object.setAuthor("习近平" + i + "号");
            object.setContent("这是习近平发表第" + i + "篇文章");
            object.setPosttime(new Date().toString());
            object.setReplys(i + "");
            object.setUrl(picList.get(i));
            items.add(object);
        }
        return items;
    }
}