package health.rubbish.recycler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import health.rubbish.recycler.R;
import health.rubbish.recycler.entity.StatItem;

import java.util.List;

public class StatAdapter extends BaseAdapter {
    private Context mContext;
    private List<StatItem> data;
    private boolean showstatus = true;
    private String presuffix = "垃圾袋",postsuffix = "个";

    public StatAdapter(Context context, List<StatItem> data){
        this.mContext=context;
        this.data=data;
    }

    public StatAdapter(Context context, List<StatItem> data, boolean showstatus){
        this(context,data);
        this.showstatus = showstatus;
    }

    public StatAdapter(Context context, List<StatItem> data, boolean showstatus, String presuffix, String postsuffix){
        this(context,data,showstatus);
        this.presuffix = presuffix;
        this.postsuffix = postsuffix;
    }

    public void setList(List<StatItem> datas){
        this.data=datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.stat_item,null);
            holder=new ViewHolder();
            holder.stat_name = (TextView) view.findViewById(R.id.stat_name);
            holder.stat_num = (TextView) view.findViewById(R.id.stat_num);
            holder.stat_status= (ImageView) view.findViewById(R.id.stat_status);

            view.setTag(holder);
        }else{
            holder= (ViewHolder) view.getTag();
        }

        holder.stat_num.setText(presuffix+data.get(i).num+postsuffix);
        holder.stat_name.setText(data.get(i).name);
        holder.stat_status.setVisibility(showstatus?View.VISIBLE:View.GONE);

        return view;
    }

    static class ViewHolder{
        TextView stat_name;
        TextView stat_num;
        ImageView stat_status;
    }
}
