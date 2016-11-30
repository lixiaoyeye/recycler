package health.rubbish.recycler.widget.jqprinter.printer.esc;

import health.rubbish.recycler.widget.jqprinter.port.Port;
import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;

public class ESC 
{
	public Text text;  
	public Image image;
	public Graphic graphic;
	public Barcode barcode;
	public CardReader card_reader;	
	
	private Port _port;
	private byte[] _cmd = { 0, 0, 0};
	private PrinterParam _param;
	
	public ESC(PrinterParam param) {
		_param = param;
		_port = _param.port;
		text = new Text(_param);
		image = new Image(_param);
		graphic = new Graphic(_param);
		barcode = new Barcode(_param);
		card_reader = new CardReader(_param);
	}

	public enum CARD_TYPE_MAIN {
		CDT_AT24Cxx(0x01), CDT_SLE44xx(0x11), CDT_CPU(0x21);
		private int _value;

		private CARD_TYPE_MAIN(int type) {
			_value = type;
		}

		public int value() {
			return _value;
		}
	};
     
	public static enum BAR_TEXT_POS
	{
		NONE,
		TOP,
		BOTTOM,				
	}
	public static enum BAR_TEXT_SIZE
	{
		ASCII_12x24,
		ASCII_8x16,						
	}
	
	public static enum BAR_UNIT
	{
		x1(1),
		x2(2),
		x3(3),
		x4(4);
		private int _value;
		private BAR_UNIT(int dots)
		{
			_value = dots;
		}		
		public int value()
		{
			return _value;
		}
	}
	public static class LINE_POINT
	{
		public int startPoint;
		public int endPoint;
		public LINE_POINT(){};
		public LINE_POINT(int start_point, int end_point)
		{
			startPoint = (short)start_point;
			endPoint = (short)end_point;
		}
	}
	/*
	 * ö�����ͣ��ı��Ŵ�ʽ
	 */
	public static enum 	TEXT_ENLARGE
	{
		NORMAL(0x00),                        //�����ַ� 
        HEIGHT_DOUBLE(0x01),                 //�����ַ�
        WIDTH_DOUBLE(0x10),                  //�����ַ�
        HEIGHT_WIDTH_DOUBLE (0x11);           //���߱����ַ�
        
        private int _value;
		private TEXT_ENLARGE(int mode)
		{
			_value = mode;
		}		
		public int value()
		{
			return _value;
		}
	}
	/*
	 * ö�����ͣ�����߶�
	 */
	public static enum FONT_HEIGHT
	{
		x24,                     
        x16,
        x32,
        x48,
        x64,        
	}
	
	public static enum IMAGE_MODE
    {
        SINGLE_WIDTH_8_HEIGHT(0x01),        //������8���
        DOUBLE_WIDTH_8_HEIGHT(0x00),        //����8���
        SINGLE_WIDTH_24_HEIGHT(0x21),       //������24���
        DOUBLE_WIDTH_24_HEIGHT(0x20);       //����24���
        private int _value;
		private IMAGE_MODE(final int mode)
		{
			_value = mode;
		}
		public int value()
		{
			return _value;
		}
    }
	public static enum IMAGE_ENLARGE
	{
		NORMAL,//����
		HEIGHT_DOUBLE,//���� 
		WIDTH_DOUBLE,//����
		HEIGHT_WIDTH_DOUBLE	//���߱���	
	}
	
	public boolean init()
	{
		_cmd[0] = 0x1B;	
		_cmd[1] = 0x40;
		return _port.write(_cmd,0,2);		
	}
	
	/*
	 * ���Ѵ�ӡ��������ʼ��
	 */
	public boolean wakeUp() 
	{
		if(!_port.writeNULL())
			return false;
		try {Thread.sleep(50);} catch (InterruptedException e) {}
		return init();
	}
	
	public boolean getState(byte []ret,int timerout_read)
    {
		_port.flushReadBuffer();
		_cmd[0] = 0x10;
		_cmd[1] = 0x04;
		_cmd[2] = 0x05;
		if (!_port.write(_cmd,0,3))
			return false;
		if (!_port.read(ret, 2,timerout_read))
			return false;
		return true;
    }
	/*
	 * ���лس�
	 */
	public boolean feedEnter() {
		_cmd[0] = 0x0D;
		_cmd[1] = 0x0A;
		return _port.write(_cmd, 0, 2);
	}
	/*
	 * ��ֽ����
	 * �������:
	 * --int lines:����
	 */
	public boolean feedLines(int lines) {
		_cmd[0] = 0x1B;
		_cmd[1] = 0x64;
		_cmd[2] = (byte) lines;
		return _port.write(_cmd, 0, 3);
	}
	/*
	 * ��ֽ����
	 * �������:
	 * --int dots:���ٸ���
	 */
	public boolean feedDots(int dots) {
		_cmd[0] = 0x1B;
		_cmd[1] = 0x4A;
		_cmd[2] = (byte) dots;
		return _port.write(_cmd, 0, 3);
	}
	public boolean lable_rignt_mark(){
		_cmd[0] = 0x0C;
		return _port.write(_cmd, 0, 1);
	}
	public boolean lable_left_mark(){
		_cmd[0] = 0x0E;
		return _port.write(_cmd, 0, 1);
	}
	
	public boolean gap_sense(){
		_cmd[0] = 0x1D;
		_cmd[1] = 0x0C;
		return _port.write(_cmd, 0, 2);
	}
}


