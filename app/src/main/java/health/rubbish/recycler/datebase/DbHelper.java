package health.rubbish.recycler.datebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lenovo on 2016/11/20.
 */

public class DbHelper extends SQLiteOpenHelper{
    public static  final String DBNAME = "recycler.db";
    public static int VERSION = 1;

    public static final String DEPART_TABLE = "department";
    public static final String CAT_TABLE = "catogary";
    public static final String TRASH_TABLE = "trash";

    public DbHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+DEPART_TABLE+" (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date VARCHAR(100),departcode  VARCHAR(100),departname  VARCHAR(100)," +
                "departareacode VARCHAR(100),departarea  VARCHAR(100),nurseid  VARCHAR(100)," +
                "nurse VARCHAR(100),nursephone  VARCHAR(100))");

        db.execSQL("CREATE TABLE IF NOT EXISTS "+CAT_TABLE+" (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date VARCHAR(100),categorycode  VARCHAR(100),categoryname  VARCHAR(100))");

        db.execSQL("CREATE TABLE IF NOT EXISTS "+TRASH_TABLE+" (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "rfidno VARCHAR(100),trashcode  VARCHAR(100),trashcancode  VARCHAR(100),status  VARCHAR(100)" +
                "colletime VARCHAR(100),categorycode  VARCHAR(100),categoryname  VARCHAR(100)" +
                "weight VARCHAR(100),collectorid  VARCHAR(100),collector  VARCHAR(100)" +
                "collectphone VARCHAR(100),departareacode  VARCHAR(100),departarea  VARCHAR(100)" +
                "departcode VARCHAR(100),departname  VARCHAR(100),nurseid  VARCHAR(100)" +
                "nurse VARCHAR(100),nursephone  VARCHAR(100),trashstation  VARCHAR(100)" +
                "dustybincode VARCHAR(100),departareacode  VARCHAR(100),transfertime  VARCHAR(100)" +
                "transferid VARCHAR(100),transfer  VARCHAR(100),transferphone  VARCHAR(100)" +
                "platnumber VARCHAR(100),entrucktime  VARCHAR(100),entruckerid  VARCHAR(100)" +
                "entrucker VARCHAR(100),entruckerphone  VARCHAR(100),driver  VARCHAR(100)" +
                "driverphone VARCHAR(100))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
