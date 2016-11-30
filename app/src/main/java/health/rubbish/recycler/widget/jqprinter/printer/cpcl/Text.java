package health.rubbish.recycler.widget.jqprinter.printer.cpcl;

import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;

public class Text  extends BaseCPCL {
	
	private int _fontHeight;
	private boolean _bFontBold,_bFontUnderline;
	private int _fontFamily;
	private int _fontSize, _fontEnlargeX, _fontEnlargeY;
	
	public Text(PrinterParam param) {
		super(param);
	}
	
	public static enum FONT_FAMILY
	{
		ASCII_Standard(0),
		ASCII_Script(1),
		ASCII_OCR_A(2),
		ASCII_Unison(4),
		ASCII_Manhattan(5),
		ASCII_MICR(6),
		ASCII_Warwick(7),
		GBK_16x16(55),
		GBK_24x24(24);
		private int _value;
		private FONT_FAMILY(int mode)
		{
			_value = mode;
		}
		public int value()
		{
			return _value;
		}
	}	
	
	/*
	 * ��������family�޶�����size
	 */
	private int getFontSize(FONT_FAMILY family, int size)
	{
		if (size < 0) size = 0;
		switch(family)
		{
			case ASCII_Standard:
				if (size > 6) size = 6;
				break;			
			case ASCII_Script:
				if (size > 0) size = 0;
				break;
			case ASCII_OCR_A:
				if (size > 1) size = 1;
				break;
			case ASCII_Unison:
				if (size > 7) size = 7;
				break;
			case ASCII_Manhattan:
				if (size > 3) size = 3;
				break;
			case ASCII_MICR:
				if (size > 0) size = 0;
				break;
			case ASCII_Warwick:
				if (size > 1) size = 1;
				break;
			default:
				break;
		}
		return size;
	}
	
    public void setFont(int FontHeight, int Angle, boolean bold, boolean underline)
    {
    	_fontHeight = FontHeight;
        _bFontBold = bold;
        _bFontUnderline = underline;

        if (_fontHeight > 40) //48
        {
        	_fontFamily = 5;
            this._fontSize = 0;
            this._fontEnlargeX = 1;
            this._fontEnlargeY = 1;

        }
        else if (_fontHeight >= 28) //32
        {
        	_fontFamily = 4;
            this._fontSize = 0;
            this._fontEnlargeX = 0;
            this._fontEnlargeY = 0;
        }
        else if (_fontHeight >= 20) //24
        {
        	_fontFamily = 5;
            this._fontSize = 0;
            this._fontEnlargeX = 0;
            this._fontEnlargeY = 0; 
        }
        else
        {
        	_fontFamily = 55;
            this._fontSize = 0;
            this._fontEnlargeX = 0;
            this._fontEnlargeY = 0; 
        }
    }
    
    public boolean _setFontSetBold(boolean bBold)
    {
    	int bold = bBold?1:0;
        String CPCLCmd = "SETBOLD " + bold;
        return portSendCmd(CPCLCmd);
    }
    
    private boolean _setFontSetUnderline(boolean underline)
    {
        String CPCLCmd; 
        if (underline)
            CPCLCmd = "UNDERLINE ON";
        else
            CPCLCmd = "UNDERLINE OFF";
        return portSendCmd(CPCLCmd);
    }
    
    //�������屶�߱���
    public boolean _setFontSetMagCPCL(int ex,int ey)
    {
        String CPCLCmd = "SETMAG " + ex + " " + ey;
        return portSendCmd(CPCLCmd);
    }
    
    public boolean drawOut(int x, int y, String text)
    {
        return _drawOut(x,y, text, _fontFamily, _fontSize, _fontEnlargeX, _fontEnlargeY, _bFontBold, _bFontUnderline);
    }
    
    private boolean _drawOut(int x, int y, String text, int fontFamily,int fontSize, int ex, int ey, boolean bold, boolean underline) 
	{
		if (!_setFontSetUnderline(underline)) return false;
//		if (!_setFontSetMagCPCL(ex, ey)) return false;
		if (!_setFontSetBold(bold))	return false;		
		return _drawOutHorizontal(x, y, text, fontFamily, fontSize);
	}
	
	/*
	 * ����ˮƽ�����ַ���
	 */
	private boolean _drawOutHorizontal(int x, int y,String text,int family,int size)
	{
		String cmd = "TEXT " + family + " " + size + " " + x + " " + y + " " + text;
		return portSendCmd(cmd);
	}
	
	
		
}
