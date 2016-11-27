package health.rubbish.recycler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.entity.CatogeryItem;
import health.rubbish.recycler.entity.DepartmentItem;

/**
 * Created by xiayanlei on 2016/11/27.
 * 外键选择适配器
 */
public class ReferenceAdapter<T> extends BaseAdapter {

    private List<T> datas = new ArrayList<>();

    private int type = -1;

    public ReferenceAdapter(int type) {
        this.type = type;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas == null ? null : datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reference_item, parent, false);
            holder.nameText = (TextView) convertView.findViewById(R.id.name_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        T t = datas.get(position);
        if (t instanceof DepartmentItem) {
            if (type == Constant.Reference.DEPART)
                holder.nameText.setText(((DepartmentItem) t).departname);
            else if (type == Constant.Reference.NURSE)
                holder.nameText.setText(((DepartmentItem) t).nurse);
            else if (type == Constant.Reference.AREA)
                holder.nameText.setText(((DepartmentItem) t).departarea);
        } else if (t instanceof CatogeryItem) {
            holder.nameText.setText(((CatogeryItem) t).categoryname);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView nameText;
    }
}
