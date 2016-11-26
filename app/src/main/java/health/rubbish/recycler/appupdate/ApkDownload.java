package health.rubbish.recycler.appupdate;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import health.rubbish.recycler.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.framed.FrameReader;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ApkDownload {

    private String FILE_PATH;
    private Context context;

    private String url;
    private NotificationManager manager;

    NotificationCompat.Builder mBuilder;

    public ApkDownload(Context context, String url, String path) {
        this.context = context;
        this.url = url;
        FILE_PATH = path;
        initNotify();
    }


    private void initNotify() {
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setWhen(System.currentTimeMillis()).setPriority(Notification.PRIORITY_DEFAULT);
        mBuilder.setOngoing(true).setSmallIcon(R.drawable.icon);
        mBuilder.setTicker("医废管理更新开始...");
    }


    public void download() {
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                                .build();
                    }
                })
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e) {
                manager.cancel(0);
                File appFile = new File(FILE_PATH);
                if(appFile.exists()){
                    new AlertDialog.Builder(context).setTitle("医废管理").setMessage("已下载更新包，是否安装？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            appInstall();
                        }
                    }).setNegativeButton("取消",null).show();
                }else{
                    new AlertDialog.Builder(context).setTitle("医废管理").setMessage("更新失败，请重试").setPositiveButton("确定", null).show();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response != null) {
                    //下载完成，保存数据到文件
                    InputStream is = response.body().byteStream();
                    FileOutputStream fos = new FileOutputStream(FILE_PATH);
                    byte[] buf = new byte[1024];
                    int hasRead = 0;
                    while((hasRead = is.read(buf)) > 0) {
                        fos.write(buf, 0, hasRead);
                    }
                    fos.close();
                    is.close();
                    manager.cancel(0);
                    new AlertDialog.Builder(context).setTitle("医废管理").setMessage("更新完成，立即安装？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            appInstall();
                        }
                    }).show();
                }

            }
        });
    }


    private void appInstall() {
        File appFile = new File(FILE_PATH);
        if (appFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String apkPath = "file://" + appFile.toString();
            Uri uri = Uri.parse(apkPath);
            String type = "application/vnd.android.package-archive";
            intent.setDataAndType(uri, type);
            context.startActivity(intent);
        } else {
            return;
        }
    }



    private ProgressListener progressListener = new ProgressListener() {
        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
            int progress = (int) (100.0 * bytesRead / contentLength);
            mBuilder.setContentTitle("医废管理");
            mBuilder.setContentText("已下载" + progress + "%");
            mBuilder.setProgress(100, progress, false);
            manager.notify(0, mBuilder.build());
        }
    };

    //自定义的ResponseBody，在其中处理进度
    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override public long contentLength() {
            return responseBody.contentLength();
        }

        @Override public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }

    //进度回调接口
    interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }

}
