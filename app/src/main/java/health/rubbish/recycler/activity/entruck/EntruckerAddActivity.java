package health.rubbish.recycler.activity.entruck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.activity.collection.WasteDetailActivity;
import health.rubbish.recycler.adapter.TrashListAdapter;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.LoginUser;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.util.DateUtil;
import health.rubbish.recycler.util.LoginUtil;
import health.rubbish.recycler.util.ToastUtil;
import health.rubbish.recycler.widget.BottomPopupItemClickListener;
import health.rubbish.recycler.widget.CenterPopupWindow;
import health.rubbish.recycler.widget.CustomProgressDialog;
import health.rubbish.recycler.widget.EmptyFiller;
import health.rubbish.recycler.widget.HeaderLayout;
import health.rubbish.recycler.mtuhf.ReadMode;
import health.rubbish.recycler.mtuhf.ReadUtil;
import health.rubbish.recycler.mtuhf.Ufh3Data;

/**
 * Created by Lenovo on 2016/11/20.
 */
public class EntruckerAddActivity extends BaseActivity implements ReadUtil.ReadListener    {
    HeaderLayout headerLayout;
    ListView listView;
    TextView entruckeradd_entrucker;
    TextView entruckeradd_entrucktime;
    TextView entruckeradd_platnumber;
    TextView entruckeradd_driver;
    TextView entruckeradd_driverphone;
    EditText entruckeradd_dustybincode;
    Button entruckeradd_dustybincode_rfid;

    TrashListAdapter adapter;
    List<TrashItem> rows = new ArrayList<>();
    CustomProgressDialog progressDialog;
    String entruckerid;
    private ReadUtil readUtil;
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
    @Override
    protected void onResume() {
        super.onResume();
        if (isFinishing() && Ufh3Data.isDeviceOpen()) {
            Ufh3Data.UhfGetData.CloseUhf();
        }
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
                    item.date = DateUtil.getDateString();
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
                Intent intent = new Intent(EntruckerAddActivity.this, WasteDetailActivity.class);
                intent.putExtra("wasteItem", rows.get(position));
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
               new CenterPopupWindow().showPopupWindow(EntruckerAddActivity.this, headerLayout, new String[]{"删除"}, new BottomPopupItemClickListener() {
                   @Override
                   public void onItemClick(int i) {
                       new AlertDialog.Builder(EntruckerAddActivity.this).setMessage("删除后本袋垃圾不装车，是否删除？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                                rows.remove(position);
                               adapter.notifyDataSetChanged();
                           }
                       }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                           }
                       }).create().show();
                   }
               });
                return false;
            }
        });

        entruckeradd_entrucker = (TextView) findViewById(R.id.entruckeradd_entrucker);
        entruckeradd_entrucker.setText(LoginUtil.getLoginUser().username);
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

        entruckeradd_dustybincode_rfid = (Button)findViewById(R.id.entruckeradd_dustybincode_rfid) ;
        entruckeradd_dustybincode_rfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readUtil.readUfhCard(ReadMode.EPC);
            }
        });
        initDevice();
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
    @Override
    public void onDataReceived(String data) {
        entruckeradd_dustybincode.setText(data);

    }

    @Override
    public void onFailure() {
        ToastUtil.shortToast(this, "读取失败");
    }

    /**
     * 初始化读卡设备
     */
    private void initDevice() {
        readUtil = new ReadUtil().setReadListener(this);
        if (readUtil.initUfh(this) != 0) {
            ToastUtil.shortToast(this, "打开设备失败");
            entruckeradd_dustybincode_rfid.setEnabled(false);
        } else{
            entruckeradd_dustybincode_rfid.setEnabled(true);
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
