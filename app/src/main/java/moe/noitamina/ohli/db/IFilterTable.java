package moe.noitamina.ohli.db;


import java.util.List;
import java.util.Map;

import moe.noitamina.ohli.db.vo.IFilterVo;

/**
 * Filter 기능 Table 객체
 * 다시 보니까 abstract class로 만들어서 최대한 공통기능 가져오는 게...
 * @param <Vo>
 */
public interface IFilterTable<Vo extends IFilterVo> {
    /**
     * 사용 중일 경우만 select
     * @param key
     * @return
     */
    Vo selectUsing(int key);

    /**
     * 삭제되지 않은 것들 선택
     * @param orderBy
     * @return
     */
    List<Vo> filteredList(String orderBy);

    /**
     * 삭제되지 않은 것들 중에 검색
     * @param query
     * @param orderBy
     * @return
     */
    List<Vo> filteredList(String query, String orderBy);

    /**
     * 사용 여부 변경
     * @param key 키
     * @param shows  사용 여부
     * @return
     */
    long updateShows(int key, boolean shows);

    /**
     * 전체 사용 선택/해제
     * @param shows
     * @return
     */
    long updateShows(boolean shows);

    /**
     * 서버에서 새로 받아오기 전에 모두 removed 처리
     * @return
     */
    long updateBeforeSync();

    /**
     * 서버에서 새로 받아온 값 저장
     * @param item
     * @return
     */
    long insertOrUpdateBySync(Map<String, Object> item);

    /**
     * 필터링돼서 보이는 것 개수
     * @return 개수
     */
    int usingCount();

    /**
     * 필터링돼서 보이는 리스트
     * @return
     */
    List<Vo> usingList();
}
