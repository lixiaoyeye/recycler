package health.rubbish.recycler.widget.jqprinter.printer.cpcl;

import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;

public class Graphic extends BaseCPCL {
	public Graphic(PrinterParam param) {
		super(param);
	}

	public boolean line(int startX, int startY, int endX, int endY, int width) {
		if (startX > 575)
			startX = 575;
		if (endX > 575)
			endX = 575;

		String CPCLCmd = "LINE " + startX + " " + startY + " " + endX + " "	+ endY + " " + width;
		return portSendCmd(CPCLCmd);
	}
	
	public boolean rectangle(int startX, int startY, int endX, int endY, int width){
		if (startX > 575)
			startX = 575;
		if (endX > 575)
			endX = 575;
		
		String CPCLCmd = "BOX " + startX + " " + startY + " " + endX + " "	+ endY + " " + width;
		return portSendCmd(CPCLCmd);	
	}
	
	public boolean inverse_line(int startX, int startY, int endX, int endY, int width){
		if (startX > 575)
			startX = 575;
		if (endX > 575)
			endX = 575;
		
		String CPCLCmd = "IL " + startX + " " + startY + " " + endX + " " + endY + " " + width;
		return portSendCmd(CPCLCmd);	
	}
}
