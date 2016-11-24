package health.rubbish.recycler.widget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import health.rubbish.recycler.R;


public class EmptyFiller {
    public static void fill(Activity activity,ListView listView,String description)
    {
        TextView textView = new TextView(activity);
        textView.setText(description);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(activity.getResources().getColor(R.color.base_color_text_gray));
        textView.setBackgroundResource(R.color.tranparent);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        activity.addContentView(textView, params);
        listView.setEmptyView(textView);
    }
}
