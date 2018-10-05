package com.nanda.fileencoder.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nanda.fileencoder.R;
import com.nanda.fileencoder.adapter.FileListAdapter;
import com.nanda.fileencoder.utils.CommonUtils;
import com.nanda.fileencoder.utils.EncoderUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DecodeActvity extends AppCompatActivity implements FileListAdapter.FileClickListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    private FileListAdapter adapter;
    private List<File> fileList;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode_decode);
        ButterKnife.bind(this);

        dialog = CommonUtils.showProgressDialog(this, "Decoding...");
        adapter = new FileListAdapter(this, false, this);

        fileList = getFileList();

        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);

        if (fileList != null && fileList.size() > 0) {
            adapter.setFileList(fileList);
            recyclerview.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            recyclerview.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }

    }

    private List<File> getFileList() {
        File file = new File(Environment.getExternalStorageDirectory() + "/File Encoder/Encoded Files");
        File[] files = file.listFiles();
        List<File> fileList = getFileList(files);
        return fileList;
    }

    private List<File> getFileList(File[] files) {
        if (files == null) {
            return null;
        }
        List<File> fileList = new ArrayList<>(files.length);
        for (int i = 0, filesLength = files.length; i < filesLength; i++) {
            File file = files[i];
            fileList.add(file);
        }
        return fileList;
    }

    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    private byte[] decodedArray(String encodedString) {
        byte[] decodedArray = EncoderUtils.decode(encodedString);
        return decodedArray;
    }

    private boolean convertByteArrayToFile(byte[] decodedArray, String fileName) {
        if (fileName.contains("txt")) {
            fileName = fileName.replace("txt", "mp3");
        }
        File path = new File(Environment.getExternalStorageDirectory(), "/File Encoder/Decoded Files");
        if (!path.exists()) {
            path.mkdir();
        }
        File audio = new File(path, fileName);

        if (audio.exists()) {
            audio.delete();
        }

        try {
            FileOutputStream fos = new FileOutputStream(audio.getPath());
            fos.write(decodedArray);
            fos.close();
            return true;
        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
            return false;
        }
    }

    private void hideProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onFileClicked(final File file) {
        showProgressDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                decodeToAudio(file);
            }
        }, 50);

    }

    private void decodeToAudio(File file) {
        try {
            String encodedString = getStringFromFile(file.getAbsolutePath());
            if (!TextUtils.isEmpty(encodedString)) {

                byte[] decodedArray = decodedArray(encodedString);

                if (decodedArray != null) {
                    if (convertByteArrayToFile(decodedArray, file.getName())) {
                        Toast.makeText(this, "Decoded Success!", Toast.LENGTH_SHORT).show();
                    } else Toast.makeText(this, "Decoded failed!", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(this, "Decoded Array is empty!", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "Encoded file is empty!", Toast.LENGTH_SHORT).show();
            hideProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            hideProgressDialog();
        }
    }
}
