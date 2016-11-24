package health.rubbish.recycler.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.entity.TrashItem;

public class TrashListAdapter extends BaseAdapter {

    private Context context;

    private List<TrashItem> data = new ArrayList<>();

    public TrashListAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<TrashItem> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int i) {
        return data == null ? null : data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.trashlist_item, null);
            holder.item_status = (TextView) view.findViewById(R.id.trashlist_item_status);
            holder.item_trashcode = (TextView) view.findViewById(R.id.trashlist_item_trashcode);
            holder.item_trashcan = (TextView) view.findViewById(R.id.trashlist_item_trashcan);
            holder.item_time = (TextView) view.findViewById(R.id.trashlist_item_time);
            holder.item_depart = (TextView) view.findViewById(R.id.trashlist_item_depart);
            holder.trashlist_item_category = (TextView) view.findViewById(R.id.trashlist_item_category);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        TrashItem bean = data.get(i);
        holder.item_trashcode.setText(bean.trashcode);
        holder.item_trashcan.setText(bean.trashcancode);
        holder.item_time.setText(bean.colletime);
        holder.item_depart.setText(bean.departname);
        holder.trashlist_item_category.setText(bean.categoryname);
        if (!TextUtils.isEmpty(bean.status) && bean.status.equals(Constant.Status.NEWCOLLECT)) {
            holder.item_status.setText("收集");
            holder.item_status.setBackgroundResource(R.drawable.status_newcollect);
        } else if (!TextUtils.isEmpty(bean.status) && bean.status.equals(Constant.Status.UPLOAD)) {
            holder.item_status.setText("上传");
            holder.item_status.setBackgroundResource(R.drawable.status_upload);
        }else if (!TextUtils.isEmpty(bean.status) && bean.status.equals(Constant.Status.DOWNLOAD)) {
            holder.item_status.setText("下载");
            holder.item_status.setBackgroundResource(R.drawable.status_download);
        }else if (!TextUtils.isEmpty(bean.status) && bean.status.equals(Constant.Status.TRASFER)) {
            holder.item_status.setText("转储");
            holder.item_status.setBackgroundResource(R.drawable.status_transfer);
        }else if (!TextUtils.isEmpty(bean.status) && bean.status.equals(Constant.Status.ENTRUCKER)) {
            holder.item_status.setText("装车");
            holder.item_status.setBackgroundResource(R.drawable.status_entrucker);
        }
        return view;
    }

    class ViewHolder {
        public TextView item_status;
        public TextView item_trashcode;
        public TextView item_trashcan;
        public TextView item_time;
        public TextView item_depart;
        public TextView trashlist_item_category;
    }
}
