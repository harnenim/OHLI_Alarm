package moe.noitamina.ohli.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import moe.noitamina.ohli.db.vo.VoMaker;

public class TableMaker extends Table implements IFilterTable<VoMaker> {

    static final String TABLE   = "maker";
    public static final String KEY     = "key";
    public static final String NICK    = "nick" ;
    public static final String POINT   = "point" ;
    public static final String SHOWS   = "shows";
    public static final String REMOVED = "removed";
    private static final String[] COLUMNS = new String[] { KEY, NICK, POINT, SHOWS, REMOVED };

    // 헷갈릴까 봐 컬럼 생성 구문은 버전 붙여줌 (세 자릿수 갈 일은 없으리라)
    static final String Cv01_KEY     = "'" + KEY     + "' INTEGER NOT NULL";
    static final String Cv01_NICK    = "'" + NICK    + "' TEXT    NOT NULL";
    static final String Cv02_POINT   = "'" + POINT   + "' INTEGER DEFAULT 0";
    static final String Cv01_SHOWS   = "'" + SHOWS   + "' INTEGER DEFAULT 0";
    static final String Cv01_REMOVED = "'" + REMOVED + "' INTEGER DEFAULT 0";

    TableMaker(DBHelper helper) {
        super(helper);
    }

    public VoMaker select(int key) {
        VoMaker result = null;
        Cursor cursor = select(TABLE, COLUMNS, KEY + " = ?", new String[] { "" + key });
        if (cursor.moveToFirst()) {
            result = new VoMaker(cursor);
        }
        cursor.close();
        return result;
    }

    @Override
    public VoMaker selectUsing(int key) {
        VoMaker result = null;
        Cursor cursor = select(TABLE, COLUMNS, KEY + " = ? AND " + SHOWS + " = 1", new String[] { "" + key });
        if (cursor.moveToFirst()) {
            result = new VoMaker(cursor);
        }
        cursor.close();
        return result;
    }

    @Override
    public List<VoMaker> filteredList(String orderBy) {
        List<VoMaker> result = new ArrayList<>();
        Cursor cursor = select(TABLE, COLUMNS, REMOVED + " = 0", null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                result.add(new VoMaker(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    @Override
    public List<VoMaker> filteredList(String query, String orderBy) {
        List<VoMaker> result = new ArrayList<>();
        Cursor cursor = select(TABLE, COLUMNS, NICK + " LIKE ('%' || ? || '%') AND " + REMOVED + " = 0", new String[] { query }, orderBy);
        if (cursor.moveToFirst()) {
            do {
                result.add(new VoMaker(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    private Cursor selectUsingMakerList() {
        return select(TABLE, COLUMNS, SHOWS + " > 0 AND " + REMOVED + " = 0", null, NICK);
    }

    @Override
    public int usingCount() {
        return selectUsingMakerList().getCount();
    }

    @Override
    public List<VoMaker> usingList() {
        List<VoMaker> result = new ArrayList<>();
        Cursor cursor = selectUsingMakerList();
        if (cursor.moveToFirst()) {
            do {
                result.add(new VoMaker(cursor));
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
        int key = (int) (double) item.get("no");
        String nick = item.get("nick").toString();
        int point = (int) (double) item.get("pointP");

        VoMaker maker = selectMaker(key);
        if (maker == null) {
            return insert(key, nick, point);
        } else {
            return updateBySync(key, nick, point);
        }
    }

    private VoMaker selectMaker(int key) {
        VoMaker result = null;
        Cursor cursor = select(TABLE, COLUMNS, KEY + " = ?", new String[] { "" + key });
        if (cursor.moveToFirst()) {
            result = new VoMaker(cursor);
        }
        cursor.close();
        return result;
    }

    private long insert(int key, String nick, int point) {
        ContentValues values = new ContentValues();
        values.put(KEY, key);
        values.put(NICK, nick);
        values.put(POINT, point);
        values.put(SHOWS, 0);
        values.put(REMOVED, 0);
        return insert(TABLE, values);
    }

    private long updateBySync(int key, String nick, int point) {
        ContentValues values = new ContentValues();
        values.put(NICK, nick);
        values.put(POINT, point);
        values.put(REMOVED, 0);
        return update(TABLE, values, KEY + " = ?", new String[] { "" + key });
    }

    @Override
    public long updateShows(int key, boolean shows) {
        ContentValues values = new ContentValues();
        values.put(SHOWS, shows ? 1 : 0);
        return update(TABLE, values, KEY + " = ?", new String[]{"" + key});
    }

    @Override
    public long updateShows(boolean shows) {
        ContentValues values = new ContentValues();
        values.put(SHOWS, shows);
        return update(TABLE, values, null, null);
    }
}
