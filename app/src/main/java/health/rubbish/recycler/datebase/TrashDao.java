package health.rubbish.recycler.datebase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import health.rubbish.recycler.base.App;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.entity.TrashItem;
import health.rubbish.recycler.util.DateUtil;

/**
 * Created by Lenovo on 2016/11/20.
 */

public class TrashDao {
    private static TrashDao instance = null;
    private static DbHelper dbHelper;

    private TrashDao() {
        dbHelper = new DbHelper(App.ctx);
    }

    public synchronized static TrashDao getInstance() {
        if (instance == null) {
            instance = new TrashDao();
        }
        return instance;
    }

    //获取当天垃圾信息
    public List<TrashItem> getAllTrashToday() {
        List<TrashItem> result = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.TRASH_TABLE, null,  "date = ?", new String[]{DateUtil.getDateString()}, null, null, null);
        while (cursor.moveToNext()) {
            result.add(getTrash(cursor));
        }
        return result;
    }

    //当前垃圾信息入库
    public  void setAllTrash(List<TrashItem> trashItems) {
        deleteOldTrash(-7);
        for (TrashItem item:trashItems)
        {
            setTrash(item);
        }
    }

    public TrashItem getTrash(Cursor cursor) {
        TrashItem item = new TrashItem();
        item.date = cursor.getString(cursor.getColumnIndex("date"));
        item.trashcode = cursor.getString(cursor.getColumnIndex("trashcode"));
        item.status = cursor.getString(cursor.getColumnIndex("status"));
        item.trashcancode = cursor.getString(cursor.getColumnIndex("trashcancode"));
        item.colletime = cursor.getString(cursor.getColumnIndex("colletime"));
        item.categorycode = cursor.getString(cursor.getColumnIndex("categorycode"));
        item.categoryname = cursor.getString(cursor.getColumnIndex("categoryname"));
        item.weight = cursor.getString(cursor.getColumnIndex("weight"));
        item.collectorid = cursor.getString(cursor.getColumnIndex("collectorid"));
        item.collector = cursor.getString(cursor.getColumnIndex("collector"));
        item.collectphone = cursor.getString(cursor.getColumnIndex("collectphone"));

        item.departcode = cursor.getString(cursor.getColumnIndex("departcode"));
        item.departname = cursor.getString(cursor.getColumnIndex("departname"));
        item.departareacode = cursor.getString(cursor.getColumnIndex("departareacode"));
        item.departarea = cursor.getString(cursor.getColumnIndex("departarea"));
        item.nurseid = cursor.getString(cursor.getColumnIndex("nurseid"));
        item.nurse = cursor.getString(cursor.getColumnIndex("nurse"));
        item.nursephone = cursor.getString(cursor.getColumnIndex("nursephone"));

        item.trashstation = cursor.getString(cursor.getColumnIndex("trashstation"));
        item.dustbincode = cursor.getString(cursor.getColumnIndex("dustbincode"));
        item.transfertime = cursor.getString(cursor.getColumnIndex("transfertime"));
        item.transferid = cursor.getString(cursor.getColumnIndex("transferid"));
        item.transfer = cursor.getString(cursor.getColumnIndex("transfer"));
        item.transferphone = cursor.getString(cursor.getColumnIndex("transferphone"));

        return item;
    }

    public String getIdByTrash(TrashItem item) {
        String result = "";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.TRASH_TABLE, null, "trashcode = ? and date = ?", new String[]{item.trashcode,DateUtil.getDateString()}, null, null, null);
        if (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex("id"));
        }
        return result;
    }

    public void setTrash(TrashItem item) {
        String id = getIdByTrash(item);
        if (TextUtils.isEmpty(id)) {
            insertTrash(item);
        } else {
            updateTrash(id, item);
        }
    }

    public void insertTrash(TrashItem item) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.insertWithOnConflict(DbHelper.TRASH_TABLE, null, getContentValues(item), SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void updateTrash(String id, TrashItem item) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.updateWithOnConflict(DbHelper.TRASH_TABLE, getContentValues(item), "id = ?", new String[]{id}, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private ContentValues getContentValues(TrashItem item)
    {
        ContentValues values = new ContentValues();
        values.put("date", DateUtil.getDateString());
        values.put("trashcode", item.trashcode);
        values.put("status", item.status);
        values.put("trashcancode", item.trashcancode);
        values.put("colletime", item.colletime);
        values.put("categorycode", item.categorycode);
        values.put("categoryname", item.categoryname);
        values.put("weight", item.weight);
        values.put("collectorid", item.collectorid);
        values.put("collector", item.collector);
        values.put("collectphone", item.collectphone);
        values.put("departcode", item.departcode);

        values.put("departname", item.departname);
        values.put("departareacode", item.departareacode);
        values.put("departarea", item.departarea);
        values.put("nurseid", item.nurseid);
        values.put("nurse", item.nurse);
        values.put("nursephone", item.nursephone);

        values.put("trashstation", item.trashstation);
        values.put("dustbincode", item.dustbincode);
        values.put("transfertime", item.transfertime);
        values.put("transferid", item.transferid);
        values.put("transfer", item.transfer);
        values.put("transferphone", item.transferphone);
        return values;
    }

    public void deleteAllTrash()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete(DbHelper.TRASH_TABLE,null,null);
    }

    //删除 dayago 之前的所有数据
    public void deleteOldTrash(int dayago)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,dayago);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete(DbHelper.TRASH_TABLE,"date < ?",new String[]{DateUtil.getDateString(calendar.getTime())});
    }
}
