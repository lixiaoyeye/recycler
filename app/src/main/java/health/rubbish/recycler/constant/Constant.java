package health.rubbish.recycler.constant;

/**
 * Created by Lenovo on 2016/11/20.
 */

public class Constant {
    public static final int QR_CODE = 200;
    public static class File {
        public static final String DOWNLOADDIR = "recycler";
        public static final String CACHEDIR = "cache";
    }

    public static class Status {
        public static final String NEWCOLLECT = "0"; //新收集
        public static final String UPLOAD = "1";  //已上传
        public static final String DOWNLOAD = "2"; //已下载
        public static final String TRASFERING = "30"; //转储中
        public static final String TRASFER = "3"; //已转储
        public static final String ENTRUCKERING = "40"; //转储中
        public static final String ENTRUCKER = "4"; //已装车
    }

    public static class Reference {
        public static final String REFER_TYPE = "referType";
        public static final String REFER_TITLE = "referTitle";//选择页面的标题
        public static final int DEPART = 0;//科室选择
        public static final int NURSE = 1;//护士选择，护士跟科室有关联关系
        public static final int CATEGORY = 2;//垃圾类型
        public static final int AREA = 3;//院区选择
        public static final String REFER_KEY = "referKey";//选择的主键
        public static final String REFER_VALUE = "referValue";//选择的名称
    }
}
