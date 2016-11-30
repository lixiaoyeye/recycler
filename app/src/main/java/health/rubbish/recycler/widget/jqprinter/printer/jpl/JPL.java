package health.rubbish.recycler.widget.jqprinter.printer.jpl;

import health.rubbish.recycler.widget.jqprinter.port.Port;
import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;

public class JPL {
	/*
	 * ö�����ͣ�������ת��ʽ
	 */	
	public static enum ROTATE
	{
		x0(0x00),
		x90(0x01),
		x180(0x10),
		x270(0x11);
		private int _value;
		private ROTATE(int mode)
		{
			_value = mode;
		}
		public int value()
		{
			return _value;
		}
	}
	public static enum COLOR
	{
		White,
		Black,		
	}	
	private Port _port;
	private byte[] _cmd = { 0, 0, 0, 0, 0, 0};
	private PrinterParam _param;
	public Page page;
	public Barcode barcode;
	public Text text;
	public TextArea textarea;
	public Graphic graphic;
	public Image image;
		
	public JPL(PrinterParam param) {
		_param = param;
		_port = _param.port;
		page = new Page(_param);
		barcode = new Barcode(_param);
		text = new Text(_param);
		graphic = new Graphic(_param);
		image = new Image(_param);
		textarea = new TextArea(_param);
	}
	
	/*
	 * ��ֽ����һ�ű�ǩ��ʼ
	 */
	public boolean feedNextLabelBegin() {
		_cmd[0] = 0x1A;
		_cmd[1] = 0x0C;
		_cmd[2] = 0x00;
		return _port.write(_cmd, 0, 3);
	}
	public static enum FEED_TYPE
	{
		MARK_OR_GAP,
		LABEL_END,
		MARK_BEGIN,
		MARK_END,
		BACK, //����
	}

	private boolean feed(FEED_TYPE feed_type, int offset) {
		_cmd[0] = 0x1A;
		_cmd[1] = 0x0C;
		_cmd[2] = 0x01;
		_cmd[3] = (byte) feed_type.ordinal();
		_cmd[4] = (byte) offset;
		_cmd[5] = (byte) (offset >> 8);
		return _port.write(_cmd, 0, 6);
	}
	
	//����JPLģʽ
	public boolean intoGPL(int timerout_read)
    {
		_port.flushReadBuffer();
		_cmd[0] = 0x1b;
		_cmd[1] = 0x23;
		_cmd[2] = 0x00;
		if (!_port.write(_cmd,0,3))
			return false;
		return true;
    }
	
	//�˳�JPLģʽ
	public boolean exitGPL(int timerout_read)
    {
		_port.flushReadBuffer();
		_cmd[0] = 0x1a;
		_cmd[1] = 0x23;
		_cmd[2] = 0x00;
		if (!_port.write(_cmd,0,3))
			return false;
		return true;
    }
	/*
	 * ��ӡֽ����
	 * ע��:1.��Ҫ��ӡ��JLP351�Ĺ̼��汾3.0.0.0������
	 *      2.��Ҫ����������ô�ӡ����ʹ��FeedBack״̬
	 */
	public boolean feedBack(int dots)
	{
		return feed(FEED_TYPE.BACK,dots);
	}
	
	public boolean feedNextLabelEnd(int dots)
	{
		return feed(FEED_TYPE.LABEL_END,dots);
	}
	
	public boolean feedMarkOrGap(int dots)
	{
		return feed(FEED_TYPE.MARK_OR_GAP,dots);
	}
	
	public boolean feedMarkEnd(int dots)
	{
		return feed(FEED_TYPE.MARK_END,dots);
	}
	
	public boolean feedMarkBegin(int dots)
	{
		return feed(FEED_TYPE.MARK_BEGIN,dots);
	}
}
