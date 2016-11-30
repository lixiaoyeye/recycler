package health.rubbish.recycler.widget.jqprinter.printer.esc;

import java.io.UnsupportedEncodingException;

import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;
import health.rubbish.recycler.widget.jqprinter.printer.Printer_define.ALIGN;
import health.rubbish.recycler.widget.jqprinter.printer.common.Code128;
public class Barcode extends BaseESC
{	
	public enum ESC_BAR_2D{				   	
		PDF417(0),
		DATAMATIX(1),
	    QRCODE(2),	
	    GRIDMATIX(10); 
	    
	    private int _value;
	    private ESC_BAR_2D(int id){
	    	_value = id;
	    }		
	    public int value()
	    {
	    	return _value;
	    }
    };
    
    /*
	 * ���캯��
	 */
	public Barcode(PrinterParam param) {
		super(param);
	}
	/*
	 * ����1ά����߶�
	 */
	public boolean set1DHeight(int height) {
		_cmd[0] = 0x1D;
		_cmd[1] = 0x68;
		_cmd[2] = (byte) height;
		return _port.write(_cmd, 0, 3);
	}
	/*
	 * ����1ά��2ά���������Ԫ��С
	 */
	public boolean setUnit(ESC.BAR_UNIT unit) {
		_cmd[0] = 0x1D;
		_cmd[1] = 0x77;
		_cmd[2] = (byte) unit.value();
		return _port.write(_cmd, 0, 3);
	}
	/*
	 * ������������λ��
	 */
	public boolean setTextPosition(ESC.BAR_TEXT_POS pos) {
		_cmd[0] = 0x1D;
		_cmd[1] = 0x48;
		_cmd[2] = (byte) pos.ordinal();
		return _port.write(_cmd, 0, 3);
	}
	/*
	 * �����������ִ�С
	 */
	public boolean setTextSize(ESC.BAR_TEXT_SIZE size) {
		_cmd[0] = 0x1D;
		_cmd[1] = 0x66;
		_cmd[2] = (byte) size.ordinal();
		return _port.write(_cmd, 0, 3);
	}
	/*
	 * EANУ���
	 */
	private byte EAN_checksum(byte []str,int str_len)
	{	
		int i;
		int check_sum = 0;
		
		if ( str_len%2 == 1) //������
		{
			for( i = 0; i < str_len; i++)
			{
				if(i%2==0)	
					check_sum += (str[i]-'0')*3;
				else	
					check_sum += (str[i]-'0');
			}
		}
		else //ż����
		{
			for( i = 0; i < str_len; i++)
			{
				if(i%2==0)	
					check_sum += (str[i]-'0');
				else	
					check_sum += (str[i]-'0')*3;
			}
		}
		check_sum = check_sum%10;
		if(check_sum != 0)
			check_sum = 10 - check_sum;
		check_sum = '0'+check_sum;
		return (byte)check_sum;
	}
	/*
	 * ��ȡUPCE��У���
	 */
	private byte UPCE_getChecksum(byte []src,int length)
	{
		byte []buf =new byte[12];	
		int i;
		int j=0;
	    if (src[6] == '0' || src[6] == '1' || src[6] == '2')
	    {                
			buf[j++] = src[0];
	        buf[j++] = src[1];
			buf[j++] = src[2];
	        buf[j++] = src[6];
			for (i = 0; i < 4; i++) 
				buf[j++] = '0';
			buf[j++] = src[3];
			buf[j++] = src[4];
			buf[j++] = src[5];
	    }
	    else if (src[6] == '3')
	    {
	    	buf[j++] = src[0];
	    	buf[j++] = src[1];
	    	buf[j++] = src[2];
	    	buf[j++] = src[3];
	        for (i = 0; i < 5; i++) 
	        	buf[j++] = '0';
	        buf[j++] = src[4];
	        buf[j++] = src[5];
	    }
	    else if (src[6] == '4')
	    {
	    	buf[j++] = src[0];
	    	buf[j++] = src[1];
	    	buf[j++] = src[2];
	    	buf[j++] = src[3];
	    	buf[j++] = src[4];
	        for (i = 0; i < 5; i++)	
	        	buf[j++] = '0';
	        buf[j++] = src[5];
	    }
	    else 
	    {
	    	buf[j++] = src[0];
	    	buf[j++] = src[1];
	    	buf[j++] = src[2];
	    	buf[j++] = src[3];
	    	buf[j++] = src[4];
	    	buf[j++] = src[5];
	        for (i = 0; i < 4; i++)	
	        	buf[j++] = '0';
	        buf[j++] = src[6];
	     }
		return EAN_checksum(buf,11);
	}
	/*
	 * UPCE������ʽ���˺����������У��͡���Ҫ���Լ���data�����У���
	 */
	private boolean UPCE_base(byte[] data, int offset, int length) {
		_cmd[0] = 0x1D;
		_cmd[1] = 0x6B;
		_cmd[2] = 0x01;
		_port.write(_cmd, 0, 3);
		return _port.write(data, offset, length);
	}
	/*
	 * UPCE�Զ�����У���
	 * --String str������7���ַ�
	 */
	public boolean UPCE_auto(String text)
	{
		byte []buf =new byte[9];
		if(text.length() != 7) //��鳤��
			return false;	
		for(int i = 0; i < text.length(); i++) //����Ƿ�Ϊ����
		{
			if(text.charAt(i) <'0'||text.charAt(i) >'9') 
				return false;
		}
		if(text.charAt(0)!='0' && text.charAt(1)!='1') 
			return false;
		for(int i = 0;i < 7;i++)
		{
			buf[i] = (byte)text.charAt(i);
		}
		buf[7] = UPCE_getChecksum(buf,7);
		buf[8] = 0;
		return UPCE_base(buf,0,9);
	}
	/*
	 * UPCA������ʽ���˺����������У��͡���Ҫ���Լ���data�����У���
	 */
	private boolean UPCA_base(byte[] data, int offset, int length) {
		_cmd[0] = 0x1D;
		_cmd[1] = 0x6B;
		_cmd[2] = 0x00;
		_port.write(_cmd, 0, 3);
		return _port.write(data, offset, length);
	}
	/*
	 * UPCA�Զ�����У���
	 * --String str������11���ַ�
	 */
	public boolean UPCA_auto(String str) {
		byte[] buf = new byte[13];
		if (str.length() != 11)
			return false;
		for (int i = 0; i < str.length(); i++) // ����Ƿ�Ϊ����
		{
			if (str.charAt(i) < '0' || str.charAt(i) > '9')
				return false;
		}
		for (int i = 0; i < str.length(); i++) {
			buf[i] = (byte) str.charAt(i);
		}
		buf[11] = EAN_checksum(buf, 11);
		buf[12] = 0;
		return UPCA_base(buf, 0, 13);
	}
	/*
	 * EAN13������ʽ���˺����������У��͡���Ҫ���Լ���data�����У���
	 */
	private boolean EAN13_base(byte[] data, int offset, int length) {
		_cmd[0] = 0x1D;
		_cmd[1] = 0x6B;
		_cmd[2] = 0x03;
		_port.write(_cmd, 0, 3);
		return _port.write(data, 0, length);
	}
	/*
	 * EAN13�Զ�����У���
	 * ���������
	 * --String str������12���ַ�
	 */
	public boolean EAN13_auto(String str) {
		byte[] buf = new byte[14];
		if (str.length() != 12)
			return false;
		for (int i = 0; i < str.length(); i++) // ����Ƿ�Ϊ����
		{
			if (str.charAt(i) < '0' || str.charAt(i) > '9')
				return false;
		}
		for (int i = 0; i < str.length(); i++) {
			buf[i] = (byte) str.charAt(i);
		}
		buf[12] = EAN_checksum(buf, 12);
		buf[13] = 0;
		return EAN13_base(buf, 0, 14);
	}
	/*
	 * EAN8������ʽ���˺����������У��͡���Ҫ���Լ���data�����У���
	 */
	private boolean EAN8_base(byte[] data, int offset, int length) {
		_cmd[0] = 0x1D;
		_cmd[1] = 0x6B;
		_cmd[2] = 0x02;
		_port.write(_cmd, 0, 3);
		return _port.write(data, offset, length);
	}
	/*
	 * EAN8�Զ�����У���
	 * ���������
	 * --String str������7���ַ�
	 */
	public boolean EAN8_auto(String str) {
		if (str.length() != 7)
			return false;
		for (int i = 0; i < str.length(); i++) // ����Ƿ�Ϊ����
		{
			if (str.charAt(i) < '0' || str.charAt(i) > '9')
				return false;
		}
		byte[] buf = new byte[9];
		for (int i = 0; i < str.length(); i++) {
			buf[i] = (byte) str.charAt(i);
		}
		buf[7] = EAN_checksum(buf, 7);
		buf[8] = 0;
		return EAN8_base(buf, 0, 9);
	}
	/*
	 * CODE128������ʽ���˺����������У��͡���Ҫ���Լ���data�����У���
	 */
	private boolean CODE128_base(byte[] data, int offset, int length) {
		_cmd[0] = 0x1D;
		_cmd[1] = 0x6B;
		_cmd[2] = 0x08;
		_port.write(_cmd, 0, 3);
		_port.write(data, offset, length);
		return _port.writeNULL();
	}

