package health.rubbish.recycler.network.request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.network.entity.MultiTrashResp;
import health.rubbish.recycler.network.entity.WasteUploadResp;

/**
 * Created by xiayanlei on 2016/11/24.
 * 网络请求响应的数据解析类
 */
public class ParseUtil {

    private static final String RESULT_OK = "success";

    /**
     * @param json
     * @return 垃圾上传的响应
     */
    public List<WasteUploadResp> parseWasteUploadResp(String json) {
        List<WasteUploadResp> resps = null;
        try {
            JSONObject object = new JSONObject(json);
            String result = object.optString("result");
            if (RESULT_OK.equals(result)) {
                JSONArray array = object.optJSONArray("rows");
                resps = com.alibaba.fastjson.JSONObject.parseArray(array.toString(), WasteUploadResp.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resps;
    }

    /**
     * 解析下载数据
     *
     * @param json
     */
    public List<MultiTrashResp> parsDownloadResp(String json) {
        List<MultiTrashResp> multiTrashResps = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(json);
            JSONArray array = object.optJSONArray("rows");
            //multiTrashResps = com.alibaba.fastjson.JSONObject.parseArray(array.toString(), MultiTrashResp.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return multiTrashResps;
    }
}
