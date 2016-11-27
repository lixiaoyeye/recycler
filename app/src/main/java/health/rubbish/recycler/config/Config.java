package health.rubbish.recycler.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import health.rubbish.recycler.R;

/**
 * Created by xiayanlei on 2016/11/13.
 * 本地信息配置库
 */
public class Config {

    public static final String MODULE_NAME = "module_name";
    public static final String MODULE_IMAGE = "module_image";

    public static List<HashMap<String, Integer>> getMainModules() {
        List<HashMap<String, Integer>> modules = new ArrayList<>();

        HashMap<String, Integer> collect_module = new HashMap<>();
        collect_module.put(MODULE_NAME, R.string.rubbish_collection);
        modules.add(collect_module);

        HashMap<String, Integer> storage_module = new HashMap<>();
        storage_module.put(MODULE_NAME, R.string.rubbish_collection);
        modules.add(storage_module);
        HashMap<String, Integer> incar_module = new HashMap<>();
        incar_module.put(MODULE_NAME, R.string.rubbish_incar);
        modules.add(incar_module);
        HashMap<String, Integer> sync_module = new HashMap<>();
        sync_module.put(MODULE_NAME, R.string.data_sync);
        modules.add(sync_module);
        HashMap<String, Integer> query_module = new HashMap<>();
        query_module.put(MODULE_NAME, R.string.data_query);
        modules.add(query_module);
        return modules;
    }
}
