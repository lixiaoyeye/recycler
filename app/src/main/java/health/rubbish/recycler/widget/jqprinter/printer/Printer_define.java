package health.rubbish.recycler.widget.jqprinter.printer;

public class Printer_define {
	/*
	 * ö�����ͣ�����ID
	 */
	public static enum FONT_ID
	{
		ASCII_12x24(0x0000),                     
        ASCII_8x16(0x0001),
        ASCII_16x32(0x0003),
        ASCII_24x48(0x0004),
        ASCII_32x64(0x0005),
        GBK_24x24(0x0010),
        GBK_16x16(0x0011),
        GBK_32x32(0x0013),
        GB2312_48x48(0x0014);
        
        private int _value;
		private FONT_ID(int id)
		{
			_value = id;
		}		
		public int value()
		{
			return _value;
		}	
	}
	
	/*
	 * ö�����ͣ���ӡ���ͺ�
	 */
	public static enum PRINTER_MODEL
	{
		VMP02,
		VMP02_P,
		JLP351,
		JLP351_IC,
		ULT113x,
		ULT1131_IC,
		EXP341,
	}
	
	/*
	 * ö�����ͣ����뷽ʽ
	 */
	public static enum ALIGN
	{
		LEFT,
		CENTER,
		RIGHT;
	}
}
