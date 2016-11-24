package health.rubbish.recycler.activity.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.base.App;
import health.rubbish.recycler.datebase.CatogeryDao;
import health.rubbish.recycler.datebase.DepartmentDao;
import health.rubbish.recycler.entity.CatogeryItem;
import health.rubbish.recycler.entity.DepartmentItem;
import health.rubbish.recycler.entity.LoginUser;
import health.rubbish.recycler.R;
import health.rubbish.recycler.activity.MainActivity;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.util.LoginUtil;
import health.rubbish.recycler.util.NetUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lenovo on 2016/11/20.
 */

public class LoginActivity extends BaseActivity {
    private EditText userNameView;
    private EditText passwordView;
    private ImageView setIpView;
    private Button loginView;

    private static final String ADDRESS = "address";
    private String ipStr = "168.168.10.43";
    private String portStr = "9023";

    private String userid;
    private String password;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        saveDefaultAddress();
        initView();
        setDefaultData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView()
    {
        userNameView  = (EditText)findViewById(R.id.username_edittext);
        passwordView = (EditText)findViewById(R.id.password_edittext);
        setIpView = (ImageView) findViewById(R.id.setip_img);
        setIpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SetIpActivity.class);
                startActivity(intent);
            }
        });

        loginView = (Button) findViewById(R.id.login_btn);
        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        userid = userNameView.getText().toString().trim();
       password = passwordView.getText().toString().trim();

        if (TextUtils.isEmpty(userid)) {
            toast(R.string.username_cannot_null);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            toast(R.string.password_can_not_null);
            return;
        }

        //判断是否有网络连接，如果有，网络验证连接，没有则离线登录。
        if (NetUtil.isNetAvailable()) {
            hideDialog();
            netIdentify();
        } else {
            new AlertDialog.Builder(LoginActivity.this).setTitle("医废管理系统").setMessage("暂时无法连接到网络，请检查网络").setPositiveButton("确定", null).show();
        }
    }

    private void netIdentify() {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userid", userid)
                .add("password", password)
                .build();
        Call call = mOkHttpClient.newCall(NetUtil.getRequest("login",requestBody));
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e) {
                hideDialog();
                new AlertDialog.Builder(LoginActivity.this).setMessage(R.string.netnotavaliable).setPositiveButton("确定", null).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideDialog();
                parseLoginResponse(response.body().string());
            }
        });
    }


    private void saveDefaultAddress() {
        SharedPreferences sharedPreferences = getSharedPreferences(ADDRESS, Activity.MODE_PRIVATE);
        String tempIp = sharedPreferences.getString("ip", null);
        if (tempIp == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ip", ipStr);
            editor.putString("port", portStr);
            editor.commit();
        } else {
            return;
        }
    }

    //获取保存在本地的用户名和密码
    private void setDefaultData() {
        LoginUser loginUser = App.getCurrentUser();
        if (loginUser != null) {
            userNameView.setText(loginUser.userid);
            passwordView.setText(loginUser.password);
        }
    }

    private void parseLoginResponse(String result)
    {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String success = jsonObject.getString("result");
            if (success.equals("success"))
            {
                LoginUser loginUser = new LoginUser();
                loginUser.userid = userid;
                loginUser.password = password;
                loginUser.username = jsonObject.getString("username");
                loginUser.positioncode = jsonObject.getString("positioncode");
                loginUser.position = jsonObject.getString("position");
                loginUser.authority = jsonObject.getString("authority");

                App.ctx.setCurrentUser(loginUser);
                LoginUtil.saveLoginUser(loginUser);

                JSONArray jsonArray = jsonObject.getJSONArray("department_row");
                final  List<DepartmentItem> departmentItemList = new ArrayList<>();
                DepartmentItem departmentItem;
                JSONObject object;
                for (int i= 0;i<jsonArray.length();i++)
                {
                    object = jsonArray.getJSONObject(i);
                    departmentItem = new DepartmentItem();
                    departmentItem.departcode = object.getString("departcode");
                    departmentItem.departname = object.getString("departname");
                    departmentItem.departareacode = object.getString("departareacode");
                    departmentItem.departarea = object.getString("departarea");
                    departmentItem.nurseid = object.getString("nurseid");
                    departmentItem.nurse = object.getString("nurse");
                    departmentItem.nursephone = object.getString("nursephone");
                    departmentItemList.add(departmentItem);
                }


                jsonArray = jsonObject.getJSONArray("trashsort_row");
                final List<CatogeryItem> catogeryItems = new ArrayList<>();
                CatogeryItem categorycode;
                for (int i= 0;i<jsonArray.length();i++)
                {
                    object = jsonArray.getJSONObject(i);
                    categorycode = new CatogeryItem();
                    categorycode.categorycode = object.getString("categorycode");
                    categorycode.categoryname = object.getString("categoryname");
                    catogeryItems.add(categorycode);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DepartmentDao.getInstance().setAllDepartment(departmentItemList);
                        CatogeryDao.getInstance().setAllCatogery(catogeryItems);
                    }
                }).start();


                jumpToMain();
            }
            else
            {
                toast("获取数据错误，无法登陆");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void jumpToMain()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}


