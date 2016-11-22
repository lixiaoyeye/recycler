package health.rubbish.recycler.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import health.rubbish.recycler.base.App;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Lenovo on 2016/11/20.
 */

public class NetUtil {

    public static boolean isNetAvailable() {
        ConnectivityManager manager = (ConnectivityManager) App.ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isAvailable());
    }

    public static String getServiceUrl(String action) {
        SharedPreferences sharedPreferences =  App.ctx.getSharedPreferences("address", Activity.MODE_PRIVATE);
        String tempIp = sharedPreferences.getString("ip", null);
        String tempPort = sharedPreferences.getString("port", null);
        String tempUrl = "http://" + tempIp + ":" + tempPort + "/Liems/webservice/" + action;
        return tempUrl;
    }

    public static Request getRequest(String action,RequestBody requestBody)
    {
        Request request = new Request.Builder()
                .url(getServiceUrl(action))
                .post(requestBody)
                .build();
        return request;
    }
}
