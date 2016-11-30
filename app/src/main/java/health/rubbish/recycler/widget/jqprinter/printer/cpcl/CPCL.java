package health.rubbish.recycler.widget.jqprinter.printer.cpcl;

import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;

public class CPCL {
	private byte[] _cmd = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private PrinterParam _param;
	public Page page;
	public Text text;
	public Graphic graphic;
	public Barcode barcode;
	public Image image;
	public CPCL(PrinterParam param) 
	{		
		_param = param;
		
		page = new Page(_param);
		text = new Text(_param);
		graphic = new Graphic(_param);
		barcode =  new Barcode(_param);
		image = new Image(_param);
	}
	
	public boolean feedMarkBegin()
	{
		_cmd[0] = 0x0E;
		return _param.port.write(_cmd, 0, 1);
	}
	

}
