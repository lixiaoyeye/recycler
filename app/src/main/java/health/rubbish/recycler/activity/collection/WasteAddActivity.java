package health.rubbish.recycler.activity.collection;

import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import health.rubbish.recycler.R;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.mtuhf.ReadMode;
import health.rubbish.recycler.mtuhf.ReadUtil;
import health.rubbish.recycler.mtuhf.Ufh3Data;
import health.rubbish.recycler.util.DateUtil;
import health.rubbish.recycler.util.ToastUtil;
import health.rubbish.recycler.util.Utils;
import health.rubbish.recycler.widget.HeaderLayout;
import health.rubbish.recycler.widget.jqprinter.ui.PrintHomeActivity;

/**
 * Created by xiayanlei on 2016/11/23.
 * 新增垃圾
 */
public class WasteAddActivity extends BaseActivity implements View.OnClickListener, ReadUtil.ReadListener {
    private TextView formCodeText;
    private EditText garbageCanCodeText;
    private TextView areaText;
    private TextView roomText;
    private TextView nurseText;
    private TextView typeText;
    private EditText weightText;
    private TextView collectorText;
    private TextView dtmText;
    private Button rfidBtn;
    private ReadUtil readUtil;
    private TrashItem trashItem = new TrashItem();

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
        garbageCanCodeText = (EditText) findViewById(R.id.garbagecan_code_text);
        areaText = (TextView) findViewById(R.id.area_text);
        roomText = (TextView) findViewById(R.id.room_text);
        nurseText = (TextView) findViewById(R.id.nurse_text);
        typeText = (TextView) findViewById(R.id.waste_type_text);
        weightText = (EditText) findViewById(R.id.weight_text);
        collectorText = (TextView) findViewById(R.id.collector_text);
        dtmText = (TextView) findViewById(R.id.dtm_text);
        rfidBtn = (Button) findViewById(R.id.rfid_button);
        Button saveBtn = (Button) findViewById(R.id.save_print_btn);
        rfidBtn.setOnClickListener(this);
        areaText.setOnClickListener(this);
        roomText.setOnClickListener(this);
        nurseText.setOnClickListener(this);
        typeText.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        initDevice();
        //设置默认值
        formCodeText.setText(Utils.getUserId() + System.currentTimeMillis());
        collectorText.setText(Utils.getUserName());
        dtmText.setText(DateUtil.getTimeString());


        garbageCanCodeText.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
        weightText.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
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
        Intent intent = new Intent(this, ReferenceActivity.class);
        switch (v.getId()) {
            case R.id.rfid_button:
                readUtil.readUfhCard(ReadMode.EPC);
                break;
            case R.id.area_text:
                intent.putExtra(Constant.Reference.REFER_TITLE, "院区选择");
                intent.putExtra(Constant.Reference.REFER_TYPE, Constant.Reference.AREA);
                startActivityForResult(intent, Constant.Reference.AREA);
                break;
            case R.id.room_text:
                intent.putExtra(Constant.Reference.REFER_TITLE, "科室选择");
                intent.putExtra(Constant.Reference.REFER_TYPE, Constant.Reference.DEPART);
                startActivityForResult(intent, Constant.Reference.DEPART);
                break;
            case R.id.nurse_text:
                intent.putExtra(Constant.Reference.REFER_TITLE, "护士选择");
                intent.putExtra(Constant.Reference.REFER_TYPE, Constant.Reference.NURSE);
                startActivityForResult(intent, Constant.Reference.NURSE);
                break;
            case R.id.waste_type_text:
                intent.putExtra(Constant.Reference.REFER_TITLE, "垃圾类型选择");
                intent.putExtra(Constant.Reference.REFER_TYPE, Constant.Reference.CATEGORY);
                startActivityForResult(intent, Constant.Reference.CATEGORY);
                break;
            case R.id.save_print_btn:
                saveTrash();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String key = data.getStringExtra(Constant.Reference.REFER_KEY);
            String value = data.getStringExtra(Constant.Reference.REFER_VALUE);
            if (Constant.Reference.DEPART == requestCode) {
                roomText.setText(value);
                trashItem.departcode = key;
            } else if (requestCode == Constant.Reference.NURSE) {
                nurseText.setText(value);
                trashItem.nurseid = key;
            } else if (Constant.Reference.CATEGORY == requestCode) {
                typeText.setText(value);
                trashItem.categorycode = key;
            }else if (Constant.Reference.AREA == requestCode){
                areaText.setText(value);
                trashItem.departareacode = key;
            }
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

    /**
     * 初始化读卡设备
     */
    private void initDevice() {
        readUtil = new ReadUtil().setReadListener(this);
        if (readUtil.initUfh(this) != 0) {
            ToastUtil.shortToast(this, "打开设备失败");
            rfidBtn.setEnabled(false);
        } else{
            rfidBtn.setEnabled(true);
        }
    }

    /**
     * 保存垃圾信息,todo 打印功能
     */
    private void saveTrash() {
        trashItem.date = DateUtil.getDateString();
        trashItem.trashcode = formCodeText.getText().toString();
        trashItem.trashcancode = garbageCanCodeText.getText().toString();
        trashItem.departarea = areaText.getText().toString();
        trashItem.departname = roomText.getText().toString();
        trashItem.nurse = nurseText.getText().toString();
        trashItem.categoryname = typeText.getText().toString();
        trashItem.weight = weightText.getText().toString();
        trashItem.collector = collectorText.getText().toString();
        trashItem.collectorid = Utils.getUserId();
        trashItem.colletime = dtmText.getText().toString();
        trashItem.status = Constant.Status.NEWCOLLECT;
        TrashDao trashDao = TrashDao.getInstance();
        trashDao.setTrash(trashItem);

        Intent intent = new Intent(this, PrintHomeActivity.class);
        intent.putExtra("TrashItem",trashItem);
        startActivity(intent);

    }
}
