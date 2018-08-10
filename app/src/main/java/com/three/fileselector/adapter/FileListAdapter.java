package com.three.fileselector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.three.fileselector.R;
import com.three.fileselector.bean.FileInfoBean;
import com.three.fileselector.util.FileUtil;

import java.util.List;

/**
 * Created by three on 2018/8/8.
 */

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.MyViewHolder> implements View.OnClickListener {

    private List<FileInfoBean> list;//存放数据
    private Context context;
    private OnItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public FileListAdapter(List<FileInfoBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_list_file, null);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.body.setTag(position);
        FileInfoBean bean = list.get(position);
        if (bean != null) {
            holder.mFileDate.setText(bean.getTime());
            holder.mFileName.setText(bean.getFileName());
            holder.mFileSize.setText(FileUtil.FormetFileSize(bean.getFileSize()));
            holder.mFileIcon.setImageResource(FileUtil.getFileTypeImageId(context,bean.getFileName()));
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null){
            mItemClickListener.onItemClick((Integer) v.getTag());
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        View body;
        /**文件图标*/
        ImageView mFileIcon;
        /**文件名称*/
        TextView mFileName;
        /**文件概要*/
        TextView mFileDate;
        /**文件大小*/
        TextView mFileSize;

        public MyViewHolder(View itemView) {
            super(itemView);
            body = itemView.findViewById(R.id.body);
            mFileIcon = itemView.findViewById(R.id.iv_file_icon);
            mFileName = itemView.findViewById(R.id.tv_file_name);
            mFileDate = itemView.findViewById(R.id.tv_date);
            mFileSize = itemView.findViewById(R.id.tv_size);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
