package health.rubbish.recycler.widget.jqprinter.printer.jpl;

import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define.ALIGN;
import health.rubbish.recycler.widget.jqprinter.printer.common.Code128;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.JPL.ROTATE;

public class Barcode extends BaseJPL
{
	 /*
     * ���캯��
     */
	public Barcode(PrinterParam param) {
		super(param);
	}
	/*
	 * ö�����ͣ�JPL������������
	 */
    public static enum BAR_1D_TYPE
    {
        UPCA_AUTO(0x41),
        UPCE_AUTO(0x42),
        EAN8_AUTO(0x43),
        EAN13_AUTO(0x44),
        CODE39_AUTO(0x45),
        ITF25_AUTO(0x46),
        CODABAR_AUTO(0x47),
        CODE93_AUTO(0x48),
        CODE128_AUTO(0x49);
        private int _value;
		private BAR_1D_TYPE(int id)
		{
			_value = id;
		}		
		public int value()
		{
			return _value;
		}	
    }
    /*
	 * ö�����ͣ����뵥Ԫ,JPL����
	 */
    public enum BAR_UNIT
    {
        x1(1),
        x2(2),
        x3(3),
        x4(4),
        x5(5),
        x6(6),
        x7(7);
        private int _value;
		private BAR_UNIT(int id)
		{
			_value = id;
		}		
		public int value()
		{
			return _value;
		}
    }
    // <summary>
    /// ��ӡ������ת�Ƕ�
    /// </summary>
    public enum BAR_ROTATE
    {
        ANGLE_0,
        ANGLE_90,
        ANGLE_180,
        ANGLE_270
    }    

	/*
	 *һά�������
	 */
	private boolean _1D_barcode(int x, int y, BAR_1D_TYPE type, int height, BAR_UNIT unit_width, BAR_ROTATE rotate, String text)
    {
        _cmd[0] = 0x1A;
        _cmd[1] = 0x30;
        _cmd[2] = 0x00;
        _cmd[3] = (byte)x;
        _cmd[4] = (byte)(x>>8);
        _cmd[5] = (byte)y;
        _cmd[6] = (byte)(y>>8);
        _cmd[7] = (byte)type.value();
        _cmd[8] = (byte)height;
        _cmd[9] = (byte)(height>>8);
        _cmd[10] = (byte)unit_width.value();
        _cmd[11] = (byte)rotate.ordinal();
        _port.write(_cmd,0,12);
        return _port.write(text);
    }
	/*
	 * code128
	 */
	public boolean code128(int x,int y, int bar_height, BAR_UNIT unit_width,BAR_ROTATE rotate,String text)
	{
		return _1D_barcode(x,y, BAR_1D_TYPE.CODE128_AUTO,bar_height, unit_width, rotate, text);
	}
	/*
	 * Code128
	 * ����x����startX,endX�����뷽ʽ����
	 */
	public boolean code128(ALIGN align,int startX, int endX, int y, int bar_height, BAR_UNIT unit_width,BAR_ROTATE rotate,String text)
	{
		if (endX < startX)
		{
			int temp = startX;
			startX = endX;
			endX = temp;
		}
		int x = startX;
		int width = endX - startX;
		
		Code128 code128 = new Code128(text);
		if (code128.encode_data == null)
			return false;
		if (!code128.decode(code128.encode_data))
			return false;
		int bar_width = code128.decode_string.length() * unit_width.value();
		
		if (width < bar_width)
		{
			align = ALIGN.LEFT;
		}
		if (align ==ALIGN.CENTER)
			x = startX + (width - bar_width )/2;
		else if(align ==ALIGN.RIGHT)
			x = startX + width - bar_width;
		
		return _1D_barcode(x,y, BAR_1D_TYPE.CODE128_AUTO,bar_height, unit_width, rotate, text);
	}
	
