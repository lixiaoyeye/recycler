package health.rubbish.recycler.base;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import health.rubbish.recycler.entity.LoginUser;
import health.rubbish.recycler.util.LoginUtil;
import health.rubbish.recycler.widget.jqprinter.port.SerialPort;
import health.rubbish.recycler.widget.jqprinter.port.SerialPortFinder;
import health.rubbish.recycler.widget.jqprinter.printer.JQPrinter;

/**
 * Created by Lenovo on 2016/11/20.
 */

public class App extends Application {

    public static App ctx;
    private static LoginUser currentUser = null;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        currentUser = LoginUtil.getLoginUser();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public static LoginUser getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(LoginUser currentUser) {
        App.currentUser = currentUser;
    }


    public int getAppVersion() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(),
                    0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }


    public JQPrinter printer = null;
    public BluetoothAdapter btAdapter = null;

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;

    //通过配置文件来打开串口
    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort != null)
            return mSerialPort;

		/* Read serial port parameters */
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String path = sharedPref.getString("DEVICE", "");
        int baudrate = Integer.decode(sharedPref.getString("BAUDRATE", "-1"));

		/* Check parameters */
        if ((path.length() == 0) || (baudrate == -1)) {

            Toast.makeText(this, "请选择串口和波特率", Toast.LENGTH_LONG).show();
            return null;
        }

		/* Open the serial port */
        mSerialPort = new SerialPort(new File(path), baudrate, 0);

        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort == null) return;
        mSerialPort.close();
        mSerialPort = null;
    }
}
