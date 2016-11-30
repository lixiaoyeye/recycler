package health.rubbish.recycler.widget.jqprinter.printer.esc;

import android.util.Log;

import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define.ALIGN;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define.FONT_ID;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define.PRINTER_MODEL;

public class Text extends BaseESC
{	
	/*
	 * ���캯��
	 */
	public Text(PrinterParam param) {
		super(param);
	}
	/*
	 * �������ֵķŴ�ʽ
	 */
	public boolean setFontEnlarge(ESC.TEXT_ENLARGE enlarge) {
		_cmd[0] = 0x1D;
		_cmd[1] = 0x21;
		_cmd[2] = (byte) enlarge.value();
		return _port.write(_cmd, 0, 3);
	}

	/*
	 * �����ı�����ID
	 */
	public boolean setFontID(FONT_ID id) {
		switch (id) 
		{
			case ASCII_16x32:
			case ASCII_24x48:
			case ASCII_32x64:
			case GBK_32x32:
			case GB2312_48x48:
				if (_model == PRINTER_MODEL.VMP02
					|| _model == PRINTER_MODEL.ULT113x) {
					Log.e("JQ", "not support FONT_ID:" + id);
					return true;
				}
				break;
			default:
				break;
		}
		_cmd[0] = 0x1B;
		_cmd[1] = 0x4D;
		_cmd[2] = (byte) id.value();
		return _port.write(_cmd, 0, 3);
	}
	/*
	 * �����ı�����߶�
	 */
	public boolean setFontHeight(ESC.FONT_HEIGHT height)
	{
		switch(height)
		{
			case x24:
				setFontID(FONT_ID.ASCII_12x24);
				setFontID(FONT_ID.GBK_24x24);
				break;
			case x16:
				setFontID(FONT_ID.ASCII_8x16);
				setFontID(FONT_ID.GBK_16x16);
				break;
			case x32:
				setFontID(FONT_ID.ASCII_16x32);
				setFontID(FONT_ID.GBK_32x32);
				break;
			case x48:
				setFontID(FONT_ID.ASCII_24x48);
				setFontID(FONT_ID.GB2312_48x48);
				break;
			case x64:
				setFontID(FONT_ID.ASCII_32x64);
				break;
			default:
				return false;
		}	
		return true;
	}
	/*
	 * �����ı��Ӵַ�ʽ
	 */
	public boolean setBold(boolean bold) {
		_cmd[0] = 0x1B;
		_cmd[1] = 0x45;
		_cmd[2] = (byte) (bold ? 1 : 0);
		return _port.write(_cmd, 0, 3);
	}	
	/*
	 * �����ı��»��߷�ʽ
	 */
	public boolean setUnderline(boolean underline) {
		_cmd[0] = 0x1B;
		_cmd[1] = 0x2D;
		_cmd[2] = (byte) (underline ? 1 : 0);
		return _port.write(_cmd, 0, 3);
	}
	/*
	 * ��ӡ����ı�
	 */
	public boolean drawOut(String text) {
		return _port.write(text);
	}
	/*
	 * ��ӡ����ı�
	 * x: x����
	 * y��y����
	 * ע�⣺x��y�����������Ƶģ���ο�setXY����Դ����
	 */
	public boolean drawOut(int x,int y,String text) {
		if (!setXY(x,y))
			return false;
		return _port.write(text);
	}	
	/*
	 * ��ӡ����ı�
	 */
	public boolean drawOut(int x, int y, ESC.TEXT_ENLARGE enlarge,String text) {
		if (!setXY(x, y))
			return false;
		if (!setFontEnlarge(enlarge))
			return false;
		return _port.write(text);
	}	
	/*
	 * ��ӡ����ı�
	 */
	public boolean drawOut(ESC.FONT_HEIGHT height, String text) {
		if (!setFontHeight(height))
			return false;
		return _port.write(text);
	}
	
