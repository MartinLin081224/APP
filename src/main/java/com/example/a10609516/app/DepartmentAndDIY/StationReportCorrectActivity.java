package com.example.a10609516.app.DepartmentAndDIY;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10609516.app.R;
import com.example.a10609516.app.Tools.WQPServiceActivity;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StationReportCorrectActivity extends WQPServiceActivity {

    private TextView id_txt, product_txt;
    private EditText count_edt, amount_edt;
    private Button check_btn, cancel_btn, delete_btn, delete_check_btn;

    private String LOG = "StationReportCorrectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_report_correct);

        //動態取得 View 物件
        InItFunction();
        //取得OrderSearchActivity傳遞過來的值
        GetResponseData();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        id_txt = (TextView) findViewById(R.id.id_txt);
        product_txt = (TextView) findViewById(R.id.product_txt);
        count_edt = (EditText) findViewById(R.id.count_edt);
        amount_edt = (EditText) findViewById(R.id.amount_edt);
        check_btn = (Button) findViewById(R.id.check_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        delete_btn = (Button) findViewById(R.id.delete_btn);
        delete_check_btn = (Button) findViewById(R.id.delete_check_btn);

        check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //與OkHttp建立連線 修改日報明細
                sendRequestWithOkHttpForStationReportCorrectDetail();
                Toast.makeText(StationReportCorrectActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StationReportCorrectActivity.this, "取消", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_check_btn.setVisibility(View.VISIBLE);
                delete_btn.setVisibility(View.GONE);
            }
        });

        delete_check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //與OkHttp建立連線 刪除日報明細
                sendRequestWithOkHttpForStationReportDeleteDetail();
                Toast.makeText(StationReportCorrectActivity.this, "刪除此明細", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * 取得SearchActivity傳遞過來的值
     */
    private void GetResponseData() {
        Bundle bundle = getIntent().getExtras();
        String ResponseText0 = bundle.getString("ResponseText" + 0);
        String ResponseText1 = bundle.getString("ResponseText" + 1);
        String ResponseText2 = bundle.getString("ResponseText" + 2);
        String ResponseText3 = bundle.getString("ResponseText" + 3);
        id_txt.setText(ResponseText0);
        product_txt.setText(ResponseText1);
        count_edt.setText(ResponseText2);
        amount_edt.setText(ResponseText3);
        Log.e(LOG, ResponseText0);
        Log.e(LOG, ResponseText1);
        Log.e(LOG, ResponseText2);
        Log.e(LOG, ResponseText3);
    }

    /**
     * 與OkHttp建立連線 修改日報明細
     */
    private void sendRequestWithOkHttpForStationReportCorrectDetail() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String D_R_ID = id_txt.getText().toString();
                String item_count = count_edt.getText().toString();
                String item_amount = amount_edt.getText().toString();

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("D_R_ID", D_R_ID)
                            .add("ITEM_COUNT", item_count)
                            .add("ITEM_AMOUNT", item_amount)
                            .build();
                    Log.e(LOG, D_R_ID);
                    Request request = new Request.Builder()
                            //.url("http://192.168.0.172/WQP/StationShopBusinessCorrectDetail.php")
                            .url("http://a.wqp-water.com.tw/WQP/StationShopBusinessCorrectDetail.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e(LOG, requestBody.toString());
                    Log.e(LOG, response.toString());
                    Log.e(LOG, responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 與OkHttp建立連線 刪除日報明細
     */
    private void sendRequestWithOkHttpForStationReportDeleteDetail() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String D_R_ID = id_txt.getText().toString();

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("D_R_ID", D_R_ID)
                            .build();
                    Log.e(LOG, D_R_ID);
                    Request request = new Request.Builder()
                            //.url("http://192.168.0.172/WQP/StationShopBusinessDeleteDetail.php")
                            .url("http://a.wqp-water.com.tw/WQP/StationShopBusinessDeleteDetail.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e(LOG, requestBody.toString());
                    Log.e(LOG, response.toString());
                    Log.e(LOG, responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
