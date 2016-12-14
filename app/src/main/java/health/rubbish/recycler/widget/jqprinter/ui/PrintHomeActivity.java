package health.rubbish.recycler.widget.jqprinter.ui;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import health.rubbish.recycler.R;
import health.rubbish.recycler.activity.entruck.EntruckerAddActivity;
import health.rubbish.recycler.base.App;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.util.DateUtil;
import health.rubbish.recycler.util.FileUtil;
import health.rubbish.recycler.util.QRCodeUtil;
import health.rubbish.recycler.widget.HeaderLayout;
import health.rubbish.recycler.widget.jqprinter.port.SerialPort;
import health.rubbish.recycler.widget.jqprinter.printer.JQPrinter;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define.PRINTER_MODEL;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.Barcode;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.JPL;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.Page;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.Text;
import health.rubbish.recycler.widget.jqprinter.ui.card_reader.MainCardReaderActivity;
import health.rubbish.recycler.widget.jqprinter.ui.enforcement_bill.ebMainActivity;
import health.rubbish.recycler.widget.jqprinter.ui.express.MainExpressActivity;
import health.rubbish.recycler.widget.jqprinter.ui.express_form.ExpressFormMainActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;




public class PrintHomeActivity extends BaseActivity {

	private final static int REQUEST_BT_ENABLE = 0;
	private final static int REQUEST_BT_ADDR = 1;
	private boolean mBtOpenSilent = true;
	private BluetoothAdapter btAdapter = null;
	private JQPrinter printer = new JQPrinter(PRINTER_MODEL.JLP351);

	private App mApplication = null;
	private Button mButtonBtScan = null;
	HeaderLayout headerLayout;
	private ImageView barcodeView ;
	private TextView trashcodeView ;
	private TextView trashcancodeView ;
	private TextView departareaView ;
	private TextView departView ;
	private TextView nurseView ;
	private TextView typeView ;
	private TextView weightView ;
	private TextView dateView ;



	private long mLastTime = 0;
	private TrashItem trashItem;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_printmain;
	}

	@Override
	protected void init() {
		create();
		initHeaderView();
		getView();
	}

	private void initHeaderView() {
		headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
		headerLayout.isShowBackButton(true);
		headerLayout.showTitle("打印预览");

	}
	protected void create() {
		trashItem = (TrashItem)getIntent().getSerializableExtra("TrashItem");
		setTitle("济强打印机Demo     ");

		mApplication = (App)getApplication();  //tip:App需要在AndroidManifest.xml的application中注册  android:name="health.rubbish.recycler.widget.jqprinter.ui.App"
		mButtonBtScan = (Button)findViewById(R.id.BTButton);
		mButtonBtScan.setText("打印");

		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null) {
			Toast.makeText(this, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		mApplication.btAdapter = btAdapter;

		// 如果本地蓝牙没有开启，则开启
		//以下操作需要在AndroidManifest.xml中注册权限android.permission.BLUETOOTH ；android.permission.BLUETOOTH_ADMIN
		if (!btAdapter.isEnabled()) {
			Toast.makeText(this, "正在开启蓝牙", Toast.LENGTH_SHORT).show();
			if (!mBtOpenSilent)
			{
				// 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
				// 那么将会收到RESULT_OK的结果，
				// 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
				Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(mIntent,REQUEST_BT_ENABLE );
			}
			else { // 用enable()方法来开启，无需询问用户(无声息的开启蓝牙设备),这时就需要用到android.permission.BLUETOOTH_ADMIN权限。
				btAdapter.enable();
				Toast.makeText(this, "本地蓝牙已打开", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(this, "本地蓝牙已打开", Toast.LENGTH_SHORT).show();
		}
	}

	private void getView()
	{
		  barcodeView = (ImageView)findViewById(R.id.barcode);
		  trashcodeView = (TextView)findViewById(R.id.trashcode);
		  trashcancodeView = (TextView)findViewById(R.id.trashcancode);
		  departareaView = (TextView)findViewById(R.id.departarea);
		  departView = (TextView)findViewById(R.id.depart);
		  nurseView = (TextView)findViewById(R.id.nurse);
		  typeView  = (TextView)findViewById(R.id.type);
		  weightView  = (TextView)findViewById(R.id.weight);
		  dateView  = (TextView)findViewById(R.id.date);


		final String filePath = new FileUtil().getDownloadDir() + File.separator
				+ "qr_shareunify.jpg";

		//二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean success = QRCodeUtil.createQRImage(trashItem.trashcode, 800, 800,
						null,
						filePath);

				if (success) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							barcodeView.setImageBitmap(BitmapFactory.decodeFile(filePath));
						}
					});
				}
			}
		}).start();
		trashcodeView.setText(trashItem.trashcode);
		trashcancodeView.setText(trashItem.trashcancode);
		departareaView.setText(trashItem.departarea);
		departView.setText(trashItem.departname);
		nurseView.setText(trashItem.nurse);
		typeView.setText(trashItem.categoryname);
		weightView.setText(trashItem.weight);
		dateView.setText(trashItem.date);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
