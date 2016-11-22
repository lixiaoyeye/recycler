package health.rubbish.recycler.activity.login;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import health.rubbish.recycler.base.App;
import health.rubbish.recycler.R;
import health.rubbish.recycler.activity.MainActivity;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.entity.LoginUser;
import health.rubbish.recycler.util.DateUtil;
/**
 * Created by Lenovo on 2016/11/20.
 */

public class SplashActivity extends BaseActivity {
    public static final int SPLASH_DURATION = 1500;
    private static final int GO_MAIN_MSG = 1;
    private static final int GO_LOGIN_MSG = 2;

    @Override
    protected int getLayoutId() {
        return R.layout.entry_splash_layout;
    }

    @Override
    protected void init() {
        if (autoLogin()) {
            handler.sendEmptyMessageDelayed(GO_MAIN_MSG, SPLASH_DURATION);
        } else {
            handler.sendEmptyMessageDelayed(GO_LOGIN_MSG, SPLASH_DURATION);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent();
            switch (msg.what) {
                case GO_MAIN_MSG:
                    intent.setClass(ctx, MainActivity.class);
                    break;
                case GO_LOGIN_MSG:
                    intent.setClass(ctx, LoginActivity.class);
                    break;
            }
            ctx.startActivity(intent);
            finish();
        }
    };

    private boolean autoLogin() {
        boolean result =false;
        LoginUser loginUser = App.getCurrentUser();
        if (loginUser!=null && DateUtil.equals(loginUser.sysdate,DateUtil.getDateString()))
        {
            result =true;
        }

        return result;
    }
}
