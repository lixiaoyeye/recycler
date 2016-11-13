package health.rubbish.recycler.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by xiayanlei on 2016/11/13.
 * 所有activity的基类
 */
public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initViewAndEvents();
    }

    /**
     * 页面布局
     */
    protected abstract int getLayoutId();

    /**
     * 初始化页面，设置页面监听等操作
     */
    protected abstract void initViewAndEvents();
}
