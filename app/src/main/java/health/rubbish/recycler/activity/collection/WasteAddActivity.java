package health.rubbish.recycler.activity.collection;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import health.rubbish.recycler.R;
import health.rubbish.recycler.base.BaseActivity;

/**
 * Created by xiayanlei on 2016/11/23.
 */
public class WasteAddActivity extends BaseActivity implements View.OnClickListener {
    private TextView formCodeText;
    private TextView garbageCanCodeText;
    private TextView roomText;
    private TextView nurseText;
    private TextView typeText;
    private EditText weightText;
    private TextView collectorText;
    private TextView dtmText;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_waste_add;
    }

    @Override
    protected void init() {
        formCodeText = (TextView) findViewById(R.id.form_code_text);
        garbageCanCodeText = (TextView) findViewById(R.id.garbagecan_code_text);
        roomText = (TextView) findViewById(R.id.room_text);
        nurseText = (TextView) findViewById(R.id.nurse_text);
        typeText = (TextView) findViewById(R.id.waste_type_text);
        weightText = (EditText) findViewById(R.id.weight_text);
        collectorText = (TextView) findViewById(R.id.collector_text);
        dtmText = (TextView) findViewById(R.id.dtm_text);
        Button rfidBtn = (Button) findViewById(R.id.rfid_button);
        Button saveBtn = (Button)findViewById(R.id.save_print_btn);
        rfidBtn.setOnClickListener(this);
        roomText.setOnClickListener(this);
        nurseText.setOnClickListener(this);
        typeText.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rfid_button:
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
}
