package health.rubbish.recycler.widget.jqprinter.printer.jpl;

import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;

public class Page  extends BaseJPL
{
	public static enum PAGE_ROTATE
	{
		x0,
		x90,
	}
	/*
	 * ���캯��
	 */
	public Page(PrinterParam param) {
		super(param);
	}

	/*
	 * ��ӡҳ�濪ʼ ʹ�ô�ӡ��ȱʡ�������ƴ�ӡ��ҳ�� ȱʡҳ���576dots(72mm),��640dots��80mm��
	 */
	public boolean start() {
		_param.pageWidth = 576;
		_param.pageHeight = 640;
		_cmd[0] = 0x1A;
		_cmd[1] = 0x5B;
		_cmd[2] = 0x00;		
		return _port.write(_cmd,0,3);
	}

	/*
	 * ��ӡҳ�濪ʼ
	 */
	public boolean start(int originX, int originY, int pageWidth, int pageHeight, PAGE_ROTATE rotate) 
	{
		if (originX < 0) originX = 0;
		if (originY < 0) originY = 0;
		if (rotate == PAGE_ROTATE.x0)
		{
			if (originX > 575) originX = 0;
			if (originX + pageWidth > 576) pageWidth = 576 - originX;
		}
		else
		{
			if (originY > 575) originY = 0;
			if (originY + pageHeight > 576) pageHeight = 576 - originY;
		}
		
		_param.pageWidth = pageWidth;
		_param.pageHeight = pageHeight;
		
		_cmd[0] = 0x1A;
		_cmd[1] = 0x5B;
		_cmd[2] = 0x01;
		_cmd[3] = (byte)originX;
		_cmd[4] = (byte)(originX>>8);
		_cmd[5] = (byte)originY;
		_cmd[6] = (byte)(originY>>8);
		_cmd[7] = (byte)pageWidth;
		_cmd[8] = (byte)(pageWidth>>8);
		_cmd[9] = (byte)pageHeight;
		_cmd[10] = (byte)(pageHeight>>8);
		_cmd[11] = (byte)rotate.ordinal();

		return _port.write(_cmd,0,12);
	}

	/*
	 * ���ƴ�ӡҳ�����
	 */
	public boolean end() {
		_cmd[0] = 0x1A;
		_cmd[1] = 0x5D;
		_cmd[2] = 0x00;
		return _port.write(_cmd, 0, 3);
	}

	/*
	 * ��ӡҳ������ ֮ǰ����ҳ�洦��ֻ�ǰѴ�ӡ���󻭵��ڴ��У�����Ҫͨ��������������ݴ�ӡ����
	 */
	public boolean print() {
		_cmd[0] = 0x1A;
		_cmd[1] = 0x4F;
		_cmd[2] = 0x00;
		return _port.write(_cmd, 0, 3);
	}
	
	/*
	 * ��ǰҳ���ظ���ӡ����
	 */
	public boolean print(int count) {
		_cmd[0] = 0x1A;
		_cmd[1] = 0x4F;
		_cmd[2] = 0x01;
		_cmd[3] = (byte) count;
		return _port.write(_cmd, 0, 4);
	}
	
}
