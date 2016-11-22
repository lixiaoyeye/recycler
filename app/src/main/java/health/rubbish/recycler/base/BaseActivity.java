package health.rubbish.recycler.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import health.rubbish.recycler.R;
import health.rubbish.recycler.widget.CustomProgressDialog;

/**
 * Created by xiayanlei on 2016/11/13.
 * 所有activity的基类
 */
public abstract class BaseActivity extends FragmentActivity {
    protected Context ctx;
    public View mDialogView;
    public CustomProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ctx = this;
        init();
    }

    /**
     * 页面布局
     */
    protected abstract int getLayoutId();

    /**
     * 初始化页面，设置页面监听等操作
     */
    protected abstract void init();

    protected void showDialog() {
        if (mDialog == null) {
            mDialog = new CustomProgressDialog(this);
        }
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setMessage(getString(R.string.dialog_msg_laoding));
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    protected void hideDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void hideSoftInputView() {
        if (getWindow().getAttributes().softInputMode !=
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                manager.hideSoftInputFromWindow(currentFocus.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    protected void setSoftInputMode() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    protected void initActionBar() {
        initActionBar(null);
    }

    protected void initActionBar(String title) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            if (title != null) {
                actionBar.setTitle(title);
            }
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);//给左上角设置一个返回的图标
        }
    }

    protected void initActionBar(int id) {
        initActionBar(getString(id));
    }

    protected void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    protected void toast(Exception e) {
        if (e != null) {
            toast(e.getMessage());
        }
    }

    protected void toast(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

}
