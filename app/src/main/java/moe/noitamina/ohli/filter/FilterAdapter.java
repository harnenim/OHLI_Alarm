package moe.noitamina.ohli.filter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import moe.noitamina.ohli.R;
import moe.noitamina.ohli.db.IFilterTable;
import moe.noitamina.ohli.db.vo.IFilterVo;


class FilterAdapter<Vo extends IFilterVo> extends ArrayAdapter<Vo> implements AdapterView.OnItemClickListener {

    private static final int resourceId = R.layout.item_filter;
    private LayoutInflater inflater;
    private FilterActivity activity;
    protected List<Vo> list;
    private IFilterTable<Vo> table;

    public void setList(List<Vo> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 전체 선택/해제
     * @param checked
     */
    public void setCheckAll(boolean checked) {
        if (activity.hasQuery()) {
            for (IFilterVo item : list) {
                table.updateShows(item.getKey(), checked);
                item.setShows(checked);
            }
            notifyDataSetChanged();
        } else {
            table.updateShows(checked);
            activity.refreshList();
        }
    }

    FilterAdapter(FilterActivity activity, List<Vo> list, IFilterTable<Vo> table) {
        super(activity, resourceId, list);
        this.activity = activity;
        this.list = list;
        this.table = table;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final IFilterVo vo = list.get(position);

        if (convertView == null) {
            convertView = inflater.inflate(resourceId, null);
        }

        if (vo != null) {
            CheckBox cb = convertView.findViewById(R.id.cbItem);
            cb.setText(vo.getTitle());
            cb.setChecked(vo.getShows());

            TextView tv = convertView.findViewById(R.id.tvComment);
            tv.setText(vo.getComment());
        }

        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        IFilterVo vo = list.get(position);
        boolean shows = !vo.getShows();
        vo.setShows(shows);

        table.updateShows(vo.getKey(), shows);

        CheckBox cb = view.findViewById(R.id.cbItem);
        cb.setChecked(shows);

        updateCheckedAll();

        activity.afterItemClick();
    }

    /**
     * 선택 상태 바뀐 후 전체 선택 여부 체크
     */
    public void updateCheckedAll() {
        activity.cbAllLockedByAdapter = true;
        if (getCheckedCount() == list.size()) {
            activity.cbAll.setChecked(true);
        } else {
            if (activity.cbAll.isChecked()) {
                activity.cbAll.setChecked(false);
            }
        }
        activity.cbAllLockedByAdapter = false;
    }

    /**
     * 현재 사용 중인 것들 개수
     * @return
     */
    protected int getCheckedCount() {
        int count = 0;
        for (Vo item : list) {
            if (item.getShows()) {
                count++;
            }
        }
        return count;
    }
}
