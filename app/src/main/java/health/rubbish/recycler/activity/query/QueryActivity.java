package health.rubbish.recycler.activity.query;

import health.rubbish.recycler.R;
import health.rubbish.recycler.base.BaseActivity;
import health.rubbish.recycler.widget.HeaderLayout;

/**
 * Created by xiayanlei on 2016/11/27.
 * 查询页面
 */
public class QueryActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_query;
    }

    @Override
    protected void init() {
        HeaderLayout headerLayout = (HeaderLayout) findViewById(R.id.header_layout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(R.string.data_query);
    }
}
