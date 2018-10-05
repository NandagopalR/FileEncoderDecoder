package com.nanda.fileencoder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nanda.fileencoder.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {

    private Context context;
    private List<File> fileList;
    private FileClickListener listener;
    private boolean isAESType;

    public FileListAdapter(Context context, boolean isAESType, FileClickListener listener) {
        this.context = context;
        this.isAESType = isAESType;
        this.listener = listener;
        fileList = new ArrayList<>();
    }

    public interface FileClickListener {
        void onFileClicked(File file);
    }

    public void setFileList(List<File> itemList) {
        if (itemList == null) {
            return;
        }
        fileList.clear();
        fileList.addAll(itemList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file_list, viewGroup, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder fileViewHolder, int position) {
        File file = fileList.get(position);
        fileViewHolder.bindDataToView(file);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_file_name)
        TextView tvFileName;
        @BindView(R.id.btn_Encode)
        Button btnEncode;
        @BindView(R.id.tv_file_path)
        TextView tvFilePath;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btnEncode.setText("Decode");
        }

        public void bindDataToView(File file) {
            tvFileName.setText(file.getName());
            tvFilePath.setText(file.getAbsolutePath());
            if (isAESType) {
                btnEncode.setText("Decrypt");
            } else btnEncode.setText("Decode");
        }

        @OnClick(R.id.btn_Encode)
        public void onDecodeClick() {

            int position = getAdapterPosition();
            if (position < 0)
                return;
            if (listener != null) {
                listener.onFileClicked(fileList.get(position));
            }

        }
    }

}
