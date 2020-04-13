package moe.noitamina.ohli.db.vo;

/**
 * Filter 기능 vo 객체
 * 이것도 abstract class가 나으려나...
 */
public interface IFilterVo {
    int getKey();
    String getTitle();
    String getComment();
    boolean getShows();
    void setShows(boolean shows);
}
