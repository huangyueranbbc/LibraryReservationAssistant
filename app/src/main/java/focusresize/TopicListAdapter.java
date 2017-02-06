package focusresize;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.library.FocusResizeAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbTopic;

/**
 * Created by borjabravo on 11/6/16.
 */
public class TopicListAdapter extends FocusResizeAdapter<RecyclerView.ViewHolder> {

    public static final int OFFSET_TEXT_SIZE = 4;
    public static final float OFFSET_TEXT_ALPHA = 100f;
    public static final float ALPHA_SUBTITLE = 0.81f;
    public static final float ALPHA_SUBTITLE_HIDE = 0f;

    private List<TbTopic> items;
    private Context context;
    private ImageLoader imageLoader;

    public TopicListAdapter(Context context, int height) {
        super(context, height);
        this.context = context;
        items = new ArrayList<TbTopic>();
        imageLoader = new ImageLoader(context.getApplicationContext());
    }

    public void addItems(List<TbTopic> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getFooterItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindFooterViewHolder(RecyclerView.ViewHolder holder, int position) {
        TbTopic topic = items.get(position);
        fill((CustomViewHolder) holder, topic);
    }

    private void fill(CustomViewHolder holder, TbTopic topic) {
        holder.tvAuthor.setText(topic.getAuthorName());
        holder.tvContent.setText("【" + topic.getHobbyName() + "】" + topic.getContent());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.tvPosttime.setText(format.format(topic.getCreated()));
        holder.tvReplys.setText(topic.getReplyCount() + "");
        imageLoader.DisplayImage(topic.getTopicPic(), holder.topicImage);
        // subtitleTextView 小文本
        // titleTextView 大文本
    }

    @Override
    public void onItemBigResize(RecyclerView.ViewHolder viewHolder, int position, int dyAbs) {
        if (((CustomViewHolder) viewHolder).tvContent.getTextSize() + (dyAbs / OFFSET_TEXT_SIZE) >= context.getResources().getDimension(R.dimen.font_xxxlarge)) {
            ((CustomViewHolder) viewHolder).tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.font_xxxlarge));
//            ((CustomViewHolder) viewHolder).tvContent.setSingleLine(false);
        } else {
            ((CustomViewHolder) viewHolder).tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((CustomViewHolder) viewHolder).tvContent.getTextSize() + (dyAbs / OFFSET_TEXT_SIZE));
        }

        float alpha = dyAbs / OFFSET_TEXT_ALPHA;
        if (((CustomViewHolder) viewHolder).tvAuthor.getAlpha() + alpha >= ALPHA_SUBTITLE) {
            ((CustomViewHolder) viewHolder).tvAuthor.setAlpha(ALPHA_SUBTITLE);
        } else {
            ((CustomViewHolder) viewHolder).tvAuthor.setAlpha(((CustomViewHolder) viewHolder).tvAuthor.getAlpha() + alpha);
        }

        if (((CustomViewHolder) viewHolder).tvPosttime.getAlpha() + alpha >= ALPHA_SUBTITLE) {
            ((CustomViewHolder) viewHolder).tvPosttime.setAlpha(ALPHA_SUBTITLE);
        } else {
            ((CustomViewHolder) viewHolder).tvPosttime.setAlpha(((CustomViewHolder) viewHolder).tvPosttime.getAlpha() + alpha);
        }

//        if (((CustomViewHolder) viewHolder).tvReplys.getAlpha() + alpha >= ALPHA_SUBTITLE) {
//            ((CustomViewHolder) viewHolder).tvReplys.setAlpha(ALPHA_SUBTITLE);
//        } else {
//            ((CustomViewHolder) viewHolder).tvReplys.setAlpha(((CustomViewHolder) viewHolder).tvReplys.getAlpha() + alpha);
//        }
    }

    @Override
    public void onItemBigResizeScrolled(RecyclerView.ViewHolder viewHolder, int position, int dyAbs) {
        ((CustomViewHolder) viewHolder).tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.font_xxxlarge));
        ((CustomViewHolder) viewHolder).tvAuthor.setAlpha(ALPHA_SUBTITLE);
        ((CustomViewHolder) viewHolder).tvPosttime.setAlpha(ALPHA_SUBTITLE);
