package com.example.a10609516.app.Workers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.os.Handler;
import android.os.Message;

import com.example.a10609516.app.R;
import com.example.a10609516.app.Tools.WQPServiceActivity;

import java.util.ArrayList;

public class SearchActivity extends WQPServiceActivity {

    private Button search_button, time_start_button, time_end_button, clean_start_button, clean_end_button;
    private Button[] dynamically_btn;
    private Spinner SelectList, ReturnsList;
    private EditText customer_editText;
    private TableLayout search_TableLayout;
    private LinearLayout search_LinearLayout, separate_linearLayout;
    private ScrollView search_scrollView;
    public int badgeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //動態取得 View 物件
        InItFunction();
        //派工類別的Spinner下拉選單
        WorkTypeSpinner();
        //回報狀態的Spinner下拉選單
        ReturnsSpinner();
        //確認是否有最新版本，進行更新
        //CheckFirebaseVersion();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        search_scrollView = (ScrollView) findViewById(R.id.search_scrollView);
        search_TableLayout = (TableLayout) findViewById(R.id.search_TableLayout);
        search_LinearLayout = (LinearLayout) findViewById(R.id.search_LinearLayout);
        separate_linearLayout = (LinearLayout) findViewById(R.id.separate_linearLayout);
        SelectList = (Spinner) findViewById(R.id.selectList);
        ReturnsList = (Spinner) findViewById(R.id.returnsList);
        time_start_button = (Button) findViewById(R.id.start2);
        time_end_button = (Button) findViewById(R.id.end2);
        clean_start_button = (Button) findViewById(R.id.clean_button1);
        clean_end_button = (Button) findViewById(R.id.clean_button2);
        customer_editText = (EditText) findViewById(R.id.customer_editText);
        search_button = (Button) findViewById(R.id.search_button);

