package com.example.a10609516.app.Workers;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class GPSActivity extends WQPServiceActivity {

    private ScrollView gps_scv;
    private LinearLayout gps_llt;
    private Button[] dynamically_btn;
    private Button search_btn, time_start_btn, time_end_btn, clean_start_btn, clean_end_btn;
    private EditText eng_edt;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        //動態取得 View 物件
        InItFunction();
        //確認是否有最新版本，進行更新
        //CheckFirebaseVersion();
        //請求開啟權限
        UsesPermission();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        gps_scv = (ScrollView) findViewById(R.id.gps_scv);
        gps_llt = (LinearLayout) findViewById(R.id.gps_llt);
        search_btn = (Button) findViewById(R.id.search_btn);
        time_start_btn = (Button) findViewById(R.id.start_btn);
        time_end_btn = (Button) findViewById(R.id.end_btn);
        clean_start_btn = (Button) findViewById(R.id.clean_btn1);
        clean_end_btn = (Button) findViewById(R.id.clean_btn2);
        eng_edt = (EditText) findViewById(R.id.eng_edt);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps_llt.removeAllViews();
                //取得工務打卡的GPS位置
                sendRequestWithOkHttpForGPS();
                gps_llt.setVisibility(View.VISIBLE);
            }
        });

        //Clean_Start_Button.setOnClickListener監聽器  //清空time_start_btn內的文字
        clean_start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_start_btn.setText("");
            }
        });//end setOnItemClickListener
        //Clean_End_Button.setOnClickListener監聽器  //清空time_end_btn內的文字
        clean_end_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_end_btn.setText("");
            }
        });//end setOnItemClickListener
    }

    /**
     * 與OkHttp建立連線 取得工務打卡的GPS位置_new
     */
    private void sendRequestWithOkHttpForGPS() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //獲取日期挑選器的數據
                String time_start = time_start_btn.getText().toString();
                String time_end = time_end_btn.getText().toString();
                String eng_name = eng_edt.getText().toString();
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("START_DATE", time_start)
                            .add("END_DATE", time_end)
                            .add("ENG_NAME", eng_name)
                            .build();
                    Log.e("GPSActivity1", time_start);
                    Log.e("GPSActivity1", time_end);
                    Log.e("GPSActivity1", eng_name);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WorkerGPSHR.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("GPSActivity4", responseData);
                    parseJSONWithJSONObjectForGPS(responseData);
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
    private void parseJSONWithJSONObjectForGPS(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            dynamically_btn = new Button[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String eng_name = jsonObject.getString("ENG_NAME");
                String hr_date = jsonObject.getString("PUNCH_DATE");
                String hr_time = jsonObject.getString("PUNCH_TIME");
                String gps_location = jsonObject.getString("GPS_LOCATION");

                Log.e("GPSActivity5", eng_name);
                Log.e("GPSActivity6", hr_date);
                Log.e("GPSActivity7", hr_time);
                Log.e("GPSActivity8", gps_location);

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(eng_name);
                JArrayList.add(hr_date);
                JArrayList.add(hr_time);
                JArrayList.add(gps_location);

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
                    LinearLayout small_llt0 = new LinearLayout(GPSActivity.this);
                    small_llt0.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt1 = new LinearLayout(GPSActivity.this);
                    small_llt1.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt2 = new LinearLayout(GPSActivity.this);
                    small_llt2.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt3 = new LinearLayout(GPSActivity.this);
                    small_llt3.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt4 = new LinearLayout(GPSActivity.this);
                    small_llt4.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt5 = new LinearLayout(GPSActivity.this);
                    small_llt5.setOrientation(LinearLayout.HORIZONTAL);
                    small_llt5.setGravity(Gravity.CENTER);
                    LinearLayout big_llt = new LinearLayout(GPSActivity.this);
                    big_llt.setOrientation(LinearLayout.VERTICAL);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    //int i = b.getStringArrayList("JSON_data").size();
                    JArrayList = jb.getStringArrayList("JSON_data");

                    //顯示每筆LinearLayout的工務
                    TextView dynamically_name;
                    dynamically_name = new TextView(GPSActivity.this);
                    dynamically_name.setText("工務姓名 : " + JArrayList.get(0));
                    dynamically_name.setGravity(Gravity.CENTER);
                    //dynamically_name.setWidth(50);
                    dynamically_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //顯示每筆LinearLayout的打卡日期
                    TextView dynamically_date;
                    dynamically_date = new TextView(GPSActivity.this);
                    dynamically_date.setText("打卡日期 : " + JArrayList.get(1).substring(0, 10));
                    dynamically_date.setGravity(Gravity.CENTER);
                    dynamically_date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //顯示每筆LinearLayout的打卡時間
                    TextView dynamically_time;
                    dynamically_time = new TextView(GPSActivity.this);
                    dynamically_time.setText("打卡時間 : " + JArrayList.get(2));
                    dynamically_time.setGravity(Gravity.CENTER);
                    dynamically_time.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //顯示每筆LinearLayout的GPS位置
                    TextView dynamically_gps;
                    dynamically_gps = new TextView(GPSActivity.this);
                    dynamically_gps.setText("GPS位置 : " + JArrayList.get(3));
                    dynamically_gps.setGravity(Gravity.CENTER);
                    //dynamically_gps.setWidth(50);
                    dynamically_gps.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //設置每筆TableLayout的分隔線
                    TextView dynamically_txt = new TextView(GPSActivity.this);
                    dynamically_txt.setBackgroundColor(Color.rgb(220, 220, 220));

                    //設置每筆TableLayout的Button監聽器、與動態新增Button的ID
                    int loc = 0;
                    for (int i = 0; i < dynamically_btn.length; i++) {
                        if (dynamically_btn[i] == null) {
                            loc = i;
                            break;
                        }
                    }
                    dynamically_btn[loc] = new Button(GPSActivity.this);
                    dynamically_btn[loc].setBackgroundResource(R.drawable.googlemap);
                    //dynamically_btn[loc].setText("Google Map");
                    //dynamically_btn[loc].setPadding(10, 0, 10, 0);
                    dynamically_btn[loc].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    dynamically_btn[loc].setTextColor(Color.rgb(6, 102, 219));
                    dynamically_btn[loc].setId(loc);
                    dynamically_btn[loc].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int a = 0; a < dynamically_btn.length; a++) {
                                if (v.getId() == dynamically_btn[a].getId()) {
                                    Intent intent_gps = new Intent(GPSActivity.this, WorkersMapsActivity.class);
                                    LinearLayout location_llt = (LinearLayout) gps_llt.getChildAt(a);
                                    for (int x = 1; x < 5; x++) {
                                        LinearLayout gps_hr_llt = (LinearLayout) location_llt.getChildAt(x);
                                        TextView gps_txt = (TextView) gps_hr_llt.getChildAt(0);
                                        String gps = gps_txt.getText().toString();
                                        //將SQL裡的資料傳到MapsActivity
                                        Bundle bundle = new Bundle();
                                        bundle.putString("gps_location" + x, gps);

                                        //intent_gps.putExtra("TitleText", TitleText);//可放所有基本類別
                                        intent_gps.putExtras(bundle);//可放所有基本類別
                                    }
                                    startActivity(intent_gps);
                                    //進入MapsActivity後 清空gps_llt的資料
                                    gps_llt.removeAllViews();
                                }
                            }
                        }
                    });

                    LinearLayout.LayoutParams small_pm = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                    LinearLayout.LayoutParams btn_pm = new LinearLayout.LayoutParams(350, 80);

                    small_llt0.addView(dynamically_txt, LinearLayout.LayoutParams.MATCH_PARENT, 3);
                    small_llt1.addView(dynamically_name, small_pm);
                    small_llt2.addView(dynamically_date, small_pm);
                    small_llt3.addView(dynamically_time, small_pm);
                    small_llt4.addView(dynamically_gps, small_pm);
                    small_llt5.addView(dynamically_btn[loc], btn_pm);

                    big_llt.addView(small_llt0);
                    big_llt.addView(small_llt1);
                    big_llt.addView(small_llt2);
                    big_llt.addView(small_llt3);
                    big_llt.addView(small_llt4);
                    big_llt.addView(small_llt5);

                    gps_llt.addView(big_llt);
                    LinearLayout first_llt = (LinearLayout) gps_llt.getChildAt(0);
                    first_llt.getChildAt(0).setVisibility(View.GONE);
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
                gps_scv.fullScroll(ScrollView.FOCUS_UP);
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
                gps_scv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    /**
     * 請求開啟儲存、相機權限、GPS
     */
    private void UsesPermission() {
        // Here, thisActivity is the current activity、GPS
        if (ContextCompat.checkSelfPermission(GPSActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(GPSActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(GPSActivity.this)
                        .setMessage("我真的沒有要做壞事, 給我權限吧?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(GPSActivity.this,
                                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
                                                , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(GPSActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("GPSActivity", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("GPSActivity", "onDestroy");
    }
}

