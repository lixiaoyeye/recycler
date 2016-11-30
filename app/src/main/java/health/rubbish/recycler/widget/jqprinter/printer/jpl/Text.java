package health.rubbish.recycler.widget.jqprinter.printer.jpl;

import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define.ALIGN;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.JPL.ROTATE;

public class Text extends BaseJPL{
	
	public Text(PrinterParam param) {
		super(param);
	}
	
	public static enum TEXT_ENLARGE
	{
		x1,
		x2,
		x3,
		x4,
	}	
	
	public boolean drawOut(int x, int y, String text)
	{
		_cmd[0] = 0x1A;
		_cmd[1] = 0x54;
		_cmd[2] = 0x00;
		_cmd[3] = (byte)x;
		_cmd[4] = (byte)(x>>8);
		_cmd[5] = (byte)y;
		_cmd[6] = (byte)(y>>8);
		_port.write(_cmd,0,7);
		return _port.write(text);
	}

	public boolean drawOut(int x, int y, String text, int fontHeight,
			boolean bold, boolean reverse, boolean underLine,
			boolean deleteLine, TEXT_ENLARGE enlargeX, TEXT_ENLARGE enlargeY,
			ROTATE rotateAngle) {
		if (x < 0 || y < 0)
			return false;
		if (x >= _param.pageWidth || y < 0)
			return false;
		
		int fontType = 0;
		if (bold)
			fontType |= 0x0001;
		if (underLine)
			fontType |= 0x0002;
		if (reverse)
			fontType |= 0x0004;
		if (deleteLine)
			fontType |= 0x0008;
		switch (rotateAngle) {
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
		_cmd[1] = 0x54;
		_cmd[2] = 0x01;
		_cmd[3] = (byte) x;
		_cmd[4] = (byte) (x >> 8);
		_cmd[5] = (byte) y;
		_cmd[6] = (byte) (y >> 8);
		_cmd[7] = (byte) fontHeight;
		_cmd[8] = (byte) (fontHeight >> 8);
		_cmd[9] = (byte) fontType;
		_cmd[10] = (byte) (fontType >> 8);
		_port.write(_cmd, 0, 11);
		return _port.write(text);
	}

	private boolean isChinese(char c) 
	{
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
             || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
            || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }
	
	private int calcTextWidth(int font_width,String text)
	{
		int hz_count = 0;
		int ascii_count = 0;
		for(int i= 0;i < text.length();i++)
		{
			if (isChinese(text.charAt(i)))
			{
				hz_count++;
			}
			else
			{
				ascii_count++;
			}
		}		
		return (hz_count*font_width + ascii_count*font_width/2);
	}
	
	private int calcFontWidth(int font_height)
	{
		if (font_height< 20)
		{
			return 16;
		}
		else if (font_height< 28)
		{
			return 24;
		}
		else if (font_height< 40)
		{
			return 32;
		}
		else if (font_height< 56)
		{
			return 48;
		}
		else 
		{
			return 64;
		}
	}
	private int calcTextStartPosition(ALIGN align,int startX, int endX, int font_height,int enlargeX,String text)
	{
		if (endX < startX) 
		{
			int t = endX;
			endX = startX;
			startX = t;
		}
		if (align == ALIGN.LEFT)
			return startX;
		
		int width = endX - startX;
		
		int font_width = calcFontWidth(font_height);
		enlargeX++;
		int font_total_width = calcTextWidth(font_width,text)*enlargeX;
		
		if(font_total_width >= width) 
			return startX;
		switch(align)
		{
			case CENTER:
				return startX + (width - font_total_width)/2; 
			case RIGHT:
				return startX + width - font_total_width;
			default:
				return startX;
		}	
	}
	
	/*
	 * ����x�������ͽ������������
	 */
	public boolean drawOut(ALIGN align, int startX, int endX, int y,
			String text, int fontHeight, boolean bold, boolean reverse,
			boolean underLine, boolean deleteLine, TEXT_ENLARGE enlargeX,
			TEXT_ENLARGE enlargeY, ROTATE rotateAngle) {
		int x = calcTextStartPosition(align, startX, endX, fontHeight,
				enlargeX.ordinal(), text);
		return drawOut(x, y, text, fontHeight, bold, reverse, underLine,
				deleteLine, enlargeX, enlargeY, rotateAngle);
	}
}
