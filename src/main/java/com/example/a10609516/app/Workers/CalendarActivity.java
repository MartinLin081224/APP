package com.example.a10609516.app.Workers;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;

import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import android.widget.TextView;

import com.example.a10609516.app.R;
import com.example.a10609516.app.Tools.WQPServiceActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CalendarActivity extends WQPServiceActivity {

    private LinearLayout search_llt, calendar_llt;
    private Spinner company_spinner;
    private TextView date_txt, search_up_txt, search_down_txt, dynamically_txt, dynamically_type,
            dynamically_customer, dynamically_phone, dynamically_address;
    private Button work_date_btn, last_btn, search_btn, next_btn;
    private ProgressBar dynamically_PGTime;

    int x, y;
    private String date, last_date, next_date;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        //動態取得 View 物件
        InItFunction();
        //分公司的Spinner下拉選單
        WorkTypeSpinner();
        //獲取當天日期
        GetDate();
        //與OKHttp連線取得行事曆資料
        sendRequestWithOKHttpOfCalendar();
        //確認是否有最新版本，進行更新
        //CheckFirebaseVersion();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        search_llt = (LinearLayout) findViewById(R.id.search_llt);
        calendar_llt = (LinearLayout) findViewById(R.id.calendar_llt);
        company_spinner = (Spinner) findViewById(R.id.company_spinner);
        date_txt = (TextView) findViewById(R.id.date_txt);
        search_up_txt = (TextView) findViewById(R.id.search_up_txt);
        search_down_txt = (TextView) findViewById(R.id.search_down_txt);
        work_date_btn = (Button) findViewById(R.id.work_date_btn);
        last_btn = (Button) findViewById(R.id.last_btn);
        search_btn = (Button) findViewById(R.id.search_btn);
        next_btn = (Button) findViewById(R.id.next_btn);

        //點查詢收起search_llt
        search_up_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_llt.setVisibility(View.GONE);
                search_up_txt.setVisibility(View.GONE);
                search_down_txt.setVisibility(View.VISIBLE);
            }
        });

        //點查詢打開search_llt
        search_down_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_llt.setVisibility(View.VISIBLE);
                search_up_txt.setVisibility(View.VISIBLE);
                search_down_txt.setVisibility(View.GONE);
            }
        });

        //查詢日期選擇器的日期
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_txt.setText(work_date_btn.getText().toString());
                calendar_llt.removeAllViews();
                sendRequestWithOKHttpOfSearch();
            }
        });

        //查詢往上一日
        last_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar_llt.removeAllViews();

                LastDate();
                date_txt.setText(last_date);
                work_date_btn.setText(last_date);

                sendRequestWithOKHttpOfLast();
            }
        });

        //查詢往下一日
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar_llt.removeAllViews();

                NextDate();
                date_txt.setText(next_date);
                work_date_btn.setText(next_date);

                sendRequestWithOKHttpOfNext();
            }
        });
    }

    /**
     * 分公司的Spinner下拉選單
     */
    private void WorkTypeSpinner() {
        final String[] select = {"請選擇  ", "台北拓亞鈦,快撥28868", "桃園分公司,快撥31338", "新竹分公司,快撥37888", "台中分公司,快撥42088", "高雄分公司,快撥73568"};
        ArrayAdapter<String> selectList = new ArrayAdapter<String>(CalendarActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                select);
        company_spinner.setAdapter(selectList);
    }

    /**
     * 獲取當天日期
     */
    private void GetDate() {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = simpleDateFormat.format(new java.util.Date());
        date_txt.setText(date);
        work_date_btn.setText(date);
    }

    /**
     * 獲取date_txt的前一日
     */
    private void LastDate() {
        String view_date = work_date_btn.getText().toString();
        Log.e("CalendarActivity2", view_date);
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(simpleDateFormat.parse(view_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, -1);
        //SimpleDateFormat outLast = new SimpleDateFormat("yyyy-MM-dd");
        last_date = simpleDateFormat.format(c.getTime());
    }

    /**
     * 獲取date_txt的後一日
     */
    private void NextDate() {
        String view_date = work_date_btn.getText().toString();
        Log.e("CalendarActivity2", view_date);
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(simpleDateFormat.parse(view_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 1);
        //SimpleDateFormat outNext = new SimpleDateFormat("yyyy-MM-dd");
        next_date = simpleDateFormat.format(c.getTime());
    }

    /**
     * 與OkHttp(CalendarData)建立連線_new
     */
    private void sendRequestWithOKHttpOfCalendar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("CalendarActivity", user_id_data);

                String getdate = date_txt.getText().toString();

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .add("Work_date", getdate)
                            .build();
                    Log.e("CalendarActivity", getdate);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/CalendarData.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("CalendarActivity", responseData);
                    parseJSONWithJSONObjectOfCalendar(responseData);
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
    private void parseJSONWithJSONObjectOfCalendar(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String esvd_booking_period = jsonObject.getString("START_TIME");
                String esvd_booking_period_end = jsonObject.getString("END_TIME");
                String WORK_TYPE_NAME = jsonObject.getString("WORK_TYPE");
                String ESV_CONTACTOR = jsonObject.getString("CONTACT_PERSON");
                String ESV_TEL_NO1 = jsonObject.getString("ESV_TEL_NO1");
                String ESV_ADDRESS = jsonObject.getString("ESV_ADDRESS");
                String User_name = jsonObject.getString("EMPLOYEE_A");
                String User_name2 = jsonObject.getString("EMPLOYEE_B");
                String Group_name = jsonObject.getString("COMPANY");
                String turn_id = jsonObject.getString("ID");

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(esvd_booking_period);
                JArrayList.add(esvd_booking_period_end);
                JArrayList.add(WORK_TYPE_NAME);
                JArrayList.add(ESV_CONTACTOR);
                JArrayList.add(ESV_TEL_NO1);
                JArrayList.add(ESV_ADDRESS);
                JArrayList.add(User_name);
                JArrayList.add(User_name2);
                JArrayList.add(Group_name);
                JArrayList.add(turn_id);

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
     * 與OkHttp(CalendarData)建立連線_new
     */
    private void sendRequestWithOKHttpOfSearch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("CalendarActivity", user_id_data);

                String s_date = work_date_btn.getText().toString();
                String group_name = String.valueOf(company_spinner.getSelectedItem()).substring(0, 5);
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .add("Work_date", s_date)
                            .add("Group_name", group_name)
                            .build();
                    Log.e("CalendarActivity", user_id_data);
                    Log.e("CalendarActivity", s_date);
                    Log.e("CalendarActivity", group_name);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/CalendarData.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("CalendarActivity", responseData);
                    parseJSONWithJSONObjectOfSearch(responseData);
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
    private void parseJSONWithJSONObjectOfSearch(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String esvd_booking_period = jsonObject.getString("START_TIME");
                String esvd_booking_period_end = jsonObject.getString("END_TIME");
                String WORK_TYPE_NAME = jsonObject.getString("WORK_TYPE");
                String ESV_CONTACTOR = jsonObject.getString("CONTACT_PERSON");
                String ESV_TEL_NO1 = jsonObject.getString("ESV_TEL_NO1");
                String ESV_ADDRESS = jsonObject.getString("ESV_ADDRESS");
                String User_name = jsonObject.getString("EMPLOYEE_A");
                String User_name2 = jsonObject.getString("EMPLOYEE_B");
                String Group_name = jsonObject.getString("COMPANY");
                String turn_id = jsonObject.getString("ID");

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(esvd_booking_period);
                JArrayList.add(esvd_booking_period_end);
                JArrayList.add(WORK_TYPE_NAME);
                JArrayList.add(ESV_CONTACTOR);
                JArrayList.add(ESV_TEL_NO1);
                JArrayList.add(ESV_ADDRESS);
                JArrayList.add(User_name);
                JArrayList.add(User_name2);
                JArrayList.add(Group_name);
                JArrayList.add(turn_id);

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
     * 與OkHttp(CalendarData)建立連線_new
     */
    private void sendRequestWithOKHttpOfLast() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String group_name = String.valueOf(company_spinner.getSelectedItem()).substring(0, 5);
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("Work_date", last_date)
                            .add("Group_name", group_name)
                            .build();
                    Log.e("CalendarActivity3", last_date);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/CalendarData.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("CalendarActivity", responseData);
                    parseJSONWithJSONObjectOfLast(responseData);
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
    private void parseJSONWithJSONObjectOfLast(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String esvd_booking_period = jsonObject.getString("START_TIME");
                String esvd_booking_period_end = jsonObject.getString("END_TIME");
                String WORK_TYPE_NAME = jsonObject.getString("WORK_TYPE");
                String ESV_CONTACTOR = jsonObject.getString("CONTACT_PERSON");
                String ESV_TEL_NO1 = jsonObject.getString("ESV_TEL_NO1");
                String ESV_ADDRESS = jsonObject.getString("ESV_ADDRESS");
                String User_name = jsonObject.getString("EMPLOYEE_A");
                String User_name2 = jsonObject.getString("EMPLOYEE_B");
                String Group_name = jsonObject.getString("COMPANY");
                String turn_id = jsonObject.getString("ID");

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(esvd_booking_period);
                JArrayList.add(esvd_booking_period_end);
                JArrayList.add(WORK_TYPE_NAME);
                JArrayList.add(ESV_CONTACTOR);
                JArrayList.add(ESV_TEL_NO1);
                JArrayList.add(ESV_ADDRESS);
                JArrayList.add(User_name);
                JArrayList.add(User_name2);
                JArrayList.add(Group_name);
                JArrayList.add(turn_id);

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
     * 與OkHttp(CalendarData)建立連線_new
     */
    private void sendRequestWithOKHttpOfNext() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String group_name = String.valueOf(company_spinner.getSelectedItem()).substring(0, 5);
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("Work_date", next_date)
                            .add("Group_name", group_name)
                            .build();
                    Log.e("CalendarActivity3", next_date);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/CalendarData.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("CalendarActivity", responseData);
                    parseJSONWithJSONObjectOfNext(responseData);
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
    private void parseJSONWithJSONObjectOfNext(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String esvd_booking_period = jsonObject.getString("START_TIME");
                String esvd_booking_period_end = jsonObject.getString("END_TIME");
                String WORK_TYPE_NAME = jsonObject.getString("WORK_TYPE");
                String ESV_CONTACTOR = jsonObject.getString("CONTACT_PERSON");
                String ESV_TEL_NO1 = jsonObject.getString("ESV_TEL_NO1");
                String ESV_ADDRESS = jsonObject.getString("ESV_ADDRESS");
                String User_name = jsonObject.getString("EMPLOYEE_A");
                String User_name2 = jsonObject.getString("EMPLOYEE_B");
                String Group_name = jsonObject.getString("COMPANY");
                String turn_id = jsonObject.getString("ID");

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(esvd_booking_period);
                JArrayList.add(esvd_booking_period_end);
                JArrayList.add(WORK_TYPE_NAME);
                JArrayList.add(ESV_CONTACTOR);
                JArrayList.add(ESV_TEL_NO1);
                JArrayList.add(ESV_ADDRESS);
                JArrayList.add(User_name);
                JArrayList.add(User_name2);
                JArrayList.add(Group_name);
                JArrayList.add(turn_id);

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
                    //製作Dip
                    Resources resources = getResources();
                    float name_Dip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, resources.getDisplayMetrics());
                    int name_dip = Math.round(name_Dip);
                    float type_Dip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 125, resources.getDisplayMetrics());
                    int type_dip = Math.round(type_Dip);
                    float pg_Dip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26, resources.getDisplayMetrics());
                    int pg_dip = Math.round(pg_Dip);

                    LinearLayout big_llt = new LinearLayout(CalendarActivity.this);
                    big_llt.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout name_llt = new LinearLayout(CalendarActivity.this);
                    name_llt.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt1 = new LinearLayout(CalendarActivity.this);
                    small_llt1.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt2 = new LinearLayout(CalendarActivity.this);
                    small_llt2.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout dynamically_llt = new LinearLayout(CalendarActivity.this);
                    dynamically_llt.setBackgroundResource(R.drawable.calendar_h_divider);
                    HorizontalScrollView dynamically_hsv = new HorizontalScrollView(CalendarActivity.this);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    JArrayList = jb.getStringArrayList("JSON_data");
                    for (int i = 0; i < jb.getStringArrayList("JSON_data").size(); i++) {
                        dynamically_txt = new TextView(CalendarActivity.this);
                        dynamically_txt.setText(JArrayList.get(6).replace("A", ""));
                        dynamically_txt.setTextColor(Color.rgb(6, 102, 219));
                        dynamically_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                        dynamically_txt.setGravity(Gravity.LEFT);
                        dynamically_txt.setPadding(10, 2, 0, 0);
                        dynamically_txt.setWidth(name_dip);
                        dynamically_txt.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                        dynamically_txt.getPaint().setAntiAlias(true);

                        name_llt.addView(dynamically_txt, LinearLayout.LayoutParams.MATCH_PARENT, name_dip);
                        if (dynamically_txt.getText().equals("")) {
                            dynamically_txt.setVisibility(View.GONE);
                        }

                        dynamically_type = new TextView(CalendarActivity.this);
                        dynamically_type.setText(JArrayList.get(0) + " " + JArrayList.get(2));
                        dynamically_type.setTextColor(Color.rgb(6, 102, 219));
                        dynamically_type.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        dynamically_type.setGravity(Gravity.LEFT);
                        dynamically_type.setPadding(5, 0, 0, 4);
                        //dynamically_type.setWidth(type_dip);
                        if (JArrayList.get(0).equals("09:00")) {
                            x = 0;
                        }
                        if (JArrayList.get(0).equals("10:00")) {
                            x = 10;
                        }
                        if (JArrayList.get(0).equals("11:00")) {
                            x = 20;
                        }
                        if (JArrayList.get(0).equals("12:00")) {
                            x = 30;
                        }
                        if (JArrayList.get(0).equals("13:00")) {
                            x = 40;
                        }
                        if (JArrayList.get(0).equals("14:00")) {
                            x = 50;
                        }
                        if (JArrayList.get(0).equals("15:00")) {
                            x = 60;
                        }
                        if (JArrayList.get(0).equals("16:00")) {
                            x = 70;
                        }
                        if (JArrayList.get(0).equals("17:00")) {
                            x = 80;
                        }
                        if (JArrayList.get(0).equals("18:00")) {
                            x = 90;
                        }
                        if (JArrayList.get(0).equals("19:00")) {
                            x = 100;
                        }
                        if (JArrayList.get(0).equals("20:00")) {
                            x = 110;
                        }
                        if (JArrayList.get(0).equals("21:00")) {
                            x = 120;
                        }
                        if (JArrayList.get(1).equals("09:00")) {
                            y = 10;
                        }
                        if (JArrayList.get(1).equals("10:00")) {
                            y = 20;
                        }
                        if (JArrayList.get(1).equals("11:00")) {
                            y = 30;
                        }
                        if (JArrayList.get(1).equals("12:00")) {
                            y = 40;
                        }
                        if (JArrayList.get(1).equals("13:00")) {
                            y = 50;
                        }
                        if (JArrayList.get(1).equals("14:00")) {
                            y = 60;
                        }
                        if (JArrayList.get(1).equals("15:00")) {
                            y = 70;
                        }
                        if (JArrayList.get(1).equals("16:00")) {
                            y = 80;
                        }
                        if (JArrayList.get(1).equals("17:00")) {
                            y = 90;
                        }
                        if (JArrayList.get(1).equals("18:00")) {
                            y = 100;
                        }
                        if (JArrayList.get(1).equals("19:00")) {
                            y = 110;
                        }
                        if (JArrayList.get(1).equals("20:00")) {
                            y = 120;
                        }
                        if (JArrayList.get(1).equals("21:00")) {
                            y = 130;
                        }
                        dynamically_PGTime = new ProgressBar(CalendarActivity.this, null, android.R.attr.progressBarStyleHorizontal);
                        dynamically_PGTime.setMax(130);
                        dynamically_PGTime.setProgress(x);
                        dynamically_PGTime.setSecondaryProgress(y);
                        dynamically_PGTime.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.progressbar_bg, null));
                        //dynamically_PGTime.setBackgroundResource(R.drawable.progressbar_bg);

                        small_llt1.addView(dynamically_type, type_dip, pg_dip);
                        small_llt1.addView(dynamically_PGTime, LinearLayout.LayoutParams.MATCH_PARENT, pg_dip);

                        dynamically_customer = new TextView(CalendarActivity.this);
                        dynamically_customer.setText("客戶名稱 : " + JArrayList.get(3));
                        dynamically_customer.setTextColor(Color.rgb(6, 102, 219));
                        dynamically_customer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                        dynamically_customer.setGravity(Gravity.LEFT);
                        dynamically_customer.setPadding(5, 0, 0, 0);

                        dynamically_phone = new TextView(CalendarActivity.this);
                        dynamically_phone.setText(" 聯絡電話 : " + JArrayList.get(4));
                        dynamically_phone.setTextColor(Color.rgb(6, 102, 219));
                        dynamically_phone.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                        dynamically_phone.setGravity(Gravity.LEFT);
                        dynamically_phone.setPadding(5, 0, 0, 0);

                        dynamically_address = new TextView(CalendarActivity.this);
                        dynamically_address.setText(" 派工地址 : " + JArrayList.get(5));
                        dynamically_address.setTextColor(Color.rgb(6, 102, 219));
                        dynamically_address.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                        dynamically_address.setGravity(Gravity.LEFT);
                        dynamically_address.setPadding(5, 0, 0, 0);
                        //dynamically_address.setTextIsSelectable(true);

                        small_llt2.addView(dynamically_customer);
                        small_llt2.addView(dynamically_phone);
                        small_llt2.addView(dynamically_address);
                    }

                    for (int a = 3; a <= 23; a++) {
                        small_llt2.getChildAt(a).setVisibility(View.GONE);
                    }
                    dynamically_hsv.addView(small_llt2);

                    big_llt.addView(name_llt, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    big_llt.addView(small_llt1, LinearLayout.LayoutParams.MATCH_PARENT, pg_dip);
                    big_llt.addView(dynamically_hsv, LinearLayout.LayoutParams.WRAP_CONTENT, pg_dip);
                    big_llt.addView(dynamically_llt);

                    calendar_llt.addView(big_llt, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("CalendarActivity", "onDestroy");
    }
}
