package health.rubbish.recycler.widget.jqprinter.printer.jpl;

import health.rubbish.recycler.widget.jqprinter.port.Port;
import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;

public class BaseJPL 
{
	protected PrinterParam _param;
	protected Port _port;
	protected byte _cmd[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	/*
	 * ���캯��
	 */
	public BaseJPL(PrinterParam param) {
		_param = param;	
		_port = _param.port;
	}
}
