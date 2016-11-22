package health.rubbish.recycler.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import health.rubbish.recycler.R;

/**
 * Created by Lenovo on 2016/11/20.
 */

public class HeaderLayout extends LinearLayout {
    private Context context;
    LayoutInflater mInflater;
    RelativeLayout header;
    TextView titleView, rightText;
    LinearLayout leftContainer, rightContainer, centerContainer;
    Button backBtn;
    ImageView rightImage;

    public HeaderLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        header = (RelativeLayout) mInflater.inflate(R.layout.chat_common_base_header, null, false);
        titleView = (TextView) header.findViewById(R.id.titleView);
        leftContainer = (LinearLayout) header.findViewById(R.id.leftContainer);
        rightContainer = (LinearLayout) header.findViewById(R.id.rightContainer);
        centerContainer = (LinearLayout) header.findViewById(R.id.centerContainer);
        backBtn = (Button) header.findViewById(R.id.backBtn);
        rightText = (TextView) header.findViewById(R.id.right_sure_text);
        rightImage = (ImageView) header.findViewById(R.id.right_im);
        addView(header);
    }

    public void showTitle(int titleId) {
        titleView.setText(titleId);
    }

    public void showTitle(String s) {
        titleView.setText(s);
    }

    public void showLeftBackButton() {
        showLeftBackButton(null);
    }

    public void showLeftBackButton(OnClickListener listener) {
        showLeftBackButton("", listener);
    }
    public void showLeftBackButton(String str, OnClickListener listener) {
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setText(str);
        if (listener == null) {
            listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) getContext()).finish();
                }
            };
        }
        backBtn.setOnClickListener(listener);
    }


    public void showRightImageButton(int rightResId, OnClickListener listener) {
        rightImage.setVisibility(View.VISIBLE);
        rightImage.setImageResource(rightResId);
        rightImage.setOnClickListener(listener);
    }

    public void showRightTextButton(String str, OnClickListener listener) {
        rightText.setVisibility(View.VISIBLE);
        rightText.setText(str);
        rightText.setOnClickListener(listener);
    }


    public void isShowRightText(boolean is) {
        if (is) {
            rightText.setVisibility(View.VISIBLE);
        } else {
            rightText.setVisibility(View.GONE);
        }
    }


    public TextView getRightTxtVew() {
        return rightText;
    }

    public void setRightTextSize(float size) {
        rightText.setTextSize(size);
    }

    public void setRightTextListener(OnClickListener l) {
        rightText.setOnClickListener(l);
    }


    public void isShowBackButton(boolean is) {
        if (is) {
            showLeftBackButton();
        } else {
            backBtn.setVisibility(View.GONE);
        }
    }


    public LinearLayout getRightContainer() {
        return rightContainer;
    }

    public LinearLayout getCenterContainer() {
        titleView.setVisibility(View.GONE);
        return centerContainer;
    }
}
