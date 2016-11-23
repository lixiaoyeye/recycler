package health.rubbish.recycler.network.http;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by xiayanlei on 2016/11/23.
 */
public abstract class CustomCallback implements Callback {

    private static final String TAG = "CustomCallback";

    /**
     * @param result 返回请求值
     */
    public abstract void onResponse(String result);

    /**
     * 服务器异常报错
     *
     * @param error
     */
    public abstract void onFailure(String error);

    @Override
    public void onFailure(Call call, IOException e) {
        onFailure(e.getMessage());
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Log.i(TAG, "response = " + response.body().string());
        onResponse(response.body().string());
    }
}
