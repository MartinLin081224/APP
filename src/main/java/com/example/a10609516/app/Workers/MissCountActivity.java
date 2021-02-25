package com.example.a10609516.app.Workers;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a10609516.app.R;
import com.example.a10609516.app.Tools.WQPServiceActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MissCountActivity extends WQPServiceActivity {

    private LinearLayout miss_llt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miss_count);
        //動態取得 View 物件
        InItFunction();
        //與OKHttp連線(GroupMissCount)工務未回報派工數量
        sendRequestWithOkHttp();
        //確認是否有最新版本，進行更新
        //CheckFirebaseVersion();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        miss_llt = (LinearLayout) findViewById(R.id.miss_llt);
    }

    /**
     * 與OkHttp建立連線
     */
    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.i("MissCountActivity", user_id_data);
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .build();
                    Log.i("MissCountActivity", user_id_data);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/GroupMissCount.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.i("MissCountActivity", responseData);
                    parseJSONWithJSONObject(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 獲得JSON字串並解析成String字串
     *
     * @param jsonData
     */
    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String eng_local = jsonObject.getString("LOCAL_PLACE");
                String eng_name = jsonObject.getString("ENG_EMP");
                String miss_count = jsonObject.getString("MISSED");

                Log.e("MissCountActivity", eng_name);

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(eng_local);
                JArrayList.add(eng_name);
                JArrayList.add(miss_count);

                //HandlerMessage更新UI
                Bundle b = new Bundle();
                b.putStringArrayList("JSON_data", JArrayList);
                Message msg = mHandler.obtainMessage();
                msg.setData(b);
                msg.what = 1;
                msg.sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新UI
     */
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    LinearLayout small_llt = new LinearLayout(MissCountActivity.this);
                    small_llt.setOrientation(LinearLayout.HORIZONTAL);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    //int i = b.getStringArrayList("JSON_data").size();
                    JArrayList = jb.getStringArrayList("JSON_data");

                    //顯示每筆LinearLayout的地區
                    TextView dynamically_local;
                    dynamically_local = new TextView(MissCountActivity.this);
                    dynamically_local.setText(JArrayList.get(0).substring(0, 2));
                    dynamically_local.setGravity(Gravity.CENTER);
                    //dynamically_local.setWidth(50);
                    dynamically_local.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

                    //顯示每筆LinearLayout的工務
                    TextView dynamically_name;
                    dynamically_name = new TextView(MissCountActivity.this);
                    dynamically_name.setText(JArrayList.get(1));
                    dynamically_name.setGravity(Gravity.CENTER);
                    //dynamically_name.setWidth(50);
                    dynamically_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

                    //顯示每筆LinearLayout的數量
                    TextView dynamically_count;
                    dynamically_count = new TextView(MissCountActivity.this);
                    dynamically_count.setText(JArrayList.get(2));
                    dynamically_count.setGravity(Gravity.CENTER);
                    dynamically_count.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

                    //設置每筆TableLayout的分隔線
                    TextView dynamically_txt = new TextView(MissCountActivity.this);
                    dynamically_txt.setBackgroundColor(Color.rgb(220, 220, 220));

                    LinearLayout.LayoutParams small_pm1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
                    LinearLayout.LayoutParams small_pm2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                    small_llt.addView(dynamically_local, small_pm1);
                    small_llt.addView(dynamically_name, small_pm2);
                    small_llt.addView(dynamically_count, small_pm2);

                    miss_llt.addView(dynamically_txt, LinearLayout.LayoutParams.MATCH_PARENT, 3);
                    miss_llt.addView(small_llt);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}