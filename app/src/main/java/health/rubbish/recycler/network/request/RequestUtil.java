package health.rubbish.recycler.network.request;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.network.http.CustomCallback;
import health.rubbish.recycler.network.http.CustomHttpClient;
import health.rubbish.recycler.util.Utils;

/**
 * Created by xiayanlei on 2016/11/13.
 * 所有页面的网络请求
 */
public class RequestUtil {

    private static final String TAG = "RequestUtil";

    private ParseUtil parseUtil;

    private ParseCallback callback;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg == null)
                return;
            switch (msg.what) {
                case HttpConstants.RESULT_OK:
                    if (callback != null)
                        callback.onComplete(msg.obj);
                    break;
                case HttpConstants.RESULT_FAIL:
                    if (callback != null)
                        callback.onError((String) msg.obj);
                    break;
            }
        }
    };

    public RequestUtil() {
        parseUtil = new ParseUtil();
    }

    public RequestUtil(ParseCallback callback) {
        parseUtil = new ParseUtil();
        this.callback = callback;
    }

    /**
     * 登录请求，返回解析后的结果
     *
     * @param username
     * @param password
     */
    public void startLogin(String username, String password) {
        CustomHttpClient client = new CustomHttpClient();
        client.addParam("userid", String.valueOf(username));
        client.addParam("password", password);
        client.executeReq(HttpConstants.LOGIN, new CustomCallback() {
            @Override
            public void onResponse(Message message) {
                if (handler != null)
                    handler.sendMessage(message);
            }
        });
    }

    public void getWasteList(final int page, int limit) {
        CustomHttpClient client = new CustomHttpClient();
        client.addParam("page", String.valueOf(page));
        client.addParam("limit", String.valueOf(limit));
        client.addParam("userid", Utils.getUserId());
        client.executeReq(HttpConstants.WASTE_LIST, new CustomCallback() {
            @Override
            public void onResponse(Message message) {
                if (handler != null)
                    handler.sendMessage(message);
            }
        });
    }

    /**
     * 上传垃圾信息
     *
     * @param wasteItems
     */
    public void uploadWaste(List<TrashItem> wasteItems) {
        CustomHttpClient client = new CustomHttpClient();
        client.addParam("userid", Utils.getUserId());
        client.addParam("datas", revertToJsonString(wasteItems));
        client.executeReq("uploadCollectTrashInfo", new CustomCallback() {
            @Override
            public void onResponse(Message message) {
                String result = (String) message.obj;
                message.obj = parseUtil.parseWasteUploadResp(result);
                if (handler != null)
                    handler.sendMessage(message);
            }
        });
    }

    /**
     * 下载垃圾信息(数据状态由已上传(1)转为已下载(2))
     *
     * @param date 日期格式：2016-11-17
     * @param type 0 全量下载，1 增量下载
     */
    public void downloadWasteIno(String date, String type) {
        CustomHttpClient client = new CustomHttpClient();
        client.addParam("userid", Utils.getUserId());
        client.addParam("date", date);
        client.addParam("type", type);
        client.executeReq("downloadMultiTrashInfo", new CustomCallback() {
            @Override
            public void onResponse(Message message) {
                if (handler != null)
                    handler.sendMessage(message);
            }
        });
    }


    public void sendPost(String service,CustomHttpClient client,ParseCallback callback) {
        this.callback = callback;
        client.executeReq(service, new CustomCallback() {
            @Override
            public void onResponse(Message message) {
                if (handler != null)
                    handler.sendMessage(message);
            }
        });
    }

    /**
     * @param wasteItems
     * @return 垃圾信息的json数组
     */
    private String revertToJsonString(List<TrashItem> wasteItems) {
        JSONArray array = new JSONArray();
        try {
            for (TrashItem item : wasteItems) {
                JSONObject object = new JSONObject();
                object.put("collectno", item.trashcode);
                object.put("rfidno", item.trashcancode);//这个不清楚
                object.put("trashcancode", item.trashcancode);
                object.put("trashcode", item.trashcode);//
                object.put("colletime", item.colletime);
                object.put("departareacode", item.departareacode);//
                object.put("departcode", item.departcode);
                object.put("nurseid", item.nurseid);
                object.put("categorycode", item.categorycode);
                object.put("weight", item.weight);
                array.put(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String json = array.toString();
        Log.i(TAG, "datas = " + json);
        return json;
    }
}
