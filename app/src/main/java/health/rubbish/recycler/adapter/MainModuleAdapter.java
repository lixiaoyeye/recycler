package health.rubbish.recycler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.config.Config;

/**
 * Created by xiayanlei on 2016/11/13.
 */
public class MainModuleAdapter extends BaseAdapter {

    private List<HashMap<String, Integer>> modules = new ArrayList<>();

    public MainModuleAdapter() {
        modules = Config.getMainModules();
    }

    @Override
    public int getCount() {
        return modules == null ? 0 : modules.size();
    }

    @Override
    public Object getItem(int position) {
        return modules == null ? null : modules.get(position);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_module_item, parent, false);
            holder.moduleText = (TextView)convertView.findViewById(R.id.main_module_text) ;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HashMap<String, Integer> module = modules.get(position);
        holder.moduleText.setText(module.get(Config.MODULE_NAME));
        return convertView;
    }

    public Integer getModule(int position) {
        return modules.get(position).get(Config.MODULE_NAME);
    }

    static class ViewHolder {
        TextView moduleText;
        ImageView moduleImage;
    }
}
