package cn.pencilsky.browser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.pencilsky.browser.R;
import cn.pencilsky.browser.entity.Download;
import cn.pencilsky.browser.util.TransferUtil;

import java.io.File;
import java.util.List;

/**
 * Created by chenlin on 19/06/2017.
 */
public class DownloadRecyclerAdapter extends RecyclerView.Adapter<DownloadRecyclerAdapter.ViewHolder> implements View.OnClickListener {
    private List<Download> data;
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;//用来计算Child位置
    private OnItemClickListener onItemClickListener;

    //对外提供接口初始化方法
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public DownloadRecyclerAdapter(Context context, List<Download> data) {
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 更新download视图
     * @param download
     */
    public void updateItem(Download download) {
        int index = data.indexOf(download);
        if (index != -1) {
            this.notifyItemChanged(index);
        }
    }

    /**
     * 创建VIewHolder，导入布局，实例化itemView
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_download, parent, false);
        //导入itemView，为itemView设置点击事件
        return new ViewHolder(itemView);
    }

    /**
     * 绑定ViewHolder，加载数据
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(DownloadRecyclerAdapter.ViewHolder holder, int position) {
        Download download = data.get(position);
        holder.fileName.setText(new File(download.getFileName()).getName());
        if (download.getStatus() == Download.DOWNLOADED) {
            holder.fileSize.setText(String.format("%.2fMB", TransferUtil.byteToMB(download.getFileSize())));
            holder.progressBar.setVisibility(View.GONE);
            holder.operation.setVisibility(View.GONE);
        } else {
            holder.fileSize.setText(String.format("%.2fMB / %.2fMB", TransferUtil.byteToMB(download.getDownloadedSize()), TransferUtil.byteToMB(download.getFileSize())));
            holder.progressBar.setMax(download.getFileSize() > Integer.MAX_VALUE ? (int) download.getFileSize() / 1024 : (int) download.getFileSize());
            holder.progressBar.setProgress(download.getDownloadedSize() > Integer.MAX_VALUE ? (int) download.getDownloadedSize() / 1024 : (int) download.getDownloadedSize());
            holder.operation.setImageResource(download.getStatus() == Download.DOWNLOADING ? R.drawable.ic_pause : R.drawable.ic_play);
            holder.operation.setTag(download);
            holder.operation.setOnClickListener(this);
        }
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
        if (onItemClickListener != null) {
            Download download = (Download) v.getTag();
            onItemClickListener.onItemClick(data.indexOf(download), download, v);
        }
    }

    /**
     * 接口回调
     * 1、定义接口，定义接口中的方法
     * 2、在数据产生的地方持有接口，并提供初始化方法，在数据产生的时候调用接口的方法
     * 3、在需要处理数据的地方实现接口，实现接口中的方法，并将接口传递到数据产生的地方
     */
    public interface OnItemClickListener {
        void onItemClick(int position, Download model, View v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        TextView fileSize;
        ProgressBar progressBar;
        ImageButton operation;

        public ViewHolder(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.fileName);
            fileSize = (TextView) itemView.findViewById(R.id.fileSize);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            operation = (ImageButton) itemView.findViewById(R.id.operation);
        }
    }
}