//        menu.clear();
		menu.add(0,0,0,"退出");
		return true;
	}

	private void exit()
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
			case 0:
				exit();
				break;
		}

		this.buttonSetupSerialPort_click();
		// 返回false允许正常的菜单处理资源，若返回true，则直接在此毁灭它
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)//主要是对这个函数的复写
	{
		if((keyCode == KeyEvent.KEYCODE_BACK)&&(event.getAction() == KeyEvent.ACTION_DOWN))
		{
			if(System.currentTimeMillis() - mLastTime >2000) // 2s内再次选择back键有效
			{
				System.out.println(Toast.LENGTH_LONG);
				Toast.makeText(this, "请再按一次返回退出", Toast.LENGTH_LONG).show();
				mLastTime = System.currentTimeMillis();
			}
			else
			{
				exit();
			}
			return true;

		}
		return super.onKeyDown(keyCode, event);
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
				exit();
				return;
			}
		}
		else if (requestCode == REQUEST_BT_ADDR)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				String btDeviceString = data.getStringExtra(BtConfigActivity.EXTRA_BLUETOOTH_DEVICE_ADDRESS);
				if (btDeviceString != null)
				{
					mButtonBtScan.setText("名称:"+data.getStringExtra(BtConfigActivity.EXTRA_BLUETOOTH_DEVICE_NAME) + "\r\n地址:" + btDeviceString);
					if(btAdapter.isDiscovering())
						btAdapter.cancelDiscovery();

					if (printer != null)
					{
						printer.close();
					}

					if (!printer.open(btDeviceString))
					{
						Toast.makeText(this, "打印机Open失败", Toast.LENGTH_SHORT).show();
						return;
					}

					if (!printer.wakeUp())
						return;

					mApplication.printer = printer;
					Log.e("JQ", "printer open ok");

					IntentFilter filter = new IntentFilter();
					filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//蓝牙断开
					registerReceiver(mReceiver, filter);

					ButtonExpress_click();
				}
			}
			else
			{
				mButtonBtScan.setText("打印");
			}
		}
	}

	//蓝牙按键点击 ,此函数需要在 activity_main.xml文件中注册
	public void bt_button_click(View view)
	{
		if (btAdapter == null)
			return;
		mButtonBtScan.setText("请等待");
		Intent myIntent = new Intent(PrintHomeActivity.this, BtConfigActivity.class);
		startActivityForResult(myIntent, REQUEST_BT_ADDR);
	}


	public void ButtonExpress_click()
	{
		// Cancel discovery because it will slow down the connection
		if(btAdapter.isDiscovering())
			btAdapter.cancelDiscovery();

		if (!printer.waitBluetoothOn(5000))
		{
			mButtonBtScan.setText("打印");
			return;
		}
		if (!printer.isOpen)
		{
			mButtonBtScan.setText("打印");
			return;
		}

		print();
	}


	public void  buttonSetupSerialPort_click()
	{
		startActivity(new Intent(PrintHomeActivity.this, SerialPortPreferences.class));
	}


	int startIndex;//开始打印的序号
	int amount;//打印的总数
	boolean rePrint = false;//是否需要重新打印
	public void print()
	{

		if (!printer.isOpen)
		{
			this.finish();
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
							PrintHomeActivity.this.finish();
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


		int y = 10, x,x1, x2, x3,x4,x5;
		x = 20;
		x1 = 100;
		x2 = 20;
		x3 = 140;
		x4 = 280;
		x5 = 380;

		int height = 30;

		if (!jpl.page.start(0, 0, 676, 504, Page.PAGE_ROTATE.x0))
			return false;

		y = 10;

		if (!jpl.barcode.QRCode(x4+90, y+80, 0, Barcode.QRCODE_ECC.LEVEL_M, Barcode.BAR_UNIT.x7, JPL.ROTATE.x0, trashItem.trashcode))
			return false;
		y = y+5;

		/*if (!jpl.text.drawOut(x, y+180, "no:", 18, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0))
            return false;
        jpl.text.drawOut(116, y+180, "nostr11111111111111", 16, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);*/

		jpl.text.drawOut(x2, y, "垃圾袋号:", 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		jpl.text.drawOut(x3, y, trashItem.trashcode, 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		y += height;

		jpl.text.drawOut(x2, y, "垃圾桶号:", 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		jpl.text.drawOut(x3, y, trashItem.trashcancode, 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		y += height;

		jpl.text.drawOut(x2, y, "院　　区:", 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		jpl.text.drawOut(x3, y, trashItem.departarea, 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		y += height;

		jpl.text.drawOut(x2, y, "科　　室:", 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		jpl.text.drawOut(x3, y, trashItem.departname, 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		y += height;
		
		jpl.text.drawOut(x2, y, "护　　士:", 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		jpl.text.drawOut(x3, y, trashItem.nurse, 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		y += height;

		jpl.text.drawOut(x2, y, "类　　型:", 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		jpl.text.drawOut(x3, y, trashItem.categoryname, 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		y += height;
		
		jpl.text.drawOut(x2, y, "重　　量:", 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		jpl.text.drawOut(x3, y, trashItem.weight + "  kg", 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		y += height;

		jpl.text.drawOut(x2, y, "日　　期:", 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		jpl.text.drawOut(x3, y, trashItem.date, 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);
		y += height+25;
		jpl.text.drawOut(x2, y, "签　　字:", 18, true, false, false, false, Text.TEXT_ENLARGE.x1, Text.TEXT_ENLARGE.x1, JPL.ROTATE.x0);

        /*if (!jpl.graphic.line(x3, y, 575, y, 1))
            return false;*/

		jpl.page.end();
		jpl.page.print();
		return printer.jpl.exitGPL(1000);

	}
}
