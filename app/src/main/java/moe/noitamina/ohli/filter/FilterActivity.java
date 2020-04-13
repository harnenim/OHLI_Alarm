package moe.noitamina.ohli.filter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import moe.noitamina.ohli.R;
import moe.noitamina.ohli.db.IFilterTable;
import moe.noitamina.ohli.db.vo.IFilterVo;

/**
 * 애니/제작자 목록 공통 기능
 * @param <Vo>
 */
public abstract class FilterActivity<Vo extends IFilterVo> extends AppCompatActivity {

    protected Button btnSync, btnSearch;
    protected EditText etSearch;
    protected CheckBox cbAll;
    protected Spinner spOrder;
    protected ListView lvList;
    protected RelativeLayout rlEmpty;
    protected RelativeLayout rlProgress;
    protected TextView tvProgress;
    protected FilterAdapter adapter;

    // 전체 선택/해제 보호... Async 같은 거 써줘야 하나?
    protected boolean cbAllLockedByAdapter = false;

    protected IFilterTable<Vo> table;
    private String query = null;
    protected String orderBy = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        setTitle(title());

        btnSync = findViewById(R.id.btnSync);
        btnSearch = findViewById(R.id.btnSearch);
        etSearch = findViewById(R.id.etSearch);
        cbAll = findViewById(R.id.cbAll);
        spOrder = findViewById(R.id.spOrder);
        lvList = findViewById(R.id.lvList);
        rlEmpty = findViewById(R.id.rlEmpty);
        rlProgress = findViewById(R.id.rlProgress);
        tvProgress = findViewById(R.id.tvProgress);

        rlProgress.setVisibility(View.GONE);

        Listener listener = new Listener(this);
        etSearch.setOnKeyListener(listener);
        btnSync  .setOnClickListener(listener);
        btnSearch.setOnClickListener(listener);
        cbAll.setOnCheckedChangeListener(listener);

        adapter = new FilterAdapter<>(this, new ArrayList<Vo>(), table = getTable());
        lvList.setAdapter(adapter);
        lvList.setOnItemClickListener(adapter);

        OrderAdapter oAdapter = new OrderAdapter(this, getOrderItems());
        spOrder.setAdapter(oAdapter);
        spOrder.setOnItemSelectedListener(oAdapter);
        spOrder.setSelection(0);
    }

    /**
     * 검색
     */
    protected void search() {
        query = etSearch.getText().toString().trim();
        refreshList();
    }

    /**
     * 현재 상태에 맞춰 리스트 갱신
     */
    protected void refreshList() {
        List<Vo> list = null;
        if (hasQuery()) {
            list = table.filteredList(query, orderBy);
        } else {
            list = table.filteredList(orderBy);
        }
        rlEmpty.setVisibility(list.size() > 0 ? View.GONE : View.VISIBLE);
        adapter.setList(list);
        adapter.updateCheckedAll();
    }

    /**
     * 검색칸 비었는지 여부
     * @return
     */
    public boolean hasQuery() {
        return query != null && query.length() > 0;
    }

    protected abstract String title();
    protected abstract IFilterTable<Vo> getTable();
    protected abstract OrderAdapter.Item[] getOrderItems();
    protected abstract void afterItemClick();
    protected abstract void runSync();

    /**
     * 서버와 동기화 기능
     */
    protected abstract static class FilterSyncTask extends AsyncTask<Void, Void, Boolean> {

        WeakReference<FilterActivity> activity;
        FilterSyncTask(FilterActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final FilterActivity activity = this.activity.get();
            activity.tvProgress.setText(R.string.sync_processing);
            activity.rlProgress.setVisibility(View.VISIBLE);
        }

        /**
         * 프로그레스 화면 메시지 변경 메서드
         * @param status
         */
        protected void setStatus(final String status) {
            final FilterActivity activity = this.activity.get();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.tvProgress.setText(status);
                }
            });
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            final FilterActivity activity = this.activity.get();
            activity.refreshList();
            activity.rlProgress.setVisibility(View.GONE);
            if (!response) {
                Toast.makeText(activity, R.string.sync_problem, Toast.LENGTH_LONG).show();
            }
        }
    }

    private static class Listener implements View.OnClickListener, View.OnKeyListener, CompoundButton.OnCheckedChangeListener {

        private FilterActivity activity;
        private Listener(FilterActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSync:
                    // 서버와 동기화
                    activity.runSync();
                    break;

                case R.id.btnSearch: {
                    // 검색
                    activity.search();
                    break;
                }
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            switch (v.getId()) {
                case R.id.etSearch: {
                    switch (keyCode) {
                        case 13:
                        case 66:
                            // 엔터 -> 검색
                            activity.btnSearch.performClick();
                            break;
                    }
                    break;
                }
            }
            return false;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.cbAll:
                    // 전체 선택/해제
                    if (!activity.cbAllLockedByAdapter) {
                        activity.adapter.setCheckAll(isChecked);
                    }
                    break;
            }
        }
    }
}
