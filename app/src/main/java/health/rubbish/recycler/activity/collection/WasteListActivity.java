package health.rubbish.recycler.activity.collection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.adapter.WasteListAdapter;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.network.entity.WasteUploadResp;
import health.rubbish.recycler.network.request.ParseCallback;
import health.rubbish.recycler.network.request.RequestUtil;
import health.rubbish.recycler.util.ToastUtil;
import health.rubbish.recycler.widget.HeaderLayout;
import health.rubbish.recycler.widget.xlist.XListView;

/**
 * Created by xiayanlei on 2016/11/23.
 */
public class WasteListActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, XListView.IXListViewListener {

    private XListView wasteListView;

    private WasteListAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_waste_list;
    }

    @Override
    protected void init() {
        HeaderLayout headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
        headerLayout.showTitle("垃圾收集");
        headerLayout.showLeftBackButton();
        headerLayout.showRightImageButton(R.drawable.eventhome_add_icon, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WasteListActivity.this, WasteAddActivity.class));
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WasteDetailActivity.launchWasteDetailActivity(this, (TrashItem) adapter.getItem(position - 1));
    }


    @Override
    public void onRefresh() {
        loadData();
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
                return trashDao.getAllTrashToday();
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
