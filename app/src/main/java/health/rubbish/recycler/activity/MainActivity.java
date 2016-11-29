package health.rubbish.recycler.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.activity.collection.WasteListActivity;
import health.rubbish.recycler.activity.entruck.EntruckerListActivity;
import health.rubbish.recycler.activity.query.QueryActivity;
import health.rubbish.recycler.activity.stat.StatHomeActivity;
import health.rubbish.recycler.activity.transfer.TransferListActivity;
import health.rubbish.recycler.adapter.MainModuleAdapter;
import health.rubbish.recycler.appupdate.AppUpdate;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.config.Config;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.Module;
import health.rubbish.recycler.entity.PopupMenuItem;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.util.DateUtil;
import health.rubbish.recycler.util.LoginUtil;
import health.rubbish.recycler.util.NetUtil;
import health.rubbish.recycler.widget.BottomPopupItemClickListener;
import health.rubbish.recycler.widget.CenterGridPopupWindow;
import health.rubbish.recycler.widget.ExpandGridView;
import health.rubbish.recycler.widget.HeaderLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 主功能界面
 */
public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    ExpandGridView gridView;
    private MainModuleAdapter adapter;
    private List<Module> modules = new ArrayList<>();
    private boolean autoupdate = true;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        apkAutoUpdate();
        HeaderLayout headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
        headerLayout.showTitle(R.string.app_name);
        gridView = (ExpandGridView) findViewById(R.id.module_gridview);
        modules = Config.getMainModules(-1);
        adapter = new MainModuleAdapter(this,modules);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ShortcutAsyncTask().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        switch (adapter.getModule(position)) {
            case R.string.rubbish_collection:
                WasteListActivity.launchListActivity(this, null, null, null, null, null);
                break;
            case R.string.rubbish_storage:
                intent.setClass(MainActivity.this, TransferListActivity.class);
                startActivity(intent);
                break;
            case R.string.rubbish_incar:
                intent.setClass(MainActivity.this, EntruckerListActivity.class);
                startActivity(intent);
                break;
            case R.string.data_sync:
                List<PopupMenuItem> popItems = new ArrayList<>();
                popItems.add(new PopupMenuItem("0", R.drawable.module_default, "全量更新", 0));
                popItems.add(new PopupMenuItem("1", R.drawable.module_default, "增量更新", 0));
                new CenterGridPopupWindow().showPopupWindow(MainActivity.this, gridView, "同步数据", popItems, new BottomPopupItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        downloadDatas(i);
                    }
                });
                break;
            case R.string.data_query:
                intent.setClass(MainActivity.this, QueryActivity.class);
                startActivity(intent);
                break;
            case R.string.data_stat:
                intent.setClass(MainActivity.this, StatHomeActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void apkAutoUpdate() {
        if (autoupdate) {
            AppUpdate appUpdate = new AppUpdate(this, true);
            appUpdate.update();
        }
        autoupdate = false;
    }

    private void downloadDatas(int type) {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userid", LoginUtil.getLoginUser().userid)
                .add("date", DateUtil.getDateString())
                .add("type", "" + type)
                .build();
        showDialog("正在同步数据，请稍候……");
        Call call = mOkHttpClient.newCall(NetUtil.getRequest("downloadMultiTrashInfo", requestBody));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideDialog();
                new AlertDialog.Builder(MainActivity.this).setMessage(R.string.netnotavaliable).setPositiveButton("确定", null).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //hideDialog();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        response.body().byteStream(), "utf-8"));//防止乱码
                String line;
                String result = "";
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                Log.e("result", "downloadMultiTrashInfo = " + result);
                parseResponse(result);
            }
        });
    }


    private void parseResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String success = jsonObject.getString("result");
            if (success.equals("success")) {
                final List<TrashItem> rows = new ArrayList<>();
                JSONArray array = jsonObject.getJSONArray("rows");
                TrashItem item;
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    item = new TrashItem();

                    item.trashcode = object.optString("trashcode");
                    //item.status = object.optString("status");

                     item.status = Constant.Status.DOWNLOAD;

                    item.trashcancode = object.optString("trashcancode");
                    item.colletime = object.optString("colletime");
                    item.categorycode = object.optString("categorycode");
                    item.categoryname = object.optString("categoryname");
                    item.weight = object.optString("weight");
                    item.collectorid = object.optString("collectorid");
                    item.collector = object.optString("collector");
                    item.collectphone = object.optString("collectphone");
                    item.departareacode = object.optString("departareacode");
                    item.departarea = object.optString("departarea");
                    item.departcode = object.optString("departcode");
                    item.departname = object.optString("departname");
                    item.nurseid = object.optString("nurseid");
                    item.nurse = object.optString("nurse");
                    item.nursephone = object.optString("nursephone");

                    item.dustybincode = object.optString("dustbincode");
                    //item.dustybincode = "1";
                    item.transfertime = object.optString("transfertime");
                    item.transferid = object.optString("transferid");
                    item.transfer = object.optString("transfer");
                    item.transferphone = object.optString("transferphone");

                    if (item.status.equals(Constant.Status.DOWNLOAD))
                    {
                        item.date = DateUtil.getDateString(item.colletime);
                    }
                    else if (item.status.equals(Constant.Status.TRASFER))
                    {
                        item.date = DateUtil.getDateString(item.transfertime);
                    }

                    rows.add(item);
                }
                Log.e("111111","rows.size()"+rows.size());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TrashDao.getInstance().setAllTrash(rows);
                        hideDialog();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideDialog();
                        toast("获取数据失败");
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class ShortcutAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            for (Module module :modules)
            {
                switch (module.name){
                    case  R.string.rubbish_collection:
                        module.shortcut = TrashDao.getInstance().getAllUnUploadTrash();
                        break;

                    case  R.string.rubbish_storage:
                        module.shortcut = TrashDao.getInstance().getAllUnTransferTrash();
                        break;
                    case  R.string.rubbish_incar:
                        module.shortcut = TrashDao.getInstance().getAllUnEntruckTrash();
                        break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

}
