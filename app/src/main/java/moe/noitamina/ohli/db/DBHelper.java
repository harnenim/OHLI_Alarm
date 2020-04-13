package moe.noitamina.ohli.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DB_NAME = "OHLI.db";
    private static DBHelper helper;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    public static DBHelper getInstance(Context context) {
        if (helper == null) {
            helper = new DBHelper(context.getApplicationContext());
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TableAni.TABLE + " " +
                "( " + TableAni.Cv01_KEY     +
                ", " + TableAni.Cv01_TITLE   +
                ", " + TableAni.Cv01_NICKS   +
                ", " + TableAni.Cv03_SDATE   +
                ", " + TableAni.Cv03_EDATE   +
                ", " + TableAni.Cv01_SHOWS   +
                ", " + TableAni.Cv01_REMOVED +
                ", CONSTRAINT " + TableAni.TABLE + "_PK PRIMARY KEY('" + TableAni.KEY + "')" +
                ");");

        db.execSQL("CREATE TABLE " + TableMaker.TABLE + " " +
                "( " + TableMaker.Cv01_KEY     +
                ", " + TableMaker.Cv01_NICK    +
                ", " + TableMaker.Cv02_POINT   +
                ", " + TableMaker.Cv01_SHOWS   +
                ", " + TableMaker.Cv01_REMOVED +
                ", CONSTRAINT " + TableMaker.TABLE + "_PK PRIMARY KEY('" + TableAni.KEY + "')" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE " + TableMaker.TABLE + " ADD COLUMN " + TableMaker.Cv02_POINT);
            case 2:
                db.execSQL("ALTER TABLE " + TableAni.TABLE + " ADD COLUMN " + TableAni.Cv03_SDATE);
                db.execSQL("ALTER TABLE " + TableAni.TABLE + " ADD COLUMN " + TableAni.Cv03_EDATE);
            default:
                break;
        }
    }

    public TableAni useAni() {
        return new TableAni(this);
    }
    public TableMaker useMaker() {
        return new TableMaker(this);
    }
}
