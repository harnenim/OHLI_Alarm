package moe.noitamina.ohli.filter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import moe.noitamina.ohli.HttpRequest;
import moe.noitamina.ohli.db.DBHelper;
import moe.noitamina.ohli.db.IFilterTable;
import moe.noitamina.ohli.db.TableMaker;
import moe.noitamina.ohli.db.vo.VoMaker;

public class FilterMakerActivity extends FilterActivity<VoMaker> {

    static final String KEY_MUST_KEYS = "MUST_KEYS";
    private int[] MUST_KEYS = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateMustKeys(PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_MUST_KEYS, ""));
    }

    private void updateMustKeys(String value) {
        String[] sKeys = value.split(",");

        if (sKeys.length > 0 && sKeys[0].trim().length() > 0) {
            try {
                int[] mustKeys = new int[sKeys.length];
                for (int i = 0; i < sKeys.length; i++) {
                    mustKeys[i] = Integer.parseInt(sKeys[i].trim());
                }
                MUST_KEYS = mustKeys;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String title() {
        return "제작자 필터 관리";
    }

    @Override
    protected TableMaker getTable() {
        return DBHelper.getInstance(getApplicationContext()).useMaker();
    }

    @Override
    protected OrderAdapter.Item[] getOrderItems() {
        return new OrderAdapter.Item[] {
                new OrderAdapter.Item("자막 포인트 높은 순", TableMaker.POINT + " DESC")
            ,   new OrderAdapter.Item("자막 포인트 낮은 순", TableMaker.POINT + " ASC" )
            ,   new OrderAdapter.Item("제작자 이름 순"     , TableMaker.NICK  + " ASC" )
            ,   new OrderAdapter.Item("제작자 이름 역순"   , TableMaker.NICK  + " DESC")
        };
    }

    @Override
    protected void afterItemClick() {
        int count = adapter.getCheckedCount();

        if (count > 0 && count < adapter.list.size()) {
            boolean needToCheck = false;
            boolean needToChecks[] = new boolean[MUST_KEYS.length];
            for (int i = 0; i < needToChecks.length; i++) {
                needToChecks[i] = false;
            }

            TableMaker table = getTable();

            for (int i = 0; i < MUST_KEYS.length; i++) {
                VoMaker vo = table.select(MUST_KEYS[i]);
                if (vo != null) {
                    if (!vo.getShows()) {
                        needToCheck = needToChecks[i] = true;
                    }
                }
            }

            if (needToCheck) {
                Toast.makeText(this, "필수 제작자를 함께 선택합니다.", Toast.LENGTH_LONG).show();

                for (int i = 0; i < MUST_KEYS.length; i++) {
                    if (needToChecks[i]) {
                        table.updateShows(MUST_KEYS[i], true);
                    }
                }

                refreshList();
            }
        }
    }

    @Override
    protected void runSync() {
        new SyncTask(this).execute();
    }

    private static class SyncTask extends FilterSyncTask {

        private SyncTask(FilterMakerActivity activity) {
            super(activity);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            IFilterTable table = this.activity.get().table;

            String response = new HttpRequest("https://ohli.moe/manage/memberList.json").requestPost();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> list = new Gson().fromJson(response, List.class);
            if (list == null) return false;// 요청 실패

            table.updateBeforeSync();

            for (Map<String, Object> item : list) {
                table.insertOrUpdateBySync(item);
            }

            try {
                response = new HttpRequest("https://ohli.moe/timetable/must.txt").requestGet();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity.get());
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(KEY_MUST_KEYS, response);
                editor.commit();

                ((FilterMakerActivity) activity.get()).updateMustKeys(response);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
    }
}
