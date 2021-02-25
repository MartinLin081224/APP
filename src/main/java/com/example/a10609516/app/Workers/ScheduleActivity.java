package com.example.a10609516.app.Workers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.PagerTitleStrip;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a10609516.app.R;
import com.example.a10609516.app.Tools.WQPServiceActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScheduleActivity extends WQPServiceActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private View view1, view2, view3;
    private List<View> viewList;//view陣列
    private ViewPager viewPager; //對應的viewPager
    private PagerTitleStrip pagerTitleStrip; //對應的viewPager標頭

    private List<String> titleList;  //標題列表陣列
    private List<TextView> txtPoints;

    private LinearLayout lin_points;
    private TableLayout today_TableLayout, week_TableLayout, missing_TableLayout;
    private ImageView[] today_about_ImageView, week_about_ImageView, missing_about_ImageView;
    private Button more_button1, more_button2, more_button3;
    private TextView today_sql_total_textView, week_sql_total_textView, missing_sql_total_textView;

    private Handler handler;
    public int badgeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        //動態取得 View 物件
        InItFunction();
        //設置ViewPager的每個頁面Title
        PagerTitle();
        //ViewPager的各項設置
        ViewPagerAdapter();
        //設置圓點
        initCircle();
        //確認是否有最新版本，進行更新
        //CheckFirebaseVersion();
        sendRequestWithOkHttpOfMissCount();
    }

    /**
     * 設置圓點
     */
    private void initCircle() {
        txtPoints = new ArrayList<>();
        int d = 20;
        int m = 7;
        for (int i = 0; i < viewList.size(); i++) {
            TextView circle_txt = new TextView(this);
            if (i == 0) {
                circle_txt.setBackgroundResource(R.drawable.point_blue);
            } else {
                circle_txt.setBackgroundResource(R.drawable.point_grey);
            }
            LinearLayout.LayoutParams circle_params = new LinearLayout.LayoutParams(d, d);
            circle_params.setMargins(m, m, m, m);
            circle_txt.setLayoutParams(circle_params);
            txtPoints.add(circle_txt);
            lin_points.addView(circle_txt);
        }
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerTitleStrip = (PagerTitleStrip) findViewById(R.id.pager_title);
        lin_points = (LinearLayout) findViewById(R.id.lin_points);
    }

    /**
     * 動態取得 ViewPager 物件
     */
    private void InViewGroupFunction() {
        today_TableLayout = (TableLayout) findViewById(R.id.today_TableLayout);
        week_TableLayout = (TableLayout) findViewById(R.id.week_TableLayout);
        missing_TableLayout = (TableLayout) findViewById(R.id.missing_TableLayout);
        today_sql_total_textView = (TextView) findViewById(R.id.today_sql_total_textView);
        week_sql_total_textView = (TextView) findViewById(R.id.week_sql_total_textView);
        missing_sql_total_textView = (TextView) findViewById(R.id.missing_sql_total_textView);
        more_button1 = (Button) findViewById(R.id.more_button1);
        more_button2 = (Button) findViewById(R.id.more_button2);
        more_button3 = (Button) findViewById(R.id.more_button3);
    }

    /**
     * 設置ViewPager的每個頁面Title
     */
    private void PagerTitle() {
        //為標題設置字體大小
        pagerTitleStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
        //為標題設置字體顏色
        pagerTitleStrip.setTextColor(Color.rgb(255, 217, 230));
        //為標題設置背景顏色
        pagerTitleStrip.setBackgroundColor(Color.rgb(0, 127, 255));
        //設置標題位置
        pagerTitleStrip.setGravity(15);
        pagerTitleStrip.getChildAt(0).setVisibility(View.GONE);
        pagerTitleStrip.getChildAt(2).setVisibility(View.GONE);

        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.activity_day_schedule, null);
        view2 = inflater.inflate(R.layout.activity_week_schedule, null);
        view3 = inflater.inflate(R.layout.activity_missing_date, null);

        viewList = new ArrayList<>();// 將要分頁顯示的View装入陣列中
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);

        titleList = new ArrayList<>();// 每個頁面的Title數據
        titleList.add("今日行程資訊");
        titleList.add("一周行程資訊");
        titleList.add("派工未填抵達日期");
    }

    /**
     * ViewPager的各項設置
     */
    private void ViewPagerAdapter() {
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public CharSequence getPageTitle(int position) {
                // TODO Auto-generated method stub
                return titleList.get(position);
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewList.size();
                //return 10000;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                // TODO Auto-generated method stub
                //根據傳來的Key，找到view,判斷與傳來的參數View arg0是不是同一個layout
                return view == viewList.get(Integer.parseInt(object.toString()));
                //return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                // TODO Auto-generated method stub
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(viewList.get(position));
                //container.addView(viewList.get(position % viewList.size()));
                //動態取得 View 物件
                InViewGroupFunction();
                switch (position) {
                    case 0:
                        //每次移動回來今日行程資訊先刪除之前的TableView再動態新增回來
                        today_TableLayout.removeAllViews();
                        //建立TodayScheduleData.php OKHttp連線
                        sendRequestWithOkHttpForToday();
                        more_button1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent1 = new Intent(ScheduleActivity.this, Day_Work.class);
                                startActivity(intent1);
                            }
                        });
                        break;
                    case 1:
                        //每次移動回來一周行程資訊先刪除之前的TableView再動態新增回來
                        week_TableLayout.removeAllViews();
                        //建立WeekScheduleData.php OKHttp連線
                        sendRequestWithOkHttpForWeek();
                        more_button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent2 = new Intent(ScheduleActivity.this, Week_Work.class);
                                startActivity(intent2);
                            }
                        });
                        break;
                    case 2:
                        //每次移動回來派工未填抵達日期先刪除之前的TableView再動態新增回來
                        missing_TableLayout.removeAllViews();
                        //建立MissingScheduleData.php OKHttp連線
                        sendRequestWithOkHttpForMissing();
                        more_button3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent3 = new Intent(ScheduleActivity.this, Missing_Date_Record.class);
                                startActivity(intent3);
                            }
                        });
                        break;
                    default:
                        break;
                }
                //把當前新增layout的位置（position）做為Key傳過去
                return position;
                //return viewList.get(position % viewList.size());
            }
        });
        //viewPager.setCurrentItem(viewList.size() * 1000);
        //viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changePoints((position)%viewList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     *圓點的位置
     * @param pos
     */
    public void changePoints(int pos) {
        if (txtPoints != null) {
            for (int i = 0; i < txtPoints.size(); i++) {
                if (pos == i) {
                    txtPoints.get(i).setBackgroundResource(R.drawable.point_blue);
                } else {
                    txtPoints.get(i).setBackgroundResource(R.drawable.point_grey);
                }
            }
        }
    }

    /**
     * 與OkHttp(Today)建立連線_new
     */
    private void sendRequestWithOkHttpForToday() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.i("ScheduleActivity", user_id_data);
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .build();
                    Log.i("ScheduleActivity", user_id_data);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/TodayScheduleData.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.i("ScheduleActivity", responseData);
                    parseJSONWithJSONObjectForToday(responseData);
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
    private void parseJSONWithJSONObjectForToday(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            today_about_ImageView = new ImageView[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String work_type_name = jsonObject.getString("WORK_TYPE_NAME");
                String esvd_service_no = jsonObject.getString("ESVD_SERVICE_NO");
                String esv_note = jsonObject.getString("ESV_NOTE");
                String time_period_name = jsonObject.getString("BOOKING_DATETIME");
                String esv_contactor = jsonObject.getString("ESV_CONTACTOR");
                String esv_tel_no1 = jsonObject.getString("ESV_TEL_NO1");
                String esv_tel_no2 = jsonObject.getString("ESV_TEL_NO2");
                String esv_address = jsonObject.getString("ESV_ADDRESS");
                String cp_name = jsonObject.getString("PAY_MODE");
                String esvd_is_get_money = jsonObject.getString("IS_GET_MONEY");
                String esvd_is_money = jsonObject.getString("RECEIVE_MONEY");
                String esvd_is_eng_money = jsonObject.getString("IS_ENG_MONEY");
                String esvd_get_eng_money = jsonObject.getString("GET_ENG_MONEY");
                String esvd_begin_date = jsonObject.getString("ESVD_BEGIN_DATE");
                String esvd_begin_time = jsonObject.getString("ESVD_BEGIN_TIME");
                String esvd_end_time = jsonObject.getString("ESVD_END_TIME");
                String esvd_booking_remark = jsonObject.getString("ESVD_BOOKING_REMARK");
                String esv_item_remark = jsonObject.getString("ESV_ITEM_REMARK");
                String esvd_remark = jsonObject.getString("ESVD_REMARK");
                String esvd_seq_id = jsonObject.getString("ESVD_SEQ_ID");
                String esvd_eng_points = jsonObject.getString("ESVD_ENG_POINTS");
                String esvd_booking_period = jsonObject.getString("ESVD_BOOKING_PERIOD");
                String esvd_booking_period_end = jsonObject.getString("ESVD_BOOKING_PERIOD_End");
                String my_ontype = jsonObject.getString("REPORT_STATUS");
                String reserve_time = jsonObject.getString("WORK_TIME");
                String work_type = jsonObject.getString("WORK_TYPE");

                Log.e("ScheduleActivity", reserve_time);
                Log.e("ScheduleActivity", work_type_name);

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(work_type_name);
                JArrayList.add(esvd_service_no);
                JArrayList.add(esv_note);
                JArrayList.add(time_period_name);
                JArrayList.add(esv_contactor);
                JArrayList.add(esv_tel_no1);
                JArrayList.add(esv_tel_no2);
                JArrayList.add(esv_address);
                JArrayList.add(cp_name);
                JArrayList.add(esvd_is_get_money);
                JArrayList.add(esvd_is_money);
                JArrayList.add(esvd_is_eng_money);
                JArrayList.add(esvd_get_eng_money);
                JArrayList.add(esvd_begin_date);
                JArrayList.add(esvd_begin_time);
                JArrayList.add(esvd_end_time);
                JArrayList.add(esvd_booking_remark);
                JArrayList.add(esv_item_remark);
                JArrayList.add(esvd_remark);
                JArrayList.add(esvd_seq_id);
                JArrayList.add(esvd_eng_points);
                JArrayList.add(esvd_booking_period);
                JArrayList.add(esvd_booking_period_end);
                JArrayList.add(my_ontype);
                JArrayList.add(reserve_time);
                JArrayList.add(work_type);

                //HandlerMessage更新UI
                Bundle b = new Bundle();
                b.putStringArrayList("JSON_data", JArrayList);
                Message msg = today_mHandler.obtainMessage();
                msg.setData(b);
                msg.what = 1;
                msg.sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新UI(Today)
     */
    Handler today_mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final String[] title_array = {"派工類別", "派工單號", "送貨客戶", "預約日期時段", "聯絡人",
                                          "主要電話", "次要電話", "派工地址", "付款方式", "是否要收款",
                                          "應收款金額", "是否已收款", "已收款金額", "抵達日期", "抵達時間",
                                          "結束時間", "任務說明", "料品說明", "工作說明", "派工資料識別碼",
                                          "工務點數", "預約開始時間", "預約結束時間", "狀態", "今日派工時段 :", "處理方式 :"};
            switch (msg.what) {
                case 1:
                    Resources resources = getResources();
                    float type_Dip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, resources.getDisplayMetrics());
                    int type_dip = Math.round(type_Dip);
                    //最外層有一個大的TableLayout,再設置TableRow包住小的TableLayout
                    today_TableLayout.setStretchAllColumns(true);
                    //設置大TableLayout的TableRow
                    TableRow big_tbr = new TableRow(ScheduleActivity.this);
                    //設定big_tbr的Weight權重
                    big_tbr.setWeightSum(3);
                    //設置每筆資料的小TableLayout
                    TableLayout small_tb = new TableLayout(ScheduleActivity.this);
                    //全部列自動填充空白處
                    small_tb.setStretchAllColumns(true);
                    //設定TableRow的寬高
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    JArrayList = jb.getStringArrayList("JSON_data");
                    //設置每筆TableLayout的Button監聽器、與動態新增Button的ID
                    int loc = 0;
                    for (int j = 0; j < today_about_ImageView.length; j++) {
                        if (today_about_ImageView[j] == null) {
                            loc = j;
                            break;
                        }
                    }
                    today_about_ImageView[loc] = new ImageView(ScheduleActivity.this);
                    today_about_ImageView[loc].setImageResource(R.drawable.about);
                    today_about_ImageView[loc].setScaleType(ImageView.ScaleType.CENTER);
                    today_about_ImageView[loc].setId(loc);
                    today_about_ImageView[loc].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int a = 0; a < today_about_ImageView.length; a++) {
                                if (v.getId() == today_about_ImageView[a].getId()) {
                                    Intent intent_maintain = new Intent(ScheduleActivity.this, MaintainActivity.class);
                                    //取得大TableLayout中的大TableRow位置
                                    TableRow tb_tbr = (TableRow) today_TableLayout.getChildAt(a);
                                    //取得大TableRow中的小TableLayout位置
                                    TableLayout tb_tbr_tb = (TableLayout) tb_tbr.getChildAt(1);
                                    //派工資料的迴圈
                                    for (int x = 0; x < 24; x++) {
                                        //取得小TableLayout中的小TableRow位置
                                        TableRow tb_tbr_tb_tbr = (TableRow) tb_tbr_tb.getChildAt(x);
                                        //小TableRow中的layout_column位置
                                        TextView ThirdTextView = (TextView) tb_tbr_tb_tbr.getChildAt(1);
                                        String ResponseText = ThirdTextView.getText().toString();
                                        //將SQL裡的資料傳到MaintainActivity
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ResponseText" + x, ResponseText);

                                        intent_maintain.putExtras(bundle);//可放所有基本類別
                                    }
                                    startActivity(intent_maintain);
                                }
                            }
                        }
                    });

                    for (int i = 0; i < jb.getStringArrayList("JSON_data").size(); i++) {
                        //顯示每筆TableLayout的Title
                        TextView dynamically_title;
                        dynamically_title = new TextView(ScheduleActivity.this);
                        dynamically_title.setText(title_array[i].toString());
                        dynamically_title.setPadding(8, 2, 0, 0);
                        dynamically_title.setGravity(Gravity.LEFT);
                        dynamically_title.setTextColor(Color.rgb(6, 102, 219));
                        dynamically_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

                        //顯示每筆TableLayout的SQL資料
                        TextView dynamically_txt;
                        dynamically_txt = new TextView(ScheduleActivity.this);
                        dynamically_txt.setText(JArrayList.get(i));
                        dynamically_txt.setPadding(0, 2, 0, 0);
                        dynamically_txt.setGravity(Gravity.LEFT);
                        dynamically_txt.setTextColor(Color.rgb(6, 102, 219));
                        dynamically_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

                        TableRow tr1 = new TableRow(ScheduleActivity.this);
                        tr1.setGravity(Gravity.CENTER_HORIZONTAL);
                        tr1.addView(dynamically_title, layoutParams);
                        tr1.addView(dynamically_txt, new TableRow.LayoutParams(type_dip, TableRow.LayoutParams.WRAP_CONTENT));

                        small_tb.addView(tr1, layoutParams);
                        //隱藏不需要的SQL資料
                        if (i < 24) {
                            small_tb.getChildAt(i).setVisibility(View.GONE);
                        }
                    }
                    //about_ImageView寬為0,高為WRAP_CONTENT,權重比為1
                    big_tbr.addView(today_about_ImageView[loc], new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                    //small_tb寬為0,高為WRAP_CONTENT,權重比為3
                    big_tbr.addView(small_tb, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));

                    TableRow.LayoutParams the_param3;
                    the_param3 = (TableRow.LayoutParams) small_tb.getLayoutParams();
                    the_param3.span = 2;
                    the_param3.width = TableRow.LayoutParams.MATCH_PARENT;
                    small_tb.setLayoutParams(the_param3);

                    today_TableLayout.addView(big_tbr);
                    //如果MySQL傳回的資料超過6筆,從第7筆資料開始隱藏
                    if (loc > 5) {
                        today_TableLayout.getChildAt(loc).setVisibility(View.GONE);
                    }
                    //顯示今日件數的總數
                    int total = today_TableLayout.getChildCount();
                    today_sql_total_textView.setText(String.valueOf(total));
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    /**
     * 與OkHttp(Week)建立連線_new
     */
    private void sendRequestWithOkHttpForWeek() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.i("ScheduleActivity2", user_id_data);
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .build();
                    Log.i("ScheduleActivity2", user_id_data);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WeekScheduleData.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.i("ScheduleActivity2", responseData);
                    parseJSONWithJSONObjectForWeek(responseData);
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
    private void parseJSONWithJSONObjectForWeek(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            week_about_ImageView = new ImageView[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String work_type_name = jsonObject.getString("WORK_TYPE_NAME");
                String esvd_service_no = jsonObject.getString("ESVD_SERVICE_NO");
                String esv_note = jsonObject.getString("ESV_NOTE");
                String time_period_name = jsonObject.getString("BOOKING_DATETIME");
                String esv_contactor = jsonObject.getString("ESV_CONTACTOR");
                String esv_tel_no1 = jsonObject.getString("ESV_TEL_NO1");
                String esv_tel_no2 = jsonObject.getString("ESV_TEL_NO2");
                String esv_address = jsonObject.getString("ESV_ADDRESS");
                String cp_name = jsonObject.getString("PAY_MODE");
                String esvd_is_get_money = jsonObject.getString("IS_GET_MONEY");
                String esvd_is_money = jsonObject.getString("RECEIVE_MONEY");
                String esvd_is_eng_money = jsonObject.getString("IS_ENG_MONEY");
                String esvd_get_eng_money = jsonObject.getString("GET_ENG_MONEY");
                String esvd_begin_date = jsonObject.getString("ESVD_BEGIN_DATE");
                String esvd_begin_time = jsonObject.getString("ESVD_BEGIN_TIME");
                String esvd_end_time = jsonObject.getString("ESVD_END_TIME");
                String esvd_booking_remark = jsonObject.getString("ESVD_BOOKING_REMARK");
                String esv_item_remark = jsonObject.getString("ESV_ITEM_REMARK");
                String esvd_remark = jsonObject.getString("ESVD_REMARK");
                String esvd_seq_id = jsonObject.getString("ESVD_SEQ_ID");
                String esvd_eng_points = jsonObject.getString("ESVD_ENG_POINTS");
                String esvd_booking_period = jsonObject.getString("ESVD_BOOKING_PERIOD");
                String esvd_booking_period_end = jsonObject.getString("ESVD_BOOKING_PERIOD_End");
                String my_ontype = jsonObject.getString("REPORT_STATUS");
                String reserve_time = jsonObject.getString("WORK_TIME");
                String work_type = jsonObject.getString("WORK_TYPE");

                Log.i("ScheduleActivity2", reserve_time);
                Log.i("ScheduleActivity2", work_type_name);

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(work_type_name);
                JArrayList.add(esvd_service_no);
                JArrayList.add(esv_note);
                JArrayList.add(time_period_name);
                JArrayList.add(esv_contactor);
                JArrayList.add(esv_tel_no1);
                JArrayList.add(esv_tel_no2);
                JArrayList.add(esv_address);
                JArrayList.add(cp_name);
                JArrayList.add(esvd_is_get_money);
                JArrayList.add(esvd_is_money);
                JArrayList.add(esvd_is_eng_money);
                JArrayList.add(esvd_get_eng_money);
                JArrayList.add(esvd_begin_date);
                JArrayList.add(esvd_begin_time);
                JArrayList.add(esvd_end_time);
                JArrayList.add(esvd_booking_remark);
                JArrayList.add(esv_item_remark);
                JArrayList.add(esvd_remark);
                JArrayList.add(esvd_seq_id);
                JArrayList.add(esvd_eng_points);
                JArrayList.add(esvd_booking_period);
                JArrayList.add(esvd_booking_period_end);
                JArrayList.add(my_ontype);
                JArrayList.add(reserve_time);
                JArrayList.add(work_type);

                //HandlerMessage更新UI
                Bundle b = new Bundle();
                b.putStringArrayList("JSON_data", JArrayList);
                Message msg = week_mHandler.obtainMessage();
                msg.setData(b);
                msg.what = 1;
                msg.sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新UI(Week)
     */
    Handler week_mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final String[] title_array = {"派工類別", "派工單號", "送貨客戶", "預約日期時段", "聯絡人", "主要電話",
                    "次要電話", "派工地址", "付款方式", "是否要收款", "應收款金額", "是否已收款", "已收款金額",
                    "抵達日期", "抵達時間", "結束時間", "任務說明", "料品說明", "工作說明", "派工資料識別碼",
                    "工務點數", "預約開始時間", "預約結束時間", "狀態", "派工日期時間 :", "處理方式 :"};
            switch (msg.what) {
                case 1:
                    //最外層有一個大的TableLayout,再設置TableRow包住小的TableLayout
                    week_TableLayout.setStretchAllColumns(true);

                    //設置大TableLayout的TableRow
                    TableRow big_tbr = new TableRow(ScheduleActivity.this);
                    //設定big_tbr的Weight權重
                    big_tbr.setWeightSum(4);
                    //設置每筆資料的小TableLayout
                    TableLayout small_tb = new TableLayout(ScheduleActivity.this);
                    //全部列自動填充空白處
                    small_tb.setStretchAllColumns(true);
                    //設定TableRow的寬高
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    JArrayList = jb.getStringArrayList("JSON_data");
                    //設置每筆TableLayout的Button監聽器、與動態新增Button的ID
                    int loc = 0;
                    for (int j = 0; j < week_about_ImageView.length; j++) {
                        if (week_about_ImageView[j] == null) {
                            loc = j;
                            break;
                        }
                    }
                    week_about_ImageView[loc] = new ImageView(ScheduleActivity.this);
                    week_about_ImageView[loc].setImageResource(R.drawable.about);
                    week_about_ImageView[loc].setScaleType(ImageView.ScaleType.CENTER);
                    week_about_ImageView[loc].setId(loc);
                    week_about_ImageView[loc].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int a = 0; a < week_about_ImageView.length; a++) {
                                if (v.getId() == week_about_ImageView[a].getId()) {
                                    Intent intent_maintain = new Intent(ScheduleActivity.this, MaintainActivity.class);
                                    //取得大TableLayout中的大TableRow位置
                                    TableRow tb_tbr = (TableRow) week_TableLayout.getChildAt(a);
                                    //取得大TableRow中的小TableLayout位置
                                    TableLayout tb_tbr_tb = (TableLayout) tb_tbr.getChildAt(1);
                                    //派工資料的迴圈
                                    for (int x = 0; x < 24; x++) {
                                        //取得小TableLayout中的小TableRow位置
                                        TableRow tb_tbr_tb_tbr = (TableRow) tb_tbr_tb.getChildAt(x);
                                        //小TableRow中的layout_column位置
                                        TextView ThirdTextView = (TextView) tb_tbr_tb_tbr.getChildAt(1);
                                        String ResponseText = ThirdTextView.getText().toString();
                                        //將SQL裡的資料傳到MaintainActivity
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ResponseText" + x, ResponseText);

                                        intent_maintain.putExtras(bundle);//可放所有基本類別
                                    }
                                    startActivity(intent_maintain);
                                }
                            }
                        }
                    });

                    for (int i = 0; i < jb.getStringArrayList("JSON_data").size(); i++) {
                        //顯示每筆TableLayout的Title
                        TextView dynamically_title;
                        dynamically_title = new TextView(ScheduleActivity.this);
                        dynamically_title.setText(title_array[i].toString());
                        dynamically_title.setPadding(10, 2, 0, 0);
                        dynamically_title.setGravity(Gravity.LEFT);
                        dynamically_title.setTextColor(Color.rgb(6, 102, 219));
                        dynamically_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

                        //顯示每筆TableLayout的SQL資料
                        TextView dynamically_txt;
                        dynamically_txt = new TextView(ScheduleActivity.this);
                        dynamically_txt.setText(JArrayList.get(i));
                        dynamically_txt.setPadding(0, 2, 0, 0);
                        dynamically_txt.setGravity(Gravity.LEFT);
                        dynamically_txt.setTextColor(Color.rgb(6, 102, 219));
                        dynamically_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

                        TableRow tr1 = new TableRow(ScheduleActivity.this);
                        tr1.setGravity(Gravity.CENTER_HORIZONTAL);
                        tr1.addView(dynamically_title, layoutParams);
                        tr1.addView(dynamically_txt, layoutParams);

                        small_tb.addView(tr1, layoutParams);
                        if (i < 24) {
                            small_tb.getChildAt(i).setVisibility(View.GONE);
                        }
                    }
                    //about_ImageView寬為0,高為WRAP_CONTENT,權重比為1
                    big_tbr.addView(week_about_ImageView[loc], new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                    //small_tb寬為0,高為WRAP_CONTENT,權重比為3
                    big_tbr.addView(small_tb, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3));

                    TableRow.LayoutParams the_param3;
                    the_param3 = (TableRow.LayoutParams) small_tb.getLayoutParams();
                    the_param3.span = 2;
                    the_param3.width = TableRow.LayoutParams.MATCH_PARENT;
                    small_tb.setLayoutParams(the_param3);

                    week_TableLayout.addView(big_tbr);
                    //如果MySQL傳回的資料超過6筆,從第7筆資料開始隱藏
                    if (loc > 5) {
                        week_TableLayout.getChildAt(loc).setVisibility(View.GONE);
                    }
                    //顯示一周件數的總數
                    int total = week_TableLayout.getChildCount();
                    week_sql_total_textView.setText(String.valueOf(total));
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 與OkHttp(Missing)建立連線_new
     */
    private void sendRequestWithOkHttpForMissing() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.i("ScheduleActivity3", user_id_data);
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .build();
                    Log.i("ScheduleActivity3", user_id_data);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/MissingScheduleData.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.i("ScheduleActivity3", responseData);
                    parseJSONWithJSONObjectForMissing(responseData);
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
    private void parseJSONWithJSONObjectForMissing(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            missing_about_ImageView = new ImageView[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String work_type_name = jsonObject.getString("WORK_TYPE_NAME");
                String esvd_service_no = jsonObject.getString("ESVD_SERVICE_NO");
                String esv_note = jsonObject.getString("ESV_NOTE");
                String time_period_name = jsonObject.getString("BOOKING_DATETIME");
                String esv_contactor = jsonObject.getString("ESV_CONTACTOR");
                String esv_tel_no1 = jsonObject.getString("ESV_TEL_NO1");
                String esv_tel_no2 = jsonObject.getString("ESV_TEL_NO2");
                String esv_address = jsonObject.getString("ESV_ADDRESS");
                String cp_name = jsonObject.getString("PAY_MODE");
                String esvd_is_get_money = jsonObject.getString("IS_GET_MONEY");
                String esvd_is_money = jsonObject.getString("RECEIVE_MONEY");
                String esvd_is_eng_money = jsonObject.getString("IS_ENG_MONEY");
                String esvd_get_eng_money = jsonObject.getString("GET_ENG_MONEY");
                String esvd_begin_date = jsonObject.getString("ESVD_BEGIN_DATE");
                String esvd_begin_time = jsonObject.getString("ESVD_BEGIN_TIME");
                String esvd_end_time = jsonObject.getString("ESVD_END_TIME");
                String esvd_booking_remark = jsonObject.getString("ESVD_BOOKING_REMARK");
                String esv_item_remark = jsonObject.getString("ESV_ITEM_REMARK");
                String esvd_remark = jsonObject.getString("ESVD_REMARK");
                String esvd_seq_id = jsonObject.getString("ESVD_SEQ_ID");
                String esvd_eng_points = jsonObject.getString("ESVD_ENG_POINTS");
                String esvd_booking_period = jsonObject.getString("ESVD_BOOKING_PERIOD");
                String esvd_booking_period_end = jsonObject.getString("ESVD_BOOKING_PERIOD_End");
                String my_ontype = jsonObject.getString("REPORT_STATUS");
                String reserve_time = jsonObject.getString("WORK_TIME");
                String work_type = jsonObject.getString("WORK_TYPE");

                Log.i("ScheduleActivity3", reserve_time);
                Log.i("ScheduleActivity3", work_type_name);

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(work_type_name);
                JArrayList.add(esvd_service_no);
                JArrayList.add(esv_note);
                JArrayList.add(time_period_name);
                JArrayList.add(esv_contactor);
                JArrayList.add(esv_tel_no1);
                JArrayList.add(esv_tel_no2);
                JArrayList.add(esv_address);
                JArrayList.add(cp_name);
                JArrayList.add(esvd_is_get_money);
                JArrayList.add(esvd_is_money);
                JArrayList.add(esvd_is_eng_money);
                JArrayList.add(esvd_get_eng_money);
                JArrayList.add(esvd_begin_date);
                JArrayList.add(esvd_begin_time);
                JArrayList.add(esvd_end_time);
                JArrayList.add(esvd_booking_remark);
                JArrayList.add(esv_item_remark);
                JArrayList.add(esvd_remark);
                JArrayList.add(esvd_seq_id);
                JArrayList.add(esvd_eng_points);
                JArrayList.add(esvd_booking_period);
                JArrayList.add(esvd_booking_period_end);
                JArrayList.add(my_ontype);
                JArrayList.add(reserve_time);
                JArrayList.add(work_type);

                //HandlerMessage更新UI
                Bundle b = new Bundle();
                b.putStringArrayList("JSON_data", JArrayList);
                Message msg = missing_mHandler.obtainMessage();
                msg.setData(b);
                msg.what = 1;
                msg.sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新UI(Missing)
     */
    Handler missing_mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final String[] title_array = {"派工類別", "派工單號", "送貨客戶", "預約日期時段", "聯絡人", "主要電話",
                    "次要電話", "派工地址", "付款方式", "是否要收款", "應收款金額", "是否已收款", "已收款金額",
                    "抵達日期", "抵達時間", "結束時間", "任務說明", "料品說明", "工作說明", "派工資料識別碼",
                    "工務點數", "預約開始時間", "預約結束時間", "狀態", "派工日期時間 :", "處理方式 :"};
            switch (msg.what) {
                case 1:
                    //最外層有一個大的TableLayout,再設置TableRow包住小的TableLayout
                    missing_TableLayout.setStretchAllColumns(true);
                    //設置大TableLayout的TableRow
                    TableRow big_tbr = new TableRow(ScheduleActivity.this);
                    //設定big_tbr的Weight權重
                    big_tbr.setWeightSum(4);
                    //設置每筆資料的小TableLayout
                    TableLayout small_tb = new TableLayout(ScheduleActivity.this);
                    //全部列自動填充空白處
                    small_tb.setStretchAllColumns(true);
                    //設定TableRow的寬高
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    JArrayList = jb.getStringArrayList("JSON_data");
                    //設置每筆TableLayout的Button監聽器、與動態新增Button的ID
                    int loc = 0;
                    for (int j = 0; j < missing_about_ImageView.length; j++) {
                        if (missing_about_ImageView[j] == null) {
                            loc = j;
                            break;
                        }
                    }
                    missing_about_ImageView[loc] = new ImageView(ScheduleActivity.this);
                    missing_about_ImageView[loc].setImageResource(R.drawable.about);
                    missing_about_ImageView[loc].setScaleType(ImageView.ScaleType.CENTER);
                    missing_about_ImageView[loc].setId(loc);
                    missing_about_ImageView[loc].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int a = 0; a < missing_about_ImageView.length; a++) {
                                if (v.getId() == missing_about_ImageView[a].getId()) {
                                    Intent intent_maintain = new Intent(ScheduleActivity.this, MaintainActivity.class);
                                    //取得大TableLayout中的大TableRow位置
                                    TableRow tb_tbr = (TableRow) missing_TableLayout.getChildAt(a);
                                    //取得大TableRow中的小TableLayout位置
                                    TableLayout tb_tbr_tb = (TableLayout) tb_tbr.getChildAt(1);
                                    //派工資料的迴圈
                                    for (int x = 0; x < 24; x++) {
                                        //取得小TableLayout中的小TableRow位置
                                        TableRow tb_tbr_tb_tbr = (TableRow) tb_tbr_tb.getChildAt(x);
                                        //小TableRow中的layout_column位置
                                        TextView ThirdTextView = (TextView) tb_tbr_tb_tbr.getChildAt(1);
                                        String ResponseText = ThirdTextView.getText().toString();
                                        //將SQL裡的資料傳到MaintainActivity
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ResponseText" + x, ResponseText);

                                        intent_maintain.putExtras(bundle);//可放所有基本類別
                                    }
                                    startActivity(intent_maintain);
                                }
                            }
                        }
                    });

                    for (int i = 0; i < jb.getStringArrayList("JSON_data").size(); i++) {
                        //顯示每筆TableLayout的Title
                        TextView dynamically_title;
                        dynamically_title = new TextView(ScheduleActivity.this);
                        dynamically_title.setText(title_array[i].toString());
                        dynamically_title.setPadding(10, 2, 0, 0);
                        dynamically_title.setGravity(Gravity.LEFT);
                        dynamically_title.setTextColor(Color.rgb(6, 102, 219));
                        dynamically_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

                        //顯示每筆TableLayout的SQL資料
                        TextView dynamically_txt;
                        dynamically_txt = new TextView(ScheduleActivity.this);
                        dynamically_txt.setText(JArrayList.get(i));
                        dynamically_txt.setPadding(0, 2, 0, 0);
                        dynamically_txt.setGravity(Gravity.LEFT);
                        dynamically_txt.setTextColor(Color.rgb(6, 102, 219));
                        dynamically_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

                        TableRow tr1 = new TableRow(ScheduleActivity.this);
                        tr1.setGravity(Gravity.CENTER_HORIZONTAL);
                        tr1.addView(dynamically_title, layoutParams);
                        tr1.addView(dynamically_txt, layoutParams);

                        small_tb.addView(tr1, layoutParams);
                        if (i < 24) {
                            small_tb.getChildAt(i).setVisibility(View.GONE);
                        }
                    }
                    //about_ImageView寬為0,高為WRAP_CONTENT,權重比為1
                    big_tbr.addView(missing_about_ImageView[loc], new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                    //small_tb寬為0,高為WRAP_CONTENT,權重比為3
                    big_tbr.addView(small_tb, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3));

                    TableRow.LayoutParams the_param3;
                    the_param3 = (TableRow.LayoutParams) small_tb.getLayoutParams();
                    the_param3.span = 2;
                    the_param3.width = TableRow.LayoutParams.MATCH_PARENT;
                    small_tb.setLayoutParams(the_param3);

                    missing_TableLayout.addView(big_tbr);
                    //如果MySQL傳回的資料超過6筆,從第7筆資料開始隱藏
                    if (loc > 5) {
                        missing_TableLayout.getChildAt(loc).setVisibility(View.GONE);
                    }
                    //顯示未回派工件數的總數
                    int total = missing_TableLayout.getChildCount();
                    missing_sql_total_textView.setText(String.valueOf(total));
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 與OkHttp建立連線(未回派工數量)
     */
    private void sendRequestWithOkHttpOfMissCount() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.i("ScheduleActivity", user_id_data);
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/MissWorkCount.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("ScheduleActivity", responseData);
                    parseJSONWithJSONObjectOfMissCount(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 取得未回出勤的數量
     * @param jsonData
     */
    private void parseJSONWithJSONObjectOfMissCount(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String miss_count = jsonObject.getString("COUNT");

                Log.e("ScheduleActivity", miss_count);
                if(miss_count.toString().equals("0")){
                    badgeCount = 0;
                    ShortcutBadger.removeCount(ScheduleActivity.this);
                }else{
                    int count = Integer.valueOf(miss_count);
                    ShortcutBadger.applyCount(ScheduleActivity.this, count);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ScheduleActivity", "onDestroy");
        handler = null; //此處在Activity退出時即時回收
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ScheduleActivity", "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ScheduleActivity", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ScheduleActivity", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ScheduleActivity", "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("ScheduleActivity", "onRestart");
        if (today_TableLayout != null) {
            today_TableLayout.removeAllViews();
            sendRequestWithOkHttpForToday();
        }
        if (week_TableLayout != null) {
            week_TableLayout.removeAllViews();
            sendRequestWithOkHttpForWeek();
        }
        if (missing_TableLayout != null) {
            missing_TableLayout.removeAllViews();
            sendRequestWithOkHttpForMissing();
        }
        /*sendRequestWithOkHttpForToday();
        sendRequestWithOkHttpForWeek();
        sendRequestWithOkHttpForMissing();*/
        sendRequestWithOkHttpOfMissCount();
    }
}
