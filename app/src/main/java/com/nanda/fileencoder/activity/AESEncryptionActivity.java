package com.nanda.fileencoder.activity;

import android.app.ProgressDialog;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nanda.fileencoder.R;
import com.nanda.fileencoder.adapter.AssetListAdapter;
import com.nanda.fileencoder.base.BaseActivity;
import com.nanda.fileencoder.utils.CommonUtils;
import com.nanda.fileencoder.utils.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AESEncryptionActivity extends BaseActivity implements AssetListAdapter.EncodeClickListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    private AssetListAdapter adapter;
    private ProgressDialog dialog;
    private List<String> fileList;

    private static String algorithm = "AES";
    private static String algorithm_type = "AES/ECB/PKCS5Padding";
    private static SecretKey yourKey = null;
    private static final String AES_KEY = "U2FsdGVkX1965POk1ARRzvLAGTtL076KhjSdHAj8OkCl76iC9y0CDYyA/rLJXYZO\n" +
            "zxwwJ5lelG/Xb5vE1zUHbVq7FwxxW57XdbPCKAEmvVo=";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode_decode);
        ButterKnife.bind(this);

        adapter = new AssetListAdapter(this, true, this);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);

        dialog = CommonUtils.showProgressDialog(this, "Encoding...");

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

    private SecretKey generateKey(String key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            return new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
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

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        yourKey = keyGenerator.generateKey();
        return yourKey;
    }

    public static byte[] encodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] encrypted = null;
        byte[] data = yourKey.getEncoded();
        String dataString = data.toString();
        byte[] testAttay = dataString.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length,
                algorithm);
        Cipher cipher = Cipher.getInstance(algorithm_type);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        encrypted = cipher.doFinal(fileData);
        return encrypted;
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

    private byte[] loadFile(String fileName) throws IOException {
        AssetFileDescriptor assetFileDescriptor = getAssets().openFd("tracks/" + fileName);
        InputStream is = assetFileDescriptor.createInputStream();

        byte[] bytes = FileUtils.getBytesFromAssetPath(is);
        return bytes;
    }

    void saveFile(String fileName, byte[] bytesOfFile) {
        try {
            File path = new File(Environment.getExternalStorageDirectory() + "/File Encoder/AES/Encrypted");
            if (!path.exists()) {
                path.mkdirs();
            }

            File file = new File(path, fileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            yourKey = generateKey(AES_KEY);
            byte[] filesBytes = encodeFile(yourKey, bytesOfFile);
            bos.write(filesBytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEncodeClick(String file) {

        String fileName = file.replace("mp3", "txt");

        try {
            byte[] bytesOfFile = loadFile(file);
            saveFile(fileName.trim().toString(), bytesOfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
