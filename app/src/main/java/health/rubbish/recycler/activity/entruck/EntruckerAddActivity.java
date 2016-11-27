package health.rubbish.recycler.activity.entruck;

import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.adapter.TrashListAdapter;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.LoginUser;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.util.DateUtil;
import health.rubbish.recycler.util.LoginUtil;
import health.rubbish.recycler.widget.CustomProgressDialog;
import health.rubbish.recycler.widget.EmptyFiller;
import health.rubbish.recycler.widget.HeaderLayout;


/**
 * Created by Lenovo on 2016/11/20.
 */
public class EntruckerAddActivity extends BaseActivity {
    HeaderLayout headerLayout;
    ListView listView;
    TextView entruckeradd_entrucker;
    TextView entruckeradd_entrucktime;
    TextView entruckeradd_platnumber;
    TextView entruckeradd_driver;
    TextView entruckeradd_driverphone;
    EditText entruckeradd_dustybincode;

    TrashListAdapter adapter;
    List<TrashItem> rows = new ArrayList<>();
    CustomProgressDialog progressDialog;
    String entruckerid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_entruckeradd;
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
                    item.status = Constant.Status.ENTRUCKERING;
                    item.entruckerid = entruckerid;
                    item.entrucker = entruckeradd_entrucker.getText().toString();
                    item.entrucktime = entruckeradd_entrucktime.getText().toString();
                    item.platnumber = entruckeradd_platnumber.getText().toString();
                    item.driver = entruckeradd_driver.getText().toString();
                    item.driverphone = entruckeradd_driverphone.getText().toString();
                }
                new EntruckerAddAsyncTask().execute();
            }
        });
    }

    private void initView() {

        listView = (ListView) findViewById(R.id.entruckeradd_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: 2016/11/23  详情
            }
        });

        entruckeradd_entrucker = (TextView) findViewById(R.id.entruckeradd_entrucker);
        entruckeradd_entrucktime = (TextView) findViewById(R.id.entruckeradd_entrucktime);
        entruckeradd_platnumber = (TextView) findViewById(R.id.entruckeradd_platnumber);
        entruckeradd_driver = (TextView) findViewById(R.id.entruckeradd_driver);
        entruckeradd_driverphone = (TextView) findViewById(R.id.entruckeradd_driverphone);
        entruckeradd_dustybincode = (EditText) findViewById(R.id.entruckeradd_dustybincode);
        /*entruckeradd_dustybincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/11/23 扫rfid卡
            }
        });*/

        entruckeradd_dustybincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                new EntruckerAddListAsyncTask().execute(entruckeradd_dustybincode.getText().toString());
            }
        });
        
        initDefaultData();
    }

    private void initDefaultData()
    {
        entruckerid = LoginUtil.getLoginUser().userid;
        entruckeradd_entrucker.setText(LoginUtil.getLoginUser().username);
        entruckeradd_entrucktime.setText(DateUtil.getTimeString());
    }

    private void setData() {
        adapter = new TrashListAdapter(this);
        adapter.setData(rows);
        listView.setAdapter(adapter);
       // EmptyFiller.fill(this,listView,"无数据");
    }



    public class EntruckerAddListAsyncTask extends AsyncTask<String, Void, List<TrashItem>> {
        @Override
        protected List<TrashItem> doInBackground(String... params) {
            return TrashDao.getInstance().getAllUnEntruckerTrashTodayByCan(params[0]);
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
                progressDialog = new CustomProgressDialog(EntruckerAddActivity.this);
            }
            progressDialog.show();
        }
    }


    public class EntruckerAddAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            TrashDao.getInstance().setAllTrash(rows);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            progressDialog.dismiss();
            EntruckerAddActivity.this.finish();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = new CustomProgressDialog(EntruckerAddActivity.this);
            }
            progressDialog.show();
        }
    }

}
