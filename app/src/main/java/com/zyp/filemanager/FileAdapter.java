package com.zyp.filemanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhangyipeng on 2018/8/4.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private final LayoutInflater inflater;
    private final Context mContext;

    private List<FileData> datas;

    public FileAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    private int prePosition = -1;
    private int curPosition = -1;

    public void notifyDataSetChangedBg(int position) {
        curPosition = position;
        notifyItemChanged(curPosition);
        notifyItemChanged(prePosition);
        prePosition = curPosition;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, FileData fileData, View view, FileAdapter adapter);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_file_info, parent, false);

        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FileViewHolder holder, final int position) {
        final FileData fileData = datas.get(position);
        if (fileData.isDirectory()) {
            holder.iv_file_type_icon.setImageResource(R.mipmap.file_dir_icon);
        } else {
            holder.iv_file_type_icon.setImageResource(R.mipmap.file_icon);
        }
        if (position == curPosition) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
        } else if (position == prePosition) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }
        holder.tv_file_name.setText(fileData.getFileName());
        holder.tv_file_size.setText(fileData.getFileSize());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position, fileData, holder.itemView, FileAdapter.this);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void setDatas(List<FileData> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_file_type_icon;
        TextView tv_file_name;
        TextView tv_file_size;
        View itemView;

        public FileViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            iv_file_type_icon = itemView.findViewById(R.id.iv_file_type_icon);
            tv_file_name = itemView.findViewById(R.id.tv_file_name);
            tv_file_size = itemView.findViewById(R.id.tv_file_size);
        }
    }
}
