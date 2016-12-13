package health.rubbish.recycler.datebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import health.rubbish.recycler.base.App;
import health.rubbish.recycler.entity.CatogeryItem;
import health.rubbish.recycler.util.DateUtil;

/**
 * Created by Lenovo on 2016/11/20.
 */

public class CatogeryDao {
    private static CatogeryDao instance = null;
    private static DbHelper dbHelper;

    private CatogeryDao() {
        dbHelper = new DbHelper(App.ctx);
    }

    public synchronized static CatogeryDao getInstance() {
        if (instance == null) {
            instance = new CatogeryDao();
        }
        return instance;
    }

    //获取垃圾分类
    public List<CatogeryItem> getAllCatogery() {
        List<CatogeryItem> result = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.CAT_TABLE, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            result.add(getCatogery(cursor));
        }
        return result;
    }

    //垃圾分类存入数据库
    public  void setAllCatogery(List<CatogeryItem> catogeryItems) {
        deleteAllCatogery();
        for (CatogeryItem item:catogeryItems)
        {
            setCatogery(item);
        }
    }

    public CatogeryItem getCatogery(Cursor cursor) {
        CatogeryItem item = new CatogeryItem();
        item.date = cursor.getString(cursor.getColumnIndex("date"));
        item.categorycode = cursor.getString(cursor.getColumnIndex("categorycode"));
        item.categoryname = cursor.getString(cursor.getColumnIndex("categoryname"));
        return item;
    }

    public String getIdByCatogery(CatogeryItem item) {
        String result = "";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.CAT_TABLE, null, "categorycode = ?", new String[]{item.categorycode}, null, null, null);
        if (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex("id"));
        }
        return result;
    }

    public String getcategoryname(String categorycode) {
        String result = "";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.CAT_TABLE, null, "categorycode = ?", new String[]{categorycode}, null, null, null);
        if (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex("categoryname"));
        }
        return result;
    }

    public void setCatogery(CatogeryItem item) {
        String id = getIdByCatogery(item);
        if (TextUtils.isEmpty(id)) {
            insertCatogery(item);
        } else {
            updateCatogery(id, item);
        }
    }

    public void insertCatogery(CatogeryItem item) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", DateUtil.getDateString());
        values.put("categorycode", item.categorycode);
        values.put("categoryname", item.categoryname);
        db.insertWithOnConflict(DbHelper.CAT_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void updateCatogery(String id, CatogeryItem item) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", DateUtil.getDateString());
        values.put("categorycode", item.categorycode);
        values.put("categoryname", item.categoryname);
        db.updateWithOnConflict(DbHelper.CAT_TABLE, values, "id = ?", new String[]{id}, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void deleteAllCatogery()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete(DbHelper.CAT_TABLE,null,null);
    }
}
