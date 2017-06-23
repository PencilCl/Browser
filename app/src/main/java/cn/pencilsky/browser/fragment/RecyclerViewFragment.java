package cn.pencilsky.browser.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.pencilsky.browser.R;

/**
 * Created by chenlin on 19/06/2017.
 */
public class RecyclerViewFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    OnCreateRecyclerView onCreateRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recyclerview, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        if (onCreateRecyclerView != null) {
            onCreateRecyclerView.onCreateRecyclerView(recyclerView);
        }

        return view;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setOnCreateRecyclerView(OnCreateRecyclerView onCreateRecyclerView) {
        this.onCreateRecyclerView = onCreateRecyclerView;
    }

    public interface OnCreateRecyclerView {
        void onCreateRecyclerView(RecyclerView recyclerView);
    }
}
