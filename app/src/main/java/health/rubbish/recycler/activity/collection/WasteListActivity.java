package health.rubbish.recycler.activity.collection;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.activity.entruck.EntruckerListActivity;
import health.rubbish.recycler.adapter.WasteListAdapter;
import health.rubbish.recycler.base.App;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.MyPrinterInfo;
import health.rubbish.recycler.entity.PrintStatItem;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.network.entity.WasteUploadResp;
import health.rubbish.recycler.network.request.ParseCallback;
import health.rubbish.recycler.network.request.RequestUtil;
import health.rubbish.recycler.util.DateUtil;
import health.rubbish.recycler.util.PrinterManager;
import health.rubbish.recycler.util.ToastUtil;
import health.rubbish.recycler.widget.HeaderLayout;
import health.rubbish.recycler.widget.jqprinter.printer.JQPrinter;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.Barcode;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.JPL;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.Page;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.Text;
import health.rubbish.recycler.widget.jqprinter.ui.BtConfigActivity;
import health.rubbish.recycler.widget.jqprinter.ui.PrintHomeActivity;
import health.rubbish.recycler.widget.jqprinter.ui.SerialPortPreferences;
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
    List<TrashItem> clone= new ArrayList<>();

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
        headerLayout.showLeftBackButton();
        if (search)
        {
            headerLayout.showTitle("搜索结果");
        }
        else {
            initPrint();
            headerLayout.showTitle("垃圾收集");
            headerLayout.showRightImageButton(R.drawable.eventhome_add_icon, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(WasteListActivity.this, WasteAddActivity.class));
                }
            });
        }

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
                cloneRows();
                uploadWaste();
            }
        });
        uploadBtn.setVisibility(search?View.GONE:View.VISIBLE);
    }

    private void cloneRows()
    {
        clone.clear();
        List<TrashItem> items = adapter.getNeedUploadTrash();
        for (TrashItem item :items)
        {
            clone.add(item);
        }
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
                return trashDao.getNewTrashToday(DateUtil.getDateString(calendar.getTime()));
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
        WasteAddActivity.garbageCanCode = "";
        WasteAddActivity.catogeryname = "";
        WasteAddActivity.catogerycode = "";
        List<TrashItem> items = adapter.getNeedUploadTrash();

        if (items.size() == 0) {
            toast("所有数据都已上传");
            return;
        }
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
                        //TrashDao.getInstance().updateTrashStatus(resp.trashcode, resp.status);
                        TrashDao.getInstance().updateTrashStatus(resp.trashcode, Constant.Status.DOWNLOAD);
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
                printClick();
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

    private List<PrintStatItem> getPrintStat()
    {
        List<PrintStatItem> result = new ArrayList<>();
        for(TrashItem trashItem:clone)
        {
            int postion = getPintStatPostion(result,trashItem);
            if (postion ==-1)
            {
                PrintStatItem item = new PrintStatItem();
                item.categoryname = trashItem.categoryname;
                item.departname = trashItem.departname;
                if (!TextUtils.isEmpty(trashItem.weight)) {
                    item.weight = Double.parseDouble(trashItem.weight);
                }
                result.add(item);
            }
            else
            {
                if (!TextUtils.isEmpty(trashItem.weight)) {
                    result.get(postion).weight =result.get(postion).weight+ Double.parseDouble(trashItem.weight);
                }
            }
        }
        return result;
    }

    private int getPintStatPostion(List<PrintStatItem> items,TrashItem trashItem)
    {
        for (int i=0;i<items.size();i++)
        {
            PrintStatItem item = items.get(i);
            if (item.departname.equals(trashItem.departname) && item.categoryname.equals(trashItem.categoryname))
            {
                return i;
            }
        }
        return -1;
    }


    private final static int REQUEST_BT_ENABLE = 0;
    private final static int REQUEST_BT_ADDR = 1;
    private boolean mBtOpenSilent = true;
    private BluetoothAdapter btAdapter = null;
    private JQPrinter printer = new JQPrinter(Printer_define.PRINTER_MODEL.JLP351);
    private long mLastTime = 0;
    private App mApplication = null;

    private void initPrint()
    {
        mApplication = (App)getApplication();  //tip:App需要在AndroidManifest.xml的application中注册  android:name="health.rubbish.recycler.widget.jqprinter.ui.App"
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(this, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mApplication.btAdapter = btAdapter;
        if (!btAdapter.isEnabled()) {
            Toast.makeText(this, "正在开启蓝牙", Toast.LENGTH_SHORT).show();
            if (!mBtOpenSilent)
            {
                Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(mIntent,REQUEST_BT_ENABLE );
            }
            else {
                btAdapter.enable();
                Toast.makeText(this, "本地蓝牙已打开", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "本地蓝牙已打开", Toast.LENGTH_SHORT).show();
        }
    }

    private void exitPrint()
    {
        if (printer != null)
        {
            if(!printer.close())
                Toast.makeText(this, "打印机关闭失败", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "打印机关闭成功", Toast.LENGTH_SHORT).show();
            printer = null;
        }
        if(btAdapter != null )	{
            if (btAdapter.isEnabled())	{
                btAdapter.disable();
                Toast.makeText(this, "关闭蓝牙成功", Toast.LENGTH_LONG).show();
            }
        }
        finish();
        System.exit(0); //凡是非零都表示异常退出!0表示正常退出!
    }

    private void  printClick()
    {
        if (btAdapter == null)
            return;
        printerInfo = new PrinterManager().getPrinter();
        if (printerInfo!=null)
        {
            prePrint(printerInfo);
        }
        else {
            Intent myIntent = new Intent(WasteListActivity.this, BtConfigActivity.class);
            startActivityForResult(myIntent, REQUEST_BT_ADDR);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
            {
                if (printer != null)
                {
                    if (printer.isOpen)
                        printer.close();
                }
                Toast.makeText(context,"蓝牙连接已断开",Toast.LENGTH_LONG).show();
            }
        };
    };
    private MyPrinterInfo printerInfo = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BT_ENABLE)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(this, "蓝牙已打开", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "不允许蓝牙开启", Toast.LENGTH_SHORT).show();
                exitPrint();
                return;
            }
        }
        else if (requestCode == REQUEST_BT_ADDR)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                printerInfo = new MyPrinterInfo();
                printerInfo.address = data.getStringExtra(BtConfigActivity.EXTRA_BLUETOOTH_DEVICE_ADDRESS);
                printerInfo.name = data.getStringExtra(BtConfigActivity.EXTRA_BLUETOOTH_DEVICE_NAME);
                prePrint(printerInfo);
            }
        }
    }

    private void prePrint(MyPrinterInfo myPrinterInfo)
    {
        if (myPrinterInfo.address != null)
        {
            if(btAdapter.isDiscovering())
                btAdapter.cancelDiscovery();

            if (printer != null)
            {
                printer.close();
            }

            if (!printer.open(myPrinterInfo.address))
            {
                Toast.makeText(this, "打印机Open失败", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!printer.wakeUp())
                return;

            new PrinterManager().setPrinter(myPrinterInfo);

            mApplication.printer = printer;
            Log.e("JQ", "printer open ok");

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//蓝牙断开
            registerReceiver(mReceiver, filter);

            if(btAdapter.isDiscovering())
                btAdapter.cancelDiscovery();

            print();
        }
    }

    int startIndex;//开始打印的序号
    int amount;//打印的总数
    boolean rePrint = false;//是否需要重新打印
    public void print()
    {
        if (!printer.waitBluetoothOn(5000))
        {
            return;
        }

        if (!printer.isOpen)
        {
            return;
        }
        if (!rePrint)
        {
            amount = 1;
            startIndex = 1;
        }
        else
        {
            //软件无法判断当前打印的内容是否打印完好，所以需要重新打印当前张。你可以增加一个按钮来决定是打当前张还是打下一张。
            rePrint = false;
        }


        if (getPrinterState()) {
            if(printExpress())
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(300);
                            WasteListActivity.this.finish();
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private boolean getPrinterState()
    {
        if (!printer.getPrinterState(3000))
        {
            Toast.makeText(this, "获取打印机状态失败", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (printer.isCoverOpen)
        {
            Toast.makeText(this, "打印机纸仓盖未关闭", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (printer.isNoPaper)
        {
            Toast.makeText(this, "打印机缺纸", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean printExpress()
    {
        printer.jpl.intoGPL(1000);
        if (!printer.getJPLsupport())
        {
            Toast.makeText(this, "不支持JPL，请设置正确的打印机型号！", Toast.LENGTH_SHORT).show();
            return false;
        }
        JPL jpl = printer.jpl;

        int y = 10;
        int t1 = 100, t2 = 340, t3 = 500;
        int x1 = 20, x2 = 290, x3 = 470;
        int z1 = 20, z2= 140,z3 = 300;

        int height = 30;

        if (!jpl.page.start(0, 0, 676, 504, Page.PAGE_ROTATE.x0))
            return false;

        jpl.text.drawOut(t1, y, "科　室:", 19, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jpl.text.drawOut(t2, y, "类　型", 19, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jpl.text.drawOut(t3, y, "重量(kg)", 19, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        y += height;

        List<PrintStatItem> printStatItems =  getPrintStat();
        for (PrintStatItem item :printStatItems)
        {
            jpl.text.drawOut(x1, y, item.departname, 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
            jpl.text.drawOut(x2, y, item.categoryname, 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
            jpl.text.drawOut(x3, y, String.valueOf(item.weight), 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
            y += height;
        }

        y += height+30;
        jpl.text.drawOut(z1, y, "日　期:", 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jpl.text.drawOut(z2, y, DateUtil.getDateString(), 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
        jpl.text.drawOut(z3, y, "签　字:", 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);

        jpl.page.end();
        jpl.page.print();
        return printer.jpl.exitGPL(1000);

    }
}