//        ((CustomViewHolder) viewHolder).tvReplys.setAlpha(ALPHA_SUBTITLE);
    }

    @Override
    public void onItemSmallResizeScrolled(RecyclerView.ViewHolder viewHolder, int position, int dyAbs) {
        ((CustomViewHolder) viewHolder).tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.font_medium));
        ((CustomViewHolder) viewHolder).tvAuthor.setAlpha(ALPHA_SUBTITLE_HIDE);
        ((CustomViewHolder) viewHolder).tvPosttime.setAlpha(ALPHA_SUBTITLE_HIDE);
//        ((CustomViewHolder) viewHolder).tvReplys.setAlpha(ALPHA_SUBTITLE_HIDE);
    }

    @Override
    public void onItemSmallResize(RecyclerView.ViewHolder viewHolder, int position, int dyAbs) {
        if (((CustomViewHolder) viewHolder).tvContent.getTextSize() - (dyAbs / OFFSET_TEXT_SIZE) <= context.getResources().getDimension(R.dimen.font_medium)) {
            ((CustomViewHolder) viewHolder).tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.font_medium));
//            ((CustomViewHolder) viewHolder).tvContent.setSingleLine(true);
        } else {
            ((CustomViewHolder) viewHolder).tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((CustomViewHolder) viewHolder).tvContent.getTextSize() - (dyAbs / OFFSET_TEXT_SIZE));
        }

        float alpha = dyAbs / OFFSET_TEXT_ALPHA;
        if (((CustomViewHolder) viewHolder).tvAuthor.getAlpha() - alpha < ALPHA_SUBTITLE_HIDE) {
            ((CustomViewHolder) viewHolder).tvAuthor.setAlpha(ALPHA_SUBTITLE_HIDE);
        } else {
            ((CustomViewHolder) viewHolder).tvAuthor.setAlpha(((CustomViewHolder) viewHolder).tvAuthor.getAlpha() - alpha);
        }

        if (((CustomViewHolder) viewHolder).tvPosttime.getAlpha() - alpha < ALPHA_SUBTITLE_HIDE) {
            ((CustomViewHolder) viewHolder).tvPosttime.setAlpha(ALPHA_SUBTITLE_HIDE);
        } else {
            ((CustomViewHolder) viewHolder).tvPosttime.setAlpha(((CustomViewHolder) viewHolder).tvPosttime.getAlpha() - alpha);
        }

//        if (((CustomViewHolder) viewHolder).tvReplys.getAlpha() - alpha < ALPHA_SUBTITLE_HIDE) {
//            ((CustomViewHolder) viewHolder).tvReplys.setAlpha(ALPHA_SUBTITLE_HIDE);
//        } else {
//            ((CustomViewHolder) viewHolder).tvReplys.setAlpha(((CustomViewHolder) viewHolder).tvReplys.getAlpha() - alpha);
//        }
    }

    @Override
    public void onItemInit(RecyclerView.ViewHolder viewHolder) {
        ((CustomViewHolder) viewHolder).tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                context.getResources().getDimensionPixelSize(R.dimen.font_xxxlarge));
        ((CustomViewHolder) viewHolder).tvAuthor.setAlpha(ALPHA_SUBTITLE);
        ((CustomViewHolder) viewHolder).tvPosttime.setAlpha(ALPHA_SUBTITLE);
//        ((CustomViewHolder) viewHolder).tvReplys.setAlpha(ALPHA_SUBTITLE);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        // 作者
        TextView tvAuthor;
        // 发表时间
        TextView tvPosttime;
        // 回复数
        TextView tvReplys;
        // 内容
        TextView tvContent;
        // 图片
        ImageView topicImage;

        public CustomViewHolder(View v) {
            super(v);
            // 作者
            tvAuthor = (TextView) v.findViewById(R.id.tv_author);
            // 发表时间
            tvPosttime = (TextView) v.findViewById(R.id.tv_posttime);
            // 回复数
            tvReplys = (TextView) v.findViewById(R.id.tv_reply_count);
            // 内容
            tvContent = (TextView) v.findViewById(R.id.tv_content);
            // 图片
            topicImage = (ImageView) v.findViewById(R.id.topic_image);
        }
    }
}