	/*
	 * ��ӡ����ı�
	 */
	public boolean drawOut(int x, int y, ESC.FONT_HEIGHT height, ESC.TEXT_ENLARGE enlarge, String text) {
		if (!setXY(x, y))
			return false;
		if (!setFontHeight(height))
			return false;
		if (!setFontEnlarge(enlarge))
			return false;
		return _port.write(text);
	}	
	/*
	 * ��ӡ����ı�
	 */
	public boolean drawOut(int x, int y, ESC.FONT_HEIGHT height, boolean bold, ESC.TEXT_ENLARGE enlarge, String text) {
		if (!setXY(x, y))
			return false;
		if (!setFontHeight(height))
			return false;
		if (!setFontEnlarge(enlarge))
			return false;
		if (!setBold(bold))
			return false;
		return _port.write(text);
	}
	/*
	 * ��ӡ����ı�
	 */
	public boolean drawOut(int x, int y, ESC.FONT_HEIGHT height, boolean bold, String text) {
		if (!setXY(x, y))
			return false;
		if (!setFontHeight(height))
			return false;
		if (!setBold(bold))
			return false;
		return _port.write(text);
	}
	/*
	 * ��ӡ����ı�
	 */
	public boolean drawOut(ALIGN align, ESC.FONT_HEIGHT height, boolean bold, ESC.TEXT_ENLARGE enlarge, String text) {
		if (!setAlign(align))
			return false;
		if (!setFontHeight(height))
			return false;
		if (!setBold(bold))
			return false;
		if (!setFontEnlarge(enlarge))
			return false;
		return _port.write(text);
	}
	/*
	 * ��ӡ����ı�
	 */
	public boolean drawOut(ALIGN align, boolean bold,String text) {
		if (!setAlign(align))
			return false;
		if (!setBold(bold))
			return false;
		return _port.write(text);
	}
	
	/// <summary>
    /// ��ӡ����ı�
    /// 1)�ļ��������
    /// 2)��ָ�����Ч����ȱʡֵ
	/// ע�⣺ʹ�ô˷���ʱ���뾡����֤���ݲ�Ҫ����һ�С�
	///      ��Ϊ��y������0�������ӡ���ݳ���һ�У���һ�г���λ�ûᵽpage�Ľ���λ�á�
    /// </summary>
    public boolean printOut(int x, int y, ESC.FONT_HEIGHT height,boolean bold, ESC.TEXT_ENLARGE enlarge, String text)
    {
        if (!setXY(x, y))   return false;
        if (bold) if (!setBold(true)) return false;
        if (!setFontHeight(height)) return false;
        if (!setFontEnlarge(enlarge)) return false;
        if (!_port.write(text)) return false;            
        enter();
        //�ָ�����Ч��
        if (bold) if (!setBold(false)) return false;
        if (!setFontHeight(ESC.FONT_HEIGHT.x24)) return false;
        if (!setFontEnlarge(ESC.TEXT_ENLARGE.NORMAL)) return false;

        return true;
    }
    
  /// <summary>
    /// ��ӡ����ı�
    /// 1)�ļ��������
    /// 2)��ָ�����Ч����ȱʡֵ
	/// ע�⣺ʹ�ô˷���ʱ���뾡����֤���ݲ�Ҫ����һ�С�
	///      ��Ϊ��y������0�������ӡ���ݳ���һ�У���һ�г���λ�ûᵽpage�Ľ���λ�á�
    /// </summary>
    public boolean printOut(int x, int y, ESC.FONT_HEIGHT height,boolean bold,boolean underline, ESC.TEXT_ENLARGE enlarge, String text)
    {
        if (!setXY(x, y))   return false;
        if (bold) if (!setBold(true)) return false;
        if (underline) setUnderline(true);
        if (!setFontHeight(height)) return false;
        if (!setFontEnlarge(enlarge)) return false;
        if (!_port.write(text)) return false;            
        enter();
        //�ָ�����Ч��
        if (bold) if (!setBold(false)) return false;
        if (underline) setUnderline(false);
        if (!setFontHeight(ESC.FONT_HEIGHT.x24)) return false;
        if (!setFontEnlarge(ESC.TEXT_ENLARGE.NORMAL)) return false;

        return true;
    }

    
    public boolean printOut(ALIGN align, ESC.FONT_HEIGHT height, boolean bold, ESC.TEXT_ENLARGE enlarge, String text)
    {
        if (!setAlign(align))   return false;
        if (!setFontHeight(height)) return false;
        if (bold) if (!setBold(true)) return false;
        if (!setFontEnlarge(enlarge))   return false;
        if (!_port.write(text))  return false;
        enter();
        //�ָ�        
        if (!setAlign(ALIGN.LEFT)) return false;
        if (!setFontHeight(ESC.FONT_HEIGHT.x24)) return false;
        if (bold) if (!setBold(false)) return false;            
        if (!setFontEnlarge(ESC.TEXT_ENLARGE.NORMAL)) return false;
        return true;
    }
    
    public boolean printOut(String text)
    {
        if (!_port.write(text))
            return false;
        return enter();            
    }
}
