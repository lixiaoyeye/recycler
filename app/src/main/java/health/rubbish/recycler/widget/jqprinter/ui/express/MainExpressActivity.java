package health.rubbish.recycler.widget.jqprinter.ui.express;

import health.rubbish.recycler.R;
import health.rubbish.recycler.base.App;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.widget.jqprinter.printer.JQPrinter;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define.PRINTER_MODEL;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.Barcode.BAR_UNIT;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.Barcode.QRCODE_ECC;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.JPL;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.JPL.ROTATE;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.Page.PAGE_ROTATE;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.Text.TEXT_ENLARGE;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainExpressActivity extends Activity {
	int startIndex;//开始打印的序号
	int amount;//打印的总数
	boolean rePrint = false;//是否需要重新打印
	private JQPrinter Printer = new JQPrinter(PRINTER_MODEL.JLP351);
	private Button buttonPrint = null;
	private TrashItem trashItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_express);

		buttonPrint = (Button)findViewById(R.id.buttonExpressPrint);

		App app = (App)getApplication();
		if (app.printer != null)
		{
			Printer = app.printer;
		}
		else
		{
			Log.e("JQ", "app.printer null");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_express, menu);
		return true;
	}

	private boolean getPrinterState()
	{
		if (!Printer.getPrinterState(3000))
		{
			Toast.makeText(this, "获取打印机状态失败", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (Printer.isCoverOpen)
		{
			Toast.makeText(this, "打印机纸仓盖未关闭", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (Printer.isNoPaper)
		{
			Toast.makeText(this, "打印机缺纸", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}



	private boolean printExpress()
	{
		Printer.jpl.intoGPL(1000);
		if (!Printer.getJPLsupport())
		{
			Toast.makeText(this, "不支持JPL，请设置正确的打印机型号！", Toast.LENGTH_SHORT).show();
			return false;
		}
		JPL jpl = Printer.jpl;


		int y = 10, x,x1, x2, x3,x4,x5;
		x = 20;
		x1 = 100;
		x2 = 20;
		x3 = 120;
		x4 = 280;
		x5 = 380;

		int height = 30;


		if (!jpl.page.start(0, 0, 576, 560, PAGE_ROTATE.x0))
			return false;

		y = 5;

/*
		if (!jpl.barcode.QRCode(x1, y, 0, QRCODE_ECC.LEVEL_M, BAR_UNIT.x5, ROTATE.x0, "http://www.jqsh.com/front/bin/ptdetail.phtml?Part=pro_08&Rcg=1"))
			return false;
		y = y+200;

		*/
/*if (!jpl.text.drawOut(x, y+180, "no:", 18, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0))
            return false;
        jpl.text.drawOut(116, y+180, "nostr11111111111111", 16, false, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);*//*


		jpl.text.drawOut(x2, y, "垃圾袋号:", 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
		jpl.text.drawOut(x3, y, trashcode, 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
		y += height;

		jpl.text.drawOut(x2, y, "垃圾桶号:", 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
		jpl.text.drawOut(x3, y, trashcancode, 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
		y += height;

		jpl.text.drawOut(x2, y, "院　　区:", 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
		jpl.text.drawOut(x3, y, departarea, 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
		y += height;

		jpl.text.drawOut(x2, y, "科　　室:", 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
		jpl.text.drawOut(x3, y, depart, 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);

		jpl.text.drawOut(x4, y, "护　　士:", 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
		jpl.text.drawOut(x5, y, nurse, 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
		y += height;

		jpl.text.drawOut(x2, y, "类　　型:", 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
		jpl.text.drawOut(x3, y, type, 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);

		jpl.text.drawOut(x4, y, "重　　量:", 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
		jpl.text.drawOut(x5, y, weight, 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
		y += height+20;

		jpl.text.drawOut(x2, y, "日　　期:", 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
		jpl.text.drawOut(x3, y, "timestr", 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
		jpl.text.drawOut(x2, y, "签　　字:", 17, true, false, false, false, TEXT_ENLARGE.x1, TEXT_ENLARGE.x1, ROTATE.x0);
*/

        /*if (!jpl.graphic.line(x3, y, 575, y, 1))
            return false;*/

		jpl.page.end();
		jpl.page.print();
		return Printer.jpl.exitGPL(1000);

	}

	public void ButtonExpressPrint_click(View view)
	{
		buttonPrint.setText("打印");
		buttonPrint.setVisibility(Button.INVISIBLE);

		if (!Printer.isOpen)
		{
			this.finish();
			return;
		}
//		if (!getPrinterState())
//		{
//			buttonPrint.setVisibility(Button.VISIBLE);
//			return;
//		}
		if (!rePrint)
		{
			amount = 1;
			startIndex = 1;
		}
		else
		{
			//软件无法判断当前打印的内容是否打印完好，所以需要重新打印当前张。你可以增加一个按钮来决定是打当前张还是打下一张。
			rePrint = false;
		}
		if (printExpress())
//			if (print("437801"));
			buttonPrint.setVisibility(Button.VISIBLE);
	}
}
