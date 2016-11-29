package health.rubbish.recycler.activity.entruck;

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
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.activity.collection.WasteDetailActivity;
import health.rubbish.recycler.activity.stat.StatCollectActivity;
import health.rubbish.recycler.activity.transfer.TransferAddActivity;
import health.rubbish.recycler.activity.transfer.TransferListActivity;
import health.rubbish.recycler.adapter.EntruckerListAdapter;
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

public class EntruckerListActivity extends BaseActivity  implements View.OnClickListener{
    HeaderLayout headerLayout;
    ListView listView;
    TextView entruckerView;
    private ImageView date_pageturn_arrow_left;
    private ImageView date_pageturn_arrow_right;
    private TextView date_pageturn_date;

    EntruckerListAdapter adapter;
    List<TrashItem> rows = new ArrayList<>();
    //List<TrashItem> rows_download = new ArrayList<>();
    CustomProgressDialog progressDialog;
    private Calendar calendar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_entruckerlist;
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
        headerLayout.showTitle("垃圾装车列表");
        headerLayout.showRightTextButton("批量上传", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadEntruckerTrashInfo();
            }
        });
    }

    private void initView() {
        date_pageturn_arrow_left = (ImageView) findViewById(R.id.date_pageturn_arrow_left);
        date_pageturn_arrow_right = (ImageView) findViewById(R.id.date_pageturn_arrow_right);
        date_pageturn_date = (TextView) findViewById(R.id.date_pageturn_date);
        date_pageturn_arrow_left.setOnClickListener(this);
        date_pageturn_arrow_right.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.entruckerlist_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(EntruckerListActivity.this, WasteDetailActivity.class);
                intent.putExtra("wasteItem", rows.get(position));
                startActivity(intent);
            }
        });

        entruckerView = (TextView) findViewById(R.id.entruckerlist_entrucker);
        entruckerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EntruckerListActivity.this,EntruckerAddActivity.class);
                startActivity(intent);

            }
        });
    }


    private void setData() {
        adapter = new EntruckerListAdapter(this);
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
        new EntruckerListAsyncTask().execute();
    }

    private void uploadEntruckerTrashInfo() {
        CustomHttpClient client = new CustomHttpClient();
        client.addParam("userid",  LoginUtil.getLoginUser().userid);
        Log.e("1111","getdatas(): "+getdatas());
        client.addParam("datas", getdatas());
        showDialog("正在上传……");
        new RequestUtil().sendPost("uploadEntruckerTrashInfo",client,new ParseCallback<String>() {
            @Override
            public void onComplete(String result) {
                Log.e("123","uploadEntruckerTrashInfo = "+result);
                hideDialog();
                parseResponse(result);
            }

            @Override
            public void onError(String error) {
                hideDialog();
                new AlertDialog.Builder(EntruckerListActivity.this).setMessage(R.string.netnotavaliable).setPositiveButton("确定", null).show();
            }
        });

    }


    private String  getdatas()
    {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject object;
            for (TrashItem item : rows) {
                if (item.status.equals(Constant.Status.ENTRUCKERING)) {
                    object = new JSONObject();
                    object.put("id", item.trashcode);
                    object.put("trashcode", item.trashcode);
                    object.put("status", Constant.Status.ENTRUCKER);
                    object.put("platnumber", item.platnumber);
                    object.put("entrucktime", item.entrucktime);
                    object.put("entruckerid", LoginUtil.getLoginUser().userid);
                    object.put("entrucker", LoginUtil.getLoginUser().username);
                    object.put("entruckerphone", "123");
                    object.put("driver", item.driver);
                    object.put("driverphone", item.driverphone);
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
                    if (object.getString("status").equals(Constant.Status.ENTRUCKER)) {
                        TrashItem item = getTrashItemByCode(object.getString("trashcode"));
                        if (item!=null )
                        {
                            item.status =Constant.Status.ENTRUCKER;
                            item.date = DateUtil.getDateString();
                            items.add(item);
                        }
                    }
                }


                        TrashDao.getInstance().setAllTrash(items);
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

    public class EntruckerListAsyncTask extends AsyncTask<Void, Void, List<TrashItem>> {
        @Override
        protected List<TrashItem> doInBackground(Void... params) {
            return TrashDao.getInstance().getAllEntruckeredTrashToday(DateUtil.getDateString(calendar.getTime()));
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
                progressDialog = new CustomProgressDialog(EntruckerListActivity.this);
            }
            progressDialog.show();
        }
    }

}
