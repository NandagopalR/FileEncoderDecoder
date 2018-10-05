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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AssetListAdapter extends RecyclerView.Adapter<AssetListAdapter.AssetViewHolder> {

    private Context context;
    private EncodeClickListener listener;
    private List<String> filePathList;
    private boolean isAESType;

    public AssetListAdapter(Context context, boolean isAESType, EncodeClickListener listener) {
        this.context = context;
        this.isAESType = isAESType;
        this.listener = listener;
        filePathList = new ArrayList<>();
    }

    public void setFilePathList(List<String> itemList) {
        if (itemList == null) {
            return;
        }
        filePathList.clear();
        filePathList.addAll(itemList);
        notifyDataSetChanged();
    }

    public interface EncodeClickListener {
        void onEncodeClick(String file);
    }

    @NonNull
    @Override
    public AssetViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file_list, viewGroup, false);
        return new AssetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetViewHolder assetViewHolder, int position) {
        String fileName = filePathList.get(position);
        assetViewHolder.bindDataToView(fileName);
    }

    @Override
    public int getItemCount() {
        return filePathList.size();
    }

    class AssetViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_file_name)
        TextView tvFileName;
        @BindView(R.id.tv_file_path)
        TextView tvFilePath;
        @BindView(R.id.btn_Encode)
        Button btnEncode;

        public AssetViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindDataToView(String fileName) {
            tvFileName.setText(fileName);
            if (isAESType) {
                btnEncode.setText("Encrypt");
            } else btnEncode.setText("Encode");
        }

        @OnClick(R.id.btn_Encode)
        public void onEncodeClick() {
            int position = getAdapterPosition();
            if (position < 0)
                return;
            if (listener != null) {
                listener.onEncodeClick(filePathList.get(position));
            }
        }
    }

}
