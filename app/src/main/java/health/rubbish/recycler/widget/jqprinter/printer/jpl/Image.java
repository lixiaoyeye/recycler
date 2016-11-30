package health.rubbish.recycler.widget.jqprinter.printer.jpl;

import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Image extends BaseJPL
{
	public Image(PrinterParam param) {
		super(param);
	}
	// <summary>
    /// ��ת�Ƕ�
    /// </summary>
    public enum IMAGE_ROTATE
    {
        x0,
        x90,
        x180,
        x270 
    } 
	
	public boolean drawOut(int x, int y, int width, int height, char[] data) {
		if (width < 0 || height < 0)
			return false;
		int HeightWriteUnit = 10;
		int WidthByte = ((width - 1) / 8 + 1);
		int HeightWrited = 0;
		int HeightLeft = height;

		_cmd[0] = 0x1A;
		_cmd[1] = 0x21;
		_cmd[2] = 0x00;
		_cmd[3] = (byte) x;
		_cmd[4] = (byte) (x >> 8);
		_cmd[7] = (byte) width;
		_cmd[8] = (byte) (width >> 8);
		while (true) {
			if (HeightLeft <= HeightWriteUnit) {
				_cmd[5] = (byte) y;
				_cmd[6] = (byte) (y >> 8);
				_cmd[9] = (byte) HeightLeft;
				_cmd[10] = (byte) (HeightLeft >> 8);
				_port.write(_cmd, 0, 11);
				_port.write(data, HeightWrited * WidthByte, HeightLeft
						* WidthByte, 1);
				return true;
			} else {
				_cmd[5] = (byte) y;
				_cmd[6] = (byte) (y >> 8);
				_cmd[9] = (byte) HeightWriteUnit;
				_cmd[10] = (byte) (HeightWriteUnit >> 8);
				_port.write(_cmd, 0, 11);
				_port.write(data, HeightWrited * WidthByte, HeightWriteUnit
						* WidthByte, 1);
				y += HeightWriteUnit;
				HeightWrited += HeightWriteUnit;
				HeightLeft -= HeightWriteUnit;
			}
		}
	}
	
	public boolean drawOut(int x, int y, int width, int height, byte[] data,
			boolean Reverse, IMAGE_ROTATE Rotate, int EnlargeX, int EnlargeY) {
		if (width < 0 || height < 0)
			return false;
		int WidthByte = ((width - 1) / 8 + 1);
		int dataSize  = WidthByte*height;
		if (dataSize != data.length)
			return false;

		short ShowType = 0;
		if (Reverse)
			ShowType |= 0x0001;
		ShowType |= (Rotate.ordinal() << 1) & 0x0006;
		ShowType |= (EnlargeX << 8) & 0x0F00;
		ShowType |= (EnlargeY << 14) & 0xF000;

		int HeightWriteUnit = 10;
		int HeightWrited = 0;
		int HeightLeft = height;
		
		_cmd[0] = 0x1A;
        _cmd[1] = 0x21;
        _cmd[2] = 0x01;
        _cmd[7] = (byte)(width);
        _cmd[8] = (byte)(width >> 8);
        _cmd[11] = (byte)(ShowType);
        _cmd[12] = (byte)(ShowType >> 8); 
        while (true)
        {
            _cmd[3] = (byte)(x);
            _cmd[4] = (byte)(x >> 8);
            _cmd[5] = (byte)(y);
            _cmd[6] = (byte)(y >> 8);

            if (HeightLeft > HeightWriteUnit)
            {
                _cmd[9] = (byte)(HeightWriteUnit);
                _cmd[10] = (byte)(HeightWriteUnit >> 8);
                _port.write(_cmd, 0, 13);

                _port.write(data, HeightWrited * WidthByte, HeightWriteUnit * WidthByte);
                switch (Rotate)
                {
                    case x0:
                        y += HeightWriteUnit * ((int)EnlargeX + 1);
                        break;
                    case x90:
                        x -= HeightWriteUnit * ((int)EnlargeY + 1);
                        break;
                    case x180:
                        y -= HeightWriteUnit * ((int)EnlargeX + 1);
                        break;
                    case x270:
                        x += HeightWriteUnit * ((int)EnlargeY + 1);
                        break;
                }
                HeightWrited += HeightWriteUnit;
                HeightLeft -= HeightWriteUnit;
            }
            else
            {
                _cmd[9] = (byte)(HeightLeft);
                _cmd[10] = (byte)(HeightLeft >> 8);
                _port.write(_cmd,0,13);
                return _port.write(data, HeightWrited * WidthByte, HeightLeft * WidthByte);
            }
        }
	}
	  
	private boolean PixelIsBlack(int color, int gray_threshold) {
		int red = ((color & 0x00FF0000) >> 16);
		int green = ((color & 0x0000FF00) >> 8);
		int blue = color & 0x000000FF;
		int grey = (int) ((float) red * 0.299 + (float) green * 0.587 + (float) blue * 0.114);
		return (grey < gray_threshold);
	}
	  
	public byte[] CovertImageHorizontal(Bitmap bitmap, int gray_threshold)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int BytesPerLine = (width - 1) / 8 + 1;

        byte[] data = new byte[BytesPerLine * height];
        for (int i=0;i<data.length;i++)
        {
        	data[i] = 0;
        }
        int index = 0;

        //����λͼ����ĻҶ�ֵ��ȷ����ӡλͼ��Ӧ�ĵ�ĺڰ�ɫ
        int x = 0, y = 0;                                                                          //λͼ��x��y����ֵ��
        for (int i = 0; i < height; i++)                                        //ÿ���ж�һ�����У���Ҫ�ж�BmpHeight��
        {
            for (int j = 0; j < BytesPerLine; j++)                                                    //ÿ����ҪLengthRow�ֽڴ�����ݣ�
            {
                for (int k = 0; k < 8; k++)                                                        //ÿ���ֽڴ��8���㣬��1��1λ��
                {
                    x = (j << 3) + k;                                                              //x����Ϊ�Ѽ����ֽڡ�8+��ǰ�ֽڵ�λk��
                    y = i;                                                                         //��ǰ�У�
                    if (x >= width)                                             //���λͼ��ǰ���ص��y�������ʵ��λͼ���(λͼʵ�ʿ�ȿ��ܲ�Ϊ8��������)�����Ըõ���ɫ�����жϣ�
                    {
                        continue;
                    }
                    else
                    {
                        if (PixelIsBlack(bitmap.getPixel(x, y), gray_threshold))
                        {
                            data[index] |= (byte)(0x01 << k);
                        }
                    }
                }
                index++;                                                       //һ��������8���ж���Ϻ�λͼ����ʵ�ʳ��ȼ�1��
            }
            x = 0;
        }
        return data;
    }
	
	public boolean drawOut(int x, int y, Resources res, int id,
			IMAGE_ROTATE rotate) {
		Bitmap bitmap = BitmapFactory.decodeResource(res, id);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width > _param.pageWidth || height > _param.pageHeight)
			return false;
		return drawOut(x, y, width, height, CovertImageHorizontal(bitmap, 128),
				false, rotate, 0, 0);
	}
}
