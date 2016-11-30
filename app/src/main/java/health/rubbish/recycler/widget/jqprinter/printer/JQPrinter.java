package health.rubbish.recycler.widget.jqprinter.printer;

import health.rubbish.recycler.widget.jqprinter.port.Port;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define.PRINTER_MODEL;
import health.rubbish.recycler.widget.jqprinter.printer.cpcl.CPCL;
import health.rubbish.recycler.widget.jqprinter.printer.esc.ESC;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.JPL;

public class JQPrinter {
	private Port _port;
	private byte[] _cmd = { 0, 0 };
	
	public ESC esc;
	public JPL jpl;
	public CPCL cpcl;
	public boolean isOpen = false;
	private byte[] state={0,0};
	private PrinterParam param;	
	
	public final static int STATE_NOPAPER_UNMASK = 0x01;
	public final static int STATE_OVERHEAT_UNMASK = 0x02;
	public final static int STATE_BATTERYLOW_UNMASK = 0x04;
	public final static int STATE_PRINTING_UNMASK = 0x08;
	public final static int STATE_COVEROPEN_UNMASK = 0x10;
	
	/*
	 * ��ӡ��ȱֽ
	 * 0x01
	 */
	public boolean isNoPaper;

	/*
	 * ��ӡ����ӡͷ����
	 * 0x02
	 */
	public boolean isOverHeat;
	
	/*
	 * ��ӡ����ص�ѹ����
	 * 0x04
	 */
	public boolean isBatteryLow;
	
	/*
	 * ��ӡ�����ڴ�ӡ
	 * 0x08
	 */
	public boolean isPrinting;
	
	/*
	 * ��ӡ��ֽ�ָ�δ�ر�
	 * 0x10
	 */
	public boolean isCoverOpen;
	
	/*
	 * ��λ��ӡ�����״̬��׼
	 */
	public void stateReset()
	{
		isNoPaper = false;
		isOverHeat = false;
		isBatteryLow = false;
		isPrinting = false; 
		isCoverOpen = false;
	}

	/*
	 * ���캯��
	 */
	public JQPrinter(PRINTER_MODEL model)
	{
		_port = new Port();
		param = new PrinterParam(model, _port);
		esc = new ESC(param);
		jpl = new JPL(param);
		cpcl = new CPCL(param);
	}

	//�򿪶˿�
	//ע�⣺��ò�Ҫ���˺�������Activity��onCreate�����У���Ϊbluetooth connectʱ���ж�����ʱ����ʱ�����ҳ����ʾ������������Ϊû�е����ť
	public boolean open(String btDeviceString)
	{
		if (isOpen)	return true;
		
		if (btDeviceString == null) return false;
		
		if (!_port.open(btDeviceString,3000)) return false;
		
	
		isOpen =  true;
		return true;
	}
	
	public boolean close()
	{
		if (!isOpen)
			return false;

		isOpen = false;
		return _port.close();
	}
	
	/*
	 * �ȴ�����Ϊ����״̬
	 */
	public boolean waitBluetoothOn(int timeout)
	{
		return _port.getBluetoothStateON(timeout);
	}

	/*
	 * ���Ѵ�ӡ��
	 * ע��:�����ֳ��������ӵ�һ�β��ȶ�������ɿ�ͷ�ַ����룬����ͨ��������������������
	 */
	public boolean wakeUp() 
	{
		if(!_port.writeNULL())
			return false;
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return esc.text.init();
	}
	/*
	 * ��ֽ���Һڱ�
	 */
	public boolean feedRightMark()
	{						
		switch(param.model)
		{
			case JLP351: //JLP351ϵ��û���Һڱ괫������ֻ��ʹ���м�ı�ǩ�촫�����������Һڱ괫����
			case JLP351_IC:
				_cmd[0] = 0x1D;
				_cmd[1] = 0x0C;
				return _port.write(_cmd,0,2);//ָ��0x1D 0x01C ��Ч�� 0x0E
			default:
				_cmd[0] = 0x0C;
				return _port.write(_cmd,0,1);
		}		
	}
	/*
	 * ��ֽ����ڱ�
	 */
	public boolean feedLeftMark()
	{		
		switch(param.model)
		{
			case JLP351: //JLP351ϵ��û���Һڱ괫������ֻ��ʹ���м�ı�ǩ�촫�����������Һڱ괫����
			case JLP351_IC:
				_cmd[0] = 0x0C;
				return _port.write(_cmd,0,1);//0x0C 
			default:
				_cmd[0] = 0x0E;
				return _port.write(_cmd,0,1);
		}
	}
	
	/*
	 * ��ȡ��ӡ��״̬
	 */
	public boolean getPrinterState(int timeout_read) {
		
		stateReset();
		if (!esc.getState(state,timeout_read))
			return false;
		if ((state[0] & STATE_NOPAPER_UNMASK) != 0 )
		{
			isNoPaper = true;
		}
		if ((state[0] & STATE_BATTERYLOW_UNMASK) != 0)
		{
			isBatteryLow = true;
		}	
		if ((state[0] & STATE_COVEROPEN_UNMASK) != 0)
		{
			isCoverOpen = true;
		}
		if ((state[0] & STATE_OVERHEAT_UNMASK) != 0)
		{
			isOverHeat = true;
		}
		if ((state[0] & STATE_PRINTING_UNMASK) != 0)
		{
			isPrinting = true;
		}
		return true;  
	}
	
	/*
	 * ��ȡ�Ƿ�֧��JPL����
	 */
	public boolean getJPLsupport()
	{
		return param.jplSupport;	
	}
	
	/*
	 * ��ȡ�Ƿ�֧��ESC����
	 */
	public boolean getESCsupport()
	{
		return param.escSupport;	
	}
	
	/*
	 * ��ȡ�Ƿ�֧��CPCL����
	 */
	public boolean getCPCLsupport()
	{
		return param.cpclSupport;	
	}
	
	/*
	 * ��ȡ��ӡ���ͺ�
	 */
	public PRINTER_MODEL getModel()
	{
		return param.model;	
	}
}
