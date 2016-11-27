package health.rubbish.recycler.network.http;

import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import health.rubbish.recycler.network.request.HttpConstants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by xiayanlei on 2016/11/23.
 */
public abstract class CustomCallback implements Callback {

    private static final String TAG = "CustomCallback";

    /**
     * 结果统一封装成消息，因为okhttp的回调不在主线程
     *
     * @param message
     */
    public abstract void onResponse(Message message);

    @Override
    public void onFailure(Call call, IOException e) {
        Message message = Message.obtain();
        message.what = HttpConstants.RESULT_FAIL;
        message.obj = e.getMessage();
        onResponse(message);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(response.body().byteStream(), "utf-8"));//防止乱码
        String line;
        String result = "";
        while ((line = in.readLine()) != null) {
            result += line;
        }
        Log.d(TAG, "response = " + result);
        Message message = Message.obtain();
        message.what = HttpConstants.RESULT_OK;
        message.obj = result;
        onResponse(message);
    }
}
