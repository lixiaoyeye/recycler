package health.rubbish.recycler.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import health.rubbish.recycler.R;


/**
 * Created by xiayanlei on 2016/11/13.
 * 页面头布局
 */
public class HeaderLayout extends RelativeLayout {

    private Button leftBtn;
    private TextView titleText;
    private Button rightBtn;

    public HeaderLayout(Context context) {
        this(context, null);
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化头布局
     */
    private void init() {
        View view = inflate(getContext(), R.layout.widget_headerlayout, null);
        leftBtn = (Button) view.findViewById(R.id.left_btn);
        titleText = (TextView) view.findViewById(R.id.title_text);
        rightBtn = (Button) view.findViewById(R.id.right_btn);
        addView(view);
    }
}
