package health.rubbish.recycler.widget.jqprinter.ui.express_form;

import health.rubbish.recycler.R;
import health.rubbish.recycler.base.App;
import health.rubbish.recycler.widget.jqprinter.printer.JQPrinter;
import health.rubbish.recycler.widget.jqprinter.printer.cpcl.CPCL;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ExpressFormMainActivity extends Activity {

	private Button buttonPrint;
	private JQPrinter printer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_express_form_main);

		buttonPrint = (Button) findViewById(R.id.buttonExpressFormPrint);

		App app = (App) getApplication();
		printer = app.printer;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.express_form_main, menu);
		return true;
	}

	private boolean getPrinterState() {
		if (!printer.getPrinterState(3000)) {
			Toast.makeText(this, "获取打印机状态失败", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (printer.isCoverOpen) {
			Toast.makeText(this, "打印机纸仓盖未关闭", Toast.LENGTH_SHORT).show();
			return false;
		} else if (printer.isNoPaper) {
			Toast.makeText(this, "打印机缺纸", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private boolean print() {
		if (!printer.getCPCLsupport()) {
			Toast.makeText(this, "不支持CPCL，请设置正确的打印机型号！", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (!EXP341_PrintSenderHorizontal(3000))
			return false;
		return true;
	}

	public boolean EXP341_PrintSenderHorizontal(int timeout) {
		// 记录打印机SDK的每一次调用结果，如果为false，立刻返回，不再继续调用SDK指令
		printer.esc.feedDots(32);
		CPCL cpcl = printer.cpcl;
		int orgX = 0, orgY = 0;
		int x = 0, y = 0;
		// EXP341.jp_page_create(78, 86, false);
		if (!cpcl.page.start(1256, 576, 1))
			return false;
//		cpcl.page.bar_sense_right();
//		cpcl.page.bar_sense_left();
		cpcl.page.gap_sense();
//		cpcl.page.setPageWidth(576);

		x += orgX;
		y += orgY;
		
		
		//韵达快递单
		//横向线条
		cpcl.graphic.line(12, 192, 576, 192, 1);
		cpcl.graphic.line(12, 448, 440, 448, 1);
		cpcl.graphic.line(12, 512, 440, 512, 1);
		cpcl.graphic.line(12, 775, 576, 775, 1);
		cpcl.graphic.line(12, 925, 576, 925, 1);
		cpcl.graphic.line(12, 1162, 576, 1162, 1);
		
		//纵向线条
		cpcl.graphic.line(440, 192, 440, 670, 1);
		cpcl.graphic.line(440, 775, 440, 940, 1);
		
		//打印首联框架里非加粗文字
		cpcl.text._setFontSetBold(false);
		cpcl.text.setFont(10, 0, false, false);
		cpcl.text._setFontSetMagCPCL(0, 0);
		cpcl.text.drawOut(24, 8, "始发网点：网点程序测试");
		cpcl.text.drawOut(80, 40, "收件人：张三");
		cpcl.text.drawOut(80, 60, "电话：13511111111");
		cpcl.text.drawOut(80, 80, "收件地址：上海市浦东区");
		cpcl.text.drawOut(80, 100, "上海市浦东区崧复路123号 ");
		cpcl.text.drawOut(448, 8, "体积：");
		cpcl.text.drawOut(448, 28, " ");
		cpcl.text.drawOut(448, 48, " ");
		cpcl.text.drawOut(380, 168, " ");
		
		//打印首联框架里加粗文字
		cpcl.text._setFontSetBold(true);
		cpcl.text._setFontSetMagCPCL(1, 1);
//		cpcl.text.setFont(10, 0, false, false);
		cpcl.text.drawOut(24, 40, "送达");
		cpcl.text.drawOut(24, 65, "地址");
		cpcl.text.drawOut(16, 160, " ");
		cpcl.text.drawOut(328, 8, " ");
		cpcl.text.drawOut(328, 33, " ");
		
		//打印第二框架里的文字和数字
		cpcl.text._setFontSetMagCPCL(2, 2);
		cpcl.text.drawOut(32, 368, "沪浦东新区 ");
//		cpcl.text.setFont(28, 0, false, false);
		cpcl.text.drawOut(200, 200, " 185");
		cpcl.text.drawOut(337,280," 333");
		cpcl.text.drawOut(24,470,"运单编号：40000000008");
		
		cpcl.text.setFont(10, 0, false, false);
		cpcl.text._setFontSetMagCPCL(0, 0);
		cpcl.text.drawOut(32, 600, "收件人/代签人：");
		cpcl.text.drawOut(32,634, "签收时间：   年   月   日");
		cpcl.text.drawOut(420, 728, " ");
		
		//打印尾联收发方信息等
		cpcl.text._setFontSetBold(false);
		cpcl.text.drawOut(24, 782, "发件人：申二");
		cpcl.text.drawOut(24, 802, "电话：18932654879");
		cpcl.text.drawOut(24, 822, "发件地址：上海市，青浦区");
		cpcl.text.drawOut(24, 842, " 盈港东路3365号");
		cpcl.text.drawOut(32, 864, "收件人：张三");
		cpcl.text.drawOut(32, 882, "电话：13511111111");
		cpcl.text.drawOut(32, 902, "收件地址上海市青浦区");
//		cpcl.text.drawOut(32, 922, " 上海市青浦区崧复路1250号");
		cpcl.text.drawOut(440, 782, " ");
		cpcl.text.drawOut(440, 802, " ");
		cpcl.text.drawOut(440, 822, " ");
		cpcl.text.drawOut(440, 842, " ");
		cpcl.text.drawOut(24, 964, "发件人：申二");
		cpcl.text.drawOut(24, 984, "电话：18932654879");
		cpcl.text.drawOut(24, 1004, "发件地址：上海市，青浦区");
		cpcl.text.drawOut(24, 1024, "盈港东路3365号 ");
		cpcl.text.drawOut(32, 1044, "收件人：张三");
		cpcl.text.drawOut(32, 1064, "电话：13511111111");
		cpcl.text.drawOut(32, 1084, "收件地址：上海市青浦区崧复路1250号");
		cpcl.text.drawOut(32, 1104, " ");
		
		//打印尾联其他信息
		cpcl.text.drawOut(448, 1055, " ");
		cpcl.text.drawOut(448, 1080, " ");
		cpcl.text._setFontSetMagCPCL(2, 2);
		cpcl.text.drawOut(32, 1130, "运单编号:40000000008");
		
		//落款
		cpcl.text.setFont(10, 0, false, false);
		cpcl.text._setFontSetMagCPCL(0, 0);
		cpcl.text.setFont(10, 0, false, false);
		cpcl.text.drawOut(24, 1168, "官方网址：http://www.yundaex.com 客服热线： 400-821-6789  发货人联");
		cpcl.barcode.code128(334, 118, 48, 1, " 40000000008");
		cpcl.barcode.QRCode(32, 216, 5, 1, " 40000000008");
		cpcl.barcode.Barcode_Text(7, 0, 5);
		cpcl.barcode.code128(23, 680, 50, 1, "40000000008 ");
		cpcl.barcode.code128v(470, 636, 80, 2, " 40000000008");
		
        cpcl.text.drawOut(300, 260, "件数：" +"/" );
        cpcl.text.drawOut(50, 145, "billcode"+"-");//条码
        cpcl.page.print();
		

        //远成快运sample
//		 CPCL cpcl = printer.cpcl;
//	        if (!cpcl.page.start(480, 580, 1))
//	            return false;
//
//	        cpcl.page.gap_sense();
//	        //打印首联框架里非加粗文字
//	        cpcl.text.setFont(20, 0, true, false);
//	        cpcl.text._setFontSetBold(true);
//	        cpcl.text._setFontSetMagCPCL(2, 2);
//	        cpcl.text.drawOut(15, 0, "远成快运");//公司名称
//	        cpcl.text._setFontSetBold(false);
//	        cpcl.text.setFont(20, 0, false, false);
//	        cpcl.text._setFontSetMagCPCL(0, 0);
//
//	        cpcl.barcode.code128(15, 55, 80, 2, "billcode");//条形码
//
//	        cpcl.text.drawOut(145, 30, "日期："+"datetime");//时间
//
//	        cpcl.text.drawOut(15, 175,  "始发城市："+"oriCity");
//	        cpcl.text.drawOut(300, 175, "目的城市："+"destCity");
//	        cpcl.text.drawOut(15,210, "始发站点："+"oriDepatment");
//	        cpcl.text.drawOut(300,210, "目的站点：" + "destDepartment");
//	        //cpcl.text.drawOut(15,300, "配送地址：");
//	        //计算配送地址长度，分多行打印
//	        String strprintaddr = "配送地址：" + "sendAddr";
//	        int xprintpos = 300;
//	       
//	      
//	            cpcl.text.drawOut(15,xprintpos, strprintaddr);
//
//	        
//	            cpcl.text.drawOut(300, 260, "件数：1/1");
//	            cpcl.text.drawOut(50, 145, "billcode");//条码
//	            return cpcl.page.print();
	       
	       

	         
		
       //中铁物流sample
//		x = 40;
//		
//
//		y += 40;
//		
//		cpcl.text.setFont(48, 0, true, false);		
//		cpcl.text.drawOut(x, y, "中铁物流集团");
//		cpcl.text.setFont(24, 0, true, false);
//
//		x += 8;
//		y += 52;
//		cpcl.text.drawOut(x, y, "全国客服:400-000-5566");
//		y += 26;
//		cpcl.text.drawOut(x, y, "官方网站:www.ztky.com");
//
//		cpcl.text.setFont(24, 0, true, false);
//		cpcl.text.drawOut(52 * 8, 6 * 8, "发货人联");
//
//		y += 30;
//		cpcl.graphic.line(0, y, 575, y, 3); // 画横线
//		cpcl.graphic.line(40, 0, 40, y, 3); // 画竖线
//
//		String bar = "2222232364";
//		y += 8;
//		cpcl.barcode.code128(0, y, 72, 2, bar);
//		cpcl.text.setFont(40, 0, true, false);
//		y += 80;
//		cpcl.text.drawOut(0, y, bar);
//
//		y -= 80;
//		cpcl.text.setFont(16, 0, true, false);
//		cpcl.text.drawOut(37 * 8, y, "扫描右侧二维码");
//
//		// 打印二维码
//		cpcl.barcode.Barcode_Text(7, 0, 5);
//		cpcl.barcode.QRCode(54 * 8, y, 4, 4, "www.ztky.com");
//		
//
//		y += 20;
//		cpcl.text.drawOut(37 * 8, y, " 访问中铁官网");
//		y += 20;
//		cpcl.text.drawOut(37 * 8, y, " 了解更多信息");
//		y += 76;
//
//		cpcl.graphic.line(0, y, 50 * 8, y, 3); // 画横线

		/*
		 * 
		 * 
		 * 
		 * 
		 * 
		 * //发货人信息 EXP341.jp_page_set_font(font, 3, 0, true, false, false); flag
		 * = EXP341.jp_draw_text(x, 30 + y, string.Format("发货人:{0}", "王二")); if
		 * (!flag) return; flag = EXP341.jp_draw_text(x, 33 + y,
		 * string.Format("发货公司:{0}", "中铁物流集团北京分公司")); if (!flag) return;
		 * 
		 * //如果发货地址长度超过了20个字符，那么就分成两行打印 string sendAddress =
		 * "北京市朝阳区黑庄户乡大鲁店张台路98号"; if (sendAddress.Length >= 20) { flag =
		 * EXP341.jp_draw_text(x, 36 + y, string.Format("发货地址:{0}",
		 * sendAddress.Substring(0, 18))); if (!flag) return; flag =
		 * EXP341.jp_draw_text(x, 39 + y, string.Format("         {0}",
		 * sendAddress.Substring(18))); if (!flag) return; } else { flag =
		 * EXP341.jp_draw_text(x, 39 + y, string.Format("发货地址:{0}",
		 * sendAddress)); if (!flag) return; }
		 * 
		 * flag = EXP341.jp_draw_text(x, 42 + y, string.Format("联系电话:{0}",
		 * "13912345678")); if (!flag) return; flag = EXP341.jp_draw_line(0, 46
		 * + y, 78, 46 + y, 3); //画横线 if (!flag) return;
		 * 
		 * //收货人信息 EXP341.jp_page_set_font(font, 3, 0, true, false, false); flag
		 * = EXP341.jp_draw_text(x, 47 + y, string.Format("收货人:{0}", "李小明")); if
		 * (!flag) return; flag = EXP341.jp_draw_text(x, 50 + y,
		 * string.Format("收货公司:{0}", "中铁互联有限公司")); if (!flag) return;
		 * 
		 * string receiveAddress = "天津市和平区南京路219号";
		 * 
		 * //如果收货地址长度超过了20个字符，那么就分成两行打印 if (receiveAddress.Length >= 20) { flag
		 * = EXP341.jp_draw_text(x, 53 + y, string.Format("收货地址:{0}",
		 * receiveAddress.Substring(0, 18))); if (!flag) return; flag =
		 * EXP341.jp_draw_text(x, 56 + y, string.Format("         {0}",
		 * receiveAddress.Substring(18))); if (!flag) return; } else { flag =
		 * EXP341.jp_draw_text(x, 56 + y, string.Format("收货地址:{0}",
		 * receiveAddress)); if (!flag) return; } flag = EXP341.jp_draw_text(x,
		 * 59 + y, string.Format("联系电话:{0}", "18600001234")); if (!flag) return;
		 * flag = EXP341.jp_draw_line(x, 63 + y, 78, 63 + y, 3); //画横线 if
		 * (!flag) return;
		 * 
		 * //单号信息 EXP341.jp_page_set_font(font, 3, 0, true, false, false); flag
		 * = EXP341.jp_draw_text(x, 64 + y, string.Format("品名:{0}", "药品")); if
		 * (!flag) return; flag = EXP341.jp_draw_text(x, 67 + y,
		 * string.Format("件数:{0}件   计费重量:{1}kg", "18", "500")); if (!flag)
		 * return; flag = EXP341.jp_draw_text(x, 70 + y,
		 * string.Format("重量:{0}kg   声明价值:{1}元", "500", "36000")); if (!flag)
		 * return; flag = EXP341.jp_draw_text(x, 73 + y,
		 * string.Format("体积:{0}M³  运费:{1}元", "2.2", "850")); if (!flag) return;
		 * flag = EXP341.jp_draw_text(x, 76 + y,
		 * string.Format("             总费用:{0}元 ", "936")); if (!flag) return;
		 * 
		 * flag = EXP341.jp_draw_line(50, 63 + y, 50, 85, 3); //画竖线 if (!flag)
		 * return; EXP341.jp_page_set_font(font, 3, 0, true, false, false); flag
		 * = EXP341.jp_draw_text(51 + x, 64 + y, string.Format("配送方式:{0}",
		 * "送货")); if (!flag) return; flag = EXP341.jp_draw_text(51 + x, 67 + y,
		 * string.Format("付款方式:{0}", "现货")); if (!flag) return;
		 * 
		 * //如果增值服务长度超过3，就分两行打印 string huizhiyaoqiu = "签回单"; if
		 * (huizhiyaoqiu.Trim().Length >= 3) { flag = EXP341.jp_draw_text(51 +
		 * x, 70 + y, string.Format("增值服务:{0}", huizhiyaoqiu.Trim().Substring(0,
		 * 2))); if (!flag) return; flag = EXP341.jp_draw_text(57 + x, 73 + y,
		 * string.Format("{0}", huizhiyaoqiu.Substring(2))); if (!flag) return;
		 * } else { flag = EXP341.jp_draw_text(51 + x, 73 + y,
		 * string.Format("增值服务:{0}", huizhiyaoqiu.Trim())); if (!flag) return; }
		 * flag = EXP341.jp_draw_text(51 + x, 76 + y, "代收贷款:"); if (!flag)
		 * return;
//		 */
//	                y += 10;
//		 
////		cpcl.text.drawOut(51 + x, y, "3600元");	
////		 cpcl.page.feed();
		return cpcl.page.print();
//		return cpcl.feedMarkBegin();
	}

	public void buttonExpressFormPrint_click(View view) {
		buttonPrint.setText("打印");
		buttonPrint.setVisibility(Button.INVISIBLE);

		if (!printer.isOpen) {
			this.finish();
			return;
		}

//		if (!getPrinterState()) {
//			buttonPrint.setVisibility(Button.VISIBLE);
//			return;
//		}

		if (print())
			;
		buttonPrint.setVisibility(Button.VISIBLE);

	}
}
