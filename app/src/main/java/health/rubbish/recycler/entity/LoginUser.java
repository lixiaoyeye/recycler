package health.rubbish.recycler.entity;

import java.io.Serializable;

/**
 * Created by Lenovo on 2016/11/20.
 */

public class LoginUser implements Serializable{
    /*username : 用户名，
     positioncode : 岗位编号，
     position : 岗位（收集人员|装箱人员|装车人员），
     sysdate : 系统时间，
     authority:模块权限（0,1,2）*/

    public String userid;
    public String username;
    public String password;
    public String positioncode;
    public String position;
    public String authority;

    public String sysdate ="";//上次登陆时间
}
