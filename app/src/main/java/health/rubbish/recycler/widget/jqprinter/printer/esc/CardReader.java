package health.rubbish.recycler.widget.jqprinter.printer.esc;

import health.rubbish.recycler.widget.jqprinter.printer.PrinterParam;

import android.util.Log;

public class CardReader extends BaseESC
{
	public CardReader(PrinterParam param) {
		super(param);
	}
	public enum CARD_ERROR
    {
        ERROR_OP_CMD(0xF3),//��֧�ֵĲ�������
        ERROR_ACK_DATA_ZERO(0xF4),
        ERROR_EXCUEE_FRAME(0xF5),
        ERROR_VERIFY_PASSWORD(0xF6),
        ERROR_OP_ERROR(0xF7),
        ERROR_NO_CARD(0xF8),
        ERROR_OP_READ(0xF9),
        ERROR_OP_WRITE(0xFA),
        ERROR_PARAM_LENGTH_MIN(0xFB),
        ERROR_PARAM_LENGTH_MAX(0xFC),
        ERROR_CHANNEL(0xFD), //ͨ������
        ERROR_CARD_POWER_OFF(0xFE), //��δ�ϵ�
        ERROR_CARD_TYPE(0xFF);//�����ʹ���
        private int _value;
		private CARD_ERROR(int mode)
		{
			_value = mode;
		}		
		public int value()
		{
			return _value;
		}
    }
	
	public enum CARD_CMD
    {
        SET_CARD_TYPE(0xF3),
        RESET(0xF6),
        WRITE_READ(0xF7),
        CHECK_CARD(0xF8);
        private int _value;
		private CARD_CMD(int mode)
		{
			_value = mode;
		}		
		public int value()
		{
			return _value;
		}
    }
	
	public enum CARD_TYPE_SUB_IC 
    {
        CDT_CPU_T0,
        CDT_CPU_T1
    }
	
	 private byte[] req = new byte[256 + 16];
	 public byte[] rsp = new byte[256 + 16];
	 public int rsp_len;

    /// ����ģ���ϵ�
    /// 1)���ϵ粻�ܴ�ָ����ƣ��󿨲���ʱ�Զ��ϵ磬�γ�ʱ�Զ��µ硣
    public boolean powerOn()
    {
        int timeout = 3000;
        _port.flushReadBuffer();
        
        _cmd[0] = 0x1B;
        _cmd[1] = 0x17;
        if (!_port.write(_cmd, 0, 2))
        {
            return false;
        }
        if (!_port.read(rsp, 2, timeout))
        {
            return false;
        }
        if (rsp[0] == (byte)0xAA && rsp[1] == (byte)0xFE)
        {
            return true;
        }
        Log.e("JQ","error "+ (rsp[0])+" "+ (rsp[1]));
        return false;
    } 
    /*
     * ����ģ���µ�
     * 1)���ϵ粻�ܴ�ָ����ƣ��󿨲���ʱ�Զ��ϵ磬�γ�ʱ�Զ��µ硣
     */
    public boolean powerOff()
    {
    	int timeout = 3000;
    	_port.flushReadBuffer();
        
    	_cmd[0] = 0x1B;
        _cmd[1] = 0x18;
        if (!_port.write(_cmd, 0, 2))
            return false;
        if (!_port.read(rsp, 2, timeout))
            return false;
        if ((rsp[0]&0xFF) == 0x55 && (rsp[1]&0xFF) == 0xFE)
        {
            return true;
        }
    	return false;
    }

