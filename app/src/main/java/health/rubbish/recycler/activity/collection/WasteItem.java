package health.rubbish.recycler.activity.collection;

import java.io.Serializable;

/**
 * Created by xiayanlei on 2016/11/23.
 */
public class WasteItem implements Serializable {
    public String wasteId;//单号
    public String canId;//垃圾桶号
    public String collectorId;//收集人id
    public String collectorNam;//收集人名称
    public String dtm;//收集时间
    public String state;//垃圾状态
    public String roomNo;//科室主键
    public String roomNam;//科室名称
    public String nurseId;//护士id
    public String nurseNam;//护士名称
    public String typNo;//类型主键
    public String typNam;//类型名称
    public String weight;//重量
    public String boxId;//箱号
    public String carId;//车号

    public String getStateNam() {//todo 根据state的状态值，返回对应的名称
        return state;
    }
}
