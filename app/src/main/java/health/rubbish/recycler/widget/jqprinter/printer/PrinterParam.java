package health.rubbish.recycler.widget.jqprinter.printer;

import health.rubbish.recycler.widget.jqprinter.port.Port;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define.PRINTER_MODEL;

public class PrinterParam {
	public PRINTER_MODEL model;
	public Port port;
	public boolean escSupport;
	public boolean jplSupport;
	public boolean cpclSupport;
	public int pageWidth;
	public int pageHeight;
	public int escPageWidth;
	public int escPageHeight;
	public int cmdBufferSize;
	
	/*
	 * ���ô�ӡ���ͺ�
	 * ���ݴ�ӡ����������ز���
	 */
	public void setModel(PRINTER_MODEL model)
	{
		this.model = model;
		switch(this.model)
		{
			case VMP02:
				escPageWidth = 384;
				escPageHeight = 100;
				cmdBufferSize = 384*3-8;
				break;
			case VMP02_P:
				escPageWidth = 384;
				escPageHeight = 200;
				cmdBufferSize = 4096;
				break;
			case ULT113x:
				escPageWidth = 576;
				escPageHeight = 120;
				cmdBufferSize = 1024-8;
				break;
			case JLP351:
			case JLP351_IC:
				escPageWidth = 576;
				escPageHeight = 250;
				cmdBufferSize = 4096;
				jplSupport = true;
				break;
			case EXP341:
				escPageWidth = 576;
				escPageHeight = 250;
				cmdBufferSize = 4096;
				cpclSupport = true;
				break;
			default:
				break;
		}	
	}

	/*
	 * ���캯��
	 */
	public PrinterParam(PRINTER_MODEL model,Port port)
	{
		this.port = port;
		
		escSupport = true;
		setModel(model);
	}

}
