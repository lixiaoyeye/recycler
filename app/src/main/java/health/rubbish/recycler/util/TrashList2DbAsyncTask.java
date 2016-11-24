package health.rubbish.recycler.util;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import health.rubbish.recycler.activity.transfer.TransferAddActivity;
import health.rubbish.recycler.constant.Constant;
import health.rubbish.recycler.datebase.TrashDao;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.widget.CustomProgressDialog;

/**
 * Created by Lenovo on 2016/11/24.
 */

public class TrashList2DbAsyncTask extends AsyncTask<Void, Void, Void> {
    Context context;
    List<TrashItem> rows;
    CustomProgressDialog progressDialog;
    public TrashList2DbAsyncTask(Context context, List<TrashItem> rows)
    {
        this.context = context;
        this.rows = rows;
    }

    @Override
    protected Void doInBackground(Void... params) {
        TrashDao.getInstance().setAllTrash(rows);
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        progressDialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressDialog == null) {
            progressDialog = new CustomProgressDialog(context);
        }
        progressDialog.show();
    }
}

