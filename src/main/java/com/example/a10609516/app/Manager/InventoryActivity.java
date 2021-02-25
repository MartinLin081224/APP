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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10609516.app.R;
import com.example.a10609516.app.Tools.ScannerActivity;
import com.example.a10609516.app.Tools.WQPServiceActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InventoryActivity extends WQPServiceActivity {

    private TextView date_txt;
    private ScrollView inventory_scv;
    private LinearLayout warehouse_llt, inventory_record_llt, loss_llt, stock_llt, vendor_llt;
    private TextView item_number_txt, item_name_txt, item_format_txt, stock_txt, unit_txt1,
            unit_txt2, vendor_txt, safe_txt, recent_in_txt, recent_out_txt,
            last_inventory_txt, loss_txt, record_txt, barcode_txt;
    private EditText factory_id_edt, actual_edt;
    private Button factory_id_btn, upload_btn;

    private String COMPANY, MB001, MB002, MB003, MB004, MB064,
            vendor_no, vendor_name, warehouse_no, warehouse_name, safe,
            in, out, inventory, company_ch, user_name,
            item_id, barcode;
    private String w_id, w_name, d_id;

    //轉畫面的Activity參數
    private Class<?> mClss;
    //ZXING_CAMERA權限
    private static final int ZXING_CAMERA_PERMISSION = 1;

    private Context context;
    private Spinner company_spinner, warehouse_spinner;
    private ArrayAdapter<String> company_listAdapter, warehouse_listAdapter;
    private String[] company_list = new String[]{"拓霖", "拓亞鈦", "倍偉特"};
    private String[] empty = new String[]{"(請選擇)"};
    private String[] warehouse_list = new String[]{};

    private String LOG = "InventoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        //動態取得 View 物件
        InItFunction();
        //獲取當天日期
        GetDate();
        //公司別的Spinner下拉選單
        CompanySpinner();
        //確認是否有最新版本，進行更新
        //CheckFirebaseVersion();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        inventory_scv = (ScrollView) findViewById(R.id.inventory_scv);
        date_txt = (TextView) findViewById(R.id.date_txt);
        item_number_txt = (TextView) findViewById(R.id.item_number_txt);
        item_name_txt = (TextView) findViewById(R.id.item_name_txt);
        item_format_txt = (TextView) findViewById(R.id.item_format_txt);
        factory_id_edt = (EditText) findViewById(R.id.factory_id_edt);
        factory_id_btn = (Button) findViewById(R.id.factory_id_btn);
        barcode_txt = (TextView) findViewById(R.id.barcode_txt);
        company_spinner = (Spinner) findViewById(R.id.company_spinner);
        warehouse_spinner = (Spinner) findViewById(R.id.warehouse_spinner);
        unit_txt1 = (TextView) findViewById(R.id.unit_txt1);
        unit_txt2 = (TextView) findViewById(R.id.unit_txt2);
        stock_txt = (TextView) findViewById(R.id.stock_txt);
        safe_txt = (TextView) findViewById(R.id.safe_txt);
        vendor_txt = (TextView) findViewById(R.id.vendor_txt);
        recent_in_txt = (TextView) findViewById(R.id.recent_in_txt);
        recent_out_txt = (TextView) findViewById(R.id.recent_out_txt);
        last_inventory_txt = (TextView) findViewById(R.id.last_inventory_txt);
        actual_edt = (EditText) findViewById(R.id.actual_edt);
        warehouse_llt = (LinearLayout) findViewById(R.id.warehouse_llt);
        inventory_record_llt = (LinearLayout) findViewById(R.id.inventory_record_llt);
        loss_llt = (LinearLayout) findViewById(R.id.loss_llt);
        stock_llt = (LinearLayout) findViewById(R.id.stock_llt);
        vendor_llt = (LinearLayout) findViewById(R.id.vendor_llt);
        upload_btn = (Button) findViewById(R.id.upload_btn);
        loss_txt = (TextView) findViewById(R.id.loss_txt);
        record_txt = (TextView) findViewById(R.id.record_txt);

        warehouse_llt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });

        factory_id_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_btn.setVisibility(View.VISIBLE);
                loss_llt.setVisibility(View.GONE);
                actual_edt.setFocusable(true);
                actual_edt.setFocusableInTouchMode(true);
                actual_edt.requestFocus();
                factory_id_edt.setFocusable(true);
                factory_id_edt.setFocusableInTouchMode(true);
                factory_id_edt.requestFocus();
                item_number_txt.setText("");
                item_name_txt.setText("");
                item_format_txt.setText("");
                stock_txt.setText("");
                actual_edt.setText("");
                recent_in_txt.setText("");
                recent_out_txt.setText("");
                last_inventory_txt.setText("");
                inventory_record_llt.removeAllViews();
                sendRequestWithOkHttpForFactoryID();
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestWithOkHttpForWareHouseInventory();
                upload_btn.setVisibility(View.GONE);
                stock_llt.setVisibility(View.GONE);
                loss_llt.setVisibility(View.VISIBLE);
                actual_edt.setFocusable(false);
                actual_edt.setFocusableInTouchMode(false);
                float q = Float.parseFloat(stock_txt.getText().toString());
                float a = Float.parseFloat(actual_edt.getText().toString());
                final float count = a - q;
                InventoryActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loss_txt.setText("" + count);
                        inventory_scv.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                inventory_record_llt.removeAllViews();
                //與WareHouseLog.PHP取得連線
                sendRequestWithOkHttpForWareHouseLog();
            }
        });

        record_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inventory_record_llt.removeAllViews();
                //與WareHouseLog.PHP取得連線
                sendRequestWithOkHttpForWareHouseLog();
            }
        });

        //接收LoginActivity傳過來的值
        SharedPreferences department_id = getSharedPreferences("department_id", MODE_PRIVATE);
        d_id = department_id.getString("D_ID", "");
    }

    /**
     * 獲取當天日期
     */
    private void GetDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(new java.util.Date());
        date_txt.setText(date);
    }

    /**
     * 公司別的Spinner下拉選單
     */
    private void CompanySpinner() {
        context = this;
        //程式剛啟始時載入第一個下拉選單
        company_listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, company_list);
        company_listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        company_spinner.setAdapter(company_listAdapter);
        company_spinner.setOnItemSelectedListener(selectListener);
        //因為下拉選單第一個為請選擇，所以不載入
        warehouse_listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, empty);
        warehouse_listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        warehouse_spinner.setAdapter(warehouse_listAdapter);
    }
    /*private void CompanySpinner() {
        //Spinner下拉選單
        company_listAdapter = new ArrayAdapter<String>(InventoryActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                company_list);
        company_spinner.setAdapter(company_listAdapter);
        company_spinner.setOnItemSelectedListener(selectListener);
    }*/

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
    /*private void WareHouseSpinner() {
        warehouse_listAdapter = new ArrayAdapter<String>(InventoryActivity.this,
                android.R.layout.simple_spinner_item,
                warehouse_list);
        warehouse_listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        warehouse_spinner.setAdapter(warehouse_listAdapter);
        warehouse_spinner.setOnItemSelectedListener(selectListener);
    }*/

    /**
     * 與OkHttp建立連線(DIYStore)
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
                } else {
                    company_ch = "BWT";
                }

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("COMPANY", company_ch)
                            .build();
                    Log.e(LOG, company_ch);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/Warehouse.php")
                            //.url("http://192.168.0.172/WQP/Warehouse.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e(LOG, responseData);
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
            stock_txt.setText("");
            actual_edt.setText("");
            recent_in_txt.setText("");
            recent_out_txt.setText("");
            last_inventory_txt.setText("");
            inventory_record_llt.removeAllViews();
            InventoryActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    upload_btn.setVisibility(View.VISIBLE);
                    loss_llt.setVisibility(View.GONE);
                    actual_edt.setFocusable(true);
                    actual_edt.setFocusableInTouchMode(true);
                    actual_edt.requestFocus();
                    factory_id_edt.setFocusable(true);
                    factory_id_edt.setFocusableInTouchMode(true);
                    factory_id_edt.requestFocus();
                    //與WareHouseMoreSearch.PHP取得連線
                    sendRequestWithOkHttpForWareHouseMoreSearch();
                    if (d_id.toString().equals("1000")){
                        stock_llt.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    /**
     * Button的設置
     */
    public void scanCode(View view) {
        //startActivityForResult(new Intent(this, ScannerActivity.class), 1);
        launchActivity(ScannerActivity.class);
        upload_btn.setVisibility(View.VISIBLE);
        loss_llt.setVisibility(View.GONE);
        actual_edt.setFocusable(true);
        actual_edt.setFocusableInTouchMode(true);
        actual_edt.requestFocus();
        factory_id_edt.setFocusable(true);
        factory_id_edt.setFocusableInTouchMode(true);
        factory_id_edt.requestFocus();
        if (d_id.toString().equals("1000")){
            stock_llt.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 轉畫面的封包，兼具權限和Intent跳轉畫面
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
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //ZXing回傳的內容
                String contents = intent.getStringExtra("SCAN_RESULT");
                barcode_txt.setText(intent.getStringExtra("result_text"));
                actual_edt.setText("");
                sendRequestWithOkHttpForWareHouseSearch();
                inventory_record_llt.removeAllViews();
                //與WareHouseLog.PHP取得連線
                sendRequestWithOkHttpForWareHouseLog();
            } else {
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(InventoryActivity.this, "取消掃描", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * 與OKHttp連線(查詢原廠序號資料)
     */
    private void sendRequestWithOkHttpForFactoryID() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String f_id = factory_id_edt.getText().toString();
                Log.e( LOG, f_id);

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
                    Log.e(LOG, responseData);
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
                //safe = jsonObject.getString("總安全庫存");
                //vendor_no = jsonObject.getString("廠商代號");
                //vendor_name = jsonObject.getString("廠商名稱");
                item_id = jsonObject.getString("ITEM");
                barcode = jsonObject.getString("BarCode");

                InventoryActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        item_number_txt.setText(MB001.toString());
                        item_name_txt.setText(MB002.toString());
                        item_format_txt.setText(MB003.toString());
                        unit_txt1.setText(MB004.toString());
                        unit_txt2.setText(MB004.toString());
                        //vendor_txt.setText(vendor_no.toString() + "|" + vendor_name.toString());
                        //safe_txt.setText(safe.toString());
                        barcode_txt.setText(barcode.toString());
                        if (d_id.toString().equals("1000")){
                            stock_llt.setVisibility(View.VISIBLE);
                        }
                        //company_spinner.setVisibility(View.VISIBLE);
                        //warehouse_spinner.setVisibility(View.VISIBLE);
                        //與OKHttp連線(查詢倉庫盤點資料)
                        sendRequestWithOkHttpForWareHouseMoreSearch();
                        /*//公司別的Spinner下拉選單
                        CompanySpinner();
                        //倉庫庫別的Spinner下拉選單
                        //WareHouseSpinner();
                        //轉換公司別中英文
                        if (COMPANY.toString().equals("WQP")) {
                            company_ch = "拓霖";
                        } else if (COMPANY.toString().equals("TYT")) {
                            company_ch = "拓亞鈦";
                        } else {
                            company_ch = "倍偉特";
                        }
                        //當迴圈與COMPANY內容一致時跳出迴圈 並顯示該公司別的Spinner位置
                        for (int c = 0; c < company_list.length; c++) {
                            if (company_list[c].equals(company_ch)) {
                                company_spinner.setSelection(c, true);
                                break;
                            }
                        }*/
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 與OKHttp連線(查詢倉庫盤點資料)
     */
    private void sendRequestWithOkHttpForWareHouseSearch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String barcode = barcode_txt.getText().toString();
                Log.e(LOG, barcode);

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("barcode", barcode)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WareHouseSearch.php")
                            //.url("http://192.168.0.172/WQP/WareHouseSearch.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e(LOG, responseData);
                    parseJSONWithJSONObjectForWareHouseSearch(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 取得盤點貨物之品名、規格、單位、總安全庫存、廠商代號、廠商名稱
     *
     * @param jsonData
     */
    private void parseJSONWithJSONObjectForWareHouseSearch(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //COMPANY = jsonObject.getString("COMPANY");
                MB001 = jsonObject.getString("MB001");
                MB002 = jsonObject.getString("MB002");
                MB003 = jsonObject.getString("MB003");
                MB004 = jsonObject.getString("MB004");
                //MB064 = jsonObject.getString("數量");
                safe = jsonObject.getString("SAFE");
                vendor_no = jsonObject.getString("V_NO");
                vendor_name = jsonObject.getString("V_NAME");
                item_id = jsonObject.getString("ITEM");
                barcode = jsonObject.getString("BarCode");

                InventoryActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        item_number_txt.setText(MB001.toString());
                        item_name_txt.setText(MB002.toString());
                        item_format_txt.setText(MB003.toString());
                        factory_id_edt.setText(item_id.toString());
                        unit_txt1.setText(MB004.toString());
                        unit_txt2.setText(MB004.toString());
                        vendor_txt.setText(vendor_no.toString() + "|" + vendor_name.toString());
                        safe_txt.setText(safe.toString());
                        //company_spinner.setVisibility(View.VISIBLE);
                        //warehouse_spinner.setVisibility(View.VISIBLE);
                        //與OKHttp連線(查詢倉庫盤點資料)
                        sendRequestWithOkHttpForWareHouseMoreSearch();
                        /*//公司別的Spinner下拉選單
                        CompanySpinner();
                        //倉庫庫別的Spinner下拉選單
                        //WareHouseSpinner();
                        //轉換公司別中英文
                        if (COMPANY.toString().equals("WQP")) {
                            company_ch = "拓霖";
                        } else if (COMPANY.toString().equals("TYT")) {
                            company_ch = "拓亞鈦";
                        } else {
                            company_ch = "倍偉特";
                        }
                        //當迴圈與COMPANY內容一致時跳出迴圈 並顯示該公司別的Spinner位置
                        for (int c = 0; c < company_list.length; c++) {
                            if (company_list[c].equals(company_ch)) {
                                company_spinner.setSelection(c, true);
                                break;
                            }
                        }*/
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 與OKHttp連線(查詢倉庫盤點資料)
     */
    private void sendRequestWithOkHttpForWareHouseMoreSearch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String company_select_more = String.valueOf(company_spinner.getSelectedItem());
                String MB001 = item_number_txt.getText().toString();
                String warehouse_no_select_more = String.valueOf(warehouse_spinner.getSelectedItem());
                w_id = warehouse_no_select_more.substring(0, warehouse_no_select_more.indexOf("("));
                /*if(warehouse_no_select_more.equals("BWT(BWT總倉)")){
                    w_id = "BWT";
                }else if (warehouse_no_select_more.equals("BWT-De(BWT不良品)")){
                    w_id = "BWT-De";
                }else if (warehouse_no_select_more.equals("OM-BZA(委外-鉑中)")){
                    w_id = "OM-BZA";
                }else if (warehouse_no_select_more.equals("WPA(暫存倉)")){
                    w_id = "WPA";
                }else if (warehouse_no_select_more.equals("WQP(拓霖倉)")){
                    w_id = "WQP";
                }*/

                Log.e(LOG, MB001);
                Log.e(LOG, warehouse_no_select_more);
                Log.e(LOG, w_id);

                if (company_select_more.toString().equals("拓霖")) {
                    company_select_more = "WQP";
                } else if (company_select_more.toString().equals("拓亞鈦")) {
                    company_select_more = "TYT";
                } else {
                    company_select_more = "BWT";
                }

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("COMPANY", company_select_more)
                            .add("MB001", MB001)
                            .add("WH_NO", w_id)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WareHouseMoreSearch.php")
                            //.url("http://192.168.0.172/WQP/WareHouseMoreSearch.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e(LOG, responseData);
                    parseJSONWithJSONObjectForWareHouseMoreSearch(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 取得盤點貨物之庫別、最近入庫日、最近出貨日、上次盤點日
     *
     * @param jsonData
     */
    private void parseJSONWithJSONObjectForWareHouseMoreSearch(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                warehouse_no = jsonObject.getString("W_NO");
                warehouse_name = jsonObject.getString("W_NAME");
                MB064 = jsonObject.getString("ACTUAL");
                //safe = jsonObject.getString("安全庫存");
                in = jsonObject.getString("R_IN");
                out = jsonObject.getString("R_OUT");
                inventory = jsonObject.getString("R_LAST");

                Log.e(LOG, MB064);

                InventoryActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stock_txt.setText(MB064.toString());
                        recent_in_txt.setText(in.toString());
                        recent_out_txt.setText(out.toString());
                        last_inventory_txt.setText(inventory.toString());
                        //使浮標移動到最後
                        actual_edt.setSelection(actual_edt.getText().length());
                    }
                });

                /*if (vendor_txt.getText().toString().equals(":|:")){
                    vendor_txt.setText("");
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 與OKHttp連線(查詢倉庫盤點之盤點人員、數量、倉庫名稱、盤點日期的Log)
     */
    private void sendRequestWithOkHttpForWareHouseLog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String company_select_log = String.valueOf(company_spinner.getSelectedItem());
                String MB001 = item_number_txt.getText().toString();
                String warehouse_no_select_log = String.valueOf(warehouse_spinner.getSelectedItem());
                String w_id_log = warehouse_no_select_log.substring(0, warehouse_no_select_log.indexOf("("));
                /*if(warehouse_no_select_log.equals("BWT(BWT總倉)")){
                    w_id = "BWT";
                    w_name = "BWT總倉";
                }else if (warehouse_no_select_log.equals("BWT-De(BWT不良品)")){
                    w_id = "BWT-De";
                    w_name = "BWT不良品";
                }else if (warehouse_no_select_log.equals("OM-BZA(委外-鉑中)")){
                    w_id = "OM-BZA";
                    w_name = "委外-鉑中";
                }else if (warehouse_no_select_log.equals("WPA(暫存倉)")){
                    w_id = "WPA";
                    w_name = "暫存倉";
                }else if (warehouse_no_select_log.equals("WQP(拓霖倉)")){
                    w_id = "WQP";
                    w_name = "拓霖倉";
                }else if (warehouse_no_select_log.equals("BRT(測試研發倉)")){
                    w_id = "BRT";
                    w_name = "測試研發倉";
                }else if (warehouse_no_select_log.equals("DSV(百及倉)")){
                    w_id = "DSV";
                    w_name = "百及倉";
                }else if (warehouse_no_select_log.equals("OM-SOL(委外-利騏)")){
                    w_id = "OM-SOL";
                    w_name = "委外-利騏";
                }else if (warehouse_no_select_log.equals("Borrow(場外借用)")){
                    w_id = "Borrow";
                    w_name = "場外借用";
                }else if (warehouse_no_select_log.equals("01-10000AT(拓霖AQT倉)")){
                    w_id = "01-10000AT";
                    w_name = "拓霖AQT倉";
                }else if (warehouse_no_select_log.equals("01-10000TP(拓霖台北倉)")){
                    w_id = "01-10000TP";
                    w_name = "拓霖台北倉";
                }else if (warehouse_no_select_log.equals("01-10000TY(拓霖桃園倉)")){
                    w_id = "01-10000TY";
                    w_name = "拓霖桃園倉";
                }else if (warehouse_no_select_log.equals("01-10000HS(拓霖新竹倉)")){
                    w_id = "01-10000HS";
                    w_name = "拓霖新竹倉";
                }else if (warehouse_no_select_log.equals("01-10000TC(拓霖台中倉)")){
                    w_id = "01-10000TC";
                    w_name = "拓霖台中倉";
                }else if (warehouse_no_select_log.equals("01-10000KH(拓霖高雄倉)")){
                    w_id = "01-10000KH";
                    w_name = "拓霖高雄倉";
                }else if (warehouse_no_select_log.equals("01-00001TY(新百及倉庫)")){
                    w_id = "01-00001TY";
                    w_name = "新百及倉庫";
                }else if (warehouse_no_select_log.equals("04-00005(PCHOME倉庫)")){
                    w_id = "04-00005";
                    w_name = "PCHOME倉庫";
                }else if (warehouse_no_select_log.equals("04-00012(MOMO購物)")){
                    w_id = "04-00012";
                    w_name = "MOMO購物";
                }else if (warehouse_no_select_log.equals("04-00019(YAHOO購物)")){
                    w_id = "04-00019";
                    w_name = "YAHOO購物";
                }else if (warehouse_no_select_log.equals("04-00020(全國北倉)")){
                    w_id = "04-00020";
                    w_name = "全國北倉";
                }else if (warehouse_no_select_log.equals("04-00021(全國中倉)")){
                    w_id = "04-00021";
                    w_name = "全國中倉";
                }else if (warehouse_no_select_log.equals("04-00022(全國南倉)")){
                    w_id = "04-00022";
                    w_name = "全國南倉";
                }else if (warehouse_no_select_log.equals("04-00023(全國花東倉)")){
                    w_id = "04-00023";
                    w_name = "全國花東倉";
                }else if (warehouse_no_select_log.equals("04-00024(全國宜蘭倉)")){
                    w_id = "04-00024";
                    w_name = "全國宜蘭倉";
                }else if (warehouse_no_select_log.equals("04-00028(樂購商城)")){
                    w_id = "04-00028";
                    w_name = "樂購商城";
                }else if (warehouse_no_select_log.equals("04-00029(東森購物)")){
                    w_id = "04-00029";
                    w_name = "東森購物";
                }else if (warehouse_no_select_log.equals("04-00030(燦坤寄倉)")){
                    w_id = "04-00030";
                    w_name = "燦坤寄倉";
                }*/

                Log.e(LOG, MB001);
                Log.e(LOG, warehouse_no_select_log);
                Log.e(LOG, w_id_log);

                if (company_select_log.toString().equals("拓霖")) {
                    company_select_log = "WQP";
                } else if (company_select_log.toString().equals("拓亞鈦")) {
                    company_select_log = "TYT";
                } else {
                    company_select_log = "BWT";
                }

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("COMPANY", company_select_log)
                            .add("MB001", MB001)
                            .add("WH_NO", w_id_log)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WareHouseLog.php")
                            //.url("http://192.168.0.172/WQP/WareHouseLog.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e(LOG, responseData);
                    parseJSONWithJSONObjectForWareHouseLog(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 取得盤點貨物之盤點人員、數量、倉庫名稱、盤點日期的Log
     *
     * @param jsonData
     */
    private void parseJSONWithJSONObjectForWareHouseLog(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String user_name_log = jsonObject.getString("USERS");
                String quantity_log = jsonObject.getString("QUANTITY");
                String actual_log = jsonObject.getString("ACTUAL");
                String warehouse_name_log = jsonObject.getString("W_NAME");
                String inventory_log = jsonObject.getString("DATE");

                Log.e(LOG, quantity_log);

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(user_name_log);
                JArrayList.add(quantity_log);
                JArrayList.add(actual_log);
                JArrayList.add(warehouse_name_log);
                JArrayList.add(inventory_log);

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
                    LinearLayout small_llt1 = new LinearLayout(InventoryActivity.this);
                    small_llt1.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt2 = new LinearLayout(InventoryActivity.this);
                    small_llt2.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt3 = new LinearLayout(InventoryActivity.this);
                    small_llt3.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt4 = new LinearLayout(InventoryActivity.this);
                    small_llt4.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout small_llt5 = new LinearLayout(InventoryActivity.this);
                    small_llt5.setOrientation(LinearLayout.HORIZONTAL);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    //int i = b.getStringArrayList("JSON_data").size();
                    JArrayList = jb.getStringArrayList("JSON_data");

                    //顯示每筆TableLayout的盤點人員
                    TextView dynamically_name;
                    dynamically_name = new TextView(InventoryActivity.this);
                    dynamically_name.setText("盤點人員 : " + JArrayList.get(0));
                    dynamically_name.setGravity(Gravity.CENTER);
                    //dynamically_name.setWidth(50);
                    dynamically_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //顯示每筆TableLayout的數量
                    TextView dynamically_quantity;
                    dynamically_quantity = new TextView(InventoryActivity.this);
                    dynamically_quantity.setText("系統數量 : " + JArrayList.get(1));
                    dynamically_quantity.setGravity(Gravity.CENTER);
                    dynamically_quantity.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //顯示每筆TableLayout的數量
                    TextView dynamically_actual;
                    dynamically_actual = new TextView(InventoryActivity.this);
                    dynamically_actual.setText("實際盤點數量 : " + JArrayList.get(2));
                    dynamically_actual.setGravity(Gravity.CENTER);
                    dynamically_actual.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //顯示每筆TableLayout的庫別名稱
                    TextView dynamically_vendor;
                    dynamically_vendor = new TextView(InventoryActivity.this);
                    dynamically_vendor.setText("庫別名稱 : " + JArrayList.get(3));
                    dynamically_vendor.setGravity(Gravity.CENTER);
                    //dynamically_name.setWidth(50);
                    dynamically_vendor.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //顯示每筆TableLayout的盤點日期
                    TextView dynamically_date;
                    dynamically_date = new TextView(InventoryActivity.this);
                    dynamically_date.setText("盤點日期 : " + JArrayList.get(4));
                    dynamically_date.setGravity(Gravity.CENTER);
                    dynamically_date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                    //設置每筆TableLayout的分隔線
                    TextView dynamically_txt = new TextView(InventoryActivity.this);
                    dynamically_txt.setBackgroundColor(Color.rgb(220, 220, 220));

                    LinearLayout.LayoutParams small_pm = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

                    small_llt1.addView(dynamically_name, small_pm);
                    small_llt2.addView(dynamically_quantity, small_pm);
                    small_llt3.addView(dynamically_actual, small_pm);
                    small_llt4.addView(dynamically_vendor, small_pm);
                    small_llt5.addView(dynamically_date, small_pm);

                    inventory_record_llt.addView(dynamically_txt, LinearLayout.LayoutParams.MATCH_PARENT, 3);
                    inventory_record_llt.addView(small_llt1);
                    inventory_record_llt.addView(small_llt2);
                    inventory_record_llt.addView(small_llt3);
                    inventory_record_llt.addView(small_llt4);
                    inventory_record_llt.addView(small_llt5);

                    inventory_record_llt.getChildAt(0).setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 與OKHttp連線(上傳盤點數量&盤點Log)
     */
    private void sendRequestWithOkHttpForWareHouseInventory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e(LOG, user_id_data);

                String company_select_inventory = String.valueOf(company_spinner.getSelectedItem());
                String MB001 = item_number_txt.getText().toString().trim();
                String quantity = stock_txt.getText().toString();
                String actual = actual_edt.getText().toString();
                String warehouse_no_select = String.valueOf(warehouse_spinner.getSelectedItem());
                String w_id_no = warehouse_no_select.substring(0, warehouse_no_select.indexOf("("));
                //String warehouse_name_select = String.valueOf(warehouse_spinner.getSelectedItem());
                //String w_id_name = warehouse_name_select.substring(warehouse_name_select.indexOf(":|:"), warehouse_name_select.length());
                /*if(warehouse_no_select.equals("BWT(BWT總倉)")){
                    w_id = "BWT";
                    w_name = "BWT總倉";
                }else if (warehouse_no_select.equals("BWT-De(BWT不良品)")){
                    w_id = "BWT-De";
                    w_name = "BWT不良品";
                }else if (warehouse_no_select.equals("OM-BZA(委外-鉑中)")){
                    w_id = "OM-BZA";
                    w_name = "委外-鉑中";
                }else if (warehouse_no_select.equals("WPA(暫存倉)")){
                    w_id = "WPA";
                    w_name = "暫存倉";
                }else if (warehouse_no_select.equals("WQP(拓霖倉)")){
                    w_id = "WQP";
                    w_name = "拓霖倉";
                }else if (warehouse_no_select.equals("BRT(測試研發倉)")){
                    w_id = "BRT";
                    w_name = "測試研發倉";
                }else if (warehouse_no_select.equals("DSV(百及倉)")){
                    w_id = "DSV";
                    w_name = "百及倉";
                }else if (warehouse_no_select.equals("OM-SOL(委外-利騏)")){
                    w_id = "OM-SOL";
                    w_name = "委外-利騏";
                }else if (warehouse_no_select.equals("Borrow(場外借用)")){
                    w_id = "Borrow";
                    w_name = "場外借用";
                }else if (warehouse_no_select.equals("01-10000AT(拓霖AQT倉)")){
                    w_id = "01-10000AT";
                    w_name = "拓霖AQT倉";
                }else if (warehouse_no_select.equals("01-10000TP(拓霖台北倉)")){
                    w_id = "01-10000TP";
                    w_name = "拓霖台北倉";
                }else if (warehouse_no_select.equals("01-10000TY(拓霖桃園倉)")){
                    w_id = "01-10000TY";
                    w_name = "拓霖桃園倉";
                }else if (warehouse_no_select.equals("01-10000HS(拓霖新竹倉)")){
                    w_id = "01-10000HS";
                    w_name = "拓霖新竹倉";
                }else if (warehouse_no_select.equals("01-10000TC(拓霖台中倉)")){
                    w_id = "01-10000TC";
                    w_name = "拓霖台中倉";
                }else if (warehouse_no_select.equals("01-10000KH(拓霖高雄倉)")){
                    w_id = "01-10000KH";
                    w_name = "拓霖高雄倉";
                }else if (warehouse_no_select.equals("01-00001TY(新百及倉庫)")){
                    w_id = "01-00001TY";
                    w_name = "新百及倉庫";
                }else if (warehouse_no_select.equals("04-00005(PCHOME倉庫)")){
                    w_id = "04-00005";
                    w_name = "PCHOME倉庫";
                }else if (warehouse_no_select.equals("04-00012(MOMO購物)")){
                    w_id = "04-00012";
                    w_name = "MOMO購物";
                }else if (warehouse_no_select.equals("04-00019(YAHOO購物)")){
                    w_id = "04-00019";
                    w_name = "YAHOO購物";
                }else if (warehouse_no_select.equals("04-00020(全國北倉)")){
                    w_id = "04-00020";
                    w_name = "全國北倉";
                }else if (warehouse_no_select.equals("04-00021(全國中倉)")){
                    w_id = "04-00021";
                    w_name = "全國中倉";
                }else if (warehouse_no_select.equals("04-00022(全國南倉)")){
                    w_id = "04-00022";
                    w_name = "全國南倉";
                }else if (warehouse_no_select.equals("04-00023(全國花東倉)")){
                    w_id = "04-00023";
                    w_name = "全國花東倉";
                }else if (warehouse_no_select.equals("04-00024(全國宜蘭倉)")){
                    w_id = "04-00024";
                    w_name = "全國宜蘭倉";
                }else if (warehouse_no_select.equals("04-00028(樂購商城)")){
                    w_id = "04-00028";
                    w_name = "樂購商城";
                }else if (warehouse_no_select.equals("04-00029(東森購物)")){
                    w_id = "04-00029";
                    w_name = "東森購物";
                }else if (warehouse_no_select.equals("04-00030(燦坤寄倉)")){
                    w_id = "04-00030";
                    w_name = "燦坤寄倉";
                }*/

                if (company_select_inventory.toString().equals("拓霖")) {
                    company_select_inventory = "WQP";
                } else if (company_select_inventory.toString().equals("拓亞鈦")) {
                    company_select_inventory = "TYT";
                } else {
                    company_select_inventory = "BWT";
                }

                Log.e(LOG, MB001);
                Log.e(LOG, actual);
                //Log.e("InventoryActivity", w_id);
                //Log.e("InventoryActivity", w_name);
                Log.e( LOG, w_id_no);

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("COMPANY", company_select_inventory)
                            .add("User_id", user_id_data)
                            .add("MB001", MB001)
                            .add("quantity", quantity)
                            .add("actual", actual)
                            .add("WH_NO", w_id_no)
                            //.add("WH_NAME", w_name)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WareHouseInventory.php")
                            //.url("http://192.168.0.172/WQP/WareHouseInventory.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e(LOG, responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Toast.makeText(this, "送出成功", Toast.LENGTH_SHORT).show();
    }
}