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

    public static List<HashMap<String, Integer>> getMainModules(int authority) {
        List<HashMap<String, Integer>> modules = new ArrayList<>();

        HashMap<String, Integer> collect_module = new HashMap<>();
        collect_module.put(MODULE_NAME, R.string.rubbish_collection);
        collect_module.put(MODULE_NAME, R.string.rubbish_collection);
        collect_module.put(MODULE_IMAGE, R.drawable.module_default);


        HashMap<String, Integer> storage_module = new HashMap<>();
        storage_module.put(MODULE_NAME, R.string.rubbish_storage);
        storage_module.put(MODULE_IMAGE, R.drawable.module_default);

        HashMap<String, Integer> incar_module = new HashMap<>();
        incar_module.put(MODULE_NAME, R.string.rubbish_incar);
        incar_module.put(MODULE_IMAGE, R.drawable.module_default);

        HashMap<String, Integer> sync_module = new HashMap<>();
        sync_module.put(MODULE_NAME, R.string.data_sync);
        sync_module.put(MODULE_IMAGE, R.drawable.module_default);

        HashMap<String, Integer> query_module = new HashMap<>();
        query_module.put(MODULE_NAME, R.string.data_query);
        query_module.put(MODULE_IMAGE, R.drawable.module_default);


        HashMap<String, Integer> stat_module = new HashMap<>();
        stat_module.put(MODULE_NAME, R.string.data_stat);
        stat_module.put(MODULE_IMAGE, R.drawable.module_default);

        switch (authority){
            case -1:
                modules.add(collect_module);
                modules.add(storage_module);
                modules.add(incar_module);
                modules.add(sync_module);
                modules.add(query_module);
                modules.add(stat_module);
                break;
            case 0:
                modules.add(collect_module);
                modules.add(storage_module);
                modules.add(incar_module);
                modules.add(sync_module);
                modules.add(query_module);
                modules.add(stat_module);
            case 1:
                modules.add(collect_module);
                modules.add(storage_module);
                modules.add(incar_module);
                modules.add(sync_module);
                modules.add(query_module);
                modules.add(stat_module);
            case 2:
                modules.add(collect_module);
                modules.add(storage_module);
                modules.add(incar_module);
                modules.add(sync_module);
                modules.add(query_module);
                modules.add(stat_module);
        }

        return modules;
    }
}
