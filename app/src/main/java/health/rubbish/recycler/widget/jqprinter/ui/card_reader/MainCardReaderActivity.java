package health.rubbish.recycler.widget.jqprinter.ui.card_reader;

import java.io.UnsupportedEncodingException;

import health.rubbish.recycler.R;
import health.rubbish.recycler.base.App;
import health.rubbish.recycler.widget.jqprinter.printer.JQPrinter;
import health.rubbish.recycler.widget.jqprinter.printer.esc.ESC;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainCardReaderActivity extends Activity {
	
	private JQPrinter printer;
	private Button buttonPrint = null;
	private TextView reset_info = null;
	private TextView ID_info = null;
	private TextView CardNum_info = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_card_reader);
		buttonPrint = (Button)findViewById(R.id.buttonCardReaderPrint);
		reset_info =  (TextView)findViewById(R.id.textViewReset);
		ID_info =  (TextView)findViewById(R.id.textViewID);
		CardNum_info =  (TextView)findViewById(R.id.textViewCardNum);
		App app = (App)getApplication();
		if (app.printer != null)
		{
			printer = app.printer;
		}
		else
		{
			Log.e("JQ", "app.printer null");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_card_reader, menu);
		return true;
	}
	
	 boolean GuizhouPolicChinaGongShangBankCardTest()
     {
         //ѡ�񿨹���  
		 byte[] ret = printer.esc.card_reader.rsp;
		 
         char[] ADPU_SelectCardManagement = { 0x00, 0xA4, 0x04, 0x00, 0x08, 0xA0, 0x00, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00 };
         if (!printer.esc.card_reader.cardCPU_Write(0, ADPU_SelectCardManagement, ADPU_SelectCardManagement.length,"select card mgt"))
             return false;
         else
         {
             if (printer.esc.card_reader.rsp_len != 2)
                 return false;
             if ((ret[0] == 0x61)
                 || (ret[0] == (byte)0x90 && ret[1] == 0x00))
             {
             }
             else
                 return false;
         }
         Log.e("JQ","ѡ�񿨹���OK  "+printer.esc.card_reader.rsp_len);
         //ѡ�񽻾�Ӧ��
         char[] ADPU_SelectPolicApp = { 0x00, 0xA4, 0x04, 0x00, 0x08, 0xA0, 0x00, 0x00, 0x00, 0x01, 0x50, 0x4F, 0x4C };
         if (!printer.esc.card_reader.cardCPU_Write(0, ADPU_SelectPolicApp, ADPU_SelectPolicApp.length,"select polic app"))
             return false;
         else
         {
             if (printer.esc.card_reader.rsp_len != 2)
                 return false;
             if ((ret[0] == 0x61)
                 || (ret[0] == (byte)0x90 && ret[1] == 0x00))
             {
             }
             else
                 return false;
         }
         Log.e("JQ","ѡ�񽻾�Ӧ��OK  "+printer.esc.card_reader.rsp_len);
         //ѡ��6001�ļ�
         char[] ADPU_Select6001 = { 0x00, 0xA4, 0x00, 0x00, 0x02, 0x60, 0x01 };
         if (!printer.esc.card_reader.cardCPU_Write(0, ADPU_Select6001, ADPU_Select6001.length,"select 6001"))
             return false;
         else
         {
             if (printer.esc.card_reader.rsp_len != 2)
                 return false;
             if ((ret[0] == 0x61)
                 || (ret[0] == (byte)0x90 && ret[1] == 0x00))
             {
             }
             else
                 return false;
         }
         Log.e("JQ","ѡ��6001 OK  "+printer.esc.card_reader.rsp_len);
         //��ȡ�����Ϣ
         char[] ADPU_ReadFileNumber = { 0x00, 0xB0, 0x81, 0x00, 0x28 };
         if (!printer.esc.card_reader.cardCPU_Read(0, ADPU_ReadFileNumber, ADPU_ReadFileNumber.length,"read ID"))
             return false;
         else
         {
             if (printer.esc.card_reader.rsp_len != 0x28)
                 return false;
             String str = "";
			try {
				str = new String(ret,"GB2312");
				ID_info.setText(str);
			} catch (UnsupportedEncodingException e) {
				Log.e("JQ","byte to stirng exception l:"+printer.esc.card_reader.rsp_len);
			}
         }
         Log.e("JQ","��ȡ�����ϢOK  "+printer.esc.card_reader.rsp_len);
         //ѡ�񿨹���1  
         char[] ADPU_SelectCardManagement1 = { 0x00, 0xA4, 0x04, 0x00, 0x09, 0xA0, 0x00, 0x00, 0x00, 0x01, 0x20, 0x10, 0x00,0x00 };
         if (!printer.esc.card_reader.cardCPU_Write(0, ADPU_SelectCardManagement1, ADPU_SelectCardManagement1.length,"select card mgt1"))
             return false;
         else
         {
             if (printer.esc.card_reader.rsp_len != 2)
                 return false;
             if ((ret[0] == 0x61)
                 || (ret[0] == (byte)0x90 && ret[1] == 0x00))
             {
             }
             else
                 return false;
         }

         //ѡ��6F01�ļ�
        char[] ADPU_Select6F01 = { 0x00, 0xA4, 0x00, 0x00, 0x02, 0x6F, 0x01 };
        if (!printer.esc.card_reader.cardCPU_Write(0, ADPU_Select6F01, ADPU_Select6F01.length,"select 6F01"))
             return false;
         else
         {
             if (printer.esc.card_reader.rsp_len != 2)
                 return false;
             if ((ret[0] == 0x61)
                 || (ret[0] == (byte)0x90 && ret[1] == 0x00))
             {
             }
             else
                 return false;
         }

         //��ȡ����
         int read_len = 10;
         char[] ADPU_ReadCardNumber = { 0x00, 0xB2, 0x01, 0x04, 0x09 };
         ADPU_ReadCardNumber[4] = (char)read_len;
         if (!printer.esc.card_reader.cardCPU_Read(0, ADPU_ReadCardNumber, ADPU_ReadCardNumber.length,"read Card Num"))
             return false;
         else
         {
             String card_num = "";
             if (printer.esc.card_reader.rsp_len != read_len)
                 return false;
             for (int i = 0; i < printer.esc.card_reader.rsp_len; i++)
             {
                 card_num = card_num + toHex(ret[i]);
             }
             CardNum_info.setText("���ţ�"+card_num);
         }

         return true;
     }
	 
	public static final String toHex(byte b) 
	{
		  return ("" + "0123456789ABCDEF".charAt(0xf & b >> 4) + "0123456789ABCDEF".charAt(b & 0xf));
	}
	
	private boolean ese_cardreader_test()
    {
		byte []ret =  printer.esc.card_reader.rsp;
        if (!printer.esc.card_reader.powerOn())
        {
        	Log.e("JQ","power on fail");
            return false;
        }

        byte[] card_in ={0};
        if (!printer.esc.card_reader.checkBigCardInsert(card_in))
            return false;

        if (card_in[0] == 0)
        {
        	Toast.makeText(this, "��忨", Toast.LENGTH_SHORT).show(); 
            //MessageBox.Show("����û�в��뿨");
        	return false;
        }

        if (!printer.esc.card_reader.setCardType(0, ESC.CARD_TYPE_MAIN.CDT_CPU, 0))
        {
        	Toast.makeText(this, "����������ʧ��", Toast.LENGTH_SHORT).show(); 
            return false;
        }

        if (!printer.esc.card_reader.cardCPU_Reset(0))
        {
        	Toast.makeText(this, "����λʧ��", Toast.LENGTH_SHORT).show(); 
            return false;
        }
        else
        {
        	String data = "";
            
            for (int i = 0; i < printer.esc.card_reader.rsp_len; i++)
            {
            	
                data = data+toHex(ret[i])+ " ";
            }
           	reset_info.setText("��λ��Ϣ:"+data);
        }

        if (!GuizhouPolicChinaGongShangBankCardTest())
        
            return false;

        if (!printer.esc.card_reader.powerOff())
            return false;

        return true;
    }
	
	private boolean getPrinterState()
	{		
		if (!printer.getPrinterState(3000))
		{
			Toast.makeText(this, "��ȡ��ӡ��״̬ʧ��", Toast.LENGTH_SHORT).show();			
			return false;
		}
		
		if (printer.isCoverOpen)
		{
			Toast.makeText(this, "��ӡ��ֽ�ָ�δ�ر�", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if (printer.isNoPaper)
		{
			Toast.makeText(this, "��ӡ��ȱֽ", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	public void ButtonCardReaderPrint_click(View view)
	{
		if (printer == null)
		{
			Log.e("JQ", "printer null");
			return;
		}
		buttonPrint.setText("��ӡ");
		buttonPrint.setVisibility(Button.INVISIBLE);
				
		if (!getPrinterState())
		{
			buttonPrint.setVisibility(Button.VISIBLE);
			return;
		}
		if(!ese_cardreader_test())
			Toast.makeText(this, "����ʧ��", Toast.LENGTH_SHORT).show(); 
		else
			Toast.makeText(this, "���Գɹ�", Toast.LENGTH_SHORT).show();
		
		buttonPrint.setVisibility(Button.VISIBLE);
	}

}
