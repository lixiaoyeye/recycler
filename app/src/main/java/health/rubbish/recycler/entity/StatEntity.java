package health.rubbish.recycler.entity;

import android.text.TextUtils;

/**
 * Created by Lenovo on 2016/11/25.
 */

public class StatEntity {

    private String collectnum;//收集垃圾数
    private String collectratio;//收集垃圾数环比:+10%

    private String untransfernum;//未装箱数
    private String untransferratio;//未装箱数环比

    private String dumpcartnum;//垃圾车次
    private String dumpcartratio;//垃圾车次环比

    public String getCollectnum() {
        return TextUtils.isEmpty(collectnum) ? "0" : collectnum;
    }

    public void setCollectnum(String collectnum) {
        this.collectnum = collectnum;
    }

    public String getCollectratio() {
        return TextUtils.isEmpty(collectratio) ? "0" : collectratio;
    }

    public void setCollectratio(String collectratio) {
        this.collectratio = collectratio;
    }

    public String getUntransfernum() {
        return TextUtils.isEmpty(untransfernum) ? "0" : untransfernum;
    }

    public void setUntransfernum(String untransfernum) {
        this.untransfernum = untransfernum;
    }

    public String getUntransferratio() {
        return TextUtils.isEmpty(untransferratio) ? "0" : untransferratio;
    }

    public void setUntransferratio(String untransferratio) {
        this.untransferratio = untransferratio;
    }

    public String getDumpcartnum() {
        return TextUtils.isEmpty(dumpcartnum) ? "0" : dumpcartnum;
    }

    public void setDumpcartnum(String dumpcartnum) {
        this.dumpcartnum = dumpcartnum;
    }

    public String getDumpcartratio() {
        return TextUtils.isEmpty(dumpcartratio) ? "0" : dumpcartratio;
    }

    public void setDumpcartratio(String dumpcartratio) {
        this.dumpcartratio = dumpcartratio;
    }
}