    private boolean send_and_wait(CARD_CMD cmd_flag,byte []req,int req_len,String from)
    {
        rsp_len = 0;
        int len = 1 + req_len;
        _port.flushReadBuffer();
        
        _cmd[0] = 0x1B;
        _cmd[1] = 0x1A;
        _cmd[2] = (byte)len;
        _cmd[3] = (byte)(len >> 8);
        _cmd[4] = (byte)cmd_flag.value();
        
        if (!_port.write(_cmd, 0, 5))
        {
        	Log.e("JQ","cmd write error");
            return false;
        }
        if (req_len > 0)
        {
            if (!_port.write(req, 0, req_len))
            {   
            	Log.e("JQ","req write error");	
                return false;             
            }
        }
        
        if (!_port.read(rsp, 1, 3000)) //read cmd_flag
        {
        	Log.e("JQ","read cmd_flag error");	
            return false;
        }
        if (rsp[0] != (byte)cmd_flag.value())
        {
            if (rsp[0] == (byte)0xFF)
            {
            	Log.e("JQ","�������� " + rsp[0]);//MessageBox.Show();
            	return false;
            }
            Log.e("JQ","cmd_flag error "+rsp[0] +" from:" +from);	
            return false;
        }
        
          if (!_port.read(rsp, 1, 3000)) //read success flag
            return false;
        if (rsp[0] == 0x00) //����ɹ����޷�������
            return true;
        else if (rsp[0] == 0x01) //����ɹ����з�������
        {
            if (!_port.read(rsp, 2, 3000)) //read data len
                return false;
            rsp_len = rsp[0] | (rsp[1] << 8);
            if (rsp_len > rsp.length)
            {
            	Log.e("JQ","rsp ������̫С <" + rsp_len);//MessageBox.Show("rsp ������̫С <" + rsp_len.ToString());                
                return false;
            }
            return _port.read(rsp, rsp_len, 3000); //read data
        }
        else if ((rsp[0]&0xFF) == 0xFF)//����ʧ��
        {
            if (!_port.read(rsp, 1, 3000)) //read success flag
                return false; 
            return false;
        }
        return false;          
    }

    /// �������Ƿ��п�����
    public boolean checkBigCardInsert(byte[] state)
    {
        req[0] = 0;//��ʱֻ֧��channel 0
        if (!send_and_wait(CARD_CMD.CHECK_CARD, req, 1,"check card in"))
        {
        	Log.e("JQ","send and wait error");
            return false;
        }
        if (rsp_len != 1)
            return false;
        state[0] = rsp[0];
        return true;
    }

    /// ���ÿ�����
    /// 1)channel 1,2ֻ֧��CPU��
   public boolean setCardType(int channel,ESC.CARD_TYPE_MAIN card_type_main,int card_type_sub)
    {
        req[0] = (byte)channel;
        req[1] = (byte)card_type_main.value();
        req[2] = (byte)card_type_sub;
        if (!send_and_wait(CARD_CMD.SET_CARD_TYPE, req, 3,"set card type"))
            return false;
         return true;
    }

    /// CPU����λ
    public boolean cardCPU_Reset(int channel)
    {
        req[0] = (byte)channel;
        if (!send_and_wait(CARD_CMD.RESET, req, 1,"reset"))
            return false;
        return true;
    }

    /// ��CPU��������
    /// 1)�����������Ҫ����ADPU��
    /// 2)���صĽ����read��
	public boolean cardCPU_Read(int channel, char[] ADPU, int adpu_len,
			String from) {
		req[0] = (byte) channel;
		req[1] = 0;// read
		for (int i = 0; i < adpu_len; i++) {
			req[2 + i] = (byte) ADPU[i];
		}
		if (!send_and_wait(CARD_CMD.WRITE_READ, req, adpu_len + 2, from))
			return false;
		if (rsp_len == 2) {
			if ((rsp[0] & 0xFF) == 0x99 && rsp[1] == 0x61)
				return false;
		}
		return true;
	}


    /// ��CPU��д����
    /// 1)��������������Ҫ����ADPU��
    /// 2)���صĽ����ret��
    /// 3)Select ������ô˷���
	public boolean cardCPU_Write(int channel, char[] ADPU, int adpu_len,
			String from) {
		req[0] = (byte) channel;
		req[1] = 1;// write
		for (int i = 0; i < adpu_len; i++) {
			req[2 + i] = (byte) ADPU[i];
		}
		if (!send_and_wait(CARD_CMD.WRITE_READ, req, adpu_len + 2, from))
			return false;
		if (rsp_len == 0) {
			return false;
		} else if (rsp_len == 2) {
			if ((rsp[0] & 0xFF) == 0x99 && rsp[1] == 0x61)
				return false;
		}
		return true;
	}
}
