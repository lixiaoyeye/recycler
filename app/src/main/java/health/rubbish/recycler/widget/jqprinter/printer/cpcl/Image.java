package health.rubbish.recycler.widget.jqprinter.printer.cpcl;

import android.R.string;

import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;

public class Image extends BaseCPCL{
	
	public Image(PrinterParam param) {
		super(param);
		// TODO Auto-generated constructor stub
	}

	
	/*ͼ���ӡ����
	width��λͼ���ֽڿ��
	height�� λͼ�ĵ�߶�
	x��λͼ��ʼ��x����
	y��λͼ��ʼ��y����
	data��λͼ����*/
public boolean Image_drawout(int width, int height, int x, int y, String data)
{
	String CPCLCmd = "EG " + width + " " + height + " " + x + " " + y +" " + data;
    return portSendCmd(CPCLCmd);
}


}
