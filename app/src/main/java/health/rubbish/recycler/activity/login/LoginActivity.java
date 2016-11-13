package health.rubbish.recycler.activity.login;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import health.rubbish.recycler.R;
import health.rubbish.recycler.activity.MainActivity;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.network.request.RequestUtil;
import health.rubbish.recycler.network.request.ResponseParser;
import health.rubbish.recycler.util.ToastUtil;
import health.rubbish.recycler.widget.CustomProgressDialog;


/**
 * Created by xiayanlei on 2016/11/13.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText userNamaText;

    private EditText passwordText;

    private CustomProgressDialog progressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViewAndEvents() {
        userNamaText = (EditText) findViewById(R.id.username_edittext);
        passwordText = (EditText) findViewById(R.id.password_edittext);
        Button loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                onLoginClick();
                break;
        }
    }

    /**
     * 校验用户名和密码是否非空
     *
     * @param username
     * @param password
     */
    private boolean isUserValid(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            ToastUtil.shortToast(this, R.string.username_input_hint);
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.shortToast(this, R.string.password_input_hint);
            return false;
        }
        return true;
    }

    /**
     * 登录到主界面
     */
    private void onLoginClick() {
        String username = userNamaText.getText().toString();
        String password = passwordText.getText().toString();
        if (!isUserValid(username, password))
            return;
        if (progressDialog == null)
            progressDialog = new CustomProgressDialog(this);
        progressDialog.setMessage(R.string.start_logining).show();
        RequestUtil.getInstance().startLogin(username, password, new ResponseParser() {
            @Override
            public void onParserComplete(Exception e, Object o) {
                loginSuccess();
            }
        });
    }

    /**
     * 登录成功，跳转到主界面
     */
    private void loginSuccess() {
        progressDialog.dismiss();
        startActivity(new Intent(this, MainActivity.class));
    }
}
