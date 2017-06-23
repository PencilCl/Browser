package cn.pencilsky.browser.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

/**
 * Created by chenlin on 20/06/2017.
 * 实现了Filterable接口的ArrayAdapter
 * Filter直接返回所有数据，不对数据进行过滤
 */
public class MyNoFilterArrayAdapter  extends ArrayAdapter<String> implements Filterable {
    String[] data;
    Filter mFilter;

    public MyNoFilterArrayAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        this.data = objects;

        mFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                results.values = data;
                results.count = data.length;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }



    @Override
    public Filter getFilter() {
        return mFilter;
    }
}