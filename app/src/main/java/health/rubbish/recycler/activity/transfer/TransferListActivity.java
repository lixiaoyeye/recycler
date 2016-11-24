package health.rubbish.recycler.activity.transfer;

import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.adapter.TransferListAdapter;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.widget.CustomProgressDialog;
import health.rubbish.recycler.widget.EmptyFiller;
import health.rubbish.recycler.widget.HeaderLayout;


/**
 * Created by Lenovo on 2016/11/20.
 */

public class TransferListActivity extends BaseActivity {
    HeaderLayout headerLayout;
    ListView listView;
    TextView transferView;

    TransferListAdapter adapter;
    List<TrashItem> rows = new ArrayList<>();
    CustomProgressDialog progressDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_transferlist;
    }

    @Override
    protected void init() {
        initHeaderView();
        initView();
        setData();
    }

    private void initHeaderView() {
        headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
        headerLayout.isShowBackButton(true);
        headerLayout.showTitle("今日转储列表");
        headerLayout.showRightTextButton("批量上传", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/11/23
            }
        });
    }

    private void initView() {

        listView = (ListView) findViewById(R.id.transferlist_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: 2016/11/23  详情上传
            }
        });

        transferView = (TextView) findViewById(R.id.transferlist_transfer);
        transferView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/11/23
            }
        });
    }


    private void setData() {
        adapter = new TransferListAdapter(this);
        adapter.setData(rows);
        listView.setAdapter(adapter);
        EmptyFiller.fill(this,listView,"无数据");

        new TransferListAsyncTask().execute();
    }

    public class TransferListAsyncTask extends AsyncTask<Void, Void, List<TrashItem>> {
        @Override
        protected List<TrashItem> doInBackground(Void... params) {
            return TrashDao.getInstance().getAllTransferedTrashToday();
        }

        @Override
        protected void onPostExecute(List<TrashItem> items) {
            progressDialog.dismiss();
            rows = items;
            adapter.setData(rows);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = new CustomProgressDialog(TransferListActivity.this);
            }
            progressDialog.show();
        }
    }

}
