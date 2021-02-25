package com.example.a10609516.app.Workers;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.a10609516.app.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Week_Work extends AppCompatActivity {

    private TableLayout week_work_TableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_work);
        //動態取得 View 物件
        InItFunction();
        //建立WeekScheduleData.php OKHttp連線
        sendRequestWithOkHttpOfWeek();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        week_work_TableLayout = (TableLayout) findViewById(R.id.week_work_TableLayout);
    }

    /**
     * 與OkHttp建立連線_new
     */
    private void sendRequestWithOkHttpOfWeek() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.i("Week_Work", user_id_data);
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .build();
                    Log.i("Week_Work", user_id_data);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WeekScheduleData.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.i("Week_Work", responseData);
                    parseJSONWithJSONObjectOfWeek(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 獲得JSON字串並解析成String字串
     * @param jsonData
     */
    private void parseJSONWithJSONObjectOfWeek(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
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

                Log.i("Week_Work", reserve_time);
                Log.i("Week_Work", work_type_name);

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
     * 更新UI
     */
    Handler week_mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final String[] title_array = {"派工類別", "派工單號", "送貨客戶", "派工日期時段", "聯絡人", "主要電話",
                    "次要電話", "派工地址", "付款方式", "是否要收款", "應收款金額", "是否已收款", "已收款金額",
                    "抵達日期", "抵達時間", "結束時間", "任務說明", "料品說明", "工作說明", "今日派工時段 :", "處理方式 :"};
            switch (msg.what) {
                case 1:
                    //最外層有一個大的TableLayout,再設置TableRow包住小的TableLayout
                    week_work_TableLayout.setStretchAllColumns(true);
                    //設置大TableLayout的TableRow
                    TableRow big_tbr = new TableRow(Week_Work.this);
                    //設置每筆資料的小TableLayout
                    TableLayout small_tb = new TableLayout(Week_Work.this);
                    //全部列自動填充空白處
                    small_tb.setStretchAllColumns(true);
                    small_tb.setBackgroundResource(R.drawable.whiteline);
                    //設定TableRow的寬高
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    JArrayList = jb.getStringArrayList("JSON_data");
                    //設置每筆派工的派工單號、送貨客戶
                    LinearLayout dynamically_llt = new LinearLayout(Week_Work.this);
                    TextView dynamically_title2;
                    dynamically_title2 = new TextView(Week_Work.this);
                    dynamically_title2.setText(title_array[1].toString() + " : " + JArrayList.get(1));
                    dynamically_title2.setPadding(40, 10, 0, 10);
                    dynamically_title2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    dynamically_title2.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_title2.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);

                    dynamically_llt.addView(dynamically_title2);
                    TableRow tr1 = new TableRow(Week_Work.this);
                    tr1.addView(dynamically_llt);
                    //將動態新增TableRow合併 讓分隔線延伸
                    TableRow.LayoutParams the_param1;
                    the_param1 = (TableRow.LayoutParams) dynamically_llt.getLayoutParams();
                    the_param1.span = 2;
                    dynamically_llt.setLayoutParams(the_param1);

                    LinearLayout dynamically_llt2 = new LinearLayout(Week_Work.this);
                    TextView dynamically_title3;
                    dynamically_title3 = new TextView(Week_Work.this);
                    dynamically_title3.setText(title_array[2].toString() + " : " + JArrayList.get(2));
                    dynamically_title3.setPadding(40, 10, 0, 10);
                    dynamically_title3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    dynamically_title3.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_title3.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);

                    dynamically_llt2.addView(dynamically_title3);
                    TableRow tr2 = new TableRow(Week_Work.this);
                    tr2.addView(dynamically_llt2);

                    //將動態新增TableRow合併 讓分隔線延伸
                    TableRow.LayoutParams the_param2;
                    the_param2 = (TableRow.LayoutParams) dynamically_llt2.getLayoutParams();
                    the_param2.span = 2;
                    dynamically_llt2.setLayoutParams(the_param2);

                    for (int i = 0; i < jb.getStringArrayList("JSON_data").size(); i++) {
                        //顯示每筆TableLayout的Title
                        TextView dynamically_title;
                        dynamically_title = new TextView(Week_Work.this);
                        dynamically_title.setText(title_array[i].toString());
                        dynamically_title.setPadding(40, 10, 0, 10);
                        dynamically_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        dynamically_title.setTextColor(Color.rgb(6, 102, 219));
                        dynamically_title.setMaxWidth(350);
                        dynamically_title.setMaxHeight(150);

                        //顯示每筆TableLayout的SQL資料
                        TextView dynamically_txt;
                        dynamically_txt = new TextView(Week_Work.this);
                        dynamically_txt.setText(JArrayList.get(i));
                        dynamically_txt.setPadding(0, 10, 10, 10);
                        dynamically_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        dynamically_txt.setTextColor(Color.rgb(6, 102, 219));
                        dynamically_txt.setMaxWidth(350);
                        dynamically_txt.setTextIsSelectable(true);

                        TableRow tr3 = new TableRow(Week_Work.this);
                        tr3.setGravity(Gravity.CENTER_HORIZONTAL);
                        tr3.addView(dynamically_title, layoutParams);
                        tr3.addView(dynamically_txt, layoutParams);

                        small_tb.addView(tr3, layoutParams);
                        //隱藏不需要的SQL資料
                        if (i > 18 || 7 < i && i < 16 || 0 < i && i < 3) {
                            small_tb.getChildAt(i).setVisibility(View.GONE);
                        }
                    }
                    big_tbr.addView(small_tb);

                    TableRow.LayoutParams the_param3;
                    the_param3 = (TableRow.LayoutParams) small_tb.getLayoutParams();
                    the_param3.span = 2;
                    the_param3.width = TableRow.LayoutParams.MATCH_PARENT;
                    small_tb.setLayoutParams(the_param3);

                    week_work_TableLayout.addView(tr1);
                    week_work_TableLayout.addView(tr2);
                    week_work_TableLayout.addView(big_tbr);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}