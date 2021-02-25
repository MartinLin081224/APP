package com.example.a10609516.app.Manager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
//import android.support.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10609516.app.R;
import com.example.a10609516.app.Tools.ScannerActivity;
import com.example.a10609516.app.Tools.WQPServiceActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PickingActivity extends WQPServiceActivity {

    private LinearLayout picking_llt, id_llt, picking_record_llt, large_llt, big_llt;
    private ImageView add_imv, add_qrcode;
    private Button master_btn, id_btn, check_btn, vendor_sign_btn, sales_sign_btn;
    private TextView order_txt, separate_txt, item_number_txt, item_name_txt, item_format_txt,
            barcode_txt, unit_txt, today_record_txt, past_record_txt;
    private EditText factory_id_edt, picking_edt, note_edt;
    private Spinner type_spinner, company_spinner, warehouse_spinner;

    private ArrayAdapter<String> type_listAdapter, company_listAdapter, warehouse_listAdapter;
    private String[] type_list = new String[]{"請選擇", "撿料", "還料"};
    private String[] company_list = new String[]{"請選擇", "拓霖", "拓亞鈦", "倍偉特"};
    private String[] empty = new String[]{"(請選擇)"};
    private String[] warehouse_list = new String[]{};

    private String company, order_type, order_number, cust_id,
            COMPANY, MB001, MB002, MB003, MB004, company_ch,
            item_id, barcode, w_id, w_name, COUNT;

    //轉畫面的Activity參數
    private Class<?> mClss;
    //ZXING_CAMERA權限
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking);
        //動態取得 View 物件
        InItFunction();
        //取得OrderSearchActivity傳遞過來的值
        GetResponseData();
        //與資料庫WareHousePickingSearch.php進行連線(查詢有無建立撿料單)
        sendRequestWithOkHttpForPickingSearch();
        //領/還料的Spinner
        TypeSpinner();
        //公司別的Spinner下拉選單
        CompanySpinner();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        picking_llt = (LinearLayout) findViewById(R.id.picking_llt);
        id_llt = (LinearLayout) findViewById(R.id.id_llt);
        picking_record_llt = (LinearLayout) findViewById(R.id.picking_record_llt);
        master_btn = (Button) findViewById(R.id.master_btn);
        id_btn = (Button) findViewById(R.id.id_btn);
        check_btn = (Button) findViewById(R.id.check_btn);
        vendor_sign_btn = (Button) findViewById(R.id.vendor_sign_btn);
        sales_sign_btn = (Button) findViewById(R.id.sales_sign_btn);
        add_imv = (ImageView) findViewById(R.id.add_imv);
        add_qrcode = (ImageView) findViewById(R.id.add_qrcode);
        order_txt = (TextView) findViewById(R.id.order_txt);
        separate_txt = (TextView) findViewById(R.id.separate_txt);
        item_number_txt = (TextView) findViewById(R.id.item_number_txt);
        item_name_txt = (TextView) findViewById(R.id.item_name_txt);
        item_format_txt = (TextView) findViewById(R.id.item_format_txt);
        barcode_txt = (TextView) findViewById(R.id.barcode_txt);
        unit_txt = (TextView) findViewById(R.id.unit_txt);
        today_record_txt = (TextView) findViewById(R.id.today_record_txt);
        past_record_txt = (TextView) findViewById(R.id.past_record_txt);
        factory_id_edt = (EditText) findViewById(R.id.factory_id_edt);
        picking_edt = (EditText) findViewById(R.id.picking_edt);
        note_edt = (EditText) findViewById(R.id.note_edt);
        type_spinner = (Spinner) findViewById(R.id.type_spinner);
        company_spinner = (Spinner) findViewById(R.id.company_spinner);
        warehouse_spinner = (Spinner) findViewById(R.id.warehouse_spinner);

        //picking_llt.setOnClickListener監聽器  //顯示/隱藏鍵盤
        picking_llt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });
        //master_btn.setOnClickListener監聽器  //建立撿料單單頭
        master_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //與資料庫WareHousePickingMaster.php進行連線(建立撿料單單頭)
                sendRequestWithOkHttpForPickingMaster();
                Toast.makeText(PickingActivity.this, "【建立完成】", Toast.LENGTH_SHORT).show();
                master_btn.setVisibility(View.GONE);
            }
        });
        //add_btn.setOnClickListener監聽器  //手動輸入ID新增品號
        add_imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id_llt.getVisibility() == View.GONE) {
                    id_llt.setVisibility(View.VISIBLE);
                    separate_txt.setVisibility(View.VISIBLE);
                    factory_id_edt.setFocusable(true);
                    factory_id_edt.setFocusableInTouchMode(true);
                    factory_id_edt.requestFocus();
                } else {
                    id_llt.setVisibility(View.GONE);
                    separate_txt.setVisibility(View.GONE);
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });
        //id_btn.setOnClickListener監聽器  //手動輸入ID新增品號
        id_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picking_llt.setVisibility(View.VISIBLE);
                item_number_txt.setText("");
                item_name_txt.setText("");
                item_format_txt.setText("");
                barcode_txt.setText("");
                sendRequestWithOkHttpForFactoryID();
                factory_id_edt.setText("");
                picking_edt.setText("");
                note_edt.setText("");
                id_llt.setVisibility(View.GONE);
                check_btn.setVisibility(View.VISIBLE);
                //領/還料的Spinner
                //TypeSpinner();
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });
        //check_btn.setOnClickListener監聽器  //撿料/還料完成，與OKHTTP進行連線
        check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //與資料庫WareHousePickingSearch.php進行連線(查詢有無建立撿料單)
                sendRequestWithOkHttpForPickingSearch();
                if (COUNT.equals("1")) {
                    if (String.valueOf(type_spinner.getSelectedItem()).equals("請選擇")) {
                        Toast.makeText(PickingActivity.this, "【請選擇撿料/還料】", Toast.LENGTH_SHORT).show();
                    } else {
                        if (picking_edt.getText().toString().equals("")) {
                            Toast.makeText(PickingActivity.this, "【請輸入撿貨數量】", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PickingActivity.this, "【撿料完成】", Toast.LENGTH_SHORT).show();
                            //與WareHouseLog.PHP取得連線
                            sendRequestWithOkHttpForPickingDetail();
                            check_btn.setVisibility(View.GONE);
                            //取得今日LOG
                            today_record_txt.setTextColor(Color.rgb(0, 127, 255));
                            past_record_txt.setTextColor(Color.rgb(62, 58, 57));
                            picking_record_llt.removeAllViews();
                            //與PickingLogToday.PHP取得連線
                            sendRequestWithOkHttpForPickingLogToday();
                        }
                    }
                } else {
                    Toast.makeText(PickingActivity.this, "【請先建立撿料單】", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //vendor_sign_btn.setOnClickListener監聽器  //進入廠商電子簽章頁面
        vendor_sign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle order_bundle = getIntent().getExtras();
                company = order_bundle.getString("ResponseText" + 0);
                order_type = order_bundle.getString("ResponseText" + 1);
                order_number = order_bundle.getString("ResponseText" + 2);
                cust_id = order_bundle.getString("ResponseText" + 3);
                Intent intent = new Intent(PickingActivity.this, Picking_SignatureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ResponseText0", company);
                bundle.putString("ResponseText1", order_type);
                bundle.putString("ResponseText2", order_number);
                bundle.putString("ResponseText3", cust_id);
                intent.putExtras(bundle);//可放所有基本類別
                startActivity(intent);
                Log.e("PickingActivity", company);
                Log.e("PickingActivity", order_type);
                Log.e("PickingActivity", order_number);
                Log.e("PickingActivity", cust_id);
            }
        });
        //sales_sign_btn.setOnClickListener監聽器  //進入業務電子簽章頁面
        sales_sign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle order_bundle = getIntent().getExtras();
                company = order_bundle.getString("ResponseText" + 0);
                order_type = order_bundle.getString("ResponseText" + 1);
                order_number = order_bundle.getString("ResponseText" + 2);
                cust_id = order_bundle.getString("ResponseText" + 3);
                Intent intent = new Intent(PickingActivity.this, Picking_SignatureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ResponseText0", company);
                bundle.putString("ResponseText1", order_type);
                bundle.putString("ResponseText2", order_number);
                bundle.putString("ResponseText3", cust_id);
                intent.putExtras(bundle);//可放所有基本類別
                startActivity(intent);
                Log.e("PickingActivity", company);
                Log.e("PickingActivity", order_type);
                Log.e("PickingActivity", order_number);
                Log.e("PickingActivity", cust_id);
            }
        });

        today_record_txt.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                today_record_txt.setTextColor(Color.rgb(0, 127, 255));
                past_record_txt.setTextColor(Color.rgb(62, 58, 57));
                picking_record_llt.removeAllViews();
                //與PickingLogToday.PHP取得連線
                sendRequestWithOkHttpForPickingLogToday();
            }
        });
        past_record_txt.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                past_record_txt.setTextColor(Color.rgb(0, 127, 255));
                today_record_txt.setTextColor(Color.rgb(62, 58, 57));
                picking_record_llt.removeAllViews();
                //與PickingLogPast.PHP取得連線
                sendRequestWithOkHttpForPickingLogPast();
            }
        });
    }

    /**
     * 取得SearchActivity傳遞過來的值
     */
    private void GetResponseData() {
        Bundle bundle = getIntent().getExtras();
        String ResponseText0 = bundle.getString("ResponseText" + 0);
        String ResponseText1 = bundle.getString("ResponseText" + 1);
        String ResponseText2 = bundle.getString("ResponseText" + 2);
        String ResponseText3 = bundle.getString("ResponseText" + 3);
        order_txt.setText(ResponseText1 + "-" + ResponseText2);
        Log.e("PickingActivity", ResponseText0);
        Log.e("PickingActivity", ResponseText1);
        Log.e("PickingActivity", ResponseText2);
        Log.e("PickingActivity", ResponseText3);
    }

    /**
     * Button的設置
     * 掃描QR CODE新增品號
     */
    public void scanCode(View view) {
        //startActivityForResult(new Intent(this, ScannerActivity.class), 1);
        launchActivity(ScannerActivity.class);
    }

    /**
     * 轉畫面的封包，兼具權限和Intent跳轉化面
     */
    public void launchActivity(Class<?> clss) {
        //假如還「未獲取」權限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            //startActivity(intent);
            startActivityForResult(intent, ZXING_CAMERA_PERMISSION);
        }
    }

    /**
     * 取回掃描回傳值或取消掃描
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //ZXing回傳的內容
                String contents = intent.getStringExtra("SCAN_RESULT");
                picking_llt.setVisibility(View.VISIBLE);
                factory_id_edt.setText(intent.getStringExtra("result_text"));
                item_number_txt.setText("");
                item_name_txt.setText("");
                item_format_txt.setText("");
                barcode_txt.setText("");
                sendRequestWithOkHttpForFactoryID();
                check_btn.setVisibility(View.VISIBLE);
                picking_edt.setText("");
                note_edt.setText("");
                //領/還料的Spinner
                //TypeSpinner();
                factory_id_edt.setText("");
            } else {
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(PickingActivity.this, "取消掃描", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * 領/還料的Spinner
     */
    private void TypeSpinner() {
        type_listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type_list);
        type_listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(type_listAdapter);
    }

    /**
     * 公司別的Spinner下拉選單
     */
    private void CompanySpinner() {
        context = this;
        //程式剛啟始時載入第一個下拉選單
        company_listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, company_list);
        company_listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        company_spinner.setAdapter(company_listAdapter);
        company_spinner.setOnItemSelectedListener(selectListener);
        //因為下拉選單第一個為請選擇，所以不載入
        warehouse_listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, empty);
        warehouse_listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        warehouse_spinner.setAdapter(warehouse_listAdapter);
    }

    /**
     * 倉庫庫別
     */
    /**
     * 第一個下拉類別的監看式
     */
    private AdapterView.OnItemSelectedListener selectListener = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            //讀取第一個下拉選單是選擇第幾個
            //int pos = type_spinner.getSelectedItemPosition();
            //與WareHouse.PHP取得連線
            sendRequestWithOkHttpForWareHouse();
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    /**
     * 與OKHttp連線(查詢原廠序號資料)
     */
    private void sendRequestWithOkHttpForFactoryID() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String f_id = factory_id_edt.getText().toString();
                Log.e("PickingActivity8", f_id);

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("item_id", f_id)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WareHouseFactoryID.php")
                            //.url("http://192.168.0.172/WQP/WareHouseFactoryID.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("PickingActivity999", responseData);
                    parseJSONWithJSONObjectForFactoryID(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 取得撿貨之公司別、品號、品名、規格、單位、原廠序號、BarCode
     *
     * @param jsonData
     */
    private void parseJSONWithJSONObjectForFactoryID(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                COMPANY = jsonObject.getString("COMPANY");
                MB001 = jsonObject.getString("MB001");
                MB002 = jsonObject.getString("MB002");
                MB003 = jsonObject.getString("MB003");
                MB004 = jsonObject.getString("MB004");
                item_id = jsonObject.getString("ITEM");
                barcode = jsonObject.getString("BarCode");

                PickingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        item_number_txt.setText(MB001.toString());
                        item_name_txt.setText(MB002.toString());
                        item_format_txt.setText(MB003.toString());
                        unit_txt.setText(MB004.toString());
                        barcode_txt.setText(barcode.toString());
                        /*//公司別的Spinner下拉選單
                        CompanySpinner();*/
                        //轉換公司別中英文
                        if (COMPANY.toString().equals("WQP")) {
                            company_ch = "拓霖";
                        } else if (COMPANY.toString().equals("TYT")) {
                            company_ch = "拓亞鈦";
                        } else if (COMPANY.toString().equals("BWT")){
                            company_ch = "倍偉特";
                        } else {
                            company_ch = "請選擇";
                        }
                        //當迴圈與COMPANY內容一致時跳出迴圈 並顯示該公司別的Spinner位置
                        for (int c = 0; c < company_list.length; c++) {
                            if (company_list[c].equals(company_ch)) {
                                company_spinner.setSelection(c, true);
                                break;
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新UI
     */
    /*@SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    large_llt = new LinearLayout(PickingActivity.this);
                    large_llt.setOrientation(LinearLayout.HORIZONTAL);
                    big_llt = new LinearLayout(PickingActivity.this);
                    big_llt.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout small_llt1 = new LinearLayout(PickingActivity.this);
                    small_llt1.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt2 = new LinearLayout(PickingActivity.this);
                    small_llt2.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt3 = new LinearLayout(PickingActivity.this);
                    small_llt3.setOrientation(LinearLayout.HORIZONTAL);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    //int i = b.getStringArrayList("JSON_data").size();
                    JArrayList = jb.getStringArrayList("JSON_data");

                    TextView dynamically_empty1;
                    dynamically_empty1 = new TextView(PickingActivity.this);
                    TextView dynamically_empty2;
                    dynamically_empty2 = new TextView(PickingActivity.this);
                    TextView dynamically_empty3;
                    dynamically_empty3 = new TextView(PickingActivity.this);
                    TextView dynamically_empty4;
                    dynamically_empty4 = new TextView(PickingActivity.this);
                    TextView dynamically_empty5;
                    dynamically_empty5 = new TextView(PickingActivity.this);
                    TextView dynamically_empty6;
                    dynamically_empty6 = new TextView(PickingActivity.this);
                    TextView dynamically_empty7;
                    dynamically_empty7 = new TextView(PickingActivity.this);
                    TextView dynamically_empty8;
                    dynamically_empty8 = new TextView(PickingActivity.this);

                    //顯示每筆LinearLayout的品號
                    TextView dynamically_number;
                    dynamically_number = new TextView(PickingActivity.this);
                    dynamically_number.setText("品號 : " + JArrayList.get(1));
                    dynamically_number.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_number.setPadding(20, 10, 0, 0);
                    dynamically_number.setGravity(Gravity.CENTER_VERTICAL);
                    dynamically_number.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    //dynamically_number.setWidth(50);

                    Button click_btn = new Button(PickingActivity.this);
                    click_btn.setText("確認");
                    click_btn.setBackgroundResource(R.drawable.check_button);
                    click_btn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                    click_btn.setTextColor(Color.rgb(6, 102, 219));
                    click_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //sendRequestWithOkHttpForPicking();
                        }
                    });

                    Button delete_btn = new Button(PickingActivity.this);
                    delete_btn.setText("刪除");
                    delete_btn.setBackgroundResource(R.drawable.cancel_button);
                    delete_btn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                    delete_btn.setTextColor(Color.rgb(6, 102, 219));
                    delete_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LinearLayout delete_llt = (LinearLayout) view.getParent();
                            picking_llt.removeView(delete_llt);
                        }
                    });

                    //顯示每筆LinearLayout的品名
                    TextView dynamically_name;
                    dynamically_name = new TextView(PickingActivity.this);
                    dynamically_name.setText("品名 : " + JArrayList.get(2));
                    dynamically_name.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_name.setPadding(20, 0, 0, 0);
                    dynamically_name.setGravity(Gravity.CENTER_VERTICAL);
                    dynamically_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

                    //輸入每筆LinearLayout的數量
                    EditText dynamically_count;
                    dynamically_count = new EditText(PickingActivity.this);
                    dynamically_count.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_count.setPadding(20, 0, 0, 0);
                    dynamically_count.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    dynamically_count.setInputType(InputType.TYPE_CLASS_NUMBER);

                    //顯示每筆LinearLayout的品號單位
                    TextView dynamically_unit;
                    dynamically_unit = new TextView(PickingActivity.this);
                    dynamically_unit.setText(JArrayList.get(4));
                    dynamically_unit.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_unit.setPadding(20, 0, 0, 0);
                    dynamically_unit.setGravity(Gravity.CENTER_VERTICAL);
                    dynamically_unit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

                    //設置每筆LinearLayout的分隔線
                    TextView dynamically_txt = new TextView(PickingActivity.this);
                    dynamically_txt.setBackgroundColor(Color.rgb(220, 220, 220));
                    dynamically_txt.setPadding(20, 0, 0, 10);

                    //LinearLayout.LayoutParams small_pm = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

                    small_llt1.addView(dynamically_empty1, new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, (float)0.1));
                    small_llt1.addView(dynamically_number, new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, 3));
                    small_llt1.addView(dynamically_empty2, new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    small_llt2.addView(dynamically_empty3, new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, (float)0.1));
                    small_llt2.addView(dynamically_name, new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, 3));
                    small_llt2.addView(dynamically_empty4, new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    small_llt3.addView(dynamically_empty5, new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, (float)0.1));
                    small_llt3.addView(dynamically_count, new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, 3));
                    small_llt3.addView(dynamically_unit, new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, (float)0.5));
                    small_llt3.addView(dynamically_empty6, new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, (float)0.5));

                    big_llt.addView(small_llt1);
                    big_llt.addView(small_llt2);
                    big_llt.addView(small_llt3);
                    big_llt.addView(dynamically_txt, LinearLayout.LayoutParams.MATCH_PARENT, 3);

                    large_llt.addView(big_llt, new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.WRAP_CONTENT, 8));
                    large_llt.addView(delete_btn, new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.MATCH_PARENT, 1));
                    large_llt.addView(click_btn, new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.MATCH_PARENT, 1));

                    picking_llt.addView(large_llt);

                    *//*LinearLayout gone1 = (LinearLayout) picking_llt.getChildAt(0);
                    LinearLayout gone2 = (LinearLayout) gone1.getChildAt(0);
                    LinearLayout gone3 = (LinearLayout) gone2.getChildAt(0);
                    TextView gone4 = (TextView) gone3.getChildAt(0);
                    gone4.setVisibility(View.GONE);*//*

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/

    /**
     * 與OkHttp建立連線(倉庫別)
     */
    private void sendRequestWithOkHttpForWareHouse() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String spinner_select = String.valueOf(company_spinner.getSelectedItem());

                if (spinner_select.equals("拓霖")) {
                    company_ch = "WQP";
                } else if (spinner_select.equals("拓亞鈦")) {
                    company_ch = "TYT";
                } else if (spinner_select.equals("倍偉特")) {
                    company_ch = "BWT";
                }
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("COMPANY", company_ch)
                            .build();
                    Log.e("PickingActivity", company_ch);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/Warehouse.php")
                            //.url("http://192.168.0.172/WQP/Warehouse.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("PickingActivity", responseData);
                    parseJSONWithJSONObjectForWareHouse(responseData);
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
    private void parseJSONWithJSONObjectForWareHouse(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            ArrayList<String> JArrayList = new ArrayList<String>();
            warehouse_list = new String[]{};
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String warehouse_id = jsonObject.getString("MC001").trim() + "(" + jsonObject.getString("MC002").trim() + ")";
                //JSONArray加入SearchData資料
                JArrayList.add(warehouse_id);
                warehouse_list = JArrayList.toArray(new String[i]);
                //Log.e("PictureActivity3", diy[i]);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //重新產生新的Adapter/*，用的是二維陣列type2[pos]*/
                    warehouse_listAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, warehouse_list);
                    //載入第二個下拉選單Spinner
                    warehouse_spinner.setAdapter(warehouse_listAdapter);
                    warehouse_spinner.setOnItemSelectedListener(selectListener2);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下拉類別的監看式
     */
    private AdapterView.OnItemSelectedListener selectListener2 = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            //讀取下拉選單是選擇第幾個
            //int pos = type_spinner.getSelectedItemPosition();
            picking_record_llt.removeAllViews();
            PickingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    picking_edt.setFocusable(true);
                    picking_edt.setFocusableInTouchMode(true);
                    picking_edt.requestFocus();
                    //與FactoryID.PHP取得連線
                    //sendRequestWithOkHttpForFactoryID();
                }
            });
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    /**
     * 與OKHttp連線(查詢有無建立撿料單)
     */
    private void sendRequestWithOkHttpForPickingSearch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = getIntent().getExtras();
                String COMPANY = bundle.getString("ResponseText" + 0);
                String TE001 = bundle.getString("ResponseText" + 1);
                String TE002 = bundle.getString("ResponseText" + 2);
                Log.e("PickingActivity11", COMPANY);

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("TE001", TE001)
                            .add("TE002", TE002)
                            .add("COMPANY", COMPANY)
                            .build();
                    Log.e("PickingActivity88", COMPANY + "-" + TE001 + "-" + TE002);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WareHousePickingSearch.php")
                            //.url("http://192.168.0.172/WQP/WareHousePickingSearch.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("PickingActivity", responseData);
                    parseJSONWithJSONObjectForPickingSearch(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 取得查詢有無建立撿料單
     *
     * @param jsonData
     */
    private void parseJSONWithJSONObjectForPickingSearch(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                COUNT = jsonObject.getString("COUNT");
                Log.e("PickingActivity", COUNT);
                if (COUNT.equals("1")) {
                    master_btn.setVisibility(View.GONE);
                } else {
                    master_btn.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 與OKHttp連線(上傳撿料單單頭)
     */
    private void sendRequestWithOkHttpForPickingMaster() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("PickingActivity", user_id_data);

                Bundle bundle = getIntent().getExtras();
                String COMPANY = bundle.getString("ResponseText" + 0);
                String TE001 = bundle.getString("ResponseText" + 1);
                String TE002 = bundle.getString("ResponseText" + 2);
                Log.e("PickingActivity11", COMPANY);

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("TE001", TE001)
                            .add("TE002", TE002)
                            .add("COMPANY", COMPANY)
                            .add("User_id", user_id_data)
                            .build();
                    Log.e("PickingActivity88", COMPANY + "-" + TE001 + "-" + TE002 + "-" + user_id_data);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WareHousePickingMaster.php")
                            //.url("http://192.168.0.172/WQP/WareHousePickingMaster.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("PickingActivity", responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 與OKHttp連線(上傳撿料單單身)
     */
    private void sendRequestWithOkHttpForPickingDetail() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("PickingActivity", user_id_data);

                Bundle bundle = getIntent().getExtras();
                String TE001 = bundle.getString("ResponseText" + 1).trim();
                String TE002 = bundle.getString("ResponseText" + 2).trim();
                String COMPANY = String.valueOf(company_spinner.getSelectedItem()).trim();
                String TE004 = item_number_txt.getText().toString().trim();
                String TE005 = picking_edt.getText().toString().trim();
                String TE006 = unit_txt.getText().toString().trim();
                String TE008 = String.valueOf(warehouse_spinner.getSelectedItem()).trim();
                String NOTE = note_edt.getText().toString().trim();
                String GET_BACK = String.valueOf(type_spinner.getSelectedItem()).trim();
                Log.e("PickingActivity8", TE004);

                if (warehouse_spinner.getSelectedItemId() == 0) {
                    Log.e("123456", String.valueOf(warehouse_spinner.getSelectedItem()));
                } else if (warehouse_spinner.getSelectedItemId() == 1) {
                    Log.e("123456", String.valueOf(warehouse_spinner.getSelectedItem()));
                }

                if(TE008.equals("BWT(BWT總倉)")){
                    w_id = "BWT";
                    w_name = "BWT總倉";
                }else if (TE008.equals("BWT-De(BWT不良品)")){
                    w_id = "BWT-De";
                    w_name = "BWT不良品";
                }else if (TE008.equals("OM-BZA(委外-鉑中)")){
                    w_id = "OM-BZA";
                    w_name = "委外-鉑中";
                }else if (TE008.equals("WPA(暫存倉)")){
                    w_id = "WPA";
                    w_name = "暫存倉";
                }else if (TE008.equals("WQP(拓霖倉)")){
                    w_id = "WQP";
                    w_name = "拓霖倉";
                }else if (TE008.equals("BRT(測試研發倉)")){
                    w_id = "BRT";
                    w_name = "測試研發倉";
                }else if (TE008.equals("DSV(百及倉)")){
                    w_id = "DSV";
                    w_name = "百及倉";
                }else if (TE008.equals("OM-SOL(委外-利騏)")){
                    w_id = "OM-SOL";
                    w_name = "委外-利騏";
                }else if (TE008.equals("Borrow(場外借用)")){
                    w_id = "Borrow";
                    w_name = "場外借用";
                }else if (TE008.equals("01-10000AT(拓霖AQT倉)")){
                    w_id = "01-10000AT";
                    w_name = "拓霖AQT倉";
                }else if (TE008.equals("01-10000TP(拓霖台北倉)")){
                    w_id = "01-10000TP";
                    w_name = "拓霖台北倉";
                }else if (TE008.equals("01-10000TY(拓霖桃園倉)")){
                    w_id = "01-10000TY";
                    w_name = "拓霖桃園倉";
                }else if (TE008.equals("01-10000HS(拓霖新竹倉)")){
                    w_id = "01-10000HS";
                    w_name = "拓霖新竹倉";
                }else if (TE008.equals("01-10000TC(拓霖台中倉)")){
                    w_id = "01-10000TC";
                    w_name = "拓霖台中倉";
                }else if (TE008.equals("01-10000KH(拓霖高雄倉)")){
                    w_id = "01-10000KH";
                    w_name = "拓霖高雄倉";
                }else if (TE008.equals("01-00001TY(新百及倉庫)")){
                    w_id = "01-00001TY";
                    w_name = "新百及倉庫";
                }

                if (COMPANY.toString().equals("拓霖")) {
                    COMPANY = "WQP";
                } else if (COMPANY.toString().equals("拓亞鈦")) {
                    COMPANY = "TYT";
                } else {
                    COMPANY = "BWT";
                }

                if (GET_BACK.equals("撿料")) {
                    GET_BACK = "領料";
                }

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("TE001", TE001)
                            .add("TE002", TE002)
                            .add("COMPANY", COMPANY)
                            .add("User_id", user_id_data)
                            .add("TE004", TE004)
                            .add("TE005", TE005)
                            .add("TE006", TE006)
                            .add("TE008", w_id)
                            .add("NOTE", NOTE)
                            .add("GET_BACK", GET_BACK)
                            .build();
                    Log.e("PickActivity9", COMPANY + "-" + TE004 + "-" + TE005 + "-" + TE006 + "-" + w_id);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WareHousePickingDetail.php")
                            //.url("http://192.168.0.172/WQP/WareHousePickingDetail.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("PickingActivity99", responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 與OKHttp連線(查詢撿/還料之序號、類型、品號、品名、規格、庫別、數量、單位、撿/還料日期的Log)
     */
    private void sendRequestWithOkHttpForPickingLogToday() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Bundle bundle = getIntent().getExtras();
                String COMPANY = bundle.getString("ResponseText" + 0);
                String TE001 = bundle.getString("ResponseText" + 1);
                String TE002 = bundle.getString("ResponseText" + 2);

                Log.e("PickingActivity", TE001);

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("TE001", TE001)
                            .add("TE002", TE002)
                            .add("COMPANY", COMPANY)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WareHousePickingLogToday.php")
                            //.url("http://192.168.0.172/WQP/WareHousePickingLogToday.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("PickingActivity", responseData);
                    parseJSONWithJSONObjectForPickingLog(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 與OKHttp連線(查詢撿/還料之序號、類型、品號、品名、規格、庫別、數量、單位、撿/還料日期的Log)
     */
    private void sendRequestWithOkHttpForPickingLogPast() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Bundle bundle = getIntent().getExtras();
                String COMPANY = bundle.getString("ResponseText" + 0);
                String TE001 = bundle.getString("ResponseText" + 1);
                String TE002 = bundle.getString("ResponseText" + 2);

                Log.e("PickingActivity", TE001);

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("TE001", TE001)
                            .add("TE002", TE002)
                            .add("COMPANY", COMPANY)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WareHousePickingLogPast.php")
                            //.url("http://192.168.0.172/WQP/WareHousePickingLogPast.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("PickingActivity", responseData);
                    parseJSONWithJSONObjectForPickingLog(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 取得撿料之序號、類型、品號、品名、規格、庫別、數量、單位、撿/還料日期的Log
     *
     * @param jsonData
     */
    private void parseJSONWithJSONObjectForPickingLog(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String TE003 = jsonObject.getString("TE003");
                //String GET_BACK = new String(jsonObject.getString("GET_BACK").getBytes(),"utf-8");
                String GET_BACK = jsonObject.getString("GET_BACK");
                String TE004 = jsonObject.getString("TE004");
                String MB002 = jsonObject.getString("MB002");
                String MB003 = jsonObject.getString("MB003");
                String TE005 = jsonObject.getString("TE005");
                String TE006 = jsonObject.getString("TE006");
                String TE008 = jsonObject.getString("TE008");
                String G_B_DATE = jsonObject.getString("G_B_DATE");

                Log.e("PickingActivity22", GET_BACK);

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(TE003);
                JArrayList.add(GET_BACK);
                JArrayList.add(TE004);
                JArrayList.add(MB002);
                JArrayList.add(MB003);
                JArrayList.add(TE005);
                JArrayList.add(TE006);
                JArrayList.add(TE008);
                JArrayList.add(G_B_DATE);

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
                    LinearLayout small_llt1 = new LinearLayout(PickingActivity.this);
                    small_llt1.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt2 = new LinearLayout(PickingActivity.this);
                    small_llt2.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt3 = new LinearLayout(PickingActivity.this);
                    small_llt3.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt4 = new LinearLayout(PickingActivity.this);
                    small_llt4.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt5 = new LinearLayout(PickingActivity.this);
                    small_llt5.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt6 = new LinearLayout(PickingActivity.this);
                    small_llt6.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt7 = new LinearLayout(PickingActivity.this);
                    small_llt7.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt8 = new LinearLayout(PickingActivity.this);
                    small_llt8.setOrientation(LinearLayout.HORIZONTAL);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    //int i = b.getStringArrayList("JSON_data").size();
                    JArrayList = jb.getStringArrayList("JSON_data");

                    //顯示每筆LinearLayout的序號
                    TextView dynamically_number;
                    dynamically_number = new TextView(PickingActivity.this);
                    dynamically_number.setText("序號 : " + JArrayList.get(0));
                    dynamically_number.setGravity(Gravity.LEFT);
                    dynamically_number.setPadding(20, 3, 0, 0);
                    //dynamically_number.setWidth(50);
                    dynamically_number.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //顯示每筆LinearLayout的撿料/還料
                    TextView dynamically_type;
                    dynamically_type = new TextView(PickingActivity.this);
                    dynamically_type.setText("類型 : " + JArrayList.get(1));
                    dynamically_type.setGravity(Gravity.LEFT);
                    dynamically_type.setPadding(20, 3, 0, 0);
                    dynamically_type.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //顯示每筆LinearLayout的品號
                    TextView dynamically_id;
                    dynamically_id = new TextView(PickingActivity.this);
                    dynamically_id.setText("品號 : " + JArrayList.get(2));
                    dynamically_id.setGravity(Gravity.LEFT);
                    dynamically_id.setPadding(20, 3, 0, 0);
                    dynamically_id.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //顯示每筆LinearLayout的品名
                    TextView dynamically_name;
                    dynamically_name = new TextView(PickingActivity.this);
                    dynamically_name.setText("品名 : " + JArrayList.get(3));
                    dynamically_name.setGravity(Gravity.LEFT);
                    dynamically_name.setPadding(20, 3, 0, 0);
                    dynamically_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //顯示每筆LinearLayout的規格
                    TextView dynamically_format;
                    dynamically_format = new TextView(PickingActivity.this);
                    dynamically_format.setText("規格 : " + JArrayList.get(4));
                    dynamically_format.setGravity(Gravity.LEFT);
                    dynamically_format.setPadding(20, 3, 0, 0);
                    //dynamically_format.setWidth(50);
                    dynamically_format.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //顯示每筆LinearLayout的撿貨數量
                    TextView dynamically_count;
                    dynamically_count = new TextView(PickingActivity.this);
                    dynamically_count.setText("撿貨數量 : " + JArrayList.get(5) + " " + JArrayList.get(6));
                    dynamically_count.setGravity(Gravity.LEFT);
                    dynamically_count.setPadding(20, 3, 0, 0);
                    dynamically_count.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //顯示每筆LinearLayout的庫別
                    TextView dynamically_warehouse;
                    dynamically_warehouse = new TextView(PickingActivity.this);
                    dynamically_warehouse.setText("庫別 : " + JArrayList.get(7));
                    dynamically_warehouse.setGravity(Gravity.LEFT);
                    dynamically_warehouse.setPadding(20, 3, 0, 0);
                    dynamically_warehouse.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //顯示每筆LinearLayout的撿/還料日期
                    TextView dynamically_G_B_date;
                    dynamically_G_B_date = new TextView(PickingActivity.this);
                    dynamically_G_B_date.setText("撿/還料日期 : " + JArrayList.get(8).substring(0, 19));
                    dynamically_G_B_date.setGravity(Gravity.LEFT);
                    dynamically_G_B_date.setPadding(20, 3, 0, 3);
                    dynamically_G_B_date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //設置每筆LinearLayout的分隔線
                    TextView dynamically_txt = new TextView(PickingActivity.this);
                    dynamically_txt.setBackgroundColor(Color.rgb(220, 220, 220));
                    dynamically_txt.setPadding(0, 0, 0, 0);

                    LinearLayout.LayoutParams small_pm = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

                    small_llt1.addView(dynamically_number, small_pm);
                    small_llt2.addView(dynamically_type, small_pm);
                    small_llt3.addView(dynamically_id, small_pm);
                    small_llt4.addView(dynamically_name, small_pm);
                    small_llt5.addView(dynamically_format, small_pm);
                    small_llt6.addView(dynamically_count, small_pm);
                    small_llt7.addView(dynamically_warehouse, small_pm);
                    small_llt8.addView(dynamically_G_B_date, small_pm);

                    picking_record_llt.addView(dynamically_txt, LinearLayout.LayoutParams.MATCH_PARENT, 3);
                    picking_record_llt.addView(small_llt1);
                    picking_record_llt.addView(small_llt2);
                    picking_record_llt.addView(small_llt3);
                    picking_record_llt.addView(small_llt4);
                    picking_record_llt.addView(small_llt5);
                    picking_record_llt.addView(small_llt6);
                    picking_record_llt.addView(small_llt7);
                    picking_record_llt.addView(small_llt8);

                    picking_record_llt.getChildAt(0).setVisibility(View.GONE);

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
