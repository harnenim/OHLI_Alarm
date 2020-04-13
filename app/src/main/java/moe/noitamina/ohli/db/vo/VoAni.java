package moe.noitamina.ohli.db.vo;

import android.database.Cursor;

import moe.noitamina.ohli.db.TableAni;

public class VoAni implements IFilterVo {
    private int key;
    private String title;
    private String nicks;
    private String sdate;
    private String edate;
    private boolean shows;
    private boolean removed;

    public VoAni(Cursor cursor) {
        key     = cursor.getInt   (cursor.getColumnIndex(TableAni.KEY));
        title   = cursor.getString(cursor.getColumnIndex(TableAni.TITLE));
        nicks   = cursor.getString(cursor.getColumnIndex(TableAni.NICKS));
        sdate   = cursor.getString(cursor.getColumnIndex(TableAni.SDATE));
        edate   = cursor.getString(cursor.getColumnIndex(TableAni.EDATE));
        shows   = cursor.getInt   (cursor.getColumnIndex(TableAni.SHOWS)) > 0;
        removed = cursor.getInt   (cursor.getColumnIndex(TableAni.REMOVED)) > 0;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getComment() {
        return sdate.substring(0, 4) + "-" + sdate.substring(4, 6) + "-" + sdate.substring(6, 8) +
                "\n" +
                edate.substring(0, 4) + "-" + edate.substring(4, 6) + "-" + edate.substring(6, 8);
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
