package health.rubbish.recycler.activity.transfer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.activity.collection.WasteAddActivity;
import health.rubbish.recycler.activity.collection.WasteDetailActivity;
import health.rubbish.recycler.activity.collection.WasteListActivity;
import health.rubbish.recycler.activity.entruck.EntruckerListActivity;
import health.rubbish.recycler.adapter.TransferListAdapter;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.network.http.CustomHttpClient;
import health.rubbish.recycler.network.request.ParseCallback;
import health.rubbish.recycler.network.request.RequestUtil;
import health.rubbish.recycler.util.DateUtil;
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

public class TransferListActivity extends BaseActivity  implements View.OnClickListener{
    HeaderLayout headerLayout;
    ListView listView;
    TextView transferView;
    private ImageView date_pageturn_arrow_left;
    private ImageView date_pageturn_arrow_right;
    private TextView date_pageturn_date;

    TransferListAdapter adapter;
    List<TrashItem> rows = new ArrayList<>();
    //List<TrashItem> rows_download = new ArrayList<>();
    CustomProgressDialog progressDialog;
    Calendar calendar;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_transferlist;
    }

    @Override
    protected void init() {
        calendar = Calendar.getInstance();
        initHeaderView();
        initView();
        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStartAndEndTime(0);

    }

    private void initHeaderView() {
        headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
        headerLayout.isShowBackButton(true);
        headerLayout.showTitle("今日转储列表");
        headerLayout.showRightImageButton(R.drawable.eventhome_add_icon, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransferListActivity.this,TransferAddActivity.class);
                startActivity(intent);
            }
        });
        /*headerLayout.showRightTextButton("批量上传", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTransferTrashInfo();
            }
        });*/
    }

    private void initView() {
        date_pageturn_arrow_left = (ImageView) findViewById(R.id.date_pageturn_arrow_left);
        date_pageturn_arrow_right = (ImageView) findViewById(R.id.date_pageturn_arrow_right);
        date_pageturn_date = (TextView) findViewById(R.id.date_pageturn_date);
        date_pageturn_arrow_left.setOnClickListener(this);
        date_pageturn_arrow_right.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.transferlist_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TransferListActivity.this, WasteDetailActivity.class);
                intent.putExtra("wasteItem", rows.get(position));
                startActivity(intent);
            }
        });

        transferView = (TextView) findViewById(R.id.transferlist_transfer);
        transferView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTransferTrashInfo();


            }
        });
    }

    private void setData() {
        adapter = new TransferListAdapter(this);
        adapter.setData(rows);
        listView.setAdapter(adapter);
        EmptyFiller.fill(this,listView,"无数据");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date_pageturn_arrow_left:
                updateStartAndEndTime(-1);
                break;
            case R.id.date_pageturn_arrow_right:
                updateStartAndEndTime(1);
                break;
        }
    }

    private void updateStartAndEndTime(int num) {
        calendar.add(Calendar.DAY_OF_MONTH, num);
        Date currentData = calendar.getTime();
        date_pageturn_date.setText(new SimpleDateFormat("yyyy-MM-dd EEEE").format(currentData));
        new TransferListAsyncTask().execute();
    }

    private void uploadTransferTrashInfo() {
        CustomHttpClient client = new CustomHttpClient();
        client.addParam("userid",  LoginUtil.getLoginUser().userid);
        client.addParam("datas", getdatas());
        showDialog("正在上传……");
        new RequestUtil().sendPost("uploadTransferTrashInfo",client,new ParseCallback<String>() {
            @Override
            public void onComplete(String result) {
                Log.e("123","uploadTransferTrashInfo = "+result);
                hideDialog();
                parseResponse(result);
            }

            @Override
            public void onError(String error) {
                hideDialog();
                new AlertDialog.Builder(TransferListActivity.this).setMessage(R.string.netnotavaliable).setPositiveButton("确定", null).show();
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
        Log.e("0000",jsonArray.toString());
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
                            item.date = DateUtil.getDateString();
                            Log.e("000000000",item.trashcode);
                            items.add(item);
                        }
                    }
                }
                 TrashDao.getInstance().setAllTrash(items);
                updateStartAndEndTime(0);

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
            return TrashDao.getInstance().getAllTransferedTrashToday(DateUtil.getDateString(calendar.getTime()));
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