	/*
	 * Code128�Զ�����У���
	 * ���������
	 * --String str��
	 */
	public boolean CODE128_auto(String str) {
		Code128 code128 = new Code128(str);
		byte[] buf = code128.encode_data;
		if (buf == null)
			return false;
		return CODE128_base(buf, 0, buf.length);
	}
	/*
	 * Code128�Զ�����У���
	 */
	public boolean code128_auto_drawOut(ALIGN align,ESC.BAR_UNIT unit,int height,ESC.BAR_TEXT_POS pos,ESC.BAR_TEXT_SIZE size,String str)
	{
		Code128 code128 = new Code128(str);		
		byte []buf = code128.encode_data;
		if (buf == null)
			return false;
		if (!setAlign(align))
			return false;
		setUnit(unit);
		if (!set1DHeight(height))
			return false;
		setTextPosition(pos);
		setTextSize(size);
		return CODE128_base(buf,0,buf.length);
	}
	
	 public boolean code128_auto_printOut(ALIGN align, ESC.BAR_UNIT unit, int height, ESC.BAR_TEXT_POS pos, ESC.BAR_TEXT_SIZE size, String str)
     {
         if (!code128_auto_drawOut(align,unit,height,pos,size,str))
             return false;
         enter();
         if (!setAlign(ALIGN.LEFT))
             return false;
         return true;
     }
	 
