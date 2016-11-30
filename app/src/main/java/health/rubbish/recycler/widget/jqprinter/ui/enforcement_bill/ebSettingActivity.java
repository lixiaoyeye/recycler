package health.rubbish.recycler.widget.jqprinter.ui.enforcement_bill;

import health.rubbish.recycler.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ebSettingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enforcement_bill_setting);
		setTitle("�����Ϣ");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.enforcement_bill_setting, menu);
		return true;
	}

}
