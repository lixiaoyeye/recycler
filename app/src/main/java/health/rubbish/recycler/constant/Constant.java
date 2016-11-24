package health.rubbish.recycler.constant;

/**
 * Created by Lenovo on 2016/11/20.
 */

public class Constant {
    public static class File{
        public static final String DOWNLOADDIR = "recycler";
        public static final String CACHEDIR = "cache";
    }

    public static class Status{
        public static final String NEWCOLLECT = "0"; //新收集
        public static final String UPLOAD = "1";  //已上传
        public static final String DOWNLOAD = "2"; //已下载
        public static final String TRASFERING = "30"; //转储中
        public static final String TRASFER = "3"; //已转储
        public static final String ENTRUCKER = "4"; //已装车
    }
}
