package health.rubbish.recycler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.entity.TrashItem;

/**
 * Created by xiayanlei on 2016/11/24.
 */
public class WasteListAdapter extends BaseAdapter {

    List<TrashItem> wasteItems = new ArrayList<>();

    public void setWasteItems(List<TrashItem> wasteItems) {
        this.wasteItems = wasteItems;
        notifyDataSetChanged();
    }

    /**
     * 获取需要上传的垃圾
     * @return
     */
    public List<TrashItem> getNeedUploadTrash() {
        List<TrashItem> items = new ArrayList<>();
        if (wasteItems != null){
            for (TrashItem item : wasteItems) {
                if ("0".equals(item.status))
                    items.add(item);
            }
        }
        return items;
    }

    @Override
    public int getCount() {
        return wasteItems == null ? 0 : wasteItems.size();
    }

    @Override
    public Object getItem(int position) {
        return wasteItems == null ? null : wasteItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.waste_list_item, parent, false);
            holder.wasteIdText = (TextView) convertView.findViewById(R.id.item_waste_id);
            holder.stateText = (TextView) convertView.findViewById(R.id.item_waste_state);
            holder.garbageCanText = (TextView) convertView.findViewById(R.id.item_garbagecan_id);
            holder.dtmText = (TextView) convertView.findViewById(R.id.item_dtm);
            holder.roomText = (TextView) convertView.findViewById(R.id.item_room);
            holder.typText = (TextView) convertView.findViewById(R.id.item_waste_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TrashItem item = wasteItems.get(position);
        holder.wasteIdText.setText(item.trashcode);
        holder.stateText.setText(item.getStatusNam());
        holder.garbageCanText.setText(item.trashcancode);
        holder.dtmText.setText(item.colletime);
        holder.roomText.setText(item.departname);
        holder.typText.setText(item.categoryname);
        return convertView;
    }

    static class ViewHolder {
        TextView wasteIdText;
        TextView garbageCanText;
        TextView dtmText;
        TextView roomText;
        TextView typText;
        TextView stateText;
    }
}
