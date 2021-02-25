package com.example.a10609516.app.Clerk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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

public class QuotationActivity extends WQPServiceActivity {

    private LinearLayout separate_llt;
    private TableLayout quotation_TableLayout;
    private Spinner company_spinner, quotation_type_spinner, quotation_mode_spinner;
    private EditText quotation_no_edt, customer_edt, clerk_edt;
    private Button search_btn;
    private ImageView[] dynamically_imv;
    private TextView dynamically_txt1, dynamically_txt2, dynamically_txt3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);
        //動態取得 View 物件
        InItFunction();
        //報價單公司別的Spinner下拉選單
        CompanySpinner();
        //報價單別的Spinner下拉選單
        TypeSpinner();
        //報價單狀態的Spinner下拉選單
        ModeSpinner();
        //確認是否有最新版本，進行更新
        //CheckFirebaseVersion();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        separate_llt = (LinearLayout) findViewById(R.id.separate_llt);
        quotation_TableLayout = (TableLayout) findViewById(R.id.quotation_TableLayout);
        company_spinner = (Spinner) findViewById(R.id.company_spinner);
        quotation_type_spinner = (Spinner) findViewById(R.id.quotation_type_spinner);
        quotation_mode_spinner = (Spinner) findViewById(R.id.quotation_mode_spinner);
        quotation_no_edt = (EditText) findViewById(R.id.quotation_no_edt);
        customer_edt = (EditText) findViewById(R.id.customer_edt);
        clerk_edt = (EditText) findViewById(R.id.clerk_edt);
        search_btn = (Button) findViewById(R.id.search_btn);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //讓search_tablelayout資料清空
                quotation_TableLayout.removeAllViews();
                //separate_llt、title_llt、quotation_TableLayout
                separate_llt.setVisibility(View.VISIBLE);
                quotation_TableLayout.setVisibility(View.VISIBLE);
                //建立QuotationData.php OKHttp連線
                sendRequestWithOkHttpForQuotation();
            }
        });
    }

    /**
     * 報價單公司別的Spinner下拉選單
     */
    private void CompanySpinner() {
        //Spinner下拉選單
        final String[] returns = {"拓霖", "拓亞鈦"};
        ArrayAdapter<String> company_List = new ArrayAdapter<>(QuotationActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                returns);
        company_spinner.setAdapter(company_List);
    }

    /**
     * 報價單別的Spinner下拉選單
     */
    private void TypeSpinner() {
        //Spinner下拉選單
        final String[] returns = {"W210", "W211", "W212"};
        ArrayAdapter<String> quotation_type_List = new ArrayAdapter<>(QuotationActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                returns);
        quotation_type_spinner.setAdapter(quotation_type_List);
    }

    /**
     * 報價單狀態的Spinner下拉選單
     */
    private void ModeSpinner() {
        //Spinner下拉選單
        final String[] returns = {"送審中", "報價完成", "退回"};
        ArrayAdapter<String> quotation_mode_List = new ArrayAdapter<>(QuotationActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                returns);
        quotation_mode_spinner.setAdapter(quotation_mode_List);
    }

    /**
     * 與OkHttp建立連線
     */
    private void sendRequestWithOkHttpForQuotation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.i("QuotationActivity", user_id_data);

                String company = String.valueOf(company_spinner.getSelectedItem());
                String quotation_type = String.valueOf(quotation_type_spinner.getSelectedItem());
                String quotation_no = quotation_no_edt.getText().toString();
                String customer = customer_edt.getText().toString();
                String clerk = clerk_edt.getText().toString();
                String quotation_mode = String.valueOf(quotation_mode_spinner.getSelectedItem());

                if (company.toString().equals("拓霖")) {
                    company = "WQP";
                } else {
                    company = "TYT";
                }
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .add("COMPANY", company)
                            .add("TA001", quotation_type)
                            .add("TA002", quotation_no)
                            .add("TA004", customer)
                            .add("TA005", clerk)
                            .add("PROCESS", quotation_mode)
                            .build();
                    Log.e("QuotationActivity2", user_id_data);
                    Log.e("QuotationActivity2", clerk);
                    Log.e("QuotationActivity", quotation_type);
                    Log.e("QuotationActivity", quotation_no);
                    Log.e("QuotationActivity", quotation_mode);
                     Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/QuotationSearch.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("QuotationActivity1", requestBody.toString());
                    Log.e("QuotationActivity1", response.toString());
                    Log.e("QuotationActivity1", responseData);
                    parseJSONWithJSONObjectForQuotation(responseData);
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
    private void parseJSONWithJSONObjectForQuotation(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            dynamically_imv = new ImageView[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String COMPANY = jsonObject.getString("COMPANY");
                String TA002 = jsonObject.getString("NUMBER");
                String TA004 = jsonObject.getString("CUST");
                String TA005 = jsonObject.getString("CLERK");

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(COMPANY);
                JArrayList.add(TA002);
                JArrayList.add(TA004);
                JArrayList.add(TA005);

                Log.e("QuotationActivity1", COMPANY);
                Log.e("QuotationActivity1", TA002);
                Log.e("QuotationActivity1", TA004);
                Log.e("QuotationActivity1", TA005);

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
                    Resources resources = getResources();
                    float type_Dip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, resources.getDisplayMetrics());
                    int type_dip = Math.round(type_Dip);
                    //最外層有一個大的TableLayout,再設置TableRow包住小的TableLayout
                    //平均分配列的寬度
                    quotation_TableLayout.setStretchAllColumns(true);
                    //設置大TableLayout的TableRow
                    TableRow big_tbr = new TableRow(QuotationActivity.this);
                    //設置每筆資料的小TableLayout
                    TableLayout small_tb = new TableLayout(QuotationActivity.this);
                    //全部列自動填充空白處
                    small_tb.setStretchAllColumns(true);
                    small_tb.setBackgroundResource(R.drawable.titleline);
                    //設定TableRow的寬高
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    JArrayList = jb.getStringArrayList("JSON_data");

                    Log.e("QuotationActivity", JArrayList.get(0));
                    //設置每筆TableLayout的Button監聽器、與動態新增Button的ID
                    int loc = 0;
                    for (int j = 0; j < dynamically_imv.length; j++) {
                        if (dynamically_imv[j] == null) {
                            loc = j;
                            break;
                        }
                    }
                    dynamically_imv[loc] = new ImageView(QuotationActivity.this);
                    dynamically_imv[loc].setImageResource(R.drawable.quotation);
                    dynamically_imv[loc].setScaleType(ImageView.ScaleType.CENTER);
                    dynamically_imv[loc].setId(loc);
                    dynamically_imv[loc].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int a = 0; a < dynamically_imv.length; a++) {
                                if (v.getId() == dynamically_imv[a].getId()) {
                                    Intent intent_quotation = new Intent(QuotationActivity.this, AuthorizeActivity.class);
                                    //取得大TableLayout中的大TableRow位置
                                    TableRow tb_tbr = (TableRow) quotation_TableLayout.getChildAt(a);
                                    //取得大TableRow中的小TableLayout位置
                                    TableLayout tb_tbr_tb = (TableLayout) tb_tbr.getChildAt(1);
                                    //取得小TableLayout中的小TableRow位置
                                    TableRow tb_tbr_tb_tbr = (TableRow) tb_tbr_tb.getChildAt(0);
                                    //派工資料的迴圈
                                    for (int x = 0; x < 3; x++) {
                                        //小TableRow中的layout_column位置
                                        TextView quotation_txt = (TextView) tb_tbr_tb_tbr.getChildAt(x);
                                        String ResponseText = quotation_txt.getText().toString();
                                        String company = String.valueOf(company_spinner.getSelectedItem());
                                        String quotation_type = String.valueOf(quotation_type_spinner.getSelectedItem());
                                        //將SQL裡的資料傳到MaintainActivity
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ResponseText" + x, ResponseText);
                                        bundle.putString("company", company);
                                        bundle.putString("quotation_type", quotation_type);

                                        intent_quotation.putExtras(bundle);//可放所有基本類別
                                    }
                                    quotation_TableLayout.removeAllViews();
                                    separate_llt.setVisibility(View.GONE);
                                    startActivity(intent_quotation);
                                }
                            }
                        }
                    });

                    //顯示每筆TableLayout的SQL資料
                    //TextView dynamically_txt1;
                    dynamically_txt1 = new TextView(QuotationActivity.this);
                    dynamically_txt1.setText(JArrayList.get(1));
                    dynamically_txt1.setPadding(0, 2, 0, 0);
                    dynamically_txt1.setGravity(Gravity.LEFT);
                    dynamically_txt1.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_txt1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    if (dynamically_txt1.getText().toString().equals("報價單號")) {
                        dynamically_txt1.setGravity(Gravity.CENTER_VERTICAL);
                        dynamically_txt1.setPadding(10, 5, 0, 0);
                        dynamically_txt1.setTextColor(Color.rgb(0, 51, 102));
                        //dynamically_txt1.setBackgroundResource(R.drawable.warningline);
                    }
                    //顯示每筆TableLayout的SQL資料
                    //TextView dynamically_txt2;
                    dynamically_txt2 = new TextView(QuotationActivity.this);
                    dynamically_txt2.setText(JArrayList.get(2));
                    dynamically_txt2.setPadding(0, 2, 0, 0);
                    dynamically_txt2.setGravity(Gravity.LEFT);
                    dynamically_txt2.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_txt2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    if (dynamically_txt2.getText().toString().equals("客戶代號")) {
                        dynamically_txt2.setGravity(Gravity.CENTER_VERTICAL);
                        dynamically_txt2.setPadding(30, 5, 0, 0);
                        dynamically_txt2.setTextColor(Color.rgb(0, 51, 102));
                        //dynamically_txt2.setBackgroundResource(R.drawable.warningline);
                    }
                    //顯示每筆TableLayout的SQL資料
                    //TextView dynamically_txt3;
                    dynamically_txt3 = new TextView(QuotationActivity.this);
                    dynamically_txt3.setText(JArrayList.get(3));
                    dynamically_txt3.setPadding(0, 2, 0, 0);
                    dynamically_txt3.setGravity(Gravity.LEFT);
                    dynamically_txt3.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_txt3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    if (dynamically_txt3.getText().toString().equals("業務姓名")) {
                        dynamically_txt3.setGravity(Gravity.CENTER_VERTICAL);
                        dynamically_txt3.setPadding(18, 5, 0, 0);
                        dynamically_txt3.setTextColor(Color.rgb(0, 51, 102));
                        //dynamically_txt3.setBackgroundResource(R.drawable.warningline);
                    }
                    TableRow tr1 = new TableRow(QuotationActivity.this);
                    tr1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr1.addView(dynamically_txt1, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, type_dip));
                    tr1.addView(dynamically_txt2, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, type_dip));
                    tr1.addView(dynamically_txt3, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, type_dip));

                    small_tb.addView(tr1, layoutParams);
                    //about_ImageView寬為0,高為WRAP_CONTENT,權重比為1
                    big_tbr.addView(dynamically_imv[loc], new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                    //small_tb寬為0,高為WRAP_CONTENT,權重比為3
                    big_tbr.addView(small_tb, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 2));
                    dynamically_imv[0].setVisibility(View.INVISIBLE);

                    TableRow.LayoutParams the_param3;
                    the_param3 = (TableRow.LayoutParams) small_tb.getLayoutParams();
                    the_param3.span = 2;
                    the_param3.width = TableRow.LayoutParams.MATCH_PARENT;
                    small_tb.setLayoutParams(the_param3);

                    quotation_TableLayout.addView(big_tbr);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
