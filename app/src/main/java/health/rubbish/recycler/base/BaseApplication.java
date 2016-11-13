package health.rubbish.recycler.base;

import android.app.Application;

/**
 * Created by xiayanlei on 2016/11/13.
 */
public class BaseApplication extends Application {

    private static BaseApplication instance;

    public static BaseApplication getInstance() {
        if (instance == null)
            synchronized (BaseApplication.class) {
                if (instance == null)
                    instance = new BaseApplication();
            }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
