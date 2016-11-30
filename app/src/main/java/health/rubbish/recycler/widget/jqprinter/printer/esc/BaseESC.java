package health.rubbish.recycler.widget.jqprinter.printer.esc;

import health.rubbish.recycler.widget.jqprinter.port.Port;
import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define.ALIGN;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define.PRINTER_MODEL;

public class BaseESC {
	protected Port _port;
	protected byte[] _cmd = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	protected PRINTER_MODEL _model;
	protected PrinterParam _param;
	
	public BaseESC(PrinterParam param) 
	{
		_param = param;
		_model = _param.model;
		_port = _param.port;		
	}
	
	/*
	 * ���ô�ӡ�����x��y����
	 */
	public boolean setXY(int x, int y) {
		if (x < 0 || x >= _param.escPageWidth || x > 0x1FF) {
			return false;
		}

		if (y < 0 || y >= _param.escPageHeight || y > 0x7F) {
			return false;
		}
		
		int pos = ((x & 0x1FF) | ((y & 0x7F) << 9));
		_cmd[0] = 0x1B;
		_cmd[1] = 0x24;
		_cmd[2] = (byte) pos;
		_cmd[3] = (byte) (pos >> 8);
		_port.write(_cmd, 0, 4);
		return true;
	}
	/*
	 * ���ô�ӡ������뷽ʽ
	 * ֧�ִ�ӡ����:�ı�(text),����(barcode)
	 */
	public boolean setAlign(ALIGN align)
	{
		_cmd[0] = 0x1B;
		_cmd[1] = 0x61;
		_cmd[2] = (byte)align.ordinal();
		return _port.write(_cmd, 0, 3);
	}
	/*
	 * �����м��
	 */
	public boolean setLineSpace(int dots)
	{
		_cmd[0] = 0x1B;
		_cmd[1] = 0x33;
		_cmd[2] = (byte)dots;
		return _port.write(_cmd, 0, 3);
	}
	/*
	 * ��ʼ����ӡ��
	 */
	public boolean init()
	{
		_cmd[0] = 0x1B;
		_cmd[1] = 0x40;
		return _port.write(_cmd,0,2);		
	}
	
	 /// <summary>
    /// ���лس�
    /// </summary>
	public boolean enter() {
		_cmd[0] = 0x0D;
		_cmd[1] = 0x0A;
		return _port.write(_cmd, 0, 2);
	}	
}