        //Search_Button.setOnClickListener監聽器  //傳遞JSON字串與動態新增TableLayout
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //讓search_tablelayout資料清空
                search_TableLayout.removeAllViews();
                //按search_button顯示物件search_linearlayout、search_tablelayout
                separate_linearLayout.setVisibility(View.VISIBLE);
                search_LinearLayout.setVisibility(View.VISIBLE);
                search_TableLayout.setVisibility(View.VISIBLE);
                //建立SearchData.php OKHttp連線
                sendRequestWithOkHttp();
                //取得未回派工數量
                sendRequestWithOkHttpForMissCount();
            }
        });//end setOnItemClickListener
        //Clean_Start_Button.setOnClickListener監聽器  //清空time_start_button內的文字
        clean_start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_start_button.setText("");
            }
        });//end setOnItemClickListener
        //Clean_End_Button.setOnClickListener監聽器  //清空time_end_button內的文字
        clean_end_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_end_button.setText("");
            }
        });//end setOnItemClickListener
    }

    /**
     * 派工類別的Spinner下拉選單
     */
    private void WorkTypeSpinner() {
        //Spinner下拉選單
        final String[] select = {"選擇", "換濾心", "換濾料", "加鹽", "末端", "小前置", "全屋", "社區保養", "檢修(一)", "檢修(二)", "臨時取消"};
        ArrayAdapter<String> selectList = new ArrayAdapter<>(SearchActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                select);
        SelectList.setAdapter(selectList);
    }

    /**
     * 回報狀態的Spinner下拉選單
     */
    private void ReturnsSpinner() {
        //Spinner下拉選單
        final String[] returns = {"未回報", "已回報", "未結案", "已結案", "全部"};
        ArrayAdapter<String> returnsList = new ArrayAdapter<>(SearchActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                returns);
        ReturnsList.setAdapter(returnsList);
    }

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
                search_scrollView.fullScroll(ScrollView.FOCUS_UP);
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
                search_scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
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
                Log.i("SearchActivity", user_id_data);
                //獲取日期挑選器的數據
                String time_start = time_start_button.getText().toString();
                String time_end = time_end_button.getText().toString();
                //String customer_edt = customer_editText.getText().toString();
                String type_select = String.valueOf(SelectList.getSelectedItem());
                String returns_select = String.valueOf(ReturnsList.getSelectedItem());
                String ttt = String.valueOf(SelectList.getSelectedItemId());
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .add("ESVD_BEGIN_DATE1", time_start)
                            .add("ESVD_BEGIN_DATE2", time_end)
                            //.add("ESV_NOTE", customer_edt)
                            .add("WORK_TYPE_NAME", type_select)
                            .add("RETURNS", returns_select)
                            .build();
                    Log.e("SearchActivity", time_start);
                    Log.e("SearchActivity", time_end);
                    Log.e("SearchActivity", returns_select);
                    Log.e("SearchActivity123456",ttt);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/SearchData2.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.i("SearchActivity1", responseData);
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
            dynamically_btn = new Button[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String work_type_name = jsonObject.getString("WORK_TYPE");
                String esvd_service_no = jsonObject.getString("SERVICE_NO");
                String esv_note = jsonObject.getString("CUST_NAME");
                String time_period_name = jsonObject.getString("DATE_TIME");
                String esv_contactor = jsonObject.getString("CONTACT_PERSON");
                String esv_tel_no1 = jsonObject.getString("TEL1");
                String esv_tel_no2 = jsonObject.getString("TEL2");
                String esv_address = jsonObject.getString("ADDRESS");
                String cp_name = jsonObject.getString("PAY_MODE");
                String esvd_is_get_money = jsonObject.getString("WHETHER_RECEIVABLE");
                String esvd_is_money = jsonObject.getString("RECEIVABLE_AMOUNT");
                String esvd_is_eng_money = jsonObject.getString("WHETHER_RECEIVED");
                String esvd_get_eng_money = jsonObject.getString("RECEIVED_AMOUNT");
                String esvd_begin_date = jsonObject.getString("BEGIN_DATE");
                String esvd_begin_time = jsonObject.getString("BEGIN_TIME");
                String esvd_end_time = jsonObject.getString("END_TIME");
                String esvd_booking_remark = jsonObject.getString("TASK_REMARK");
                String esv_item_remark = jsonObject.getString("MATERIAL_REMARK");
                String esvd_remark = jsonObject.getString("WORK_REMARK");
                String esvd_seq_id = jsonObject.getString("MAINTAIN_ID");
                String esvd_eng_points = jsonObject.getString("D_POINTS");
                String esvd_booking_period = jsonObject.getString("BOOKING_BEGIN");
                String esvd_booking_period_end = jsonObject.getString("BOOKING_END");
                String my_ontype = jsonObject.getString("STATUS");

                Log.e("SearchActivity1", esvd_seq_id);

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
            final String[] title_array = {"派工類別", "派工單號", "送貨客戶", "預約日期時段", "聯絡人",
                    "主要電話", "次要電話", "派工地址", "付款方式", "是否要收款",
                    "應收款金額", "是否已收款", "已收款金額", "抵達日期", "抵達時間",
                    "結束時間", "任務說明", "料品說明", "工作說明", "派工資料識別碼",
                    "工務點數", "預約開始時間", "預約結束時間", "狀態"};
            switch (msg.what) {
                case 1:
                    //最外層有一個大的TableLayout,再設置TableRow包住小的TableLayout
                    //平均分配列的寬度
                    search_TableLayout.setStretchAllColumns(true);

                    //設置大TableLayout的TableRow
                    TableRow big_tbr = new TableRow(SearchActivity.this);
                    //設置每筆資料的小TableLayout
                    TableLayout small_tb = new TableLayout(SearchActivity.this);
                    //全部列自動填充空白處
                    small_tb.setStretchAllColumns(true);
                    small_tb.setBackgroundResource(R.drawable.titleline);
                    //設定TableRow的寬高
                    //TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    //int i = b.getStringArrayList("JSON_data").size();
                    JArrayList = jb.getStringArrayList("JSON_data");

                    for (int i = 0; i < jb.getStringArrayList("JSON_data").size(); i++) {
                        //顯示每筆TableLayout的Title
                        TextView dynamically_title;
                        dynamically_title = new TextView(SearchActivity.this);
                        dynamically_title.setText(title_array[i].toString());
                        //dynamically_title.setGravity(Gravity.CENTER);
                        //dynamically_title.setWidth(50);
                        dynamically_title.setPadding(10, 0, 0, 0);
                        dynamically_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        dynamically_title.setMaxWidth(350);

                        //顯示每筆TableLayout的SQL資料
                        TextView dynamically_txt;
                        dynamically_txt = new TextView(SearchActivity.this);
                        dynamically_txt.setText(JArrayList.get(i));
                        dynamically_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        dynamically_txt.setMaxWidth(350);
                        dynamically_txt.setTextIsSelectable(true);

                        TableRow tr1 = new TableRow(SearchActivity.this);
                        tr1.setWeightSum(5);
                        tr1.addView(dynamically_title, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
                        tr1.addView(dynamically_txt, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 4));
                        //search_TableLayout.addView(tr1);
                        small_tb.addView(tr1);
                        //如果日期為0000-00-00,則把該TextView改為空值
                        if (dynamically_txt.getText().toString().equals("1900-01-01 00:00:00.000")) {
                            dynamically_txt.setText("");
                        }
                        //如果金額為.0000,則把該TextView改為0
                        if (dynamically_txt.getText().toString().equals(".0000")){
                            dynamically_txt.setText("0");
                        }
                    }
                    small_tb.getChildAt(19).setVisibility(View.GONE);
                    small_tb.getChildAt(20).setVisibility(View.GONE);
                    small_tb.getChildAt(21).setVisibility(View.GONE);
                    small_tb.getChildAt(22).setVisibility(View.GONE);
                    small_tb.getChildAt(23).setVisibility(View.GONE);

                    //如果My_OnType是0，隱藏抵達時間與離開時間
                    TableRow mini_tbr = (TableRow) small_tb.getChildAt(23);
                    TextView my_type = (TextView) mini_tbr.getChildAt(1);
                    if(my_type.getText().toString().equals("0")){
                        TableRow begin_tbr = (TableRow) small_tb.getChildAt(14);
                        TextView begin = (TextView) begin_tbr.getChildAt(1);
                        begin.setVisibility(View.GONE);
                        TableRow end_tbr = (TableRow) small_tb.getChildAt(15);
                        TextView end = (TextView) end_tbr.getChildAt(1);
                        end.setVisibility(View.GONE);
                    }

                    //設置每筆TableLayout的Button監聽器、與動態新增Button的ID
                    int loc = 0;
                    for (int i = 0; i < dynamically_btn.length; i++) {
                        if (dynamically_btn[i] == null) {
                            loc = i;
                            break;
                        }
                    }
                    dynamically_btn[loc] = new Button(SearchActivity.this);
                    dynamically_btn[loc].setBackgroundResource(R.drawable.button);
                    dynamically_btn[loc].setText("回報");
                    dynamically_btn[loc].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    dynamically_btn[loc].setTextColor(Color.rgb(6, 102, 219));
                    dynamically_btn[loc].setId(loc);
                    dynamically_btn[loc].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int a = 0; a < dynamically_btn.length; a++) {
                                if (v.getId() == dynamically_btn[a].getId()) {
                                    Intent intent_maintain = new Intent(SearchActivity.this, MaintainActivity.class);
                                    //取得大TableLayout中的大TableRow位置
                                    TableRow tb_tbr = (TableRow) search_TableLayout.getChildAt(a);
                                    //取得大TableRow中的小TableLayout位置
                                    TableLayout tb_tbr_tb = (TableLayout) tb_tbr.getChildAt(0);
                                    //派工資料的迴圈
                                    for (int x = 0; x < 24; x++) {
                                        //取得小TableLayout中的小TableRow位置
                                        TableRow tb_tbr_tb_tbr = (TableRow) tb_tbr_tb.getChildAt(x);
                                        //小TableRow中的layout_column位置
                                        TextView SecondTextView = (TextView) tb_tbr_tb_tbr.getChildAt(1);
                                        String ResponseText = SecondTextView.getText().toString();
                                        //將SQL裡的資料傳到MaintainActivity
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ResponseText" + x, ResponseText);
                                        Log.e("MAINTAIN   ResponseText" + x, ResponseText);

                                        //intent_maintain.putExtra("TitleText" + x, TitleText);//可放所有基本類別
                                        intent_maintain.putExtras(bundle);//可放所有基本類別
                                    }
                                    startActivity(intent_maintain);
                                    //進入MaintainActivity後 清空search_TableLayout的資料
                                    search_TableLayout.removeAllViews();
                                    separate_linearLayout.setVisibility(View.GONE);
                                }
                            }
                        }
                    });

                    TableRow tr2 = new TableRow(SearchActivity.this);
                    tr2.addView(dynamically_btn[loc]);
                    //將動態新增TableRow合併 讓Button置中
                    TableRow.LayoutParams the_param;
                    the_param = (TableRow.LayoutParams) dynamically_btn[loc].getLayoutParams();
                    the_param.span = 2;
                    dynamically_btn[loc].setLayoutParams(the_param);
                    small_tb.addView(tr2);

                    //設置每筆TableLayout的分隔線
                    LinearLayout dynamically_llt = new LinearLayout(SearchActivity.this);
                    dynamically_llt.setBackgroundResource(R.drawable.table_h_divider);
                    TableRow tr3 = new TableRow(SearchActivity.this);
                    tr3.addView(dynamically_llt);
                    //將動態新增TableRow合併 讓分隔線延伸
                    TableRow.LayoutParams the_param2;
                    the_param2 = (TableRow.LayoutParams) dynamically_llt.getLayoutParams();
                    the_param2.span = 2;
                    dynamically_llt.setLayoutParams(the_param2);
                    //刪去最後一筆TableLayout的分隔線
                    if ((dynamically_btn.length - 1) != loc) {
                        small_tb.addView(tr3);
                    }
                    big_tbr.addView(small_tb);

                    TableRow.LayoutParams the_param3;
                    the_param3 = (TableRow.LayoutParams) small_tb.getLayoutParams();
                    the_param3.span = 2;
                    the_param3.width = TableRow.LayoutParams.MATCH_PARENT;
                    small_tb.setLayoutParams(the_param3);

                    search_TableLayout.addView(big_tbr);
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
    private void sendRequestWithOkHttpForMissCount() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.i("SearchActivity", user_id_data);
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
                    Log.e("SearchActivity", responseData);
                    parseJSONWithJSONObjectForMissCount(responseData);
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
    private void parseJSONWithJSONObjectForMissCount(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String miss_count = jsonObject.getString("COUNT");

                Log.e("SearchActivity", miss_count);
                if(miss_count.toString().equals("0")){
                    badgeCount = 0;
                    ShortcutBadger.removeCount(SearchActivity.this);
                }else{
                    int count = Integer.valueOf(miss_count);
                    ShortcutBadger.applyCount(SearchActivity.this, count);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("SearchActivity", "onRestart");
        //讓search_tablelayout資料清空
        search_TableLayout.removeAllViews();
        //按search_button顯示物件search_linearlayout、search_tablelayout
        separate_linearLayout.setVisibility(View.VISIBLE);
        search_LinearLayout.setVisibility(View.VISIBLE);
        search_TableLayout.setVisibility(View.VISIBLE);
        try{
            // delay 1 second
            Thread.sleep(1000);
            //建立SearchData.php OKHttp連線
            sendRequestWithOkHttp();
        } catch(InterruptedException e){
            e.printStackTrace();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SearchActivity", "onDestroy");
    }
}