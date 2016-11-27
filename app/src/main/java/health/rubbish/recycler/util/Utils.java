package health.rubbish.recycler.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import health.rubbish.recycler.base.App;
/**
 * Created by Lenovo on 2016/11/20.
 */

public class Utils {

    public static int getWindowWidth(Activity cxt) {
        int width;
        DisplayMetrics metrics = cxt.getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        return width;
    }

    public static String getUserName(){
        SharedPreferences sp = App.ctx.getSharedPreferences("LoginUser", Activity.MODE_PRIVATE);
        return sp.getString("username","");
    }

    public static String getCstName(){
        SharedPreferences sp = App.ctx.getSharedPreferences("LoginUser", Activity.MODE_PRIVATE);
        return sp.getString("deptName","");
    }

    /**
     * @return �û����ź�
     */
    public static String getCstNo(){
        SharedPreferences sp = App.ctx.getSharedPreferences("LoginUser", Activity.MODE_PRIVATE);
        return sp.getString("deptNo","");
    }

    /**
     * @return ��˾��
     */
    public static String getOrgNo(){
        SharedPreferences sp = App.ctx.getSharedPreferences("LoginUser", Activity.MODE_PRIVATE);
        return sp.getString("orgNo","");
    }

    /**
     * @return �û�id
     */
    public static String getUserId() {
        SharedPreferences sp = App.ctx.getSharedPreferences("LoginUser", Activity.MODE_PRIVATE);
        return sp.getString("userid","");
    }
    public static String getUserPwd() {
        SharedPreferences sp = App.ctx.getSharedPreferences("UesrInfo", Activity.MODE_PRIVATE);
        return sp.getString("password","");
    }

    public static long getLongByTimeStr(String begin) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SS");
        String origin = "00:00:00.00";
        Date parse = format.parse(begin);
        return parse.getTime() - format.parse(origin).getTime();
    }

    public static String getEquation(int finalNum, int delta) {
        String equation;
        int abs = Math.abs(delta);
        if (delta >= 0) {
            equation = String.format("%d+%d=%d", finalNum - delta, abs, finalNum);
        } else {
            equation = String.format("%d-%d=%d", finalNum - delta, abs, finalNum);
        }
        return equation;
    }



}
