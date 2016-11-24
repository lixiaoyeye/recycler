package health.rubbish.recycler.activity.collection;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.network.entity.WasteUploadResp;
import health.rubbish.recycler.network.request.ParseCallback;
import health.rubbish.recycler.network.request.RequestUtil;
import health.rubbish.recycler.util.ToastUtil;
import health.rubbish.recycler.widget.HeaderLayout;

/**
 * Created by xiayanlei on 2016/11/23.
 */
public class WasteDetailActivity extends BaseActivity {

    private static final String WASTE_ITEM = "wasteItem";

    private WasteItem wasteItem;

    public static void launchWasteDetailActivity(Context context, WasteItem wasteItem) {
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
        TextView cartext = (TextView) findViewById(R.id.car_text);
        Button uploadBtn = (Button) findViewById(R.id.upload_btn);

        wasteItem = (WasteItem) getIntent().getSerializableExtra(WASTE_ITEM);
        if (wasteItem == null)
            return;
        wasteIdText.setText(wasteItem.wasteId);
        canIdText.setText(wasteItem.canId);
        collectorText.setText(wasteItem.collectorNam);
        dtmText.setText(wasteItem.dtm);
        stateText.setText(wasteItem.getStateNam());
        roomText.setText(wasteItem.roomNam);
        nurseText.setText(wasteItem.nurseNam);
        typeText.setText(wasteItem.typNam);
        weightText.setText(wasteItem.weight);
        boxText.setText(wasteItem.boxId);
        cartext.setText(wasteItem.carId);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadWaste();
            }
        });
    }

    /**
     * 上传垃圾
     */
    private void uploadWaste() {
        showDialog("正在上传数据...");
        List<WasteItem> items = new ArrayList<>();
        items.add(wasteItem);
        new RequestUtil().uploadWaste(items, new ParseCallback<List<WasteUploadResp>>() {

            @Override
            public void onComplete(List<WasteUploadResp> wasteUploadResps) {
                hideDialog();
                // TODO: 2016/11/24 更新本地数据库的垃圾状态
            }

            @Override
            public void onError(String error) {
                hideDialog();
                ToastUtil.shortToast(WasteDetailActivity.this,R.string.client_error);
            }
        });
    }

}
