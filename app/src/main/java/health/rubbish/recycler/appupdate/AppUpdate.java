package health.rubbish.recycler.appupdate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppUpdate {
    private Context context;
    private String filePath;
    private boolean isMainActivity;

    public AppUpdate(Context context, boolean isMainActivity) {
        this.context = context;
        this.isMainActivity = isMainActivity;
    }

    public void update() {
        String url = "https://www.pgyer.com/apiv1/app/getAppKeyByShortcut";
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("shortcut", Pgyer.shortcut)
                .add("_api_key", Pgyer.apiKey)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    object = object.optJSONObject("data");
                    final String versionId = object.optString("appVersion");
                    String appKey = object.optString("appKey");
                    PackageManager packageManager = context.getPackageManager();
                    PackageInfo packageInfo = packageManager.getPackageInfo(
                            context.getPackageName(), 0);
                    String localVersion = packageInfo.versionName;
                    if (shouldUpdate(localVersion, versionId)) {
                        updateWithDiscription(appKey,versionId);
                    } else if (!isMainActivity) {
                        Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateWithDiscription(final String appKey,final String versionId) {
        String url = "https://www.pgyer.com/apiv1/app/view";
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("aKey", appKey)
                .add("uKey",  Pgyer.uKey)
                .add("_api_key", Pgyer.apiKey)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    object = object.optJSONObject("data");
                    String appUpdateDescription = object.optString("appUpdateDescription");

                    final String apkUrl = "https://www.pgyer.com/apiv1/app/install?aKey=" + appKey + "&_api_key=" + Pgyer.apiKey + "&password=" + Pgyer.password;
                    if (appUpdateDescription == null || appUpdateDescription.equals("")) {
                        appUpdateDescription = "有新版本，是否更新？";
                    }

                    new AlertDialog.Builder(context).setTitle("软件升级").setMessage(appUpdateDescription).setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            filePath = Environment.getExternalStorageDirectory() + "/recycler/app/医废管理-" + versionId + ".apk";
                            ApkDownload fileDownLoad = new ApkDownload(context, apkUrl, filePath);
                            fileDownLoad.download();
                        }
                    }).setNegativeButton("否", null).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean shouldUpdate(String local, String server) {
        String sbL = local.replaceAll("\\.","");
        String sbS = server.replaceAll("\\.","");
        return Integer.valueOf(sbS) > Integer.valueOf(sbL);
    }
}
