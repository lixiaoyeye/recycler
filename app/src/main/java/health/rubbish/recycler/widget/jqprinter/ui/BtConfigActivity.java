package health.rubbish.recycler.widget.jqprinter.ui;

import java.lang.reflect.Method;
import java.util.Set;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import health.rubbish.recycler.base.App;

public class BtConfigActivity extends Activity {
	public static final String EXTRA_BLUETOOTH_DEVICE_ADDRESS = "Bluetooth Device Adrress";
	public static final String EXTRA_BLUETOOTH_DEVICE_NAME = "Bluetooth Device Name";
	String mBtDevChosen = null;
	String mBtDevName   = null;
	BluetoothAdapter btAdapter = null;
	App mApp = null;

	//main layout
	private LinearLayout mMainLayout;
	private TextView     mPairedTitle;
	private ListView     mPairedDevicesListView;
	ArrayAdapter<String> mPairedDevList = null;
	private TextView     mAvailableTitle;
	private ListView     mAvailableDevicesListView;
	ArrayAdapter<String> mAvailableDevList = null;
	private Button       mScanButton;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mainlayout_create();
		setResult(RESULT_CANCELED);

		mApp = (App)getApplication();
		btAdapter = mApp.btAdapter;
		if(btAdapter == null)
		{
			Log.d("Bluetooth", "The BluetoothAdapte is unvailable!");
			finish();
		}
		long start_time = SystemClock.elapsedRealtime();
		for(;;)
		{
			if (BluetoothAdapter.STATE_ON == btAdapter.getState())
			{
				break;
			}
			if (SystemClock.elapsedRealtime() -start_time > 30*1000)
			{
				Log.e("JQ", "adapter state on timeout");
				return ;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//Get and show the BondedDevices' name and address
		//if have no BondedDevice ,show "No Bonded Devices";
		//set click event of the item
		//SetBtInfoLayout();
		mPairedDevList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		mPairedDevicesListView.setAdapter(mPairedDevList);
		mPairedDevicesListView.setOnItemClickListener(mBtDeviceClicked);
		registerForContextMenu(mPairedDevicesListView);
		bonded_devices_get();

		//Get and show other available name and address , set click event of the item
		mAvailableDevList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		mAvailableDevicesListView.setAdapter(mAvailableDevList);
		mAvailableDevicesListView.setOnItemClickListener(mBtDeviceClicked);
		registerForContextMenu(mAvailableDevicesListView);

		//register the listener of mScanButton
		mScanButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//TODO Auto-generated method stub
				v.setVisibility(View.GONE);
				discovry_do();
			}
		});

		//register broadcast receiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mBroadcastReceiver, filter);
	}

	private void mainlayout_create()
	{
		//LinearLayout
		mMainLayout = new LinearLayout(this);
		mMainLayout.setOrientation(LinearLayout.VERTICAL);
		LayoutParams myLayoutParam = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mMainLayout.setLayoutParams(myLayoutParam);
		//TextView
		mPairedTitle = new TextView(this);
		myLayoutParam = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mPairedTitle.setLayoutParams(myLayoutParam);
		mPairedTitle.setText("Paired Devices");
		mPairedTitle.setVisibility(View.GONE);
		mPairedTitle.setBackgroundColor(0xff666666);
		mPairedTitle.setTextColor(0xffffffff);
		mPairedTitle.setPadding(5, 0, 0, 0);
		//ListView
		mPairedDevicesListView = new ListView(this);
		myLayoutParam = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2);
		mPairedDevicesListView.setLayoutParams(myLayoutParam);
		mPairedDevicesListView.setStackFromBottom(true);

		//TextView
		mAvailableTitle = new TextView(this);
		myLayoutParam = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mAvailableTitle.setLayoutParams(myLayoutParam);
		mAvailableTitle.setText("Available Devices");
		mAvailableTitle.setVisibility(View.GONE);
		mAvailableTitle.setBackgroundColor(0xff666666);
		mAvailableTitle.setTextColor(0xffffffff);
		mAvailableTitle.setPadding(5, 0, 0, 0);
		//ListView
		mAvailableDevicesListView = new ListView(this);
		myLayoutParam = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2);
		mAvailableDevicesListView.setLayoutParams(myLayoutParam);
		mAvailableDevicesListView.setStackFromBottom(true);

		//Button
		mScanButton = new Button(this);
		myLayoutParam = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mScanButton.setLayoutParams(myLayoutParam);
		mScanButton.setText("Scan");

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setTitle("Choose Devices");
		mMainLayout.addView(mPairedTitle);
		mMainLayout.addView(mPairedDevicesListView);
		mMainLayout.addView(mAvailableTitle);
		mMainLayout.addView(mAvailableDevicesListView);
		mMainLayout.addView(mScanButton);
		setContentView(mMainLayout);
	}

	//ListView Item click event
	private OnItemClickListener mBtDeviceClicked = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int arg2,long arg3)
		{
			// TODO Auto-generated method stub
			if(btAdapter.isDiscovering())
			{
				btAdapter.cancelDiscovery();
			}
			String BtDeviceInfo = ((TextView)v).getText().toString();
			mBtDevName = BtDeviceInfo.split("\n")[0];
			mBtDevChosen = BtDeviceInfo.substring(BtDeviceInfo.length()-17);
			activity_result_set(mBtDevChosen);
		}
	};

	//set Activity Result
	private void activity_result_set(String BtDeviceAddress)
	{
		Intent intent = new Intent();
		intent.putExtra(EXTRA_BLUETOOTH_DEVICE_NAME, mBtDevName);
		intent.putExtra(EXTRA_BLUETOOTH_DEVICE_ADDRESS, BtDeviceAddress);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	//search BlueTooth devices
	private void discovry_do()
	{
		if(!btAdapter.isEnabled())
		{
			btAdapter.enable();
			while(btAdapter.getState() != BluetoothAdapter.STATE_ON);
		}
		setProgressBarIndeterminateVisibility(true);
		setTitle("Search devices...");
		//获取蓝牙设备，并开始搜索设备
		if(btAdapter.isDiscovering())
		{
			btAdapter.cancelDiscovery();
		}
		btAdapter.startDiscovery();
	}

	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(BluetoothDevice.ACTION_FOUND.equals(action))
			{
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(device.getBondState() != BluetoothDevice.BOND_BONDED)
				{
					mAvailableTitle.setVisibility(View.VISIBLE);
					mAvailableDevList.add(device.getName()+"\n"+device.getAddress());
				}
			}
			else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
			{
				setProgressBarIndeterminateVisibility(false);
				setTitle("Choose Device");
				if(mAvailableDevList.getCount() == 0)
				{
					mAvailableDevList.add("No availabel devices!");
				}
			}
		}
	};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo)
	{
		// TODO Auto-generated method stub
		if(!btAdapter.isEnabled())
		{
			btAdapter.enable();
			while(btAdapter.getState() != BluetoothAdapter.STATE_ON);
		}

		super.onCreateContextMenu(menu, v, menuInfo);
		String device_info = (String) ((ListView)v).getItemAtPosition(((AdapterContextMenuInfo) menuInfo).position).toString();
		mBtDevName = device_info.split("\n")[0];
		mBtDevChosen = device_info.substring(device_info.length()-17);
		menu.setHeaderTitle(mBtDevName);
		BluetoothDevice device = btAdapter.getRemoteDevice(mBtDevChosen);
		menu.clear();
		if(device.getBondState() == BluetoothDevice.BOND_BONDED)
		{
			menu.add("Cancel Paired");
		}
		else
		{
			menu.add("Pair and Connect");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.toString() == "Cancel Paired")
		{
			paired_cancel(mBtDevChosen);
		}
		else if(item.toString()== "Pair and Connect")
		{
			activity_result_set(mBtDevChosen);
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		unregisterReceiver(mBroadcastReceiver);
		super.finish();
	}

	//get the Bonded devices
	private void bonded_devices_get()
	{
		mPairedDevList.clear();
		Set<BluetoothDevice> PairedDevices = btAdapter.getBondedDevices();
		if(PairedDevices.size() > 0)
		{
			mPairedTitle.setVisibility(View.VISIBLE);
			for(BluetoothDevice device : PairedDevices)
			{
				String device_info = device.getName()+"\n"+device.getAddress();
				mPairedDevList.add(device_info);
			}
		}
		else
		{
			mPairedTitle.setVisibility(View.GONE);
			mPairedDevList.add("No Bonded Devices");
		}
	}

	//Cancel Paired
	private void paired_cancel(String BtAddress)
	{
		try
		{
			BluetoothDevice device = btAdapter.getRemoteDevice(BtAddress);
			Method CancelPairedMethod = device.getClass().getMethod("removeBond");
			CancelPairedMethod.invoke(device);
			while(device.getBondState() == BluetoothDevice.BOND_BONDED);
			bonded_devices_get();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

