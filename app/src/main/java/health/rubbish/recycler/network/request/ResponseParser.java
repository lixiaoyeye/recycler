package health.rubbish.recycler.network.request;

/**
 * Created by xiayanlei on 2016/11/13.
 * 网络请求结果解析器
 */
public abstract class ResponseParser<T> {

    /**
     * @param e - 有异常的时候非空，无异常的时候为空
     * @param t - 返回值
     */
    public abstract void onParserComplete(Exception e, T t);
}
