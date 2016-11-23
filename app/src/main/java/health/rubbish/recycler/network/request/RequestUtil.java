package health.rubbish.recycler.network.request;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import health.rubbish.recycler.activity.collection.WasteItem;
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

    public RequestUtil() {
        parseUtil = new ParseUtil();
    }

    /**
     * 登录请求，返回解析后的结果
     *
     * @param username
     * @param password
     * @param parser
     */
    public void startLogin(String username, String password, ParseCallback parser) {
        CustomHttpClient client = new CustomHttpClient();
    }

    public void getWasteList(final int page, int limit, final ParseCallback parser) {
        CustomHttpClient client = new CustomHttpClient();
        client.addParam("page", String.valueOf(page));
        client.addParam("limit", String.valueOf(limit));
        client.addParam("userid", Utils.getUserId());
        client.executeReq(UrlConstants.WASTE_LIST, new CustomCallback() {
            @Override
            public void onResponse(String result) {
                parser.onComplete(result);
            }

            @Override
            public void onFailure(String error) {
                parser.onError(error);
            }
        });
    }

    /**
     * 上传垃圾信息
     *
     * @param wasteItems
     * @param callback
     */
    public void uploadWaste(List<WasteItem> wasteItems, final ParseCallback callback) {
        CustomHttpClient client = new CustomHttpClient();
        client.addParam("userid", Utils.getUserId());
        client.addParam("datas", revertToJsonString(wasteItems));
        client.executeReq("uploadCollectTrashInfo", new CustomCallback() {
            @Override
            public void onResponse(String result) {
                callback.onComplete(parseUtil.parseWasteUploadResp(result));
            }

            @Override
            public void onFailure(String error) {
                callback.onError(error);
            }
        });
    }

    /**
     * 下载垃圾信息(数据状态由已上传(1)转为已下载(2))
     *
     * @param date     日期格式：2016-11-17
     * @param type     0 全量下载，1 增量下载
     * @param callback
     */
    public void downloadWasteIno(String date, String type, final ParseCallback callback) {
        CustomHttpClient client = new CustomHttpClient();
        client.addParam("userid", Utils.getUserId());
        client.addParam("date", date);
        client.addParam("type", type);
        client.executeReq("downloadMultiTrashInfo", new CustomCallback() {
            @Override
            public void onResponse(String result) {
                callback.onComplete(parseUtil.parsDownloadResp(result));
            }

            @Override
            public void onFailure(String error) {
                callback.onError(error);
            }
        });
    }

    /**
     * @param wasteItems
     * @return 垃圾信息的json数组
     */
    private String revertToJsonString(List<WasteItem> wasteItems) {
        JSONArray array = new JSONArray();
        try {
            for (WasteItem item : wasteItems) {
                JSONObject object = new JSONObject();
                object.put("collectno", item.wasteId);
                object.put("rfidno", item.canId);//这个不清楚
                object.put("trashcancode", item.canId);
                object.put("trashcode", "");//
                object.put("colletime", item.dtm);
                object.put("departareacode", "");//
                object.put("departcode", item.roomNo);
                object.put("nurseid", item.nurseId);
                object.put("categorycode", item.typNo);
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
