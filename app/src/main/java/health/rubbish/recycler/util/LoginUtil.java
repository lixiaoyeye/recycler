package health.rubbish.recycler.util;

import android.app.Activity;
import android.content.SharedPreferences;

import health.rubbish.recycler.base.App;
import health.rubbish.recycler.entity.LoginUser;

/**
 * Created by Lenovo on 2016/11/20.
 */

public class LoginUtil {

    public static void saveLoginUser( LoginUser loginUser) {
        SharedPreferences sharedPreferences = App.ctx.getSharedPreferences("LoginUser", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userid", loginUser.userid);
        editor.putString("username", loginUser.username);
        editor.putString("password", loginUser.password);
        editor.putString("positioncode", loginUser.positioncode);
        editor.putString("position", loginUser.position);
        editor.putString("authority", loginUser.authority);
        editor.commit();
    }

    public static  LoginUser getLoginUser() {
        LoginUser loginUser = new LoginUser();
        SharedPreferences sp = App.ctx.getSharedPreferences("LoginUser",
                Activity.MODE_PRIVATE);
        if (sp == null) {
            return null;
        }
        loginUser.userid = sp.getString("userid", "");
        loginUser.username = sp.getString("username", "");
        loginUser.password = sp.getString("password", "");
        loginUser.positioncode = sp.getString("positioncode", "");
        loginUser.position = sp.getString("position", "");
        loginUser.authority = sp.getString("authority", "");
        return loginUser;
    }


}
