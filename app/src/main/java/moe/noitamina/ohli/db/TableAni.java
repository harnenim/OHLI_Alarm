package moe.noitamina.ohli.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import moe.noitamina.ohli.db.vo.VoAni;

public class TableAni extends Table implements IFilterTable<VoAni> {

    static final String TABLE   = "ani";
    public static final String KEY     = "key";
    public static final String TITLE   = "title";
    public static final String NICKS   = "nicks";
    public static final String SDATE   = "sdate";
    public static final String EDATE   = "edate";
    public static final String SHOWS   = "shows";
    public static final String REMOVED = "removed";
    private static final String[] COLUMNS = new String[] { KEY, TITLE, NICKS, SDATE, EDATE, SHOWS, REMOVED };

    // 헷갈릴까 봐 컬럼 생성 구문은 버전 붙여줌 (세 자릿수 갈 일은 없으리라)
    static final String Cv01_KEY     = "'" + KEY     + "' INTEGER NOT NULL";
    static final String Cv01_TITLE   = "'" + TITLE   + "' TEXT    NOT NULL";
    static final String Cv01_NICKS   = "'" + NICKS   + "' TEXT    NOT NULL";
    static final String Cv03_SDATE   = "'" + SDATE   + "' TEXT    DEFAULT '99999999'";
    static final String Cv03_EDATE   = "'" + EDATE   + "' TEXT    DEFAULT '99999999'";
    static final String Cv01_SHOWS   = "'" + SHOWS   + "' INTEGER DEFAULT 0";
    static final String Cv01_REMOVED = "'" + REMOVED + "' INTEGER DEFAULT 0";

    TableAni(DBHelper helper) {
        super(helper);
    }

    @Override
    public VoAni selectUsing(int key) {
        VoAni result = null;
        Cursor cursor = select(TABLE, COLUMNS, KEY + " = ? AND " + SHOWS + " = 1", new String[] { "" + key });
        if (cursor.moveToFirst()) {
            result = new VoAni(cursor);
        }
        cursor.close();
        return result;
    }

    @Override
    public List<VoAni> filteredList(String orderBy) {
        List<VoAni> result = new ArrayList<>();
        Cursor cursor = select(TABLE, COLUMNS, REMOVED + " = 0", null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                result.add(new VoAni(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    @Override
    public List<VoAni> filteredList(String query, String orderBy) {
        List<VoAni> result = new ArrayList<>();
        Cursor cursor = select(TABLE, COLUMNS, NICKS + " LIKE ('%' || ? || '%')", new String[] { query }, orderBy);
        if (cursor.moveToFirst()) {
            do {
                result.add(new VoAni(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    private Cursor selectUsingAniList() {
        return select(TABLE, COLUMNS, SHOWS + " > 0 AND " + REMOVED + " = 0", null, TITLE);
    }

    @Override
    public int usingCount() {
        return selectUsingAniList().getCount();
    }

    @Override
    public List<VoAni> usingList() {
        List<VoAni> result = new ArrayList<>();
        Cursor cursor = selectUsingAniList();
        if (cursor.moveToFirst()) {
            do {
                result.add(new VoAni(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    @Override
    public long updateBeforeSync() {
        ContentValues values = new ContentValues();
        values.put(REMOVED, 1);
        return update(TABLE, values, null, null);
    }

    @Override
    public long insertOrUpdateBySync(Map<String, Object> item) {
        int key = (int) (double) item.get("i");
        String title = item.get("s").toString();
        String sdate = "" + (int) (double) item.get("sd");
        String edate = "" + (int) (double) item.get("ed");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nickList = (List<Map<String, Object>>) item.get("n");

        StringBuilder nicks = new StringBuilder(title);
        for (Map<String, Object> nick : nickList) {
            nicks.append(' ').append(nick.get("s").toString());
        }

        VoAni ani = selectAni(key);
        if (ani == null) {
            return insert(key, title, nicks.toString(), sdate, edate);
        } else {
            return updateBySync(key, title, nicks.toString(), sdate, edate);
        }
    }

    private VoAni selectAni(int key) {
        VoAni result = null;
        Cursor cursor = select(TABLE, COLUMNS, KEY + " = ?", new String[] { "" + key });
        if (cursor.moveToFirst()) {
            result = new VoAni(cursor);
        }
        cursor.close();
        return result;
    }

    private long insert(int key, String title, String nicks, String sdate, String edate) {
        ContentValues values = new ContentValues();
        values.put(KEY, key);
        values.put(TITLE, title);
        values.put(NICKS, nicks);
        values.put(SDATE, sdate);
        values.put(EDATE, edate);
        values.put(SHOWS, 0);
        values.put(REMOVED, 0);
        return insert(TABLE, values);
    }

    private long updateBySync(int key, String title, String nicks, String sdate, String edate) {
        ContentValues values = new ContentValues();
        values.put(TITLE, title);
        values.put(NICKS, nicks);
        values.put(SDATE, sdate);
        values.put(EDATE, edate);
        values.put(REMOVED, 0);
        return update(TABLE, values, KEY + " = ?", new String[] { "" + key });
    }

    @Override
    public long updateShows(int key, boolean shows) {
        ContentValues values = new ContentValues();
        values.put(SHOWS, shows);
        return update(TABLE, values, KEY + " = ?", new String[]{"" + key});
    }

    @Override
    public long updateShows(boolean shows) {
        ContentValues values = new ContentValues();
        values.put(SHOWS, shows);
        return update(TABLE, values, null, null);
    }
}
