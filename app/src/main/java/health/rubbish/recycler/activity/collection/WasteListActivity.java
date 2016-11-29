package health.rubbish.recycler.activity.collection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.activity.entruck.EntruckerListActivity;
import health.rubbish.recycler.adapter.WasteListAdapter;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.network.entity.WasteUploadResp;
import health.rubbish.recycler.network.request.ParseCallback;
import health.rubbish.recycler.network.request.RequestUtil;
import health.rubbish.recycler.util.DateUtil;
import health.rubbish.recycler.util.ToastUtil;
import health.rubbish.recycler.widget.HeaderLayout;
import health.rubbish.recycler.widget.xlist.XListView;

/**
 * Created by xiayanlei on 2016/11/23.
 */
public class WasteListActivity extends BaseActivity  implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, XListView.IXListViewListener {

    private XListView wasteListView;
    private LinearLayout date_pageturn_lnr;
    private ImageView date_pageturn_arrow_left;
    private ImageView date_pageturn_arrow_right;
    private TextView date_pageturn_date;

    private WasteListAdapter adapter;

    private static final String DEPART = "depart";
    private static final String NURSE = "nurse";
    private static final String CATEGORY = "category";
    private static final String CANCODE = "canCode";
    private static final String TRASHCODE = "trashCode";
    private static final String SEARCH = "search";

    private String departcode;
    private String nurseid;
    private String categorycode;
    private String trashcancode;
    private String trashcode;
    private boolean search = false;
    Calendar calendar;

    public static void launchListActivity(Context context, String departcode, String nurseid, String categorycode, String trashcancode, String trashcode) {
        Intent intent = new Intent(context, WasteListActivity.class);
        intent.putExtra(DEPART, departcode);
        intent.putExtra(NURSE, nurseid);
        intent.putExtra(CATEGORY, categorycode);
        intent.putExtra(CANCODE, trashcancode);
        intent.putExtra(TRASHCODE, trashcode);
        intent.putExtra(SEARCH, trashcode!=null);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_waste_list;
    }

    @Override
    protected void init() {
        calendar = Calendar.getInstance();
        initData();
        HeaderLayout headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
        headerLayout.showTitle("垃圾收集");
        headerLayout.showLeftBackButton();
        headerLayout.showRightImageButton(R.drawable.eventhome_add_icon, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WasteListActivity.this, WasteAddActivity.class));
            }
        });

        date_pageturn_lnr = (LinearLayout)findViewById(R.id.date_pageturn_lnr);
        date_pageturn_lnr.setVisibility(search?View.GONE:View.VISIBLE);
        date_pageturn_arrow_left = (ImageView) findViewById(R.id.date_pageturn_arrow_left);
        date_pageturn_arrow_right = (ImageView) findViewById(R.id.date_pageturn_arrow_right);
        date_pageturn_date = (TextView) findViewById(R.id.date_pageturn_date);
        date_pageturn_arrow_left.setOnClickListener(this);
        date_pageturn_arrow_right.setOnClickListener(this);


        wasteListView = (XListView) findViewById(R.id.waste_listview);
        wasteListView.setOnItemClickListener(this);
        wasteListView.setOnItemLongClickListener(this);
        wasteListView.setPullLoadEnable(false);
        wasteListView.setPullRefreshEnable(false);
        adapter = new WasteListAdapter();
        wasteListView.setAdapter(adapter);
        Button uploadBtn = (Button) findViewById(R.id.upload_btn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadWaste();
            }
        });
    }

    private void initData() {
        departcode = getIntent().getStringExtra(DEPART);
        nurseid = getIntent().getStringExtra(NURSE);
        categorycode = getIntent().getStringExtra(CATEGORY);
        trashcancode = getIntent().getStringExtra(CANCODE);
        trashcode = getIntent().getStringExtra(TRASHCODE);
        search = getIntent().getBooleanExtra(SEARCH,false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStartAndEndTime(0);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WasteDetailActivity.launchWasteDetailActivity(this, (TrashItem) adapter.getItem(position - 1));
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
        loadData();
    }

    @Override
    public void onRefresh() {
        updateStartAndEndTime(0);
    }

    @Override
    public void onLoadMore() {

    }

    /**
     * 此处应该是加载本地数据
     */
    private void loadData() {
        final TrashDao trashDao = TrashDao.getInstance();
        new AsyncTask<Void, Void, List<TrashItem>>() {

            @Override
            protected List<TrashItem> doInBackground(Void... params) {
                if (search)
                {
                    return trashDao.queryAllTrash(departcode, nurseid, categorycode, trashcancode, trashcode);
                }
                return trashDao.getAllTrashToday(DateUtil.getDateString(calendar.getTime()));
            }

            @Override
            protected void onPostExecute(List<TrashItem> trashItems) {
                super.onPostExecute(trashItems);
                adapter.setWasteItems(trashItems);
                wasteListView.stopRefresh();
            }
        }.execute();
    }

    /**
     * 上传垃圾信息
     */
    private void uploadWaste() {
        List<TrashItem> items = adapter.getNeedUploadTrash();
        if (items.size() == 0)
            return;
        showDialog("正在上传数据...");
        new RequestUtil(new ParseCallback<List<WasteUploadResp>>() {

            @Override
            public void onComplete(List<WasteUploadResp> wasteUploadResps) {
                updateStatus(wasteUploadResps);
            }

            @Override
            public void onError(String error) {
                hideDialog();
                ToastUtil.shortToast(WasteListActivity.this, R.string.client_error);
            }
        }).uploadWaste(items);
    }

    /**
     * 更新垃圾状态
     *
     * @param wasteUploadResps
     */
    private void updateStatus(final List<WasteUploadResp> wasteUploadResps) {
        if (wasteUploadResps == null) {
            hideDialog();
            ToastUtil.shortToast(this, "上传失败");
            return;
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (wasteUploadResps != null) {
                    for (WasteUploadResp resp : wasteUploadResps) {
                        TrashDao.getInstance().updateTrashStatus(resp.trashcode, resp.status);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                hideDialog();
                ToastUtil.shortToast(WasteListActivity.this, "上传成功");
                //刷新本地数据
                loadData();
            }
        }.execute();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final TrashItem item = (TrashItem) adapter.getItem(position - 1);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否删除该记录？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TrashDao.getInstance().deleteTrash(item);
                loadData();
            }
        });
        builder.setNegativeButton("取消", null);
        return true;
    }
}