	public enum QRCODE_ECC
	{
		LEVEL_L,//�ɾ���7%
		LEVEL_M,//�ɾ���15%
		LEVEL_Q,//�ɾ���25%
		LEVEL_H,//�ɾ���30%	
	}
	/*
	 * QRCode
	 * int version:�汾�ţ����Ϊ0�����Զ�����汾�š�
	 *             ÿ���汾�����ɵ��ֽ���Ŀ��һ���ġ�������ݲ��㣬���Զ����հס�ͨ������һ����İ汾�����̶�QRCode��С��
	 * int ecc������ʽ,ȡֵ0, 1��2��3��������Խ�ߣ���Ч�ַ�Խ�٣�ʶ����Խ�ߡ�ȱʡΪ2
	 * int unit_width��������Ԫ��С
	 */
	public boolean QRCode(int x, int y, int version, QRCODE_ECC ecc,
			BAR_UNIT unit_width, ROTATE rotate, String text) {
		_cmd[0] = 0x1A;
		_cmd[1] = 0x31;
		_cmd[2] = 0x00;
		_cmd[3] = (byte) version;
		_cmd[4] = (byte) ecc.ordinal();
		_cmd[5] = (byte) x;
		_cmd[6] = (byte) (x >> 8);
		_cmd[7] = (byte) y;
		_cmd[8] = (byte) (y >> 8);
		_cmd[9] = (byte) unit_width.value();
		_cmd[10] = (byte) rotate.value();
		_port.write(_cmd, 0, 11);
		return _port.write(text);
	}
	/*
	 * QRCode
	 * int version:�汾�ţ����Ϊ0�����Զ�����汾�š�
	 *             ÿ���汾�����ɵ��ֽ���Ŀ��һ���ġ�������ݲ��㣬���Զ����հס�ͨ������һ����İ汾�����̶�QRCode��С��
	 * int ecc������ʽ,ȡֵ0, 1��2��3��������Խ�ߣ���Ч�ַ�Խ�٣�ʶ����Խ�ߡ�ȱʡΪ2
	 * int unit_width��������Ԫ��С
	 */
	public boolean QRCode(int x, int y, int version, QRCODE_ECC ecc,
			BAR_UNIT unit_width, ROTATE rotate, byte[] data, int datasize) {
		_cmd[0] = 0x1A;
		_cmd[1] = 0x31;
		_cmd[2] = 0x10;
		_cmd[3] = (byte) version;
		_cmd[4] = (byte) ecc.ordinal();
		_cmd[5] = (byte) x;
		_cmd[6] = (byte) (x >> 8);
		_cmd[7] = (byte) y;
		_cmd[8] = (byte) (y >> 8);
		_cmd[9] = (byte) unit_width.value();
		_cmd[10] = (byte) rotate.value();
		_cmd[11] = (byte) datasize;
		_cmd[12] = (byte) (datasize>>8);
		_port.write(_cmd, 0, 13);
		return _port.write(data,0,datasize);
	}
	/*
	 * PDF417
	 */
	public boolean PDF417(int x, int y, int col_num, int ecc, int LW_ratio,
			BAR_UNIT unit_width, ROTATE rotate, String text) {
		_cmd[0] = 0x1A;
		_cmd[1] = 0x31;
		_cmd[2] = 0x01;
		_cmd[3] = (byte) col_num;
		_cmd[4] = (byte) ecc;
		_cmd[5] = (byte) (byte) LW_ratio;
		_cmd[6] = (byte) x;
		_cmd[7] = (byte) (x >> 8);
		_cmd[8] = (byte) y;
		_cmd[9] = (byte) (y >> 8);
		_cmd[10] = (byte) unit_width.value();
		_cmd[11] = (byte) rotate.value();
		_port.write(_cmd, 0, 12);
		return _port.write(text);
	}
	
	public boolean DataMatrix(int x, int y, BAR_UNIT unit_width, ROTATE rotate,
			String text) {
		_cmd[0] = 0x1A;
		_cmd[1] = 0x31;
		_cmd[2] = 0x02;
		_cmd[3] = (byte) x;
		_cmd[4] = (byte) (x >> 8);
		_cmd[5] = (byte) y;
		_cmd[6] = (byte) (y >> 8);
		_cmd[7] = (byte) unit_width.value();
		_cmd[8] = (byte) rotate.value();
		_port.write(_cmd, 0, 9);
		return _port.write(text);
	}
	
	public boolean GridMatrix(int x, int y,byte ecc,  BAR_UNIT unit_width, ROTATE rotate, String text)
	{
		_cmd[0] = 0x1A;
		_cmd[1] = 0x31;
		_cmd[2] = 0x03;
		_cmd[3] = (byte) ecc;
		_cmd[4] = (byte) x;
		_cmd[5] = (byte) (x >> 8);
		_cmd[6] = (byte) y;
		_cmd[7] = (byte) (y >> 8);
		_cmd[8] = (byte) unit_width.value();
		_cmd[9] = (byte) rotate.value();
		_port.write(_cmd, 0, 10);
		return _port.write(text);
	}
}
