package health.rubbish.recycler.activity.stat;

import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.adapter.StatAdapter;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.entity.StatItem;
import health.rubbish.recycler.util.DateUtil;
import health.rubbish.recycler.util.LoginUtil;
import health.rubbish.recycler.util.NetUtil;
import health.rubbish.recycler.widget.EmptyFiller;
import health.rubbish.recycler.widget.HeaderLayout;
import health.rubbish.recycler.widget.xlist.XListView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StatUntransferActivity extends BaseActivity implements View.OnClickListener, XListView.IXListViewListener {
    private XListView listview;
    private ImageView date_pageturn_arrow_left;
    private ImageView date_pageturn_arrow_right;
    private TextView date_pageturn_date;

    private StatAdapter adapter;
    private List<StatItem> rows = new ArrayList<StatItem>();
    private Calendar calendar;
    private String type = "day"; //day week month
    private String title=""; //day week month
    String time;
    private String starttime;
    private String endtime;
    private int page = 1;
    private int limit = 15;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_statuntransfer;
    }

    @Override
    protected void init() {
        getIntentData();
        initHeaderView();
        initView();
        initDefaultData();
    }

    private void getIntentData() {
        type = getIntent().getStringExtra("type");
        time = getIntent().getStringExtra("time");
       // title = getIntent().getStringExtra("title");
    }

    private void initDefaultData() {
        calendar = Calendar.getInstance();
        if (!TextUtils.isEmpty(time)) {
            calendar.setTime(DateUtil.parseDate(time));
        }
        updateStartAndEndTime(0);
    }

    private void initHeaderView() {
        HeaderLayout mHeaderLayout = (HeaderLayout) findViewById(R.id.header_layout);
        mHeaderLayout.showLeftBackButton();
        mHeaderLayout.showTitle("垃圾发车统计");
    }

    private void initView() {
        date_pageturn_arrow_left = (ImageView) findViewById(R.id.date_pageturn_arrow_left);
        date_pageturn_arrow_right = (ImageView) findViewById(R.id.date_pageturn_arrow_right);
        date_pageturn_date = (TextView) findViewById(R.id.date_pageturn_date);

        listview = (XListView) findViewById(R.id.statuntransfer_listview);
        listview.setPullLoadEnable(false);
        listview.setPullRefreshEnable(true);
        adapter = new StatAdapter(this, rows,true,"未装车","袋");
        listview.setAdapter(adapter);
        EmptyFiller.fill(this,listview,"暂无数据");
        setViewListener();
    }

    private void setViewListener() {
        date_pageturn_arrow_left.setOnClickListener(this);
        date_pageturn_arrow_right.setOnClickListener(this);

        listview.setXListViewListener(this);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO: 2016/11/26
            }
        });
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
        Date firstData = null;
        Date lastData = null;
        switch (type) {
            case "day":
                calendar.add(Calendar.DAY_OF_MONTH, num);
                Date currentData = calendar.getTime();

                starttime = DateUtil.format_date.format(currentData);
                endtime = DateUtil.format_date.format(currentData);

                date_pageturn_date.setText(new SimpleDateFormat("yyyy-MM-dd EEEE").format(currentData));
                break;
            case "week":
                calendar.add(Calendar.WEEK_OF_YEAR, num);
                firstData = DateUtil.getFirstDayOfWeek(calendar).getTime();
                lastData = DateUtil.getLastDayOfWeek(calendar).getTime();

                starttime = DateUtil.format_date.format(firstData);
                endtime = DateUtil.format_date.format(lastData);

                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy.MM.dd");
                date_pageturn_date.setText(simpleDateFormat1.format(firstData) + " - " + simpleDateFormat1.format(lastData));
                break;
            case "month":
                calendar.add(Calendar.MONTH, num);
                firstData = DateUtil.getFirstDayOfMonth(calendar).getTime();
                lastData = DateUtil.getLastDayOfMonth(calendar).getTime();

                starttime = DateUtil.format_date.format(firstData);
                endtime = DateUtil.format_date.format(lastData);

                date_pageturn_date.setText(DateUtil.format_ny.format(calendar.getTime()));
                break;
        }

        onRefresh();
    }

    @Override
    public void onRefresh() {
        page = 1;
        getDataFromService();
    }

    @Override
    public void onLoadMore() {
        getDataFromService();
    }

    public void getDataFromService() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userid", LoginUtil.getLoginUser().userid)
                .add("startdate", starttime)
                .add("enddate", endtime)
                .build();
        showDialog("正在获取数据……");
        Call call = mOkHttpClient.newCall(NetUtil.getRequest("statUntransferTrashInfo",requestBody));
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e) {
                hideDialog();
                new AlertDialog.Builder(StatUntransferActivity.this).setMessage(R.string.netnotavaliable).setPositiveButton("确定", null).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideDialog();
                parseResponse(response.body().string());
            }
        });
    }

    private void parseResponse(String result) {
        try {
            if (page == 1) {
                    rows.clear();
            }
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.has("total")) {
                return;
            }
            int total = Integer.parseInt(jsonObject.optString("total"));
            listview.setPullLoadEnable(page * limit < total);
            JSONArray jsonArray = jsonObject.getJSONArray("rows");
            StatItem entity = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject tempobj = jsonArray.getJSONObject(i);
                entity = new StatItem();
                entity.code = tempobj.getString("departcode");
                entity.name = tempobj.getString("departname");
                entity.num = tempobj.getString("untransfernum");
                    rows.add(entity);
            }
            page++;
        } catch (JSONException e) {
            e.printStackTrace();
        }
            adapter.setList(rows);
    }
}
