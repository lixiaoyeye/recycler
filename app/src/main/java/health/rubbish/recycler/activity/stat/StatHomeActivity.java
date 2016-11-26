package health.rubbish.recycler.activity.stat;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.activity.login.LoginActivity;
import health.rubbish.recycler.activity.transfer.TransferAddActivity;
import health.rubbish.recycler.adapter.TransferListAdapter;
import health.rubbish.recycler.base.App;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.StatEntity;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.util.DateUtil;
import health.rubbish.recycler.util.LoginUtil;
import health.rubbish.recycler.util.NetUtil;
import health.rubbish.recycler.widget.CustomProgressDialog;
import health.rubbish.recycler.widget.EmptyFiller;
import health.rubbish.recycler.widget.HeaderLayout;
import health.rubbish.recycler.widget.ProgressWheel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Lenovo on 2016/11/20.
 */

public class StatHomeActivity extends BaseActivity {
    HeaderLayout headerLayout;
    
    private RadioGroup radioGroup;

    //垃圾收集
    private TextView collectnumText;
    private TextView collectratioText;
    private LinearLayout collectnumLyt;
    private LinearLayout collectratioLyt;

    //未装箱
    private TextView untransfernumText;
    private TextView untransferratioText;
    private LinearLayout untransfernumLyt;
    private LinearLayout untransferratioLyt;

    //已装车
    private TextView dumpcartnumText;
    private TextView dumpcartratioText;
    private LinearLayout dumpcartnumLyt;
    private LinearLayout dumpcartratioLyt;

    //加载view
    private View loadingView;
    private ProgressWheel loadingProgress;
    private TextView loadingFailText;

    private StatEntity dayStatEntity;
    private StatEntity weekStatEntity;
    private StatEntity monthStatEntity;

    private String type ="day";


    @Override
    protected int getLayoutId() {
        return R.layout.activity_stathome;
    }

    @Override
    protected void init() {
        initHeaderView();
        initViews();
        initEvents();
        loadData();
    }

    private void initHeaderView() {
        headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
        headerLayout.isShowBackButton(true);
        headerLayout.showTitle("统计看板");
    }

    private void initViews() {

        radioGroup = (RadioGroup) findViewById(R.id.stathome_radioGroup);

        collectnumText = (TextView) findViewById(R.id.stathome_collectnum_text);
        collectratioText = (TextView) findViewById(R.id.stathome_collectratio_text);
        collectnumLyt = (LinearLayout) findViewById(R.id.stathome_collectnum_lyt);
        collectratioLyt = (LinearLayout) findViewById(R.id.stathome_collectratio_lyt);

        untransfernumText = (TextView) findViewById(R.id.stathome_untransfernum_text);
        untransferratioText = (TextView) findViewById(R.id.stathome_untransferratio_text);
        untransfernumLyt = (LinearLayout) findViewById(R.id.stathome_untransfernum_lyt);
        untransferratioLyt = (LinearLayout) findViewById(R.id.stathome_untransferratio_lyt);

        dumpcartnumText = (TextView) findViewById(R.id.stathome_dumpcartnum_text);
        dumpcartratioText = (TextView) findViewById(R.id.stathome_dumpcartratio_text);
        dumpcartnumLyt = (LinearLayout) findViewById(R.id.stathome_dumpcartnum_lyt);
        dumpcartratioLyt = (LinearLayout) findViewById(R.id.stathome_dumpcartratio_lyt);

        loadingView = findViewById(R.id.stathome_loading);
        loadingProgress = (ProgressWheel) findViewById(R.id.stathome_loading_progress);
        loadingFailText = (TextView) findViewById(R.id.stathome_loading_fail);
    }

    private void initEvents() {

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.stathome_radioButton0:
                        type = "day";
                        updateUI(dayStatEntity);
                        break;
                    case R.id.stathome_radioButton1:
                        type = "week";
                        updateUI(weekStatEntity);
                        break;
                    case R.id.stathome_radioButton2:
                        type = "month";
                        updateUI(monthStatEntity);
                        break;
                }
            }
        });

        collectnumLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2016/11/26  
                Intent intent = new Intent(StatHomeActivity.this, StatHomeActivity.class);
                intent.putExtra("type",type);
                intent.putExtra("time",DateUtil.getDateString());
                startActivity(intent);
            }
        });

        collectratioLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectnumLyt.performClick();
            }
        });


        untransfernumLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2016/11/26  
            }
        });

        untransferratioLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                untransfernumLyt.performClick();
            }
        });


        dumpcartnumLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2016/11/26  
            }
        });

        dumpcartratioLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dumpcartnumLyt.performClick();
            }
        });
    }

    private void loadData() {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userid", LoginUtil.getLoginUser().userid)
                .add("date", DateUtil.getDateString())
                .build();
        showDialog("正在加载中……");
        Call call = mOkHttpClient.newCall(NetUtil.getRequest("login",requestBody));
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e) {
                hideDialog();
                new AlertDialog.Builder(StatHomeActivity.this).setMessage(R.string.netnotavaliable).setPositiveButton("确定", null).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideDialog();
                parseResponse(response.body().string());
            }
        });
    }

    private void hideLoadingView() {
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(loadingView, "alpha", 1, 0);
        ObjectAnimator translationAnim = ObjectAnimator.ofFloat(loadingView, "translationX", 0, loadingView.getWidth());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(alphaAnim, translationAnim);
        animatorSet.start();
    }

    private void parseResponse(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }

        try {

            JSONObject jsonObject = new JSONObject(result);
            JSONObject rowsObject = jsonObject.getJSONObject("rows");

            dayStatEntity = JSON.parseObject(rowsObject.getString("day"), StatEntity.class);
            weekStatEntity = JSON.parseObject(rowsObject.getString("week"), StatEntity.class);
            monthStatEntity = JSON.parseObject(rowsObject.getString("month"), StatEntity.class);

            updateUI(dayStatEntity);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void updateUI(StatEntity entity) {

        if (entity == null) {
            entity = new StatEntity();
        }

        collectnumText.setText(entity.getCollectnum());
        collectratioText.setText(entity.getCollectratio());
        if (entity.getCollectratio().contains("-")) {
            collectnumText.setTextColor(getResources().getColor(R.color.red));
            collectratioText.setTextColor(getResources().getColor(R.color.red));
        } else {
            collectnumText.setTextColor(getResources().getColor(R.color.tree_green));
            collectratioText.setTextColor(getResources().getColor(R.color.tree_green));
        }

        untransfernumText.setText(entity.getUntransfernum());
        untransferratioText.setText(entity.getUntransferratio());
        if (entity.getUntransferratio().contains("-")) {
            untransfernumText.setTextColor(getResources().getColor(R.color.red));
            untransferratioText.setTextColor(getResources().getColor(R.color.red));
        } else {
            untransfernumText.setTextColor(getResources().getColor(R.color.tree_green));
            untransferratioText.setTextColor(getResources().getColor(R.color.tree_green));
        }

        dumpcartnumText.setText(entity.getDumpcartnum());
        dumpcartratioText.setText(entity.getDumpcartratio());
        if (entity.getDumpcartratio().contains("-")) {
            dumpcartnumText.setTextColor(getResources().getColor(R.color.red));
            dumpcartratioText.setTextColor(getResources().getColor(R.color.red));
        } else {
            dumpcartnumText.setTextColor(getResources().getColor(R.color.tree_green));
            dumpcartratioText.setTextColor(getResources().getColor(R.color.tree_green));
        }
    }

}
