package health.rubbish.recycler.datebase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import health.rubbish.recycler.base.App;
import health.rubbish.recycler.entity.DepartmentItem;
import health.rubbish.recycler.util.DateUtil;

/**
 * Created by Lenovo on 2016/11/20.
 */

public class DepartmentDao {
    private static DepartmentDao instance = null;
    private static DbHelper dbHelper;

    private DepartmentDao() {
        dbHelper = new DbHelper(App.ctx);
    }

    public synchronized static DepartmentDao getInstance() {
        if (instance == null) {
            instance = new DepartmentDao();
        }
        return instance;
    }

    //获取当前科室信息
    public List<DepartmentItem> getAllDepartmentToday() {
        List<DepartmentItem> result = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.DEPART_TABLE, null, "date = ?", new String[]{DateUtil.getDateString()}, null, null, null);
        while (cursor.moveToNext()) {
            result.add(getDepartment(cursor));
        }
        return result;
    }

    public DepartmentItem getDepartByCode(String departCode) {
        List<DepartmentItem> result = getAllDepartmentToday();
        if (result != null && departCode != null) {
            for (DepartmentItem item : result) {
                if (departCode.equals(item.departcode))
                    return item;
            }
        }
        return null;
    }

    //当天科室信息入库
    public void setAllDepartment(List<DepartmentItem> departmentItems) {
        //删除7天前数据
        deleteOldDepartment(-7);
        for (DepartmentItem item : departmentItems) {
            setDepartment(item);
        }
    }

    public DepartmentItem getDepartment(Cursor cursor) {
        DepartmentItem item = new DepartmentItem();
        item.date = cursor.getString(cursor.getColumnIndex("date"));
        item.departcode = cursor.getString(cursor.getColumnIndex("departcode"));
        item.departname = cursor.getString(cursor.getColumnIndex("departname"));
        item.departareacode = cursor.getString(cursor.getColumnIndex("departareacode"));
        item.departarea = cursor.getString(cursor.getColumnIndex("departarea"));
        item.nurseid = cursor.getString(cursor.getColumnIndex("nurseid"));
        item.nurse = cursor.getString(cursor.getColumnIndex("nurse"));
        item.nursephone = cursor.getString(cursor.getColumnIndex("nursephone"));
        return item;
    }

    public String getIdByDepartment(DepartmentItem item) {
        String result = "";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.DEPART_TABLE, null, "departcode = ? and date = ?", new String[]{item.departcode, DateUtil.getDateString()}, null, null, null);
        if (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex("id"));
        }
        return result;
    }


    public String getdepartarea(String departareacode) {
        String result = "";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.DEPART_TABLE, null, "departareacode = ? and date = ?", new String[]{departareacode, DateUtil.getDateString()}, null, null, null);
        if (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex("departarea"));
        }
        return result;
    }

    public String getdepartname(String departcode) {
        String result = "";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.DEPART_TABLE, null, "departcode = ? and date = ?", new String[]{departcode, DateUtil.getDateString()}, null, null, null);
        if (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex("departname"));
        }
        return result;
    }

    public String getnurse(String nurseid) {
        String result = "";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.DEPART_TABLE, null, "nurseid = ? and date = ?", new String[]{nurseid, DateUtil.getDateString()}, null, null, null);
        if (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex("nurse"));
        }
        return result;
    }

    public void setDepartment(DepartmentItem item) {
        String id = getIdByDepartment(item);
        if (TextUtils.isEmpty(id)) {
            insertDepartment(item);
        } else {
            updateDepartment(id, item);
        }
    }

    public void insertDepartment(DepartmentItem item) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.insertWithOnConflict(DbHelper.DEPART_TABLE, null, getContentValues(item), SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void updateDepartment(String id, DepartmentItem item) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.updateWithOnConflict(DbHelper.DEPART_TABLE, getContentValues(item), "id = ?", new String[]{id}, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private ContentValues getContentValues(DepartmentItem item) {
        ContentValues values = new ContentValues();
        values.put("date", DateUtil.getDateString());
        values.put("departcode", item.departcode);
        values.put("departname", item.departname);
        values.put("departareacode", item.departareacode);
        values.put("departarea", item.departarea);
        values.put("nurseid", item.nurseid);
        values.put("nurse", item.nurse);
        values.put("nursephone", item.nursephone);
        return values;
    }

    public void deleteAllDepartment() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete(DbHelper.DEPART_TABLE, null, null);
    }

    //删除 dayago 之前的所有数据
    public void deleteOldDepartment(int dayago) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, dayago);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete(DbHelper.DEPART_TABLE, "date < ?", new String[]{DateUtil.getDateString(calendar.getTime())});
    }
}