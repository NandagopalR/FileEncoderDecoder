package com.nanda.fileencoder.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nanda.fileencoder.R;
import com.nanda.fileencoder.adapter.FileListAdapter;
import com.nanda.fileencoder.base.BaseActivity;
import com.nanda.fileencoder.utils.CommonUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AESDecryptionActivity extends BaseActivity implements FileListAdapter.FileClickListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    private FileListAdapter adapter;
    private List<File> fileList;
    private ProgressDialog dialog;

    private static String algorithm = "AES";
    private static String algorithm_type = "AES/ECB/NoPadding";
    private static SecretKey yourKey = null;
    private static final String AES_KEY = "vvdfsnvsdjkvnsdf";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode_decode);
        ButterKnife.bind(this);

        dialog = CommonUtils.showProgressDialog(this, "Decoding...");
        adapter = new FileListAdapter(this, true, this);

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
        File file = new File(Environment.getExternalStorageDirectory() + "/File Encoder/AES/Encrypted");
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

    private SecretKey getnerate256Key() {
        KeyGenerator keyGen;
        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            return secretKey;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private SecretKey generateKey(String key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            return new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    void decodeFile(String encryptedFileName) {
        try {
            byte[] encryptedData = readFile(encryptedFileName);
            byte[] decodedData = decodeFile(generateKey(AES_KEY), encryptedData);
            if (decodedData != null) {
                boolean isDecryptedFile = convertByteArrayToFile(decodedData, encryptedFileName);
                if (isDecryptedFile) {
                    showToast("Decryption Success");
                } else showToast("Decryption Failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] decodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance(algorithm_type);
        cipher.init(Cipher.DECRYPT_MODE, yourKey, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        decrypted = cipher.doFinal(fileData);
        return decrypted;
    }

    public byte[] readFile(String encryptedFileName) {
        byte[] contents = null;

        File file = new File(Environment.getExternalStorageDirectory()
                + "/File Encoder/AES/Encrypted", encryptedFileName);
        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }

    private boolean convertByteArrayToFile(byte[] decodedArray, String fileName) {
        if (fileName.contains("txt")) {
            fileName = fileName.replace("txt", "mp3");
        }
        File path = new File(Environment.getExternalStorageDirectory(), "/File Encoder/AES/Decrypted");
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

    @Override
    public void onFileClicked(File file) {
        try {
            decodeFile(file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
