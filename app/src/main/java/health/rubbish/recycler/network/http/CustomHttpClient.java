package health.rubbish.recycler.network.http;


import health.rubbish.recycler.util.NetUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by xiayanlei on 2016/11/13.
 * 统一封装网络请求库，方便修改
 */
public class CustomHttpClient {

    private OkHttpClient okHttpClient;

    private FormBody.Builder builder;

    public CustomHttpClient() {
        okHttpClient = new OkHttpClient();
        builder = new FormBody.Builder();
    }

    public void addParam(String key, String value) {
        builder.add(key, value);
    }

    public void executeReq(String service, Callback callback) {
        RequestBody requestBody = builder.build();
        Call call = okHttpClient.newCall(NetUtil.getRequest(service, requestBody));
        call.enqueue(callback);
    }
}
