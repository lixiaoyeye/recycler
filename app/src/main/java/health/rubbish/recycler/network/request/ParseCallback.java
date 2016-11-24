package health.rubbish.recycler.network.request;

/**
 * Created by xiayanlei on 2016/11/13.
 * 网络请求结果解析器
 */
public abstract class ParseCallback<T> {

    /**
     * @param t - 返回值
     */
    public abstract void onComplete(T t);

    /**
     * 服务器异常报错
     *
     * @param error
     */
    public abstract void onError(String error);
}
