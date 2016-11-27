package health.rubbish.recycler.widget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.entity.PopupMenuItem;


public class CenterGridPopupWindow {

    public void showPopupWindow(Activity context, View parentView, String title, List<PopupMenuItem> list, final BottomPopupItemClickListener itemClickListener) {
        final View view = context.getLayoutInflater().inflate(R.layout.centergridpopwindow, null);
        final PopupWindow pop = new PopupWindow(view, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new ColorDrawable());
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);

        WindowManager windowManager = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        pop.setWidth(displayMetrics.widthPixels);
        pop.setHeight(displayMetrics.heightPixels);
        pop.showAtLocation(parentView, Gravity.NO_GRAVITY,0, 0);

        TextView centergrid_title = (TextView)view.findViewById(R.id.centergrid_title) ;
        centergrid_title.setText(title);
        RelativeLayout centergrid_layout = (RelativeLayout)view.findViewById(R.id.centergrid_layout) ;
        centergrid_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
            }
        });

        GridView bottompop_listview = (GridView) view.findViewById(R.id.centergrid_grid);
        bottompop_listview.setAdapter(new MenuAdapter(context,list));
        bottompop_listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemClickListener.onItemClick(i);
                pop.dismiss();
            }
        } );
    }

    class  MenuAdapter extends BaseAdapter {
        Context context = null;
        List<PopupMenuItem> list = null;

        public MenuAdapter(Context context,List<PopupMenuItem> list)
        {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.centerpop_item_layout, null);
                holder.tagTextView = (TextView) convertView
                        .findViewById(R.id.function_tag);
                holder.dynamic_tip_num = (TextView) convertView
                        .findViewById(R.id.dynamic_tip_num);
                holder.dynamic_tip_num1 = (TextView) convertView
                        .findViewById(R.id.dynamic_tip_num1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            PopupMenuItem bean = list.get(position);
            Drawable drawable = context.getApplicationContext().getResources().getDrawable(bean.icon);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.tagTextView.setText(bean.name);
            holder.tagTextView.setCompoundDrawables(null, drawable, null, null);
            holder.tagTextView.setCompoundDrawablePadding(10);
            holder.dynamic_tip_num.setText(""+bean.num);
            holder.dynamic_tip_num.setVisibility(bean.num==0?View.GONE:View.VISIBLE);
            holder.dynamic_tip_num1.setVisibility(View.GONE);
            return convertView;
        }

        class ViewHolder {
            private TextView tagTextView;// 模块功能描述
            private TextView dynamic_tip_num;
            private TextView dynamic_tip_num1;
        }
    }
}
