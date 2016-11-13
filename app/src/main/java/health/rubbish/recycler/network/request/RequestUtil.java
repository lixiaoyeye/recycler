package health.rubbish.recycler.network.request;

import health.rubbish.recycler.network.http.CustomHttpClient;

/**
 * Created by xiayanlei on 2016/11/13.
 * 所有页面的网络请求
 */
public class RequestUtil {

    private static volatile RequestUtil instance;

    public static RequestUtil getInstance() {
        if (instance == null)
            synchronized (RequestUtil.class) {
                if (instance == null)
                    instance = new RequestUtil();
            }
        return instance;
    }

    /**
     * 登录请求，返回解析后的结果
     *
     * @param username
     * @param password
     * @param parser
     */
    public void startLogin(String username, String password, ResponseParser parser) {
        CustomHttpClient client = new CustomHttpClient();
    }
}
