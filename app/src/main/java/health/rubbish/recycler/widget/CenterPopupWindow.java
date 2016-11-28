package health.rubbish.recycler.widget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;


import java.util.List;

import health.rubbish.recycler.R;

public class CenterPopupWindow {

    public void showPopupWindow(Activity context, View parentView, List<String> label, final BottomPopupItemClickListener itemClickListener) {
        String[] strArray = new String[label.size()];
        for (int i= 0;i<label.size();i++)
        {
            strArray[i] = label.get(i);
        }
        showPopupWindow(context, parentView, strArray, itemClickListener);
    }

    public void showPopupWindow(Activity context, View parentView, String[] label, final BottomPopupItemClickListener itemClickListener) {

        final View view = context.getLayoutInflater().inflate(R.layout.centerpopwindow, null);
        final PopupWindow pop = new PopupWindow(view, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        //pop.setAnimationStyle(R.style.FolerFucPopupAnimation);
        pop.setBackgroundDrawable(new ColorDrawable());
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);

        WindowManager windowManager = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        pop.setWidth(displayMetrics.widthPixels);
        pop.setHeight(displayMetrics.heightPixels);
        pop.showAtLocation(parentView, Gravity.NO_GRAVITY,0, 0);

        RelativeLayout centerpop_layout = (RelativeLayout) view.findViewById(R.id.centerpop_layout);
        centerpop_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
            }
        });

        ListView centerpop_listview = (ListView) view.findViewById(R.id.centerpop_listview);
        centerpop_listview.setAdapter(new ArrayAdapter<String>(context,R.layout.listview_item,label));
        centerpop_listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemClickListener.onItemClick(i);
                pop.dismiss();
            }
        } );
    }
}
