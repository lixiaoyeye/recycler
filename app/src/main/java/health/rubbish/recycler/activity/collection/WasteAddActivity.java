package health.rubbish.recycler.activity.collection;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import health.rubbish.recycler.R;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.mtuhf.ReadMode;
import health.rubbish.recycler.mtuhf.ReadUtil;
import health.rubbish.recycler.mtuhf.Ufh3Data;
import health.rubbish.recycler.util.ToastUtil;
import health.rubbish.recycler.widget.HeaderLayout;

/**
 * Created by xiayanlei on 2016/11/23.
 */
public class WasteAddActivity extends BaseActivity implements View.OnClickListener, ReadUtil.ReadListener {
    private TextView formCodeText;
    private TextView garbageCanCodeText;
    private TextView roomText;
    private TextView nurseText;
    private TextView typeText;
    private EditText weightText;
    private TextView collectorText;
    private TextView dtmText;
    private Button rfidBtn;
    private ReadUtil readUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_waste_add;
    }

    @Override
    protected void init() {
        HeaderLayout headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("垃圾采集");
        formCodeText = (TextView) findViewById(R.id.form_code_text);
        garbageCanCodeText = (TextView) findViewById(R.id.garbagecan_code_text);
        roomText = (TextView) findViewById(R.id.room_text);
        nurseText = (TextView) findViewById(R.id.nurse_text);
        typeText = (TextView) findViewById(R.id.waste_type_text);
        weightText = (EditText) findViewById(R.id.weight_text);
        collectorText = (TextView) findViewById(R.id.collector_text);
        dtmText = (TextView) findViewById(R.id.dtm_text);
        rfidBtn = (Button) findViewById(R.id.rfid_button);
        Button saveBtn = (Button) findViewById(R.id.save_print_btn);
        rfidBtn.setOnClickListener(this);
        roomText.setOnClickListener(this);
        nurseText.setOnClickListener(this);
        typeText.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        readUtil = new ReadUtil().setReadListener(this);
        initDevice();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && Ufh3Data.isDeviceOpen()) {
            Ufh3Data.UhfGetData.CloseUhf();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rfid_button:
                readUtil.readUfhCard(ReadMode.EPC);
                break;
            case R.id.room_text:
                break;
            case R.id.nurse_text:
                break;
            case R.id.waste_type_text:
                break;
            case R.id.save_print_btn:
                break;
        }
    }

    @Override
    public void onDataReceived(String data) {
        garbageCanCodeText.setText(data);
    }

    @Override
    public void onFailure() {
        ToastUtil.shortToast(this, "读取失败");
    }

    private void initDevice() {
        if (readUtil.initUfh(this) != 0) {
            ToastUtil.shortToast(this, "打开设备失败");
            rfidBtn.setEnabled(false);
        } else
            rfidBtn.setEnabled(true);
    }
}
