package com.nanda.fileencoder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.nanda.fileencoder.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.btn_encode)
    Button btnEncode;
    @BindView(R.id.btn_decode)
    Button btnDecode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_encode, R.id.btn_decode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_encode:
                startActivity(new Intent(HomeActivity.this, EncodeActivity.class));
                break;
            case R.id.btn_decode:
                startActivity(new Intent(HomeActivity.this, DecodeActvity.class));
                break;
        }
    }
}
