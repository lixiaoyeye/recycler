package health.rubbish.recycler.base;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import health.rubbish.recycler.entity.LoginUser;
import health.rubbish.recycler.util.LoginUtil;

/**
 * Created by Lenovo on 2016/11/20.
 */

public class App extends Application {

    public static App ctx;
    private static LoginUser currentUser = null;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        currentUser = LoginUtil.getLoginUser();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public static LoginUser getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(LoginUser currentUser) {
        App.currentUser = currentUser;
    }


    public int getAppVersion() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(),
                    0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
