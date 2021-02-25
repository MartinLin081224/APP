package com.example.a10609516.app.DepartmentAndDIY;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class StationReportAddActivity extends WQPServiceActivity {

    private Spinner category_spinner, supplies_spinner;
    private EditText count_edt, money_edt;
    private Button add_btn;
    private TextView user_txt, shop_txt, date_txt;

    private Context context;
    private ArrayAdapter<String> adapter, adapter2;
    private String[] empty = new String[]{"(請選擇)"};
    private String[] category = new String[]{};
    private String[] supplies = new String[]{};

    private String LOG = "StationReportAddActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_report_add);

        //動態取得 View 物件
        InItFunction();
        //取得OrderSearchActivity傳遞過來的值
        GetResponseData();
        //載入設備類別下拉選單
        sendRequestWithOkHttpForDeviceCategory();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        context = this;

        category_spinner = (Spinner) findViewById(R.id.category_spinner);
        supplies_spinner = (Spinner) findViewById(R.id.supplies_spinner);
        count_edt = (EditText) findViewById(R.id.count_edt);
        money_edt = (EditText) findViewById(R.id.money_edt);
        add_btn = (Button) findViewById(R.id.add_btn);
        user_txt = (TextView) findViewById(R.id.user_txt);
        shop_txt = (TextView) findViewById(R.id.shop_txt);
        date_txt = (TextView) findViewById(R.id.date_txt);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OkHttp建立連線 新增日報明細
                sendRequestWithOkHttpForStationReportAdd();
                //Toast.makeText(StationReportAddActivity.this, "新增成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * 取得SearchActivity傳遞過來的值
     */
    private void GetResponseData() {
        Bundle bundle = getIntent().getExtras();
        String ResponseText0 = bundle.getString("USER_ID");
        String ResponseText1 = bundle.getString("SHOP_ID");
        String ResponseText2 = bundle.getString("BUSINESS_DATE");

        user_txt.setText(ResponseText0);
        shop_txt.setText(ResponseText1);
        date_txt.setText(ResponseText2);

        Log.e(LOG, ResponseText0);
        Log.e(LOG, ResponseText1);
        Log.e(LOG, ResponseText2);

    }

    /**
     * 與OkHttp建立連線(DeviceCategory)
     */
    private void sendRequestWithOkHttpForDeviceCategory() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("CATEGORY", "")
                            .build();
                    Log.e(LOG, "CATEGORY");
                    Request request = new Request.Builder()
                            //.url("http://192.168.0.172/WQP/DeviceCategory.php")
                            .url("http://a.wqp-water.com.tw/WQP/DeviceCategory.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e(LOG, responseData);
                    parseJSONWithJSONObjectForDeviceCategory(responseData);
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
    private void parseJSONWithJSONObjectForDeviceCategory(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            ArrayList<String> JArrayList = new ArrayList<String>();
            category = new String[]{};
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String device_category = jsonObject.getString("C_NAME");
                //JSONArray加入SearchData資料
                JArrayList.add(device_category);
                category = JArrayList.toArray(new String[i]);
                //Log.e("PictureActivity3", diy[i]);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new ArrayAdapter<String>(StationReportAddActivity.this, android.R.layout.simple_dropdown_item_1line, category);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    category_spinner.setAdapter(adapter);
                    category_spinner.setOnItemSelectedListener(category_selectListener);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 設備分類 第一個下拉類別的監看式
     */
    private AdapterView.OnItemSelectedListener category_selectListener = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            //讀取第一個下拉選單是選擇第幾個
            //int pos = type_spinner.getSelectedItemPosition();
            //與DeviceSupplies.PHP取得連線
            sendRequestWithOkHttpForDeviceSupplies();
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    /**
     * 與OkHttp建立連線(DIYStore)
     */
    private void sendRequestWithOkHttpForDeviceSupplies() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String category_select = String.valueOf(category_spinner.getSelectedItem());

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("CATEGORY", category_select)
                            .build();
                    Log.e(LOG, category_select);
                    Request request = new Request.Builder()
                            //.url("http://192.168.0.172/WQP/DeviceSupplies.php")
                            .url("http://a.wqp-water.com.tw/WQP/DeviceSupplies.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e(LOG, responseData);
                    parseJSONWithJSONObjectForDeviceSupplies(responseData);
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
    private void parseJSONWithJSONObjectForDeviceSupplies(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            ArrayList<String> JArrayList = new ArrayList<String>();
            supplies = new String[]{};
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String device_supplies = jsonObject.getString("C_DS_NAME");
                //JSONArray加入SearchData資料
                JArrayList.add(device_supplies);
                supplies = JArrayList.toArray(new String[i]);
                //Log.e("PictureActivity3", diy[i]);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //重新產生新的Adapter/*，用的是二維陣列type2[pos]*/
                    adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, supplies);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //載入第二個下拉選單Spinner
                    supplies_spinner.setAdapter(adapter2);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * OkHttp建立連線 新增日報明細
     */
    private void sendRequestWithOkHttpForStationReportAdd() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String user_id = user_txt.getText().toString();
                String shop_id = shop_txt.getText().toString();
                String date = date_txt.getText().toString();
                String product = String.valueOf(supplies_spinner.getSelectedItem());
                String item_count = count_edt.getText().toString();
                String item_money = money_edt.getText().toString();

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("USER_ID", user_id)
                            .add("SHOP_ID", shop_id)
                            .add("BUSINESS_DATE", date)
                            .add("ITEM_NAME", product)
                            .add("ITEM_COUNT", item_count)
                            .add("ITEM_MONEY", item_money)
                            .build();
                    Log.e(LOG, user_id);
                    Log.e(LOG, shop_id);
                    Log.e(LOG, date);
                    Log.e(LOG, product);
                    Log.e(LOG, item_count);
                    Log.e(LOG, item_money);
                    Request request = new Request.Builder()
                            //.url("http://192.168.0.172/WQP/StationShopBusinessReportAdd.php")
                            .url("http://a.wqp-water.com.tw/WQP/StationShopBusinessReportAdd.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e(LOG, requestBody.toString());
                    Log.e(LOG, response.toString());
                    Log.e(LOG, responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
