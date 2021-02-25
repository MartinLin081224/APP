package com.example.a10609516.app.Workers;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

public class EngPointsActivity extends WQPServiceActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ScrollView points_scv;
    private LinearLayout points_llt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eng_points);
        //動態取得 View 物件
        InItFunction();
        //與OKHttp連線(工務點數明細 WorkPointsDetail.php)
        sendRequestWithOkHttpForWorkPointsDetail();
        //確認是否有最新版本，進行更新
        //CheckFirebaseVersion();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        points_scv = (ScrollView) findViewById(R.id.points_scv);
        points_llt = (LinearLayout) findViewById(R.id.points_llt);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

        //UI介面下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                points_llt.removeAllViews();
                //與OKHttp連線(工務點數明細 WorkPointsDetail.php)
                sendRequestWithOkHttpForWorkPointsDetail();
            }
        });
    }

    /**
     * 與OKHttp連線(工務點數明細)_new
     */
    private void sendRequestWithOkHttpForWorkPointsDetail() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("EngPointsActivity", user_id_data);

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WorkPointsDetail.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("EngPointsActivity2", responseData);
                    parseJSONWithJSONObjectForWorkPointsDetail(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 取得工務點數(A、B)
     *
     * @param jsonData
     */
    private void parseJSONWithJSONObjectForWorkPointsDetail(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String service_date = jsonObject.getString("WISH_DATETIME");
                String esvd_service_no = jsonObject.getString("SERVICE_NO");
                String work_type_name = jsonObject.getString("WORK_TYPE");
                String esvd_a_point = jsonObject.getString("A_POINTS");
                String esvd_b_point = jsonObject.getString("B_POINTS");
                String esvd_d_point = jsonObject.getString("D_POINTS");
                String esvd_eng_money = jsonObject.getString("POINTS_MONEY");

                Log.e("EngPointsActivity", work_type_name);
                Log.e("EngPointsActivity", esvd_a_point);
                Log.e("EngPointsActivity", esvd_b_point);
                Log.e("EngPointsActivity", esvd_d_point);
                Log.e("EngPointsActivity", esvd_eng_money);

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(service_date);
                JArrayList.add(esvd_service_no);
                JArrayList.add(work_type_name);
                JArrayList.add(esvd_a_point);
                JArrayList.add(esvd_b_point);
                JArrayList.add(esvd_d_point);
                JArrayList.add(esvd_eng_money);

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
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    LinearLayout small_llt0 = new LinearLayout(EngPointsActivity.this);
                    small_llt0.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt1 = new LinearLayout(EngPointsActivity.this);
                    small_llt1.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt2 = new LinearLayout(EngPointsActivity.this);
                    small_llt2.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt3 = new LinearLayout(EngPointsActivity.this);
                    small_llt3.setOrientation(LinearLayout.HORIZONTAL);
                    small_llt3.setBackgroundResource(R.drawable.part_line3);
                    LinearLayout small_llt4 = new LinearLayout(EngPointsActivity.this);
                    small_llt4.setOrientation(LinearLayout.HORIZONTAL);
                    small_llt4.setBackgroundResource(R.drawable.part_line3);
                    LinearLayout line_llt1 = new LinearLayout(EngPointsActivity.this);
                    line_llt1.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout line_llt2 = new LinearLayout(EngPointsActivity.this);
                    line_llt2.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout line_llt3 = new LinearLayout(EngPointsActivity.this);
                    line_llt3.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout line_llt4 = new LinearLayout(EngPointsActivity.this);
                    line_llt4.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout big_llt = new LinearLayout(EngPointsActivity.this);
                    big_llt.setOrientation(LinearLayout.VERTICAL);
                    big_llt.setBackgroundResource(R.drawable.part_line1);
                    LinearLayout empty_llt1 = new LinearLayout(EngPointsActivity.this);
                    empty_llt1.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout empty_llt2 = new LinearLayout(EngPointsActivity.this);
                    empty_llt2.setOrientation(LinearLayout.VERTICAL);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    //int i = b.getStringArrayList("JSON_data").size();
                    JArrayList = jb.getStringArrayList("JSON_data");

                    //顯示每筆LinearLayout的日期
                    TextView dynamically_date;
                    dynamically_date = new TextView(EngPointsActivity.this);
                    dynamically_date.setText("派工日期 : " + JArrayList.get(0).substring(0, 16));
                    dynamically_date.setGravity(Gravity.CENTER);
                    dynamically_date.setPadding(0, 10, 0, 10);
                    //dynamically_date.setWidth(50);
                    dynamically_date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

                    //顯示每筆LinearLayout的派工類別
                    TextView dynamically_type;
                    dynamically_type = new TextView(EngPointsActivity.this);
                    dynamically_type.setText("派工類別 : " + JArrayList.get(2));
                    dynamically_type.setGravity(Gravity.CENTER);
                    dynamically_type.setPadding(0, 10, 0, 10);
                    //dynamically_type.setWidth(50);
                    dynamically_type.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

                    //顯示每筆LinearLayout的派工單號
                    TextView dynamically_no;
                    dynamically_no = new TextView(EngPointsActivity.this);
                    dynamically_no.setText("派工單號 : " + JArrayList.get(1));
                    dynamically_no.setGravity(Gravity.CENTER);
                    dynamically_no.setPadding(0, 10, 0, 10);
                    //dynamically_no.setWidth(50);
                    dynamically_no.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

                    //顯示每筆LinearLayout的A點數
                    TextView dynamically_a_points;
                    dynamically_a_points = new TextView(EngPointsActivity.this);
                    dynamically_a_points.setText("A點數 : " + JArrayList.get(3));
                    dynamically_a_points.setGravity(Gravity.CENTER);
                    dynamically_a_points.setPadding(0, 10, 0, 10);
                    //dynamically_a_points.setWidth(50);
                    dynamically_a_points.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

                    //顯示每筆LinearLayout的B點數
                    TextView dynamically_b_points;
                    dynamically_b_points = new TextView(EngPointsActivity.this);
                    dynamically_b_points.setText("B點數 : " + JArrayList.get(4));
                    dynamically_b_points.setGravity(Gravity.CENTER);
                    dynamically_b_points.setPadding(0, 10, 0, 10);
                    //dynamically_b_points.setWidth(50);
                    dynamically_b_points.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

                    //顯示每筆LinearLayout的D點數
                    TextView dynamically_d_points;
                    dynamically_d_points = new TextView(EngPointsActivity.this);
                    dynamically_d_points.setText("D點數 : " + JArrayList.get(5));
                    dynamically_d_points.setGravity(Gravity.CENTER);
                    dynamically_d_points.setPadding(0, 10, 0, 10);
                    //dynamically_d_points.setWidth(50);
                    dynamically_d_points.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

                    //顯示每筆LinearLayout的點數獎金
                    TextView dynamically_eng_money;
                    dynamically_eng_money = new TextView(EngPointsActivity.this);
                    dynamically_eng_money.setText("點數獎金 : " + JArrayList.get(6));
                    dynamically_eng_money.setGravity(Gravity.CENTER);
                    dynamically_eng_money.setPadding(0, 10, 0, 10);
                    //dynamically_eng_money.setWidth(50);
                    dynamically_eng_money.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

                    //設置每筆TableLayout的分隔線
                    TextView dynamically_txt1 = new TextView(EngPointsActivity.this);
                    dynamically_txt1.setBackgroundColor(Color.rgb(224, 255, 255));
                    //設置每筆TableLayout的分隔線
                    TextView dynamically_txt2 = new TextView(EngPointsActivity.this);
                    dynamically_txt2.setBackgroundColor(Color.rgb(224, 255, 255));
                    //設置每筆TableLayout的分隔線
                    TextView dynamically_txt3 = new TextView(EngPointsActivity.this);
                    dynamically_txt3.setBackgroundColor(Color.rgb(224, 255, 255));
                    //設置每筆TableLayout的分隔線
                    TextView dynamically_txt4 = new TextView(EngPointsActivity.this);
                    dynamically_txt4.setBackgroundColor(Color.rgb(224, 255, 255));
                    //設置每筆TableLayout的分隔線
                    TextView dynamically_txt5 = new TextView(EngPointsActivity.this);
                    dynamically_txt5.setBackgroundColor(Color.rgb(224, 255, 255));
                    //設置每筆TableLayout的分隔線
                    TextView dynamically_txt6 = new TextView(EngPointsActivity.this);
                    dynamically_txt6.setBackgroundColor(Color.rgb(224, 255, 255));

                    LinearLayout.LayoutParams small_pm = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

                    small_llt0.addView(dynamically_date, small_pm);
                    line_llt1.addView(dynamically_txt1, LinearLayout.LayoutParams.MATCH_PARENT, 3);
                    small_llt1.addView(dynamically_no, small_pm);
                    line_llt2.addView(dynamically_txt2, LinearLayout.LayoutParams.MATCH_PARENT, 3);
                    small_llt2.addView(dynamically_type, small_pm);
                    line_llt3.addView(dynamically_txt3, LinearLayout.LayoutParams.MATCH_PARENT, 3);
                    small_llt3.addView(dynamically_a_points, small_pm);
                    small_llt3.addView(dynamically_txt4, 3, LinearLayout.LayoutParams.MATCH_PARENT);
                    small_llt3.addView(dynamically_b_points, small_pm);
                    line_llt4.addView(dynamically_txt5, LinearLayout.LayoutParams.MATCH_PARENT, 3);
                    small_llt4.addView(dynamically_d_points, small_pm);
                    small_llt4.addView(dynamically_txt6, 3, LinearLayout.LayoutParams.MATCH_PARENT);
                    small_llt4.addView(dynamically_eng_money, small_pm);

                    big_llt.addView(small_llt0);
                    big_llt.addView(line_llt1);
                    big_llt.addView(small_llt1);
                    big_llt.addView(line_llt2);
                    big_llt.addView(small_llt2);
                    big_llt.addView(line_llt3);
                    big_llt.addView(small_llt3);
                    big_llt.addView(line_llt4);
                    big_llt.addView(small_llt4);
                    big_llt.addView(empty_llt2, LinearLayout.LayoutParams.MATCH_PARENT, 12);

                    points_llt.addView(big_llt);
                    points_llt.addView(empty_llt1, LinearLayout.LayoutParams.MATCH_PARENT, 10);

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 實現畫面置頂按鈕
     *
     * @param view
     */
    public void GoTopBtn(View view) {
        Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            public void run() {
                //實現畫面置頂按鈕
                points_scv.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    /**
     * 實現畫面置底按鈕
     *
     * @param view
     */
    public void GoDownBtn(View view) {
        Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            public void run() {
                //實現畫面置底按鈕
                points_scv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("EngPointsActivity", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("EngPointsActivity", "onDestroy");
    }
}

