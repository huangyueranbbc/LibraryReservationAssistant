package university.huangyueran.polytechnic.com.libraryreservationassistant.ui.adapter.yuyueadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import university.huangyueran.polytechnic.com.libraryreservationassistant.R;
import university.huangyueran.polytechnic.com.libraryreservationassistant.domain.TbReadroom;

/**
 * listview 阅览室Adapter
 */
public class ReadRoomAdapter extends ArrayAdapter<TbReadroom> {

    private final LayoutInflater mInflater;
    private final List<TbReadroom> mData;

    public ReadRoomAdapter(Context context, int layoutResourceId, List<TbReadroom> data) {
        super(context, layoutResourceId, data);
        mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            viewHolder.imageViewIcon = (ImageView) convertView.findViewById(R.id.image_view_icon);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageViewIcon.setImageResource(R.drawable.icon_2);
        viewHolder.tvName.setText(mData.get(position).getRoomName());

        convertView.setBackgroundResource(R.color.saffron);

        return convertView;
    }

    class ViewHolder {
        ImageView imageViewIcon;
        TextView tvName;
    }

}