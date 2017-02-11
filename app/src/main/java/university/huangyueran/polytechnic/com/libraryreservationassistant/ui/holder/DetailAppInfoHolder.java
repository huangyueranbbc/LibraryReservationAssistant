package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.holder;

import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

import java.text.SimpleDateFormat;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbFileShare;
import university.huangyueran.polytechnic.com.libraryreservationassistant.global.GlobalValue;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.BitmapHelper;
import university.huangyueran.polytechnic.com.libraryreservationassistant.utils.UIUtils;

/**
 * 详情页-应用信息
 *
 * @author Kevin
 * @date 2015-11-1
 */
public class DetailAppInfoHolder extends BaseHolder<TbFileShare> {

    private ImageView ivIcon;
    private TextView tvName;
    private TextView tvDownloadNum;
    private TextView tvVersion;
    private TextView tvDate;
    private TextView tvSize;
    private RatingBar rbStar;
    private BitmapUtils mBitmapUtils;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.layout_detail_appinfo);

        ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvDownloadNum = (TextView) view.findViewById(R.id.tv_download_num);
        tvVersion = (TextView) view.findViewById(R.id.tv_version);
        tvDate = (TextView) view.findViewById(R.id.tv_date);
        tvSize = (TextView) view.findViewById(R.id.tv_size);
        rbStar = (RatingBar) view.findViewById(R.id.rb_star);

        mBitmapUtils = BitmapHelper.getBitmapUtils();

        return view;
    }

    @Override
    public void refreshView(TbFileShare data) {
        String endfix = data.getRealName().substring(data.getRealName().lastIndexOf(".") + 1); // 文件后缀名
        mBitmapUtils.display(ivIcon, GlobalValue.BASE_URL + "/img/ft_" + endfix + ".png");
        tvName.setText(data.getTitle());
        tvDownloadNum.setText("下载量:" + data.getDownloads());
        // TODO 改为其他的
        switch (data.getHobbyId()) {
            case 1 + "":
                tvVersion.setText("分类:" + "理学");
                break;
            case 2 + "":
                tvVersion.setText("分类:" + "文学");
                break;
            case 3 + "":
                tvVersion.setText("分类:" + "医学");
                break;
            case 4 + "":
                tvVersion.setText("分类:" + "军事");
                break;
            case 5 + "":
                tvVersion.setText("分类:" + "哲学");
                break;
            case 6 + "":
                tvVersion.setText("分类:" + "经济");
                break;
            case 7 + "":
                tvVersion.setText("分类:" + "教育");
                break;
            case 8 + "":
                tvVersion.setText("分类:" + "管理");
                break;
            default:
                break;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        tvDate.setText(format.format(data.getCreated()));
        tvSize.setText(Formatter.formatFileSize(UIUtils.getContext(), data.getSize()));
        rbStar.setRating(4); // TODO 评分 未实现
    }

}
