package health.rubbish.recycler.entity;

import java.io.Serializable;

import health.rubbish.recycler.constant.Constant;

/**
 * Created by Lenovo on 2016/11/20.
 */
public class TrashItem implements Serializable {
    public String  trashid;
    public String date;
    public String trashcode;            // 垃圾袋编号
    public String status;            // 垃圾袋状态
    public String trashcancode;            // 垃圾桶逻辑编号(RFID编码)
    public String colletime;            // 垃圾收集时间
    public String categorycode;            // 垃圾分类编号
    public String categoryname;            // 垃圾分类
    public String weight;            // 垃圾重量（克）
    public String collectorid;            //收集人id
    public String collector;            //收集人姓名
    public String collectphone;            //收集人电话
    public String departareacode;            //院区编码
    public String departarea;            // 所属院区
    public String departcode;            // 科室编码
    public String departname;            // 科室名称
    public String nurseid;            //值班护士账号
    public String nurse;            // 值班护士名
    public String nursephone;            // 值班护士电话

    public String trashstation;            //垃圾站名
    public String dustybincode;            //垃圾箱逻辑编号(RFID编码)
    public String transfertime;            // 垃圾转储时间
    public String transferid;            //转储人id
    public String transfer;            //转储人姓名
    public String transferphone;            //转储人电话


    public String platnumber;            //车牌号
    public String entrucktime;            //垃圾装车时间
    public String entruckerid;            //装车人id
    public String entrucker;            //装车人姓名
    public String entruckerphone;            //装车人电话
    public String driver;            //司机姓名
    public String driverphone;            //司机电话

    public String getStatusNam() {
        if (Constant.Status.NEWCOLLECT.equals(status))
            return "新收集";
        else if (Constant.Status.UPLOAD.equals(status))
            return "已上传";
        else if (Constant.Status.DOWNLOAD.equals(status))
            return "已下载";
        else if (Constant.Status.TRASFER.equals(status))
            return "已转储";
        else if (Constant.Status.ENTRUCKER.equals(status))
            return "已装车";
        else if (Constant.Status.TRASFERING.equals(status))
            return "转储中";
        else if (Constant.Status.ENTRUCKERING.equals(status))
            return "装车中";
        return "";
    }

}
