package health.rubbish.recycler.activity.transfer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.adapter.TransferListAdapter;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.util.LoginUtil;
import health.rubbish.recycler.util.NetUtil;
import health.rubbish.recycler.util.TrashList2DbAsyncTask;
import health.rubbish.recycler.widget.CustomProgressDialog;
import health.rubbish.recycler.widget.EmptyFiller;
import health.rubbish.recycler.widget.HeaderLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Lenovo on 2016/11/20.
 */

public class TransferListActivity extends BaseActivity {
    HeaderLayout headerLayout;
    ListView listView;
    TextView transferView;

    TransferListAdapter adapter;
    List<TrashItem> rows = new ArrayList<>();
    //List<TrashItem> rows_download = new ArrayList<>();
    CustomProgressDialog progressDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_transferlist;
    }

    @Override
    protected void init() {
        initHeaderView();
        initView();
        setData();
    }

    private void initHeaderView() {
        headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
        headerLayout.isShowBackButton(true);
        headerLayout.showTitle("今日转储列表");
        headerLayout.showRightTextButton("批量上传", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTransferTrashInfo();
            }
        });
    }

    private void initView() {

        listView = (ListView) findViewById(R.id.transferlist_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: 2016/11/23  详情
            }
        });

        transferView = (TextView) findViewById(R.id.transferlist_transfer);
        transferView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransferListActivity.this,TransferAddActivity.class);
                startActivity(intent);

            }
        });
    }


    private void setData() {
        adapter = new TransferListAdapter(this);
        adapter.setData(rows);
        listView.setAdapter(adapter);
        EmptyFiller.fill(this,listView,"无数据");

        new TransferListAsyncTask().execute();
    }

    private void uploadTransferTrashInfo() {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userid", LoginUtil.getLoginUser().userid)
                .add("datas", getdatas())
                .build();
        Call call = mOkHttpClient.newCall(NetUtil.getRequest("uploadTransferTrashInfo",requestBody));
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e) {
                hideDialog();
                new AlertDialog.Builder(TransferListActivity.this).setMessage(R.string.netnotavaliable).setPositiveButton("确定", null).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideDialog();
                parseResponse(response.body().string());

            }
        });
    }

    private String  getdatas()
    {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject object;
            for (TrashItem item : rows) {
                if (item.status.equals(Constant.Status.TRASFERING)) {
                    object = new JSONObject();
                    object.put("id", item.trashcode);
                    object.put("trashcode", item.trashcode);
                    object.put("status", Constant.Status.TRASFER);
                    object.put("transfertime", item.transfertime);
                    object.put("transferid", item.transferid);
                    object.put("trashstation", item.trashstation);
                    object.put("dustybincode", item.dustybincode);
                    jsonArray.put(object);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    private void parseResponse(String result)
    {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String success = jsonObject.getString("result");
            if (success.equals("success"))
            {
                final List<TrashItem> items = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("rows");
                JSONObject object;
                for (int i= 0;i<jsonArray.length();i++)
                {
                    object = jsonArray.getJSONObject(i);
                    if (object.getString("status").equals(Constant.Status.TRASFER)) {
                        TrashItem item = getTrashItemByCode(object.getString("trashcode"));
                        if (item!=null )
                        {
                            item.status =Constant.Status.TRASFER;
                            items.add(item);
                        }
                    }
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TrashDao.getInstance().setAllTrash(items);
                    }
                }).start();
                adapter.setData(rows);
                toast("成功");
            }
            else
            {
                toast("获取数据失败");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private TrashItem getTrashItemByCode(String code)
    {
        for (TrashItem item:rows)
        {
            if (item.trashcode.equals(code))
            {
                return item;
            }
        }
        return null;
    }

    public class TransferListAsyncTask extends AsyncTask<Void, Void, List<TrashItem>> {
        @Override
        protected List<TrashItem> doInBackground(Void... params) {
            return TrashDao.getInstance().getAllTransferedTrashToday();
        }

        @Override
        protected void onPostExecute(List<TrashItem> items) {
            progressDialog.dismiss();
            rows = items;
            adapter.setData(rows);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = new CustomProgressDialog(TransferListActivity.this);
            }
            progressDialog.show();
        }
    }

}
