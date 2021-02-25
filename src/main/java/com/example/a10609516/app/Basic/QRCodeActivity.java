package com.example.a10609516.app.Basic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.example.a10609516.app.R;
import com.example.a10609516.app.Tools.WQPServiceActivity;
import com.example.a10609516.app.Tools.ScannerActivity;

import java.text.SimpleDateFormat;

public class QRCodeActivity extends WQPServiceActivity {

    private Button QRCode_btn;
    private TextView date_txt, result_txt;
    private WebView mWebView;

    //轉畫面的Activity參數
    private Class<?> mClss;
    //ZXING_CAMERA權限
    private static final int ZXING_CAMERA_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        //動態取得 View 物件
        InItFunction();
        //獲取當天日期
        GetDate();
        //初始畫面設置
        initSet();
        //確認是否有最新版本，進行更新
        //CheckFirebaseVersion();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        QRCode_btn = (Button) findViewById(R.id.QRCode_btn);
        date_txt = (TextView) findViewById(R.id.date_txt);
        result_txt = (TextView) findViewById(R.id.result_txt);
        mWebView = (WebView) findViewById(R.id.wv);

        /*QRCode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                if(getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size()==0)
                {
                    //未安裝
                    Toast.makeText(QRCodeActivity.this, "請至 Play商店 安裝 ZXing 條碼掃描器", Toast.LENGTH_LONG).show();
                }else{
                    //SCAN_MODE, 可判別所有支援條碼
                    //QR_CODE_MODE, 只判別QR_CODE
                    //PRODUCT_MODE, UPC and EAN條碼
                    //ONE_D_MODE, 1維條碼
                    intent.putExtra("SCAN_MODE","SCAN_MODE");

                    //呼叫ZXing Scanner,完成動作後回傳1給onActivityResult的requestCode參數
                    startActivityForResult(intent,1);
                }
            }
        });*/
    }

    /**
     * 取回掃描回傳值或取消掃描
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    /*public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                //ZXing回傳的內容
                String contents = intent.getStringExtra("SCAN_RESULT");
                result_txt.setText(contents.toString());
            }else{
                if(resultCode==RESULT_CANCELED){
                    Toast.makeText(QRCodeActivity.this, "取消掃描", Toast.LENGTH_LONG).show();
                }
            }
        }
    }*/

    /**
     * 獲取當天日期
     */
    private void GetDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(new java.util.Date());
        date_txt.setText(date);
    }

    /**
     * 起始畫面設置
     */
    private void initSet() {
        /**
         * 以下都是WebView的設定
         */
        WebSettings websettings = mWebView.getSettings();
        websettings.setSupportZoom(true); //啟用內置的縮放功能
        websettings.setBuiltInZoomControls(true);//啟用內置的縮放功能
        websettings.setDisplayZoomControls(false);//讓縮放功能的Button消失
        websettings.setJavaScriptEnabled(true);//使用JavaScript
        websettings.setAppCacheEnabled(true);//設置啟動緩存
        websettings.setSaveFormData(true);//設置儲存
        websettings.setAllowFileAccess(true);//啟用webview訪問文件數據
        websettings.setDomStorageEnabled(true);//啟用儲存數據
        mWebView.setWebViewClient(new WebViewClient());
    }

    /**
     * Button的設置
     */
    public void scanCode(View view) {
        //startActivityForResult(new Intent(this, ScannerActivity.class), 1);
        launchActivity(ScannerActivity.class);
    }

    /**
     * 轉畫面的封包，兼具權限和Intent跳轉畫面
     */
    public void launchActivity(Class<?> clss) {
        //假如還「未獲取」權限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            //startActivity(intent);
            startActivityForResult(intent, ZXING_CAMERA_PERMISSION);
        }
    }

    /**
     * 當ScannerActivity結束後的回調資訊
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.d("checkpoint", "CheckPoint");
            result_txt.setText(data.getStringExtra("result_text"));
            mWebView.loadUrl(data.getStringExtra("result_text"));
        }
    }
}