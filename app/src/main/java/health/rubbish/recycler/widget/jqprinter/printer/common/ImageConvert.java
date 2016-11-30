package health.rubbish.recycler.widget.jqprinter.printer.common;

import java.io.File;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/// <summary>
/// ͼ��ת����
/// </summary>
public class ImageConvert
{
    private int width;
    /// <summary>
    /// ͼ����
    /// </summary>
    public int getWidth()
    {
        return width;
    }
    private int height;
    /// <summary>
    /// ͼ��߶�
    /// </summary>
    public int getHeight()
    {
        return height;
    }
    /// <summary>
    /// ���캯��
    /// </summary>
    public ImageConvert()
    {
        width = 0;
        height = 0;
    }

    /// <summary>
    /// ������ֵ�ж��Ƿ�Ϊ�ڵ㡣
    /// 1)ֵ��RGB�������
    /// </summary>
    /// <param name="dot_color">�����ɫ@param
    /// <param name="gray_threshold">��ֵ@param
    /// <returns>true:Ϊ�ڵ㣻false:Ϊ�ڵ�</returns>
    private boolean PixelIsBlack(int color, int gray_threshold) {
		int red = ((color & 0x00FF0000) >> 16);
		int green = ((color & 0x0000FF00) >> 8);
		int blue = color & 0x000000FF;
		int grey = (int) ((float) red * 0.299 + (float) green * 0.587 + (float) blue * 0.114);
		return (grey < gray_threshold);
	}  

    /// <summary>
    /// ��ֱ��ʽת��ͼ��Ϊ����
    /// </summary>
    /// <param name="bitmap">ͼ��@param
    /// <param name="gray_threshold">��ֵ@param
    /// <returns>null��ʾʧ�ܣ����򷵻���������</returns>
    public byte[] CovertImageVertical(Bitmap bitmap, int gray_threshold,int column_dots)
    {
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        int count = (height - 1) / column_dots + 1;//�ֳɶ��ٸ�СͼƬ
        int column_bytes = column_dots/8;
        byte[] data = new byte[width * column_bytes * count];

        int index = 0;
        //����λͼ����ĻҶ�ֵ��ȷ����ӡλͼ��Ӧ�ĵ�ĺڰ�ɫ
        int sx = 0, sy = 0;                                                                          //λͼ��x��y����ֵ��
        for (int i = 0; i < count; i++)                                                     //8�������й�����С�У�һ��λͼ��ҪLengthColumn��С�У�
        {
            for (int j = 0; j < width; j++)                                     //һС�е�λͼ������BmpWidth�У���ҪBmpWidth���ֽڴ�ţ�
            {
                sx = j;                                                                             //λͼ��ǰ���ص��x����Ϊ����x��
                for (int k = 0; k < 8; k++)                                                        //k��С����8�������еĵ�ǰ�У�
                {
                    sy = (i << 3) + k;                                                              //λͼ��ǰ���ص��y����Ϊ(С������8)+k;
                    if (sy >= height)                                            //���λͼ��ǰ���ص��y�������ʵ��λͼ�߶�(λͼʵ�ʸ߶ȿ��ܲ�Ϊ8��������)�����Ըõ���ɫ�����жϣ�
                    {
                        continue;
                    }
                    else
                    {
                        if (PixelIsBlack(bitmap.getPixel(sx, sy), gray_threshold))                    //�жϵ�ǰ���Ƿ�Ϊ��ɫ��
                        {
                            data[index] |= (byte)(0x01 << (7 - k));      //���Ϊ��ɫ����ǰ������Ӧ�ֽڵĶ�ӦΪ��ֵΪ1��
                        }
                    }
                }
                index++;                                                      //һС�е�һ�����ص��ж���Ϻ�λͼ����ʵ�ʳ��ȼ�1��
            }
        }
        return data;
    }

    /// <summary>
    /// ��ֱ��ʽת��ͼ��Ϊ����
    /// </summary>
    /// <param name="image_path">ͼ��·��@param
    /// <param name="gray_threshold">��ֵ@param
    /// <returns>null��ʾʧ�ܣ����򷵻���������</returns>
    public byte[] CovertImageVertical(String image_path, int gray_threshold)
    {
    	if(new File(image_path).exists())
        {
    		Bitmap bitmap = BitmapFactory.decodeFile(image_path); 
    	    byte[] data = CovertImageVertical(bitmap, gray_threshold,8);
    	    if (data == null)
    	    	Log.e("JQ","ת���ļ�ʧ��");
            return data;
        }
    	else
    	{
    		Log.e("JQ","�ļ�·������:" + image_path);
    		return null;
    	}
    }

    /// <summary>
    /// ˮƽ��ʽת��ͼ��Ϊ����
    /// </summary>
    /// <param name="bitmap">ͼ��@param
    /// <param name="gray_threshold">��ֵ@param
    /// <returns>null��ʾʧ�ܣ����򷵻���������</returns>
    public byte[] CovertImageHorizontal(Bitmap bitmap, int gray_threshold)
    {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        int BytesPerLine = (width - 1) / 8 + 1;

        byte[] data = new byte[BytesPerLine * height];

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

    /// <summary>
    /// ˮƽ��ʽת��ͼ��Ϊ����
    /// </summary>
    /// <param name="image_path">ͼ��·��@param
    /// <param name="gray_threshold">��ֵ@param
    /// <returns>null��ʾʧ�ܣ����򷵻���������</returns>
    public byte[] CovertImageHorizontal(String image_path, int gray_threshold)
    {
    	if(new File(image_path).exists())
        {
    		Bitmap bitmap = BitmapFactory.decodeFile(image_path); 
    		byte[] data = CovertImageHorizontal(bitmap, gray_threshold);
    	    if (data == null)
    	    	Log.e("JQ","ת���ļ�ʧ��");
            return data;
        }
    	else
    	{
    		Log.e("JQ","�ļ�·������:" + image_path);
    		return null;
    	}   
    }
}