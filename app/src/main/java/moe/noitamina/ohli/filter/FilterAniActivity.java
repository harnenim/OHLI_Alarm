package moe.noitamina.ohli.filter;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import moe.noitamina.ohli.HttpRequest;
import moe.noitamina.ohli.db.DBHelper;
import moe.noitamina.ohli.db.IFilterTable;
import moe.noitamina.ohli.db.TableAni;
import moe.noitamina.ohli.db.vo.VoAni;

public class FilterAniActivity extends FilterActivity<VoAni> {

    @Override
    protected String title() {
        return "애니 필터 관리";
    }

    @Override
    protected TableAni getTable() {
        return DBHelper.getInstance(getApplicationContext()).useAni();
    }

    @Override
    protected OrderAdapter.Item[] getOrderItems() {
        return new OrderAdapter.Item[] {
                new OrderAdapter.Item("제목 순"     , TableAni.TITLE + " ASC" )
            ,   new OrderAdapter.Item("제목 역순"   , TableAni.TITLE + " DESC")
            ,   new OrderAdapter.Item("시작일 순"   , TableAni.SDATE + " ASC" )
            ,   new OrderAdapter.Item("시작일 역순" , TableAni.SDATE + " DESC")
            ,   new OrderAdapter.Item("종료일 순"   , TableAni.EDATE + " ASC" )
            ,   new OrderAdapter.Item("종료일 역순" , TableAni.EDATE + " DESC")
        };
    }

    @Override
    protected void afterItemClick() {

    }

    @Override
    protected void runSync() {
        new SyncTask(this).execute();
    }

    private static class SyncTask extends FilterSyncTask {

        private SyncTask(FilterAniActivity activity) {
            super(activity);
        }

        @Override
        protected Boolean doInBackground(Void... strings) {
            IFilterTable table = this.activity.get().table;

            setStatus("신작...");
            String response = new HttpRequest("https://ohli.moe/timetable/list/now").requestPost();
            @SuppressWarnings("unchecked")
            List<List<Map<String, Object>>> list = new Gson().fromJson(response, List.class);
            if (list == null) return false;// 요청 실패

            table.updateBeforeSync();

            // 신작 가져오기
            for (List<Map<String, Object>> dayList : list) {
                insertOrUpdateList(dayList, table);
            }

            // 완결작 연도별로 가져오기
            for (int year = Calendar.getInstance().get(Calendar.YEAR); year >= 1979; year--) {
                setStatus(year + "...");
                response = new HttpRequest("https://ohli.moe/timetable/end?year=" + year).requestPost();
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> yearList = new Gson().fromJson(response, List.class);
                if (yearList == null) return false;
                insertOrUpdateList(yearList, table);
            }

            return true;
        }

        private void insertOrUpdateList(List<Map<String, Object>> list, IFilterTable table) {
            for (Map<String, Object> item : list) {
                table.insertOrUpdateBySync(item);
            }
        }
    }
}
