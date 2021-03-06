package health.rubbish.recycler.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.entity.Module;

/**
 * Created by xiayanlei on 2016/11/13.
 */
public class MainModuleAdapter extends BaseAdapter {

    private List<Module> modules = new ArrayList<>();
    private Context context;
    private int screenWidth,screenHeight;

    public MainModuleAdapter(Context context, List<Module> modules) {
        this.modules =modules;
        this.context = context;

        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
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
            holder.moduletipnum = (TextView) convertView
                    .findViewById(R.id.main_module_tip_num);
            holder.moduletipnum1 = (TextView) convertView
                    .findViewById(R.id.main_module_tip_num1);
            holder.modulediverView = (View) convertView
                    .findViewById(R.id.main_module_diver);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Module module = modules.get(position);
        holder.moduleText.setText(module.name);

        Drawable drawable = context.getResources().getDrawable(module.icon);
        holder.moduleText.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        holder.moduleText.setCompoundDrawablePadding(10);
        holder.moduletipnum.setVisibility(View.GONE);
        holder.moduletipnum1.setVisibility(module.shortcut == 0?View.GONE:View.VISIBLE);
        ViewGroup.LayoutParams params = holder.modulediverView.getLayoutParams();
        params.height = screenWidth / 3;
        holder.modulediverView.setLayoutParams(params);
        return convertView;
    }

    public Integer getModule(int position) {
        return modules.get(position).name;
    }

    static class ViewHolder {
        TextView moduletipnum;
        TextView moduletipnum1;
        View modulediverView;
        TextView moduleText;
    }
}
