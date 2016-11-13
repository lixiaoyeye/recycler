package health.rubbish.recycler.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by xiayanlei on 2016/11/13.
 */
public class CustomProgressDialog extends ProgressDialog {

    private Context context;

    public CustomProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    public CustomProgressDialog setMessage(int id) {
        setMessage(context.getResources().getString(id));
        return this;
    }
}
