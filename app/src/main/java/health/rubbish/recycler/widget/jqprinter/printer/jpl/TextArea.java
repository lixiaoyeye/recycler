package health.rubbish.recycler.widget.jqprinter.printer.jpl;

import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define.ALIGN;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define.FONT_ID;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.JPL.ROTATE;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.Text.TEXT_ENLARGE;

public class TextArea extends BaseJPL{
	
	public TextArea(PrinterParam param) {
		super(param);
	}
	
	/*
	 * �����ı�����������С
	 * x,y���ı������ԭ��
	 * width:�ı�����Ŀ��
	 * height:�ı�����ĸ߶�
	 */
	public boolean setArea(int x, int y, int width, int height) {
		_cmd[0] = 0x1A;
		_cmd[1] = 0x74;
		_cmd[2] = 0x7F;
		_cmd[3] = (byte) x;
		_cmd[4] = (byte) (x >> 8);
		_cmd[5] = (byte) y;
		_cmd[6] = (byte) (y >> 8);
		_cmd[7] = (byte) width;
		_cmd[8] = (byte) (width >> 8);
		_cmd[9] = (byte) height;
		_cmd[10] = (byte) (height >> 8);
		return _port.write(_cmd, 0, 11);
	}
	
	/*
	 * �����ı�������ַ������м��
	 * charSpace���ַ����
	 * lineSpace���м��
	 */
	public boolean setSpace(int charSpace, int lineSpace) {
		_cmd[0] = 0x1A;
		_cmd[1] = 0x74;
		_cmd[2] = 0x7E;
		_cmd[3] = (byte) charSpace;
		_cmd[4] = (byte) (charSpace >> 8);
		_cmd[5] = (byte) lineSpace;
		_cmd[6] = (byte) (lineSpace >> 8);
		return _port.write(_cmd, 0, 7);
	}
	
	/*
	 * �����ı���������
	 * ascID:Ӣ������ID
	 * hzID:��������ID
	 */
	public boolean setFont(FONT_ID ascID, FONT_ID hzID) //page_textarea_set_font
	{
		_cmd[0] = 0x1A;
		_cmd[1] = 0x74;
		_cmd[2] = 0x7D;
		_cmd[3] = (byte) ascID.value();
		_cmd[4] = (byte) (ascID.value() >> 8);
		_cmd[5] = (byte) hzID.value();
		_cmd[6] = (byte) (hzID.value() >> 8);
		return _port.write(_cmd, 0, 7);
	} 
	
	/*
	 * ��������Ч��
	 * bold���Ӵ�Ч��
 	 * underLine���»���
 	 * reverse������
 	 * deleteLine�� ɾ����
	 * charRotate���ַ���ת����, 
	 * enlargeX: ˮƽ�����ַ��Ŵ���
	 * enlargeY�� ��ֱ�����ַ��Ŵ���
	 */
	public boolean setFontEffect(boolean bold, boolean underLine, boolean reverse, boolean deleteLine ,ROTATE charRotate, TEXT_ENLARGE enlargeX, TEXT_ENLARGE enlargeY) 
	{
		int fontType = 0;
		if (bold)
			fontType |= 0x0001;
		if (underLine)
			fontType |= 0x0002;
		if (reverse)
			fontType |= 0x0004;
		if (deleteLine)
			fontType |= 0x0008;
		
		switch (charRotate) {
		case x90:
			fontType |= 0x0010;
			break;
		case x180:
			fontType |= 0x0020;
			break;
		case x270:
			fontType |= 0x0030;
			break;
		default:
			break;
		}
		int ex = enlargeX.ordinal();
		int ey = enlargeY.ordinal();
		ex &= 0x000F;
		ey &= 0x000F;
		fontType |= (ex << 8);
		fontType |= (ey << 12);
		
		_cmd[0] = 0x1A;
		_cmd[1] = 0x74;
		_cmd[2] = 0x7C;
		_cmd[3] = (byte) fontType;
		_cmd[4] = (byte) (fontType >> 8);
		
		return _port.write(_cmd, 0, 5);		
	}
	
	/*
	 * ��������Ӵ�Ч��
	 */
	public boolean setFontBold(boolean bold)
	{
		_cmd[0] = 0x1A;
		_cmd[1] = 0x74;
		_cmd[2] = 0x7B;
		_cmd[3] = (byte)(bold?1:0);
		
		return _port.write(_cmd, 0, 4);	
	}
	/*
	 * ���������»���Ч��
	 */
	public boolean setFontUnderLine(boolean underLine) 
	{
		_cmd[0] = 0x1A;
		_cmd[1] = 0x74;
		_cmd[2] = 0x7A;
		_cmd[3] = (byte)(underLine?1:0);
		
		return _port.write(_cmd, 0, 4);	
	}
	
	/*
	 * �������巴��Ч��
	 */
	public boolean setFontReverse(boolean reverse) 
	{
		_cmd[0] = 0x1A;
		_cmd[1] = 0x74;
		_cmd[2] = 0x79;
		_cmd[3] = (byte)(reverse?1:0);
		return _port.write(_cmd, 0, 4);	
	}
	
	/*
	 * ��������ɾ����Ч��
	 */
	public boolean setFontDeleteLine(boolean deleteLine) 
	{
		_cmd[0] = 0x1A;
		_cmd[1] = 0x74;
		_cmd[2] = 0x78;
		_cmd[3] = (byte)(deleteLine?1:0);
		return _port.write(_cmd, 0, 4);	
	}
	
	/*
	 * �����ַ���תЧ��
	 */
	public boolean setFontCharRotate(ROTATE charRotate) 
	{
		_cmd[0] = 0x1A;
		_cmd[1] = 0x74;
		_cmd[2] = 0x77;
		_cmd[3] = (byte)(charRotate.value());
		return _port.write(_cmd, 0, 4);	
	}
	
	/*
	 * �����ַ��Ŵ�Ч��
	 */
	public boolean setFontEnlarge(TEXT_ENLARGE enlargeX, TEXT_ENLARGE enlargeY)
	{
		_cmd[0] = 0x1A;
		_cmd[1] = 0x74;
		_cmd[2] = 0x76;
		_cmd[3] = (byte)(enlargeX.ordinal());
		_cmd[4] = (byte)(enlargeY.ordinal());
		return _port.write(_cmd, 0, 5);	
	}
	
	/*
	 * ����(һ���ַ���һ���ַ�)����ı�
	 */
	public boolean  drawOut(String text)	
	{
		_cmd[0] = 0x1A;
		_cmd[1] = 0x74;
		_cmd[2] = 0x00;
		_port.write(_cmd, 0, 3);
		return _port.write(text);
	}
	
	/*
	 * ��x,y���꿪ʼ����ı�
	 */
	public boolean  drawOut(int x,int y,String text)	
	{
		_cmd[0] = 0x1A;
		_cmd[1] = 0x74;
		_cmd[2] = 0x01;
		_cmd[3] = (byte)x;
		_cmd[4] = (byte)(x>>8);
		_cmd[5] = (byte)y;
		_cmd[6] = (byte)(y>>8);
		_port.write(_cmd, 0, 7);
		return _port.write(text);
	}
	
	/*
	 * ���ݶ��뷽ʽ����ı�
	 */
	public boolean drawOut(ALIGN align ,String text)	//page_textarea_text_by_align
	{
		_cmd[0] = 0x1A;
		_cmd[1] = 0x74;
		_cmd[2] = 0x02;
		_cmd[3] = (byte)(align.ordinal());
		_port.write(_cmd, 0, 4);
		return _port.write(text);
	}
}
