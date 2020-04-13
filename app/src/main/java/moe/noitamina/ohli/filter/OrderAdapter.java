package moe.noitamina.ohli.filter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import moe.noitamina.ohli.R;

public class OrderAdapter extends ArrayAdapter<OrderAdapter.Item> implements AdapterView.OnItemSelectedListener {

    private static final int resourceId = R.layout.item_order;
    private LayoutInflater inflater;
    private FilterActivity activity;
    private Item[] list;

    OrderAdapter(@NonNull FilterActivity activity, @NonNull Item[] list) {
        super(activity, resourceId, list);
        this.activity = activity;
        this.list = list;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Item item = list[position];

        if (convertView == null) {
            convertView = inflater.inflate(resourceId, null);
        }

        if (item != null) {
            TextView tv = convertView.findViewById(R.id.tvName);
            tv.setText(item.name);
        }

        return convertView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activity.orderBy = list[position].query;
        activity.refreshList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class Item {
        private String name;
        private String query;
        Item(String name, String query) {
            this.name = name;
            this.query = query;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
