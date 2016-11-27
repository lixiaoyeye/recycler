package health.rubbish.recycler.activity.collection;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.adapter.ReferenceAdapter;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.datebase.CatogeryDao;
import health.rubbish.recycler.datebase.DepartmentDao;
import health.rubbish.recycler.entity.CatogeryItem;
import health.rubbish.recycler.entity.DepartmentItem;
import health.rubbish.recycler.widget.HeaderLayout;

/**
 * Created by xiayanlei on 2016/11/27.
 * 外键选择页面
 */
public class ReferenceActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ReferenceAdapter adapter;

    private static final String REFER_TYPE = "referType";//选择类型，根据该类型判断是加载垃圾类型还是科室数据

    private int type = -1;//读取本地数据库的判断，0-科室数据，1-垃圾类型数据

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reference;
    }

    @Override
    protected void init() {
        HeaderLayout headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(getIntent().getStringExtra(Constant.Reference.REFER_TITLE));
        ListView referListView = (ListView) findViewById(R.id.reference_listview);
        type = getIntent().getIntExtra(Constant.Reference.REFER_TYPE, -1);
        adapter = new ReferenceAdapter(type);
        referListView.setAdapter(adapter);
        referListView.setOnItemClickListener(this);
        initData();
    }

    /**
     * 加载本地数据
     */
    private void initData() {
        if (Constant.Reference.DEPART == type || Constant.Reference.NURSE == type || Constant.Reference.AREA == type) {
            loadDepartData();
        } else if (Constant.Reference.CATEGORY == type) {
            loadCategoryData();
        }
    }

    /**
     * 加载部门科室数据
     */
    private void loadDepartData() {
        final DepartmentDao departmentDao = DepartmentDao.getInstance();
        new AsyncTask<Void, Void, List<DepartmentItem>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog("正在加载数据...");
            }

            @Override
            protected List<DepartmentItem> doInBackground(Void... params) {
                List<DepartmentItem> departmentItems = departmentDao.getAllDepartmentToday();
                return departmentItems;
            }

            @Override
            protected void onPostExecute(List<DepartmentItem> items) {
                super.onPostExecute(items);
                hideDialog();
                adapter.setDatas(items);
            }
        }.execute();
    }

    private void loadCategoryData() {
        final CatogeryDao catogeryDao = CatogeryDao.getInstance();
        new AsyncTask<Void, Void, List<CatogeryItem>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog("正在加载数据...");
            }

            @Override
            protected List<CatogeryItem> doInBackground(Void... params) {
                List<CatogeryItem> items = catogeryDao.getAllCatogery();
                return items;
            }

            @Override
            protected void onPostExecute(List<CatogeryItem> catogeryItems) {
                super.onPostExecute(catogeryItems);
                hideDialog();
                adapter.setDatas(catogeryItems);
            }
        }.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = adapter.getItem(position);
        if (object == null)
            return;
        Intent intent = new Intent();
        if (Constant.Reference.DEPART == type) {
            DepartmentItem item = (DepartmentItem) object;
            intent.putExtra(Constant.Reference.REFER_KEY, item.departcode);
            intent.putExtra(Constant.Reference.REFER_VALUE, item.departname);
        } else if (type == Constant.Reference.AREA) {
            DepartmentItem item = (DepartmentItem) object;
            intent.putExtra(Constant.Reference.REFER_KEY, item.departareacode);
            intent.putExtra(Constant.Reference.REFER_VALUE, item.departarea);
        } else if (type == Constant.Reference.NURSE) {
            DepartmentItem item = (DepartmentItem) object;
            intent.putExtra(Constant.Reference.REFER_KEY, item.nurseid);
            intent.putExtra(Constant.Reference.REFER_VALUE, item.nurse);
        } else if (Constant.Reference.CATEGORY == type) {
            CatogeryItem item = (CatogeryItem) object;
            intent.putExtra(Constant.Reference.REFER_KEY, item.categorycode);
            intent.putExtra(Constant.Reference.REFER_VALUE, item.categoryname);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}
