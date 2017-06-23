package cn.pencilsky.browser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.pencilsky.browser.R;
import cn.pencilsky.browser.entity.History;

import java.util.List;

/**
 * Created by chenlin on 22/06/2017.
 */
public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    List<History> histories;
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;//用来计算Child位置
    private OnItemClickListener onItemClickListener;

    /**
     * 对外提供接口初始化方法
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public HistoryRecyclerAdapter(Context context, List<History> histories) {
        this.histories = histories;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        History history = histories.get(position);
        holder.historyUrl.setText(history.getUrl());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_history, parent, false);
        //导入itemView，为itemView设置点击事件
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        return new ViewHolder(itemView);
    }

    /**
     * 适配器绑定到RecyclerView 的时候，回将绑定适配器的RecyclerView 传递过来
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    /**
     * Item被点击时，回调传入接口方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        //RecyclerView可以计算出这是第几个Child
        int childAdapterPosition = mRecyclerView.getChildAdapterPosition(v);
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(childAdapterPosition, histories.get(childAdapterPosition));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        //RecyclerView可以计算出这是第几个Child
        int childAdapterPosition = mRecyclerView.getChildAdapterPosition(v);
        if (onItemClickListener != null) {
            return onItemClickListener.OnItemLongClick(childAdapterPosition, histories.get(childAdapterPosition));
        }
        return false;
    }

    /**
     * 接口回调
     * 1、定义接口，定义接口中的方法
     * 2、在数据产生的地方持有接口，并提供初始化方法，在数据产生的时候调用接口的方法
     * 3、在需要处理数据的地方实现接口，实现接口中的方法，并将接口传递到数据产生的地方
     */
    public interface OnItemClickListener {
        void onItemClick(int position, History model);
        boolean OnItemLongClick(int position, History model);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView historyUrl;

        public ViewHolder(View itemView) {
            super(itemView);
            historyUrl = (TextView) itemView.findViewById(R.id.historyUrl);
        }
    }
}
