package com.example.a10609516.app.Tools;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.a10609516.app.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scanner);
        //設置畫面
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.main_activity);
        //設定Scanner
        mScannerView = new ZXingScannerView(this);
        //把Scanner加入
        contentFrame.addView(mScannerView);
//        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        //設置Scanner的回調
        mScannerView.setResultHandler(this);
        //開啟相機
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        //停止相機
        mScannerView.stopCamera();
    }

    //設定回調，當你掃完QR你將要做的事情
    @Override
    public void handleResult(final Result rawResult) {
        Toast.makeText(this, "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

        /**
         * 這邊等兩秒才回調資料
         * 比較舊的設備有時候會產生無法預期的錯誤
         * 這邊作者使用延遲兩秒去debug
         * */
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //把掃描到的資料回調到MainActivity
                Intent data = new Intent();
                data.putExtra("result_text", rawResult.getText());
                setResult(RESULT_OK, data);
                finish();
                mScannerView.resumeCameraPreview(ScannerActivity.this);
            }
        }, 2000);
    }
}