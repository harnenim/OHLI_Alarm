package moe.noitamina.ohli.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;


public class Table {
    private DBHelper helper;
    Table(DBHelper helper) {
        this.helper = helper;
    }

//    protected static Map<String, Object> toMap(Cursor cursor, String[] columns) {
//        Map<String, Object> result = new HashMap<>();
//        for (String column : columns) {
//            int columnIndex = cursor.getColumnIndex(column);
//            int type = cursor.getType(columnIndex);
//            switch (type) {
//                case Cursor.FIELD_TYPE_INTEGER:
//                    result.put(column, cursor.getInt(cursor.getColumnIndex(column)));
//                    break;
//                case Cursor.FIELD_TYPE_FLOAT:
//                    result.put(column, cursor.getFloat(cursor.getColumnIndex(column)));
//                    break;
//                case Cursor.FIELD_TYPE_STRING:
//                    result.put(column, cursor.getString(cursor.getColumnIndex(column)));
//                    break;
//                case Cursor.FIELD_TYPE_BLOB:
//                    result.put(column, cursor.getBlob(cursor.getColumnIndex(column)));
//                    break;
//            }
//        }
//        return result;
//    }

//    protected Cursor select(String table, String[] columns, String selection) {
//        return select(table, columns, selection, null);
//    }
    Cursor select(String table, String[] columns, String selection, String[] selectionArgs) {
        return select(table, columns, selection, selectionArgs, null);
    }
    Cursor select(String table, String[] columns, String selection, String[] selectionArgs, String orderBy) {
        return helper.getReadableDatabase().query(table, columns, selection, selectionArgs, null, null, orderBy);
    }
    long insert(String table, ContentValues values) {
        return helper.getWritableDatabase().insert(table, null, values);
    }
    long update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return helper.getWritableDatabase().update(table, values, whereClause, whereArgs);
    }
}
