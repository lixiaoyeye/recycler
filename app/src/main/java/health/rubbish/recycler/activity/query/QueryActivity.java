package health.rubbish.recycler.activity.query;

import android.content.Intent;
import android.haobin.barcode.BarcodeManager;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import health.rubbish.recycler.R;
import health.rubbish.recycler.activity.collection.ReferenceActivity;
import health.rubbish.recycler.activity.collection.WasteListActivity;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.datebase.CatogeryDao;
import health.rubbish.recycler.datebase.DepartmentDao;
import health.rubbish.recycler.entity.DepartmentItem;
import health.rubbish.recycler.mtuhf.ReadMode;
import health.rubbish.recycler.mtuhf.ReadUtil;
import health.rubbish.recycler.mtuhf.Ufh3Data;
import health.rubbish.recycler.util.BeepManager;
import health.rubbish.recycler.util.ToastUtil;
import health.rubbish.recycler.widget.HeaderLayout;
import health.rubbish.recycler.widget.zxing.activity.CaptureActivity;

/**
 * Created by xiayanlei on 2016/11/27.
 * 查询页面
 */
public class QueryActivity extends BaseActivity implements View.OnClickListener, ReadUtil.ReadListener {
    private TextView roomText;
    private TextView nurseText;
    private TextView categoryText;
    private EditText garbageCcText;
    private TextView garbagePkgText;
    private Button rfidBtn;
    private ReadUtil readUtil;
    private String departcode;
    private String nurseid;
    private String categorycode;
    private String trashcancode;
    private String trashcode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_query;
    }

    @Override
    protected void init() {
        beepManager = new BeepManager(this, true, false);

        HeaderLayout headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(R.string.data_query);
        roomText = (TextView) findViewById(R.id.room_text);
        nurseText = (TextView) findViewById(R.id.nurse_text);
        categoryText = (TextView) findViewById(R.id.category_text);
        garbageCcText = (EditText) findViewById(R.id.garbage_can_text);
        garbagePkgText = (TextView) findViewById(R.id.garbage_package_text);
        rfidBtn = (Button) findViewById(R.id.rfid_button);
        Button qrscanBtn = (Button) findViewById(R.id.qrscan_button);
        Button searchBtn = (Button) findViewById(R.id.search_btn);
        roomText.setOnClickListener(this);
        nurseText.setOnClickListener(this);
        categoryText.setOnClickListener(this);
        rfidBtn.setOnClickListener(this);
        qrscanBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        initDevice();
        garbageCcText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_F12 && event.getAction() == KeyEvent.ACTION_UP) {
                    Toast.makeText(QueryActivity.this,"正在RFID，请稍后",Toast.LENGTH_LONG).show();

                    if (null != barcodeManager) {
                        barcodeManager.Barcode_Close();
                        barcodeManager.Barcode_Stop();
                        barcodeManager=null;
                    }
                    readUtil = new ReadUtil().setReadListener(QueryActivity.this);
                    readUtil.initUfh(QueryActivity.this);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    readUtil.readUfhCard(ReadMode.EPC);
                    return true;
                } else {
                    return false;
                }
            }
        });

        garbagePkgText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_F12 ) {
                    if ( event.getAction() == KeyEvent.ACTION_DOWN) {
                        Toast.makeText(QueryActivity.this, "正在扫描二维码，请稍后", Toast.LENGTH_LONG).show();
                        readUtil = null;
                        if (barcodeManager == null) {
                            barcodeManager = BarcodeManager.getInstance();
                        }
                        barcodeManager.Barcode_Open(QueryActivity.this, dataReceived);
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    barHandler.sendEmptyMessage(Handler_Scan);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && Ufh3Data.isDeviceOpen()) {
            Ufh3Data.UhfGetData.CloseUhf();
        }
    }

    /**
     * 初始化读卡设备
     */
    private void initDevice() {
        readUtil = new ReadUtil().setReadListener(this);
        if (readUtil.initUfh(this) != 0) {
            ToastUtil.shortToast(this, "打开设备失败");
            rfidBtn.setEnabled(false);
        } else {
            rfidBtn.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ReferenceActivity.class);
        switch (v.getId()) {
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
            case R.id.category_text:
                intent.putExtra(Constant.Reference.REFER_TITLE, "垃圾类型选择");
                intent.putExtra(Constant.Reference.REFER_TYPE, Constant.Reference.CATEGORY);
                startActivityForResult(intent, Constant.Reference.CATEGORY);
                break;
            case R.id.rfid_button:
                readUtil.readUfhCard(ReadMode.EPC);
                break;
            case R.id.qrscan_button:
                intent.setClass(this, CaptureActivity.class);
                startActivityForResult(intent, Constant.QR_CODE);
                break;
            case R.id.search_btn:
                turnToActivity();
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
                departcode = key;
            } else if (requestCode == Constant.Reference.NURSE) {
                nurseText.setText(value);
                nurseid = key;
            } else if (Constant.Reference.CATEGORY == requestCode) {
                categoryText.setText(value);
                categorycode = key;
            } else if (Constant.QR_CODE == requestCode) {
                garbagePkgText.setText(data.getStringExtra("strBarcode"));
            } else if (Constant.DEPART_SCAN == requestCode) {//修改科室和护士信息
                DepartmentItem item = DepartmentDao.getInstance().getDepartByCode(data.getStringExtra("strBarcode"));
                if (item != null) {
                    roomText.setText(item.departname);
                    departcode = item.departcode;
                    nurseText.setText(item.nurse);
                    nurseid = item.nurseid;
                }
            }
        }
    }

    @Override
    public void onDataReceived(String data) {
        garbageCcText.setText(data);
        trashcancode = data;
    }

    @Override
    public void onFailure() {
        ToastUtil.shortToast(this, "读取失败");
    }

    private void turnToActivity() {
        trashcode = garbagePkgText.getText().toString();
        WasteListActivity.launchListActivity(this, departcode, nurseid, categorycode, trashcancode, trashcode);
    }


    //二维码
    private long nowTime = 0;
    private long lastTime = 0;
    private String codeId;
    private final int Handler_Scan = 2200;
    private BeepManager beepManager;
    private BarcodeManager barcodeManager;
    private final int Handler_SHOW_RESULT = 1999;
    private byte[] codeBuffer;

    private Handler barHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Handler_SHOW_RESULT:
                    if (null != codeBuffer) {
                        String str = new String(codeBuffer);
                        garbagePkgText.setText(str);
                        beepManager.play();
                        barcodeManager.Barcode_Stop();
                    }
                    break;
                case Handler_Scan:
                    nowTime = System.currentTimeMillis();
                    barcodeManager.Barcode_Stop();
                    // 按键时间不低于200ms
                    //if (nowTime - lastTime > 200)
                {
                    System.out.println("scan(0)");
                    if (null != barcodeManager) {
                        barcodeManager.Barcode_Start();
                    }
                    lastTime = nowTime;
                }
                break;

                default:
                    break;
            }
        };
    };

    BarcodeManager.Callback dataReceived = new BarcodeManager.Callback() {

        @Override
        public void Barcode_Read(byte[] buffer, String codeId, int errorCode) {
            // TODO Auto-generated method stub
            if (null != buffer) {
                codeBuffer = buffer;
                QueryActivity.this.codeId = codeId;
                Message msg = new Message();
                msg.what = Handler_SHOW_RESULT;
                barHandler.sendMessage(msg);
                barcodeManager.Barcode_Stop();
            }
        }
    };
}
