package moe.noitamina.ohli.db.vo;

import android.database.Cursor;

import moe.noitamina.ohli.db.TableMaker;

public class VoMaker implements IFilterVo {
    private int key;
    private String nick;
    private int point;
    private boolean shows;
    private boolean removed;

    public VoMaker(Cursor cursor) {
        key     = cursor.getInt   (cursor.getColumnIndex(TableMaker.KEY));
        nick    = cursor.getString(cursor.getColumnIndex(TableMaker.NICK));
        point   = cursor.getInt   (cursor.getColumnIndex(TableMaker.POINT));
        shows   = cursor.getInt   (cursor.getColumnIndex(TableMaker.SHOWS)) > 0;
        removed = cursor.getInt   (cursor.getColumnIndex(TableMaker.REMOVED)) > 0;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public String getTitle() {
        return nick;
    }

    @Override
    public String getComment() {
        return "" + point;
    }

    @Override
    public boolean getShows() {
        return shows;
    }

    @Override
    public void setShows(boolean shows) {
        this.shows = shows;
    }
}
