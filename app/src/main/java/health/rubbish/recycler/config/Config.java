package health.rubbish.recycler.config;

import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.R;
import health.rubbish.recycler.entity.Module;

/**
 * Created by xiayanlei on 2016/11/13.
 * 本地信息配置库
 */
public class Config {

    public static final String MODULE_NAME = "module_name";
    public static final String MODULE_IMAGE = "module_image";

    public static List<Module> getMainModules(int authority) {
        List<Module> modules = new ArrayList<>();

        Module collect_module = new Module();
        collect_module.name = R.string.rubbish_collection;
        collect_module.icon = R.drawable.module_collect;


        Module storage_module = new Module();
        storage_module.name = R.string.rubbish_storage;
        storage_module.icon = R.drawable.module_transfer;

        Module incar_module = new Module();
        incar_module.name = R.string.rubbish_incar;
        incar_module.icon = R.drawable.module_entruck;

        Module sync_module = new Module();
        sync_module.name = R.string.data_sync;
        sync_module.icon = R.drawable.module_asyc;

        Module query_module = new Module();
        query_module.name = R.string.data_query;
        query_module.icon = R.drawable.module_search;


        Module stat_module = new Module();
        stat_module.name = R.string.data_stat;
        stat_module.icon = R.drawable.module_stat;

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
