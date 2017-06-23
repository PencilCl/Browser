package cn.pencilsky.browser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.pencilsky.browser.R;
import cn.pencilsky.browser.entity.Bookmark;

import java.util.List;

/**
 * Created by chenlin on 16/06/2017.
 */

public class BookmarkRecyclerAdapter extends RecyclerView.Adapter<BookmarkRecyclerAdapter.ViewHolder> implements View.OnClickListener {
    private List<Bookmark> data;
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;//用来计算Child位置
    private OnItemClickListener onItemClickListener;

    //对外提供接口初始化方法
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public BookmarkRecyclerAdapter(Context context, List<Bookmark> data) {
        this.data = data;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<Bookmark> getDataList() {
        return data;
    }

    /**
     * 创建VIewHolder，导入布局，实例化itemView
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_bookmark, parent, false);
        //导入itemView，为itemView设置点击事件
        itemView.setOnClickListener(this);
        return new ViewHolder(itemView);
    }

    /**
     * 绑定ViewHolder，加载数据
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //加载数据
        holder.icon.setImageResource(data.get(position).getIconResId());
        holder.name.setText(data.get(position).getName());
    }

    /**
     * 数据源的数量，item的个数
     * @return
     */
    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
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
     *
     * @param v 点击的View
     */
    @Override
    public void onClick(View v) {
        //RecyclerView可以计算出这是第几个Child
        int childAdapterPosition = mRecyclerView.getChildAdapterPosition(v);
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(childAdapterPosition, data.get(childAdapterPosition));
        }
    }

    /**
     * 接口回调
     * 1、定义接口，定义接口中的方法
     * 2、在数据产生的地方持有接口，并提供初始化方法，在数据产生的时候调用接口的方法
     * 3、在需要处理数据的地方实现接口，实现接口中的方法，并将接口传递到数据产生的地方
     */
    public interface OnItemClickListener {
        void onItemClick(int position, Bookmark model);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }
}