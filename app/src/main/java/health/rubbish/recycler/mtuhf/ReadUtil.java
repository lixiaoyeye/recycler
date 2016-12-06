package health.rubbish.recycler.mtuhf;

import android.content.Context;
import android.haobin.utils.Tools;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by xiayanlei on 2016/11/25.
 */
public class ReadUtil {

    private ReadListener readListener;

    private String EPCID = "";

    private String key = "00000000";//读卡区域秘钥

    private String startAddressStr = "0";//起始地址

    private int length = 4;//读取长度

    public interface ReadListener {

        void onDataReceived(String data);

        void onFailure();
    }

    public int initUfh(Context context) {
        return Ufh3Data.UHF_Open(context, 57600);
    }

    public ReadUtil setReadListener(ReadListener readListener) {
        this.readListener = readListener;
        return this;
    }

    public void readUfhCard(ReadMode readMode) {
        if (readMode == null)
            return;
        Log.e("222222",""+readMode);
        switch (readMode) {
            case TID:
                readTid();
                break;
            case EPC:
                readEpcOrUsr(1);
                break;
            case USR:
                readEpcOrUsr(3);
                break;
        }
    }

    /**
     * 获取TID信息
     */
    private void readTid() {
        byte[] data2 = new byte[4 * 2];
        data2 = Ufh3Data.UhfGetData.Read6C((byte) 0, new byte[]{}, (byte) 2, (byte) 0x00, (byte) 4, Tools.hexString2Bytes("00000000"));
        if (data2 != null) {

            String data = Tools.bytesToHexString(data2);
            Log.e("33",""+data);
            if (readListener != null)
                readListener.onDataReceived(data);
        } else {
            if (readListener != null)
                readListener.onFailure();
        }
    }

    /**
     * 读取epc或usr卡
     *
     * @param bank
     */
    private void readEpcOrUsr(int bank) {
        byte[] pwd = new byte[4];
        if (!TextUtils.isEmpty(key)) {
            if (key.contains("0x") || key.contains("0X")) {
                key = key.replace("0x", "");
                key = key.replace("0X", "");
            }
            byte[] temp = null;
            try {
                temp = Tools.hexString2Bytes(key);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (temp == null || temp.length != 4) {
                return;
            } else {
                System.arraycopy(temp, 0, pwd, 0, pwd.length);
            }
            if (Tools.isEmpty(startAddressStr)) {
                return;
            }
            if (Ufh3Data.isDeviceOpen()) {
                byte[] epcid = Tools.hexString2Bytes(EPCID);
                byte[] result2 = Ufh3Data.UhfGetData.Read6C((byte) (epcid.length / 2), epcid, (byte) bank,
                        (byte) Byte.valueOf(startAddressStr),
                        (byte) length, pwd);
                if (result2 != null) {
                    String data = Tools.bytesToHexString(result2);
                    if (readListener != null)
                        readListener.onDataReceived(data);
                } else {
                    if (readListener != null)
                        readListener.onFailure();
                }
            }
        }
    }
}
