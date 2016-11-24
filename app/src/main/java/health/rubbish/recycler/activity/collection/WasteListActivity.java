package health.rubbish.recycler.activity.collection;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.adapter.WasteListAdapter;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.network.entity.WasteUploadResp;
import health.rubbish.recycler.network.request.ParseCallback;
import health.rubbish.recycler.network.request.RequestUtil;
import health.rubbish.recycler.util.ToastUtil;
import health.rubbish.recycler.widget.HeaderLayout;
import health.rubbish.recycler.widget.xlist.XListView;

/**
 * Created by xiayanlei on 2016/11/23.
 */
public class WasteListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

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
        wasteListView.setPullLoadEnable(false);
        wasteListView.setPullRefreshEnable(true);
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
        WasteDetailActivity.launchWasteDetailActivity(this, (WasteItem) adapter.getItem(position - 1));
    }

    /**
     * 此处应该是加载本地数据？
     */
    private void loadData() {

    }

    // TODO: 2016/11/24 ,获取需要上传的数据，并在上传以后更新数据库
    private void uploadWaste() {
        showDialog("正在上传数据...");
        List<WasteItem> items = new ArrayList<>();
        new RequestUtil().uploadWaste(items, new ParseCallback<List<WasteUploadResp>>() {

            @Override
            public void onComplete(List<WasteUploadResp> wasteUploadResps) {
                hideDialog();
                // TODO: 2016/11/24 更新本地数据库的垃圾状态
            }

            @Override
            public void onError(String error) {
                hideDialog();
                ToastUtil.shortToast(WasteListActivity.this, R.string.client_error);
            }
        });
    }
}
