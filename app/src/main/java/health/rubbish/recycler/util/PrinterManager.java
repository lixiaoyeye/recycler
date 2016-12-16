package health.rubbish.recycler.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;

import health.rubbish.recycler.base.App;
import health.rubbish.recycler.entity.MyPrinterInfo;
import health.rubbish.recycler.network.request.ParseUtil;

/**
 * Created by Lenovo on 2016/12/16.
 */

public class PrinterManager {

    public MyPrinterInfo getPrinter() {
        MyPrinterInfo result = new MyPrinterInfo();
        SharedPreferences sharedPreferences = App.ctx.getSharedPreferences("MyPrinterInfo", Activity.MODE_PRIVATE);
        result.name = sharedPreferences.getString("name", null);
        result.address = sharedPreferences.getString("address", null);
        if (TextUtils.isEmpty(result.address) || TextUtils.isEmpty(result.name)) {
            return null;
        }
        return result;
    }

    public void setPrinter(MyPrinterInfo info)
    {
        SharedPreferences sharedPreferences = App.ctx.getSharedPreferences("MyPrinterInfo", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", info.name);
        editor.putString("address", info.address);
        editor.commit();
    }
}
