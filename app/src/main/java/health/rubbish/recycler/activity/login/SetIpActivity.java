package health.rubbish.recycler.activity.login;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import health.rubbish.recycler.R;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.widget.ClearEditText;
import health.rubbish.recycler.widget.HeaderLayout;


/**
 * Created by Lenovo on 2016/11/20.
 */

public class SetIpActivity extends BaseActivity {
    HeaderLayout headerLayout;
    ClearEditText ip;
    ClearEditText port;
    Button save;

    private static final String ADDRESS = "address";

    private SharedPreferences shared;

    @Override
    protected int getLayoutId() {
        return R.layout.setip_layout;
    }

    @Override
    protected void init() {
        shared = getSharedPreferences(ADDRESS, Activity.MODE_PRIVATE);
        initHeaderView();
        initView();
        setView();
        setData();
    }

    private void initHeaderView() {
        headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetIpActivity.this.finish();
            }
        });
        headerLayout.showTitle("配置");
    }

    private void initView() {

        ip = (ClearEditText) findViewById(R.id.get_ip);
        port = (ClearEditText) findViewById(R.id.get_port);
        save = (Button) findViewById(R.id.btn_conserve);
    }

    //初始化两个clearEdittext的显示
    private void setView() {
        String tempIp = shared.getString("ip", null);
        String tempPort = shared.getString("port", null);
        ip.setText(tempIp);
        port.setText(tempPort);
    }

    private void saveData(String mIp, String mPort) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("ip", mIp);
        editor.putString("port", mPort);
        editor.commit();
    }

    private void setData() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempIp = ip.getText().toString();
                String tempPort = port.getText().toString();
                if (tempIp == null || tempIp.equals("")) {
                    toast("IP不能为空！");
                    return;
                }
                if (tempPort == null || tempPort.equals("")) {
                    toast("端口不能为空！");
                    return;
                }
                saveData(tempIp, tempPort);
                toast("配置成功！");
                SetIpActivity.this.finish();
            }
        });
    }

}
