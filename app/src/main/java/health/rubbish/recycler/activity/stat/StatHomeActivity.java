package health.rubbish.recycler.activity.stat;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.io.IOException;

import health.rubbish.recycler.R;
import health.rubbish.recycler.activity.login.LoginActivity;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.entity.StatEntity;
import health.rubbish.recycler.network.http.CustomHttpClient;
import health.rubbish.recycler.network.request.ParseCallback;
import health.rubbish.recycler.network.request.RequestUtil;
import health.rubbish.recycler.util.DateUtil;
import health.rubbish.recycler.util.LoginUtil;
import health.rubbish.recycler.util.NetUtil;
import health.rubbish.recycler.util.Utils;
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
                Intent intent = new Intent(StatHomeActivity.this, StatCollectActivity.class);
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
                Intent intent = new Intent(StatHomeActivity.this, StatUntransferActivity.class);
                intent.putExtra("type",type);
                intent.putExtra("time",DateUtil.getDateString());
                startActivity(intent);
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
                Intent intent = new Intent(StatHomeActivity.this, StatDeparttimesActivity.class);
                intent.putExtra("type",type);
                intent.putExtra("time",DateUtil.getDateString());
                startActivity(intent);
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

        CustomHttpClient client = new CustomHttpClient();
        client.addParam("userid",  LoginUtil.getLoginUser().userid);
        client.addParam("date", DateUtil.getDateString());
        new RequestUtil().sendPost("statTrashInfo",client,new ParseCallback<String>() {
            @Override
            public void onComplete(String result) {
                Log.e("123","statTrashInfo = "+result);
                parseResponse(result);
                hideLoadingView();
            }

            @Override
            public void onError(String error) {
                loadingProgress.setVisibility(View.GONE);
                loadingFailText.setVisibility(View.VISIBLE);
                new AlertDialog.Builder(StatHomeActivity.this).setMessage(R.string.client_error).setPositiveButton("确定", null).show();
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

            dayStatEntity = JSON.parseObject(jsonObject.getString("day"), StatEntity.class);
            weekStatEntity = JSON.parseObject(jsonObject.getString("week"), StatEntity.class);
            monthStatEntity = JSON.parseObject(jsonObject.getString("month"), StatEntity.class);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI(dayStatEntity);
                }
            });


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
