package health.rubbish.recycler.widget.jqprinter.printer.jpl;

import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;
import health.rubbish.recycler.widget.jqprinter.printer.jpl.JPL.COLOR;

public class Graphic extends BaseJPL{
	public Graphic(PrinterParam param) {
		super(param);
	}
	/*
	 * ��ҳ���ڻ����߶� 
	 */
	public boolean line(int startX, int startY, int endX, int endY, int width,
			COLOR color) {
		_cmd[0] = 0x1A;
		_cmd[1] = 0x5C;
		_cmd[2] = 0x01;
		_cmd[3] = (byte) startX;
		_cmd[4] = (byte) (startX >> 8);
		_cmd[5] = (byte) startY;
		_cmd[6] = (byte) (startY >> 8);
		_cmd[7] = (byte) endX;
		_cmd[8] = (byte) (endX >> 8);
		_cmd[9] = (byte) endY;
		_cmd[10] = (byte) (endY >> 8);
		_cmd[11] = (byte) width;
		_cmd[12] = (byte) (width >> 8);
		_cmd[13] = (byte) color.ordinal();
		return _port.write(_cmd, 0, 14);
	}
    
	public boolean line(int startX, int startY, int endX, int endY, int width) {
		return line(startX, startY, endX, endY, width, COLOR.Black);
	}
    
	public boolean line(int startX, int startY, int endX, int endY) {
		_cmd[0] = 0x1A;
		_cmd[1] = 0x5C;
		_cmd[2] = 0x00;
		_cmd[3] = (byte) startX;
		_cmd[4] = (byte) (startX >> 8);
		_cmd[5] = (byte) startY;
		_cmd[6] = (byte) (startY >> 8);
		_cmd[7] = (byte) endX;
		_cmd[8] = (byte) (endX >> 8);
		_cmd[9] = (byte) endY;
		_cmd[10] = (byte) (endY >> 8);
		return _port.write(_cmd, 0, 11);
	}

	public boolean rect(int left, int top, int right, int bottom) {
		_cmd[0] = 0x1A;
		_cmd[1] = 0x26;
		_cmd[2] = 0x00;
		_cmd[3] = (byte) left;
		_cmd[4] = (byte) (left >> 8);
		_cmd[5] = (byte) top;
		_cmd[6] = (byte) (top >> 8);
		_cmd[7] = (byte) right;
		_cmd[8] = (byte) (right >> 8);
		_cmd[9] = (byte) bottom;
		_cmd[10] = (byte) (bottom >> 8);
		return _port.write(_cmd, 0, 11);
	}
	
	public boolean rect(int left, int top, int right, int bottom, int width, COLOR color)
	{
		_cmd[0] = 0x1A;
		_cmd[1] = 0x26;
		_cmd[2] = 0x01;
		_cmd[3] = (byte) left;
		_cmd[4] = (byte) (left >> 8);
		_cmd[5] = (byte) top;
		_cmd[6] = (byte) (top >> 8);
		_cmd[7] = (byte) right;
		_cmd[8] = (byte) (right >> 8);
		_cmd[9] = (byte) bottom;
		_cmd[10] = (byte) (bottom >> 8);
		_cmd[11] = (byte) width;
		_cmd[12] = (byte) (width >> 8);
		_cmd[13] = (byte) color.ordinal();
		return _port.write(_cmd, 0, 14);
	}
	
	public boolean rectFill(int left, int top, int right, int bottom, COLOR color)
	{
		_cmd[0] = 0x1A;
		_cmd[1] = 0x2A;
		_cmd[2] = 0x00;
		_cmd[3] = (byte) left;
		_cmd[4] = (byte) (left >> 8);
		_cmd[5] = (byte) top;
		_cmd[6] = (byte) (top >> 8);
		_cmd[7] = (byte) right;
		_cmd[8] = (byte) (right >> 8);
		_cmd[9] = (byte) bottom;
		_cmd[10] = (byte) (bottom >> 8);
		_cmd[11] = (byte) color.ordinal();
		return _port.write(_cmd, 0, 12);		
	}
}
