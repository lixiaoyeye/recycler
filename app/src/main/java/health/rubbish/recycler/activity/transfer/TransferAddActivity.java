package health.rubbish.recycler.activity.transfer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import health.rubbish.recycler.adapter.WasteListAdapter;
import health.rubbish.recycler.mtuhf.ReadMode;
import health.rubbish.recycler.mtuhf.ReadUtil;
import health.rubbish.recycler.mtuhf.Ufh3Data;
import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.activity.collection.WasteDetailActivity;
import health.rubbish.recycler.activity.entruck.EntruckerAddActivity;
import health.rubbish.recycler.adapter.TransferListAdapter;
import health.rubbish.recycler.adapter.TrashListAdapter;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.util.DateUtil;
import health.rubbish.recycler.util.LoginUtil;
import health.rubbish.recycler.util.NetUtil;
import health.rubbish.recycler.util.ToastUtil;
import health.rubbish.recycler.widget.BottomPopupItemClickListener;
import health.rubbish.recycler.widget.CenterPopupWindow;
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

public class TransferAddActivity extends BaseActivity implements ReadUtil.ReadListener  {
    HeaderLayout headerLayout;
    ListView listView;
    EditText transferadd_trashcan;
    EditText transferadd_dustybin;
    Button transferadd_trashcan_rfid;
    Button transferadd_dustybin_rfid;

    WasteListAdapter adapter;
    List<TrashItem> rows = new ArrayList<>();
    CustomProgressDialog progressDialog;
    int type = 0;

    private ReadUtil readUtil;
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
                if (!checkParams()) {
                    return;
                }
                for (TrashItem item:rows)
                {
                    item.date = DateUtil.getDateString();
                    item.status = Constant.Status.TRASFERING;
                    item.dustybincode = transferadd_dustybin.getText().toString();
                }
                new TransferAddAsyncTask().execute();
            }
        });
    }

    /**
     * 校验信息完整性
     */
    private boolean checkParams() {

        if (TextUtils.isEmpty(transferadd_trashcan.getText().toString())) {
            ToastUtil.shortToast(this, "请扫描垃圾桶RFID");
            return false;
        }
        return true;
    }

    private void initView() {


        listView = (ListView) findViewById(R.id.transferadd_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TransferAddActivity.this, WasteDetailActivity.class);
                intent.putExtra("wasteItem", rows.get(position));
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new CenterPopupWindow().showPopupWindow(TransferAddActivity.this, headerLayout, new String[]{"删除"}, new BottomPopupItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        new AlertDialog.Builder(TransferAddActivity.this).setMessage("删除后本袋垃圾不转储，是否删除？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
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

        transferadd_trashcan = (EditText) findViewById(R.id.transferadd_trashcan);
        transferadd_trashcan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                new TransferAddListAsyncTask().execute(transferadd_trashcan.getText().toString());
            }
        });
        transferadd_trashcan_rfid = (Button)findViewById(R.id.transferadd_trashcan_rfid) ;



        transferadd_dustybin = (EditText) findViewById(R.id.transferadd_dustybin);
        transferadd_dustybin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/11/23 扫rfid卡
                type = 2;
                readUtil.readUfhCard(ReadMode.EPC);
            }
        });

        transferadd_dustybin_rfid = (Button)findViewById(R.id.transferadd_dustybin_rfid) ;
        transferadd_dustybin_rfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        initDevice();
       /* transferadd_trashcan_rfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 1;
                readUtil.readUfhCard(ReadMode.EPC);
            }
        });*/

        transferadd_trashcan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_F12) {
                    type = 1;
                    readUtil.readUfhCard(ReadMode.EPC);

                    return true;
                } else {
                    return false;
                }
            }
        });


//        transferadd_trashcan.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
//        transferadd_dustybin.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
    }


    @Override
    public void onDataReceived(String data) {
        if (type ==1) {
            transferadd_trashcan.setText(data);
            //new TransferAddListAsyncTask().execute(transferadd_trashcan.getText().toString());
        }
        else if (type == 2)
        {
            transferadd_dustybin.setText(data);
        }

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
            transferadd_dustybin_rfid.setEnabled(false);
            transferadd_trashcan_rfid.setEnabled(false);
        } else{
            transferadd_trashcan_rfid.setEnabled(true);
            transferadd_dustybin_rfid.setEnabled(true);
        }
    }

    private void setData() {
        adapter = new WasteListAdapter();
        adapter.setWasteItems(rows);
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
            adapter.setWasteItems(rows);
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
