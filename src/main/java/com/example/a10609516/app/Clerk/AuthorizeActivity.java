package com.example.a10609516.app.Clerk;

import android.content.SharedPreferences;
import android.content.res.Resources;
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
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10609516.app.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthorizeActivity extends AppCompatActivity {

    private TableLayout quotation_master_tb;
    private LinearLayout quotation_detail_llt, yes_no_llt, cancellation_llt, separate_llt;
    private TextView dynamically_master_title, dynamically_master_txt, mode_txt, dynamically_item_txt, dynamically_product_txt
                   , dynamically_count_txt, dynamically_price_txt, dynamically_total_txt, dynamically_money_txt;
    private Button approved_button, reject_button, cancellation_button;
    String TA002TB002, ResponseText1, ResponseText2, company, quotation_type, COMPANY
            , TA001, TA001TA002, TA004, TA005, TA004TA006, TA009, TA010, TA011, TA020
            , TA021, PERCENTAGE, TRANSATION, PROCESS, LOCKING, TB003, TB004TB005, TB007TB008
            , TB009, TB010, TB025TB024, user_id_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize);
        //取消ActionBar
        getSupportActionBar().hide();
        //動態取得 View 物件
        InItFunction();
        //取得QuotationActivity傳遞過來的值
        GetResponseData();
        //建立QuotationMaster.php OKHttp連線
        sendRequestWithOkHttpForMaster();
        //建立QuotationMaster.php OKHttp連線
        sendRequestWithOkHttpForDetail();
        //與OkHttp建立連線(QuotationRead.php)
        sendRequestWithOkHttpForQuotationRead();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        quotation_master_tb = (TableLayout) findViewById(R.id.quotation_master_tb);
        quotation_detail_llt = (LinearLayout) findViewById(R.id.quotation_detail_llt);
        yes_no_llt = (LinearLayout) findViewById(R.id.yes_no_llt);
        cancellation_llt = (LinearLayout) findViewById(R.id.cancellation_llt);
        separate_llt = (LinearLayout) findViewById(R.id.separate_llt);
        mode_txt = (TextView) findViewById(R.id.mode_txt);
        approved_button = (Button) findViewById(R.id.approved_button);
        reject_button = (Button) findViewById(R.id.reject_button);
        cancellation_button = (Button) findViewById(R.id.cancellation_button);

        //核准報價單Button的監聽器
        approved_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TA005.toString().substring(0, 8).equals(user_id_data.toString())) {
                    Toast.makeText(AuthorizeActivity.this, "無權限審核此報價單", Toast.LENGTH_SHORT).show();
                }else {
                    //建立QuotationApproved.php OKHttp連線
                    sendRequestWithOkHttpForApproved();
                    Log.e("AuthorizeActivity",TA005.toString().substring(0, 8));
                    finish();
                }
            }
        });
        //駁回報價單Button的監聽器
        reject_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //建立QuotationReject.php OKHttp連線
                if(TA005.toString().substring(0, 8).equals(user_id_data.toString())) {
                    Toast.makeText(AuthorizeActivity.this, "無權限審核此報價單", Toast.LENGTH_SHORT).show();
                }else {
                    sendRequestWithOkHttpForReject();
                    Log.e("AuthorizeActivity", TA005.toString().substring(0, 8));
                    finish();
                }
            }
        });
        //取消退回報價單Button的監聽器
        cancellation_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //建立QuotationReject.php OKHttp連線
                if(TA005.toString().substring(0, 8).equals(user_id_data.toString())) {
                    Toast.makeText(AuthorizeActivity.this, "無權限審核此報價單", Toast.LENGTH_SHORT).show();
                }else {
                    sendRequestWithOkHttpForReject();
                    Log.e("AuthorizeActivity888", TA005.toString().substring(0, 8));
                    finish();
                }
            }
        });
    }

    /**
     * 取得SearchActivity傳遞過來的值
     */
    private void GetResponseData() {
        Bundle bundle = getIntent().getExtras();
        TA002TB002 = bundle.getString("ResponseText" + 0);
        ResponseText1 = bundle.getString("ResponseText" + 1);
        ResponseText2 = bundle.getString("ResponseText" + 2);
        company = bundle.getString("company");
        quotation_type = bundle.getString("quotation_type");

        if (company.toString().equals("拓霖")) {
            company = "WQP";
        } else {
            company = "TYT";
        }

        Log.e("AuthorizeActivity0", TA002TB002);
        Log.e("AuthorizeActivity0", ResponseText1);
        Log.e("AuthorizeActivity0", ResponseText2);
        Log.e("AuthorizeActivity0", company);
        Log.e("AuthorizeActivity0", quotation_type);
    }

    /**
     * 與OkHttp建立連線(已讀報價單)
     */
    private void sendRequestWithOkHttpForQuotationRead() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("AuthorizeActivity111", user_id_data);
                Log.e("AuthorizeActivity111", company);
                Log.e("AuthorizeActivity111", quotation_type);
                Log.e("AuthorizeActivity111", TA002TB002);

                String quotation_read = "1";

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .add("COMPANY", company)
                            .add("TA001", quotation_type)
                            .add("TA002", TA002TB002)
                            .add("Q_READ", quotation_read)
                            .build();
                    Log.e("AuthorizeActivity112", user_id_data);
                    Log.e("AuthorizeActivity112", quotation_type);
                    Log.e("AuthorizeActivity112", TA002TB002);
                    Log.e("AuthorizeActivity112", company);
                    Log.e("AuthorizeActivity112", quotation_read);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/QuotationRead.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("AuthorizeActivity111", responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 與OkHttp建立連線(報價單單頭)
     */
    private void sendRequestWithOkHttpForMaster() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                user_id_data = user_id.getString("ID", "");
                Log.e("AuthorizeActivity1", user_id_data);
                Log.e("AuthorizeActivity1", company);
                Log.e("AuthorizeActivity1", quotation_type);
                Log.e("AuthorizeActivity1", TA002TB002);

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .add("COMPANY", company)
                            .add("TA001", quotation_type)
                            .add("TA002", TA002TB002)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/QuotationMaster.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("AuthorizeActivity1", responseData);
                    parseJSONWithJSONObjectForMaster(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 獲得JSON字串並解析成String字串(報價單單頭)
     *
     * @param jsonData
     */
    private void parseJSONWithJSONObjectForMaster(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                COMPANY = jsonObject.getString("COMPANY");
                //TA001 = jsonObject.getString("單別");
                TA001TA002 = jsonObject.getString("NUMBER");
                TA005 = jsonObject.getString("CLERK");
                //TA004 = jsonObject.getString("客戶代號");
                TA004TA006 = jsonObject.getString("CUST");
                TA009 = jsonObject.getString("Q_MONEY");
                TRANSATION = jsonObject.getString("PAY_MODE");
                TA011 = jsonObject.getString("PAY_CONDITION");
                TA010 = jsonObject.getString("MONEY_CONDITION");
                TA020 = jsonObject.getString("NOTE1");
                TA021 = jsonObject.getString("NOTE2");
                PERCENTAGE = jsonObject.getString("PERCENTAGE");
                PROCESS = jsonObject.getString("PROCESS");
                LOCKING = jsonObject.getString("LOCK");

                if (COMPANY.toString().equals("WQP")) {
                    COMPANY = "拓霖";
                } else {
                    COMPANY = "拓亞鈦";
                }
                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(COMPANY);
                //JArrayList.add(TA001);
                JArrayList.add(TA001TA002);
                JArrayList.add(TA005);
                //JArrayList.add(TA004);
                JArrayList.add(TA004TA006);
                JArrayList.add(TA009);
                JArrayList.add(TRANSATION);
                JArrayList.add(TA011);
                JArrayList.add(TA010);
                JArrayList.add(TA020);
                JArrayList.add(TA021);
                JArrayList.add(PERCENTAGE);

                Log.e("AuthorizeActivity2", TA001TA002);
                Log.e("AuthorizeActivity2", "LOCK:" + LOCKING);

                //HandlerMessage更新UI
                Bundle b = new Bundle();
                b.putStringArrayList("JSON_data", JArrayList);
                Message msg = mHandlerForMaster.obtainMessage();
                msg.setData(b);
                msg.what = 1;
                msg.sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新UI(QuotationMaster)
     */
    Handler mHandlerForMaster = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //設定報價單狀態的樣式
            //核准、退回、取消確認Button的設定
            mode_txt.setText(PROCESS);
            if (PROCESS.toString().equals("送審中")) {
                mode_txt.setTextColor(Color.rgb(255, 165, 0));
            } else if (PROCESS.toString().equals("報價完成")) {
                mode_txt.setTextColor(Color.rgb(34, 195, 46));
                cancellation_llt.setVisibility(View.VISIBLE);
                yes_no_llt.setVisibility(View.GONE);
            } else {
                mode_txt.setTextColor(Color.rgb(220, 20, 60));
                cancellation_llt.setVisibility(View.GONE);
                yes_no_llt.setVisibility(View.GONE);
                separate_llt.setVisibility(View.GONE);
            }

            //單頭的Title
            final String[] title_array = {"公司別"/*, "單別"*/, "單號", "業務姓名", /*"客戶代號",*/
                    "客戶全名", "報價金額", "付款方式", "付款條件", "價格條件"
                    , "備註一", "備註二", "訂金"};
            switch (msg.what) {
                case 1:
                    //最外層有一個大的TableLayout,再設置TableRow包住小的TableLayout
                    //平均分配列的寬度
                    quotation_master_tb.setStretchAllColumns(true);
                    //設置大TableLayout的TableRow
                    TableRow big_tbr = new TableRow(AuthorizeActivity.this);
                    //設置每筆資料的小TableLayout
                    TableLayout small_tb = new TableLayout(AuthorizeActivity.this);
                    //全部列自動填充空白處
                    small_tb.setStretchAllColumns(true);
                    small_tb.setBackgroundResource(R.drawable.titleline);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    //int i = b.getStringArrayList("JSON_data").size();
                    JArrayList = jb.getStringArrayList("JSON_data");

                    for (int i = 0; i < jb.getStringArrayList("JSON_data").size(); i++) {
                        //顯示每筆TableLayout的Title
                        //TextView dynamically_title;
                        dynamically_master_title = new TextView(AuthorizeActivity.this);
                        dynamically_master_title.setText(title_array[i].toString());
                        dynamically_master_title.setPadding(20, 5, 0, 2);
                        dynamically_master_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        dynamically_master_title.setGravity(Gravity.CENTER_VERTICAL);

                        //顯示每筆TableLayout的SQL資料
                        //TextView dynamically_txt;
                        dynamically_master_txt = new TextView(AuthorizeActivity.this);
                        dynamically_master_txt.setText(JArrayList.get(i));
                        dynamically_master_txt.setPadding(10, 5, 0, 2);
                        dynamically_master_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        dynamically_master_txt.setGravity(Gravity.CENTER_VERTICAL);

                        //設置大TableLayout的TableRow
                        TableRow tr1 = new TableRow(AuthorizeActivity.this);
                        tr1.setWeightSum(2);
                        tr1.addView(dynamically_master_title, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0));
                        tr1.addView(dynamically_master_txt, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 2));

                        small_tb.addView(tr1);
                    }
                    if (PERCENTAGE.toString().equals("")) {
                        small_tb.getChildAt(10).setVisibility(View.GONE);
                    }
                    //設置報價金額的顯示方式
                    small_tb.getChildAt(4).setBackgroundColor(Color.rgb(240, 128, 128));
                    TableRow money_tbr = (TableRow) small_tb.getChildAt(4);
                    TextView money_txt = (TextView) money_tbr.getChildAt(1);
                    money_txt.setTypeface(null, Typeface.BOLD);
                    money_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);

                    big_tbr.addView(small_tb);

                    TableRow.LayoutParams the_param3;
                    the_param3 = (TableRow.LayoutParams) small_tb.getLayoutParams();
                    the_param3.span = 2;
                    the_param3.width = TableRow.LayoutParams.MATCH_PARENT;
                    small_tb.setLayoutParams(the_param3);

                    quotation_master_tb.addView(big_tbr);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 與OkHttp建立連線(報價單單身)
     */
    private void sendRequestWithOkHttpForDetail() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("AuthorizeActivity3", user_id_data);
                Log.e("AuthorizeActivity3", company);
                Log.e("AuthorizeActivity3", quotation_type);
                Log.e("AuthorizeActivity3", TA002TB002);

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .add("COMPANY", company)
                            .add("TB001", quotation_type)
                            .add("TB002", TA002TB002)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/QuotationDetail.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("AuthorizeActivity3", responseData);
                    parseJSONWithJSONObjectForDetail(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 獲得JSON字串並解析成String字串(報價單單身)
     *
     * @param jsonData
     */
    private void parseJSONWithJSONObjectForDetail(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                TB003 = jsonObject.getString("ITEMS");
                TB004TB005 = jsonObject.getString("TB004TB005");
                TB007TB008 = jsonObject.getString("QUANTITY");
                TB009 = jsonObject.getString("PRICE");
                TB010 = jsonObject.getString("TOTAL_MONEY");
                TB025TB024 = jsonObject.getString("Q_MONEY");

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(TB003);
                JArrayList.add(TB004TB005);
                JArrayList.add(TB007TB008);
                JArrayList.add(TB009);
                JArrayList.add(TB010);
                JArrayList.add(TB025TB024);

                Log.e("AuthorizeActivity2", TB025TB024);

                //HandlerMessage更新UI
                Bundle b = new Bundle();
                b.putStringArrayList("JSON_data", JArrayList);
                Message msg = mHandlerForDetail.obtainMessage();
                msg.setData(b);
                msg.what = 1;
                msg.sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新UI(QuotationDetail)
     */
    Handler mHandlerForDetail = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //製作Dip
                    Resources resources = getResources();
                    float product_Dip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, resources.getDisplayMetrics());
                    int product_dip = Math.round(product_Dip);

                    LinearLayout big_llt = new LinearLayout(AuthorizeActivity.this);
                    big_llt.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout item_llt = new LinearLayout(AuthorizeActivity.this);
                    item_llt.setOrientation(LinearLayout.HORIZONTAL);
                    item_llt.setBackgroundColor(Color.rgb(0,191,255));
                    LinearLayout product_llt = new LinearLayout(AuthorizeActivity.this);
                    product_llt.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout count_llt = new LinearLayout(AuthorizeActivity.this);
                    count_llt.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout price_llt = new LinearLayout(AuthorizeActivity.this);
                    price_llt.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout total_llt = new LinearLayout(AuthorizeActivity.this);
                    total_llt.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout money_llt = new LinearLayout(AuthorizeActivity.this);
                    money_llt.setOrientation(LinearLayout.HORIZONTAL);
                    HorizontalScrollView dynamically_hsv = new HorizontalScrollView(AuthorizeActivity.this);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    //int i = b.getStringArrayList("JSON_data").size();
                    JArrayList = jb.getStringArrayList("JSON_data");

                    dynamically_item_txt = new TextView(AuthorizeActivity.this);
                    dynamically_item_txt.setText(JArrayList.get(0));
                    dynamically_item_txt.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_item_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    dynamically_item_txt.setGravity(Gravity.LEFT);
                    dynamically_item_txt.setPadding(10, 2, 0, 0);
                    item_llt.addView(dynamically_item_txt, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    dynamically_product_txt = new TextView(AuthorizeActivity.this);
                    dynamically_product_txt.setText("品號:"+JArrayList.get(1));
                    dynamically_product_txt.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_product_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    dynamically_product_txt.setGravity(Gravity.CENTER);
                    dynamically_product_txt.setPadding(10, 2, 0, 0);
                    product_llt.addView(dynamically_product_txt, LinearLayout.LayoutParams.MATCH_PARENT, product_dip);

                    dynamically_count_txt = new TextView(AuthorizeActivity.this);
                    dynamically_count_txt.setText("數量: "+JArrayList.get(2));
                    dynamically_count_txt.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_count_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    dynamically_count_txt.setGravity(Gravity.LEFT);
                    dynamically_count_txt.setPadding(10, 2, 0, 0);
                    count_llt.addView(dynamically_count_txt, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    dynamically_price_txt = new TextView(AuthorizeActivity.this);
                    dynamically_price_txt.setText("單價: $"+JArrayList.get(3));
                    dynamically_price_txt.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_price_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    dynamically_price_txt.setGravity(Gravity.LEFT);
                    dynamically_price_txt.setPadding(10, 2, 0, 0);
                    price_llt.addView(dynamically_price_txt, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    dynamically_total_txt = new TextView(AuthorizeActivity.this);
                    dynamically_total_txt.setText("金額: $"+JArrayList.get(4));
                    dynamically_total_txt.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_total_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    dynamically_total_txt.setGravity(Gravity.LEFT);
                    dynamically_total_txt.setPadding(10, 2, 0, 0);
                    total_llt.addView(dynamically_total_txt, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    dynamically_money_txt = new TextView(AuthorizeActivity.this);
                    dynamically_money_txt.setText("報價單顯示金額: $"+JArrayList.get(5));
                    dynamically_money_txt.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_money_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    dynamically_money_txt.setGravity(Gravity.LEFT);
                    dynamically_money_txt.setPadding(10, 2, 0, 0);
                    money_llt.addView(dynamically_money_txt, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    dynamically_hsv.addView(product_llt);

                    big_llt.addView(item_llt);
                    big_llt.addView(dynamically_hsv);
                    big_llt.addView(count_llt);
                    big_llt.addView(price_llt);
                    big_llt.addView(total_llt);
                    big_llt.addView(money_llt);

                    quotation_detail_llt.addView(big_llt, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 與OkHttp建立連線(報價單核准)
     */
    private void sendRequestWithOkHttpForApproved() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("AuthorizeActivity3", user_id_data);
                Log.e("AuthorizeActivity3", company);
                Log.e("AuthorizeActivity3", quotation_type);
                Log.e("AuthorizeActivity3", TA002TB002);

                String TA005N = TA005.substring(0,8);

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .add("COMPANY", company)
                            .add("TA001", quotation_type)
                            .add("TA002", TA002TB002)
                            .add("TA005",TA005N)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/QuotationApproved.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("AuthorizeActivity3", responseData);
                    parseJSONWithJSONObjectForDetail(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 與OkHttp建立連線(報價單退回)
     */
    private void sendRequestWithOkHttpForReject() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("AuthorizeActivity3", user_id_data);
                Log.e("AuthorizeActivity3", company);
                Log.e("AuthorizeActivity3", quotation_type);
                Log.e("AuthorizeActivity3", TA002TB002);

                String TA005N = TA005.substring(0,8);

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .add("COMPANY", company)
                            .add("TA001", quotation_type)
                            .add("TA002", TA002TB002)
                            .add("TA005",TA005N)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/QuotationReject.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("AuthorizeActivity3", responseData);
                    parseJSONWithJSONObjectForDetail(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}