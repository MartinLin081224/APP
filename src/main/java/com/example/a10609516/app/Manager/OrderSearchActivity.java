package com.example.a10609516.app.Manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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

public class OrderSearchActivity extends WQPServiceActivity {

    private FrameLayout order_flt;
    private ScrollView search_scv;
    private Spinner type_spinner, company_spinner;
    private EditText number_edt, customer_edt;
    private Button search_btn;
    private TableLayout search_tlt;
    private LinearLayout separate_llt, order_search_llt, title_llt;
    private ImageView[] dynamically_imv;
    private TextView dynamically_txt1, dynamically_txt2, dynamically_txt3, dynamically_txt4;

    private ArrayAdapter<String> type_listAdapter, company_listAdapter;
    private String[] order_type = new String[]{"請選擇 ", "W200(交際申請單)", "W201(總經理室)", "W220(建材處)", "W221(B-intense)",
            "W222(銷商處)", "W223(零售處)", "W224(協銷處)", "W225(專案類訂單)", "W226(年度合約)",
            "W227(售服部)", "W228(EC部)", "W229(開帳訂單)", "W250(Aquatherm)", "W251(專案處)",
            "W298(借出訂單)", "W299(虛擬訂單)", "W300(通路訂單)", "W400(施工安裝單)"};
    private String[] company = new String[]{"請選擇", "拓霖(WQP)", "拓亞鈦(TYT)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_search);
        //動態取得 View 物件
        InItFunction();
        //訂單單別的Spinner
        TypeSpinner();
        //公司別的Spinner
        CompanySpinner();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        order_flt = (FrameLayout) findViewById(R.id.order_flt);
        search_scv = (ScrollView) findViewById(R.id.search_scv);
        type_spinner = (Spinner) findViewById(R.id.type_spinner);
        company_spinner = (Spinner) findViewById(R.id.company_spinner);
        number_edt = (EditText) findViewById(R.id.number_edt);
        customer_edt = (EditText) findViewById(R.id.customer_edt);
        search_btn = (Button) findViewById(R.id.search_btn);
        search_tlt = (TableLayout) findViewById(R.id.search_tlt);
        separate_llt = (LinearLayout) findViewById(R.id.separate_llt);
        order_search_llt = (LinearLayout) findViewById(R.id.order_search_llt);
        title_llt = (LinearLayout) findViewById(R.id.title_llt);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(company_spinner.getSelectedItem()).equals("請選擇")) {
                    Toast.makeText(OrderSearchActivity.this, "【請選擇公司別】", Toast.LENGTH_SHORT).show();
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    //讓quotation_TableLayout資料清空
                    search_tlt.removeAllViews();
                    //separate_llt、order_search_llt、quotation_TableLayout、title_llt
                    separate_llt.setVisibility(View.VISIBLE);
                    order_search_llt.setVisibility(View.VISIBLE);
                    search_tlt.setVisibility(View.VISIBLE);
                    title_llt.setVisibility(View.VISIBLE);
                    //與資料庫OrderSearch.php進行連線(查詢訂單資料)
                    sendRequestWithOkHttpForOrder();
                }
            }
        });
    }

    /**
     * 訂單單別的Spinner
     */
    private void TypeSpinner() {
        type_listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, order_type);
        type_listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(type_listAdapter);
    }

    /**
     * 公司別的Spinner
     */
    private void CompanySpinner() {
        company_listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, company);
        company_listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        company_spinner.setAdapter(company_listAdapter);
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
                search_scv.fullScroll(ScrollView.FOCUS_UP);
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
                search_scv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    /**
     * 與OkHttp建立連線
     */
    private void sendRequestWithOkHttpForOrder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.i("OrderSearchActivity", user_id_data);
                String type = String.valueOf(type_spinner.getSelectedItem()).substring(0, 4);
                String number = number_edt.getText().toString();
                String company = String.valueOf(company_spinner.getSelectedItem());
                String customer = customer_edt.getText().toString();

                if (company.toString().equals("拓霖(WQP)")) {
                    company = "WQP";
                } else if (company.toString().equals("拓亞鈦(TYT)")) {
                    company = "TYT";
                } else {
                    company = "請選擇";
                }

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("COMPANY", company)
                            .add("TC001", type)
                            .add("TC002", number)
                            .add("TC004", customer)
                            .build();
                    Log.e("OrderSearchActivity", user_id_data);
                    Log.e("OrderSearchActivity", type);
                    Log.e("OrderSearchActivity", number);
                    Log.e("OrderSearchActivity11", company);
                    Log.e("OrderSearchActivity", customer);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/OrderSearch.php")
                            //.url("http://192.168.0.172/WQP/OrderSearch.php")
                            .post(requestBody)
                            .build();
                    Log.e("OrderSearchActivity", requestBody.toString());
                    Response response = client.newCall(request).execute();
                    Log.e("OrderSearchActivity", response.toString());
                    String responseData = response.body().string();
                    Log.e("OrderSearchActivity", responseData);
                    parseJSONWithJSONObjectForOrder(responseData);
                    Log.e("OrderSearchActivity", responseData.toString());
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
    private void parseJSONWithJSONObjectForOrder(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            dynamically_imv = new ImageView[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String COMPANY = jsonObject.getString("COMPANY");
                String TC001 = jsonObject.getString("TC001");
                String TC002 = jsonObject.getString("TC002");
                String TC004 = jsonObject.getString("TC004");

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(COMPANY);
                JArrayList.add(TC001);
                JArrayList.add(TC002);
                JArrayList.add(TC004);

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
                    Resources resources = getResources();
                    float type_Dip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, resources.getDisplayMetrics());
                    int type_dip = Math.round(type_Dip);
                    //最外層有一個大的TableLayout,再設置TableRow包住小的TableLayout
                    //平均分配列的寬度
                    search_tlt.setStretchAllColumns(true);
                    //設置大TableLayout的TableRow
                    TableRow big_tbr = new TableRow(OrderSearchActivity.this);
                    //設置每筆資料的小TableLayout
                    TableLayout small_tb = new TableLayout(OrderSearchActivity.this);
                    //全部列自動填充空白處
                    small_tb.setStretchAllColumns(true);
                    small_tb.setBackgroundResource(R.drawable.warningline);
                    //設定TableRow的寬高
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    JArrayList = jb.getStringArrayList("JSON_data");

                    Log.e("OrderSearchActivity", JArrayList.get(0));
                    //設置每筆TableLayout的Button監聽器、與動態新增Button的ID
                    int loc = 0;
                    for (int j = 0; j < dynamically_imv.length; j++) {
                        if (dynamically_imv[j] == null) {
                            loc = j;
                            break;
                        }
                    }
                    dynamically_imv[loc] = new ImageView(OrderSearchActivity.this);
                    dynamically_imv[loc].setImageResource(R.drawable.quotation);
                    dynamically_imv[loc].setScaleType(ImageView.ScaleType.CENTER);
                    dynamically_imv[loc].setId(loc);
                    dynamically_imv[loc].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int a = 0; a < dynamically_imv.length; a++) {
                                if (v.getId() == dynamically_imv[a].getId()) {
                                    Intent intent_quotation = new Intent(OrderSearchActivity.this, PickingActivity.class);
                                    //取得大TableLayout中的大TableRow位置
                                    TableRow tb_tbr = (TableRow) search_tlt.getChildAt(a);
                                    //取得大TableRow中的小TableLayout位置
                                    TableLayout tb_tbr_tb = (TableLayout) tb_tbr.getChildAt(1);
                                    //取得小TableLayout中的小TableRow位置
                                    TableRow tb_tbr_tb_tbr = (TableRow) tb_tbr_tb.getChildAt(0);
                                    //派工資料的迴圈
                                    for (int x = 0; x < 4; x++) {
                                        //小TableRow中的layout_column位置
                                        TextView order_txt = (TextView) tb_tbr_tb_tbr.getChildAt(x);
                                        String ResponseText = order_txt.getText().toString();
                                        String company = String.valueOf(company_spinner.getSelectedItem());
                                        String order_type = String.valueOf(type_spinner.getSelectedItem());
                                        //將SQL裡的資料傳到MaintainActivity
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ResponseText" + x, ResponseText);
                                        bundle.putString("company", company);
                                        bundle.putString("order_type", order_type);

                                        intent_quotation.putExtras(bundle);//可放所有基本類別
                                        Log.e("OrderSearchActivity111", ResponseText);

                                    }
                                    search_tlt.removeAllViews();
                                    separate_llt.setVisibility(View.GONE);
                                    title_llt.setVisibility(View.GONE);
                                    startActivity(intent_quotation);
                                }
                            }
                        }
                    });

                    //顯示每筆TableLayout的SQL資料
                    //TextView dynamically_txt1;
                    dynamically_txt1 = new TextView(OrderSearchActivity.this);
                    dynamically_txt1.setText(JArrayList.get(0).replace(" ", ""));
                    dynamically_txt1.setPadding(0, 2, 0, 0);
                    dynamically_txt1.setGravity(Gravity.LEFT);
                    dynamically_txt1.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_txt1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    //顯示每筆TableLayout的SQL資料
                    //TextView dynamically_txt2;
                    dynamically_txt2 = new TextView(OrderSearchActivity.this);
                    dynamically_txt2.setText(JArrayList.get(1));
                    dynamically_txt2.setPadding(0, 2, 0, 0);
                    dynamically_txt2.setGravity(Gravity.LEFT);
                    dynamically_txt2.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_txt2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    //顯示每筆TableLayout的SQL資料
                    //TextView dynamically_txt3;
                    dynamically_txt3 = new TextView(OrderSearchActivity.this);
                    dynamically_txt3.setText(JArrayList.get(2));
                    dynamically_txt3.setPadding(0, 2, 0, 0);
                    dynamically_txt3.setGravity(Gravity.LEFT);
                    dynamically_txt3.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_txt3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    //顯示每筆TableLayout的SQL資料
                    //TextView dynamically_txt3;
                    dynamically_txt4 = new TextView(OrderSearchActivity.this);
                    dynamically_txt4.setText(JArrayList.get(3));
                    dynamically_txt4.setPadding(0, 2, 0, 0);
                    dynamically_txt4.setGravity(Gravity.LEFT);
                    dynamically_txt4.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_txt4.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    TableRow tr1 = new TableRow(OrderSearchActivity.this);
                    tr1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tr1.addView(dynamically_txt1, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, type_dip));
                    tr1.addView(dynamically_txt2, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, type_dip));
                    tr1.addView(dynamically_txt3, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, type_dip));
                    tr1.addView(dynamically_txt4, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, type_dip));

                    small_tb.addView(tr1, layoutParams);
                    //about_ImageView寬為0,高為WRAP_CONTENT,權重比為1
                    big_tbr.addView(dynamically_imv[loc], new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                    //small_tb寬為0,高為WRAP_CONTENT,權重比為3
                    big_tbr.addView(small_tb, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 2));
                    //dynamically_imv[0].setVisibility(View.INVISIBLE);

                    TableRow.LayoutParams the_param3;
                    the_param3 = (TableRow.LayoutParams) small_tb.getLayoutParams();
                    the_param3.span = 2;
                    the_param3.width = TableRow.LayoutParams.MATCH_PARENT;
                    small_tb.setLayoutParams(the_param3);

                    search_tlt.addView(big_tbr);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