	/// <summary>
     /// ѡ��2D��������
     /// </summary>
     /// <param name="type"></param>
     /// <returns></returns>
	public boolean barcode2D_SetType(ESC_BAR_2D type) {
		_cmd[0] = 0x1D;
		_cmd[1] = 0x5A;
		_cmd[2] = (byte) type.value();
		return _port.write(_cmd, 0, 3);
	}
     /// <summary>
     /// ����2D����
     /// </summary>
     /// <param name="m"></param>
     /// <param name="n"></param>
     /// <param name="k"></param>
     /// <param name="text"></param>
     /// <returns></returns>
     public boolean barcode2D_DrawOut(byte m, byte n, byte k, String text)
     {
        byte[] data = null;
		try {
			data = text.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}//Encoding.Default.GetByteCount(text);
         if (data.length == 0)
         {
             return false;//data is empty
         }
         _cmd[0] = 0x1B;  
         _cmd[1] = 0x5A;
         _cmd[2] = m; 
         _cmd[3] = n; 
         _cmd[4] = (byte)k;
         _cmd[5] = (byte)data.length; 
         _cmd[6] = (byte)(data.length >> 8);
         _port.write(_cmd,0, 7);
         return _port.write(text);
     }
     /// <summary>
     /// ����QRCode
     /// </summary>
     /// <param name="version">�汾��,��version = 0���Զ�����汾�ţ�ͨ���汾�ſ��Կ���QRCode�ߴ��С</param>
     /// <param name="ecc">������0~3,������Խ��Խ����ʶ�𣬵��ǿ��������ݼ�С</param>
     /// <param name="text">��������</param>
     /// <returns></returns>
     public boolean barcode2D_QRCode(byte version, byte ecc, String text)
     {
         barcode2D_SetType(ESC_BAR_2D.QRCODE);
         return barcode2D_DrawOut(version, ecc, (byte)0, text);
     }
     /// <summary>
     ///  ����QRCode,���ݲ���
     /// </summary>
     /// <param name="x">x</param>
     /// <param name="y">y</param>
     /// <param name="unit">���������Ԫ���</param>
     /// <param name="version">�汾��,��version = 0���Զ�����汾�ţ�ͨ���汾�ſ��Կ���QRCode�ߴ��С</param>
     /// <param name="ecc">������0~3,������Խ��Խ����ʶ�𣬵��ǿ��������ݼ�С</param>
     /// <param name="text">��������</param>
     /// <returns></returns>
     public boolean barcode2D_QRCode(int x, int y, ESC.BAR_UNIT unit,int version, int ecc, String text)
     {
         this.setXY(x, y);
         this.setUnit(unit);
         barcode2D_SetType(ESC_BAR_2D.QRCODE);
         return barcode2D_DrawOut((byte)version, (byte)ecc, (byte)0, text);
     }
     /// <summary>
     /// ����PDF417
     /// </summary>
     /// <param name="columnNumber">ÿ�������ַ���Ŀ</param>
     /// <param name="ecc">��������,0~8,����Խ�ߣ�����������Խ�࣬��������Խǿ������ҲԽ��</param>
     /// <param name="hwratio">�������</param>
     /// <param name="text">��������</param>
     /// <returns></returns>
     public boolean barcode2D_PDF417(byte columnNumber, byte ecc, byte hwratio, String text)
     {
         barcode2D_SetType(ESC_BAR_2D.PDF417);
         return barcode2D_DrawOut(columnNumber, ecc, hwratio, text);
     }
     /// <summary>
     /// ����DATAMatrix
     /// </summary>
     /// <param name="text">��������</param>
     /// <returns></returns>
     public boolean barcode2D_DATAMatrix(String text)
     {
         barcode2D_SetType(ESC_BAR_2D.DATAMATIX);
         return barcode2D_DrawOut((byte)0, (byte)0, (byte)0, text);
     }
     /// <summary>
     /// ����GRIDMatrix
     /// </summary>
     /// <param name="ecc">������</param>
     /// <param name="text">��������</param>
     /// <returns></returns>
     public boolean barcode2D_GRIDMatrix(byte ecc, String text)
     {
         barcode2D_SetType(ESC_BAR_2D.GRIDMATIX);
         return barcode2D_DrawOut(ecc, (byte)0, (byte)0, text);
     }
}
