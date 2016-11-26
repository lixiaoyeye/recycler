package health.rubbish.recycler.activity.transfer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.adapter.TransferListAdapter;
import health.rubbish.recycler.adapter.TrashListAdapter;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.TrashItem;
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

public class TransferAddActivity extends BaseActivity {
    HeaderLayout headerLayout;
    ListView listView;
    TextView transferadd_trashcan;
    TextView transferadd_dustybin;

    TrashListAdapter adapter;
    List<TrashItem> rows = new ArrayList<>();
    CustomProgressDialog progressDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_transferadd;
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
        headerLayout.showTitle("垃圾转储");
        headerLayout.showRightTextButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (TrashItem item:rows)
                {
                    item.status = Constant.Status.TRASFERING;
                }
                new TransferAddAsyncTask().execute();
            }
        });
    }

    private void initView() {

        listView = (ListView) findViewById(R.id.transferadd_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: 2016/11/23  详情
            }
        });

        transferadd_trashcan = (TextView) findViewById(R.id.transferadd_trashcan);
        transferadd_trashcan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/11/23 扫rfid卡
            }
        });

        transferadd_dustybin = (TextView) findViewById(R.id.transferadd_dustybin);
        transferadd_dustybin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/11/23 扫rfid卡
            }
        });

        transferadd_dustybin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                new TransferAddListAsyncTask().execute(transferadd_dustybin.getText().toString());
            }
        });
    }


    private void setData() {
        adapter = new TrashListAdapter(this);
        adapter.setData(rows);
        listView.setAdapter(adapter);
        EmptyFiller.fill(this,listView,"无数据");
    }



    public class TransferAddListAsyncTask extends AsyncTask<String, Void, List<TrashItem>> {
        @Override
        protected List<TrashItem> doInBackground(String... params) {
            return TrashDao.getInstance().getAllUnTransferTrashTodayByCan(params[0]);
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
                progressDialog = new CustomProgressDialog(TransferAddActivity.this);
            }
            progressDialog.show();
        }
    }


    public class TransferAddAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            TrashDao.getInstance().setAllTrash(rows);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            progressDialog.dismiss();
            TransferAddActivity.this.finish();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = new CustomProgressDialog(TransferAddActivity.this);
            }
            progressDialog.show();
        }
    }

}
