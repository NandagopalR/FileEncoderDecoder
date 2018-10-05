package com.nanda.fileencoder.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showToast(String input) {
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }

}
