package cn.pencilsky.browser;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import cn.pencilsky.browser.adapter.BookmarkRecyclerAdapter;

import java.util.Collections;

/**
 * Created by chenlin on 20/06/2017.
 * 处理RecyclerView中拖动item
 */
public class MyItemTouchCallback extends ItemTouchHelper.Callback {

    private final BookmarkRecyclerAdapter adapter;

    public MyItemTouchCallback(BookmarkRecyclerAdapter adapter) {
        this.adapter = adapter;
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(adapter.getDataList(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(adapter.getDataList(), i, i - 1);
            }
        }
        recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.END) {
            adapter.getDataList().remove(position);
            adapter.notifyItemRemoved(position);
        }
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag;
        int swipeFlag;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if ((layoutManager instanceof GridLayoutManager)||(layoutManager instanceof StaggeredGridLayoutManager)) {
            dragFlag = ItemTouchHelper.DOWN | ItemTouchHelper.UP
                    | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
            swipeFlag = 0;
        }
        else {
            dragFlag = ItemTouchHelper.DOWN | ItemTouchHelper.UP;
            swipeFlag = ItemTouchHelper.END;
        }
        return makeMovementFlags(dragFlag, swipeFlag);
    }
}