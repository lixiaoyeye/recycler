package health.rubbish.recycler.util;

import android.os.Environment;
import java.io.File;
import health.rubbish.recycler.constant.Constant;
/**
 * Created by Lenovo on 2016/11/20.
 */

public class FileUtil {

    public File getDiskCacheDir() {
        File cacheDir = new File(getDownloadDir()+ File.separator + Constant.File.CACHEDIR);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        return  cacheDir;
    }

    public File getDiskCacheDir( String uniqueName) {
        File cacheDir = new File(getDiskCacheDir() + File.separator + uniqueName);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        return  cacheDir;
    }

    public File getDownloadDir( ) {
        String cachePath ="";
        File cacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = Environment.getExternalStorageDirectory().getPath();
            cacheDir = new File(cachePath + File.separator + Constant.File.DOWNLOADDIR);
        } else {
            cacheDir = new File(Constant.File.DOWNLOADDIR);
        }

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        return  cacheDir;
    }

}
