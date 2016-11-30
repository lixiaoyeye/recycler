package health.rubbish.recycler.widget.jqprinter.printer.cpcl;

import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;


public class Page extends BaseCPCL {
	public Page(PrinterParam param) {
		super(param);
	}
	
	/*
	 * ҳ�濪ʼ
	 * pageHeight:��ӡ��ǩ�����߶ȵ�����1��=0.125mm����
	 * printCount: ��ӡ��ǩ����. ���Ϊ 1024
	 */
	public boolean start(int pageHeight, int Pagewidth, int printCount )
	{
		String cmd = "! 0 200 200 " + pageHeight + " " + printCount;
		portSendCmd(cmd);
		cmd = "PAGE-WIDTH " + Pagewidth;
		portSendCmd(cmd);
		return true;
	}
	
	/*
	 * ����ҳ����
	 * width:ҳ���ȵ���
	 */
	public boolean setPageWidth(int width)
	{
		String cmd = "PAGE-WIDTH " + width;
		return portSendCmd(cmd);
	}
	
	/*
	 * ��ӡҳ��
	 */
	public boolean print()
	{
		String cmd = "PRINT";
		return portSendCmd(cmd);
	}
	
	/*
	 * ��ֽ����λ��
	 * �����<BAR-SENSE>/<GAP-SENSE>ָ�����
	 * �������ݵ�ע�ͣ����ᱻ��ӡ����
	 * ֻ����<!> Commands��PRINTָ��֮�����Ч��
	 */
	public boolean feed()
	{
		String cmd = "FORM";
		return portSendCmd(cmd);
	}
		
	/*
	 * ע��
	 */
	public boolean notes(String text)
	{
		String cmd = ";" + text;
		return portSendCmd(cmd);
	}
	
	public boolean bar_sense_right()
	{
		String cmd = "BAR-SENSE";
		return portSendCmd(cmd);
	}
	
	public boolean bar_sense_left()
	{
		String cmd = "BAR-SENSE LEFT";
		return portSendCmd(cmd);
	}
	
	public boolean gap_sense()
	{
		String cmd = "GAP-SENSE";
		return portSendCmd(cmd);
	}
	
	public boolean left()
	{
		String cmd = "LEFT";
		return portSendCmd(cmd);
	}
	
	public boolean center()
	{
		String cmd = "CENTER";
		return portSendCmd(cmd);
	}
	
	public boolean right()
	{
		String cmd = "RIGHT";
		return portSendCmd(cmd);
	}
	
	public boolean end()
	{
		String cmd = "END";
		return portSendCmd(cmd);
	}
	
	public boolean abort()
	{
		String cmd = "ABORT";
		return portSendCmd(cmd);
	}
	
	public boolean contrast(int level)
	{
		String cmd = "CONTRAST " + level ;
		return portSendCmd(cmd);
	}
	
	public boolean speed(int speed_level)
	{
		String cmd = "CONTRAST " + speed_level ;
		return portSendCmd(cmd);
	}
	
	public boolean prefeed(int length)
	{
		String cmd = "PREFEED " + length ;
		return portSendCmd(cmd);
	}
	
	public boolean postfeed(int length)
	{
		String cmd = "POSTFEED " + length ;
		return portSendCmd(cmd);
	}
}
