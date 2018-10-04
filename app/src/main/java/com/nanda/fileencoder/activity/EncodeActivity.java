package com.nanda.fileencoder.activity;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nanda.fileencoder.R;
import com.nanda.fileencoder.adapter.AssetListAdapter;
import com.nanda.fileencoder.utils.EncoderUtils;
import com.nanda.fileencoder.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EncodeActivity extends AppCompatActivity implements AssetListAdapter.EncodeClickListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    private AssetListAdapter adapter;

    private List<String> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode_decode);
        ButterKnife.bind(this);

        adapter = new AssetListAdapter(this, this);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);

        AssetManager assetManager = getAssets();
        try {
            fileList = Arrays.asList(assetManager.list("tracks"));

            if (fileList != null && fileList.size() > 0) {
                adapter.setFilePathList(fileList);
                recyclerview.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            } else {
                recyclerview.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void encodeFileToBase64Binary(String fileName)
            throws IOException {

        byte[] encodedArray = loadFile(fileName);
        String encodedString = EncoderUtils.encode(encodedArray);

        if (TextUtils.isEmpty(encodedString))
            return;
        if (writeStringToFileInPath(encodedString, fileName)) {
            Toast.makeText(this, "File Creation Success", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "File Creation Failed.", Toast.LENGTH_SHORT).show();
    }

    private boolean writeStringToFileInPath(String encodedPath, String fileName) {
        File path = new File(Environment.getExternalStorageDirectory() + "/File Encoder/Encoded Files");
        if (!path.exists()) {
            path.mkdirs();
        }
        if (fileName.contains(".mp3")) {
            fileName = fileName.replace(".mp3", ".txt");
        }
        File file = new File(path, fileName);
        if (file.exists())
            return false;
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            writer.append(encodedPath);
            writer.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private byte[] loadFile(String fileName) throws IOException {
        AssetFileDescriptor assetFileDescriptor = getAssets().openFd("tracks/" + fileName);
        InputStream is = assetFileDescriptor.createInputStream();

        byte[] bytes = FileUtils.getBytesFromAssetPath(is);
        return bytes;
    }

    @Override
    public void onEncodeClick(String fileName) {

        try {
            encodeFileToBase64Binary(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
