package health.rubbish.recycler.activity.collection;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.network.entity.WasteUploadResp;
import health.rubbish.recycler.network.request.ParseCallback;
import health.rubbish.recycler.network.request.RequestUtil;
import health.rubbish.recycler.util.ToastUtil;
import health.rubbish.recycler.widget.HeaderLayout;
import health.rubbish.recycler.widget.jqprinter.ui.PrintHomeActivity;

/**
 * Created by xiayanlei on 2016/11/23.
 */
public class WasteDetailActivity extends BaseActivity {

    private static final String WASTE_ITEM = "wasteItem";

    private TrashItem wasteItem;

    public static void launchWasteDetailActivity(Context context, TrashItem wasteItem) {
        Intent intent = new Intent(context, WasteDetailActivity.class);
        intent.putExtra(WASTE_ITEM, wasteItem);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_waste_detail;
    }

    @Override
    protected void init() {
        HeaderLayout headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("垃圾收集详情");
        TextView wasteIdText = (TextView) findViewById(R.id.form_code_text);
        TextView canIdText = (TextView) findViewById(R.id.garbagecan_code_text);
        TextView collectorText = (TextView) findViewById(R.id.collector_text);
        TextView dtmText = (TextView) findViewById(R.id.dtm_text);
        TextView stateText = (TextView) findViewById(R.id.state_text);
        TextView roomText = (TextView) findViewById(R.id.room_text);
        TextView nurseText = (TextView) findViewById(R.id.nurse_text);
        TextView typeText = (TextView) findViewById(R.id.waste_type_text);
        TextView weightText = (TextView) findViewById(R.id.weight_text);
        TextView boxText = (TextView) findViewById(R.id.box_text);
        TextView carText = (TextView) findViewById(R.id.car_text);
        Button uploadBtn = (Button) findViewById(R.id.upload_btn);

        wasteItem = (TrashItem) getIntent().getSerializableExtra(WASTE_ITEM);
        if (wasteItem == null)
            return;
        wasteIdText.setText(wasteItem.trashcode);
        canIdText.setText(wasteItem.trashcancode);
        collectorText.setText(wasteItem.collector);
        dtmText.setText(wasteItem.colletime);
        stateText.setText(wasteItem.getStatusNam());
        roomText.setText(wasteItem.departname);
        nurseText.setText(wasteItem.nurse);
        typeText.setText(wasteItem.categoryname);
        weightText.setText(wasteItem.weight);
        boxText.setText(wasteItem.dustybincode);
        carText.setText(wasteItem.platnumber);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("0".equals(wasteItem.status) ) {
                    uploadWaste();
                }
                else
                {
                    Intent intent = new Intent(WasteDetailActivity.this, PrintHomeActivity.class);
                    intent.putExtra("TrashItem",wasteItem);
                    startActivity(intent);
                }
            }
        });
        if ("0".equals(wasteItem.status) )
        {
            uploadBtn.setText("上  传");
        }
        else
        {
            uploadBtn.setText("打  印");
        }

        //uploadBtn.setVisibility("0".equals(wasteItem.status) ? View.VISIBLE : View.GONE);
    }

    /**
     * 上传垃圾
     */
    private void uploadWaste() {
        showDialog("正在上传数据...");
        List<TrashItem> items = new ArrayList<>();
        items.add(wasteItem);
        new RequestUtil(new ParseCallback<List<WasteUploadResp>>() {

            @Override
            public void onComplete(List<WasteUploadResp> wasteUploadResps) {
                hideDialog();
                updateStatus(wasteUploadResps);
            }

            @Override
            public void onError(String error) {
                hideDialog();
                ToastUtil.shortToast(WasteDetailActivity.this,R.string.client_error);
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
                ToastUtil.shortToast(WasteDetailActivity.this, "上传成功");
                finish();
            }
        }.execute();
    }
}
