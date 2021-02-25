package com.example.a10609516.app.DepartmentAndDIY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10609516.app.R;
import com.example.a10609516.app.Tools.Adapter;
import com.example.a10609516.app.Tools.WQPServiceActivity;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.impl.OnItemClickListener;
import com.yanzhenjie.album.widget.divider.Api21ItemDivider;
import com.yanzhenjie.album.widget.divider.Divider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StationReportSearchActivity extends WQPServiceActivity {

    private ScrollView search_scv;
    private LinearLayout search_llt, result_llt, correct_llt;
    private TableLayout detail_tlt;
    private Button date_btn, search_btn, correct_btn, check_btn, cancel_btn, add_btn, preview_btn, choice_btn, upload_btn;
    private Spinner type_spinner, store_spinner, count_spinner, quantity_spinner;
    private TextView search_up_txt, search_down_txt, id_txt, p_id_txt, type_txt, store_txt, date_txt;
    private TextView dynamically_txt1, dynamically_txt2, dynamically_txt3, dynamically_txt4;
    private EditText day_amount_edt, return_count_edt, return_money_edt, reason_edt, response_edt;

    private ArrayAdapter<String> adapter, adapter2, adapter3;
    private String[] type = new String[]{"請選擇", "百貨通路", "特力屋", "大潤發", "普來利"};
    private String[] empty = new String[]{"(請選擇)"};
    private String[] diy = new String[]{};
    private Context context;

    private String[] count = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "30以上"};

    private ImageView[] dynamically_imv;

    private Adapter mAdapter;
    private ArrayList<AlbumFile> mAlbumFiles;
    private RecyclerView recyclerView;

    private OkHttpClient photo_client;
    private String path;
    private String savePath = "";
    private File image_file;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private String LOG = "StationReportSearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_report_search);

        //動態取得 View 物件
        InItFunction();
        //需求類別Spinner
        SpinnerAdapter();
        //照片選擇器
        AlbumGlide();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        recyclerView = findViewById(R.id.recycler_view);
        search_scv = (ScrollView) findViewById(R.id.search_scv);
        search_llt = (LinearLayout) findViewById(R.id.search_llt);
        result_llt = (LinearLayout) findViewById(R.id.result_llt);
        correct_llt = (LinearLayout) findViewById(R.id.correct_llt);
        detail_tlt = (TableLayout) findViewById(R.id.detail_tlt);
        date_btn = (Button) findViewById(R.id.date_btn);
        search_btn = (Button) findViewById(R.id.search_btn);
        correct_btn = (Button) findViewById(R.id.correct_btn);
        check_btn = (Button) findViewById(R.id.check_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        add_btn = (Button) findViewById(R.id.add_btn);
        preview_btn = (Button) findViewById(R.id.preview_btn);
        choice_btn = (Button) findViewById(R.id.choice_btn);
        upload_btn = (Button) findViewById(R.id.upload_btn);
        type_spinner = (Spinner) findViewById(R.id.type_spinner);
        store_spinner = (Spinner) findViewById(R.id.store_spinner);
        count_spinner = (Spinner) findViewById(R.id.count_spinner);
        quantity_spinner = (Spinner) findViewById(R.id.quantity_spinner);
        search_up_txt = (TextView) findViewById(R.id.search_up_txt);
        search_down_txt = (TextView) findViewById(R.id.search_down_txt);
        id_txt = (TextView) findViewById(R.id.id_txt);
        p_id_txt = (TextView) findViewById(R.id.p_id_txt);
        type_txt = (TextView) findViewById(R.id.type_txt);
        store_txt = (TextView) findViewById(R.id.store_txt);
        date_txt = (TextView) findViewById(R.id.date_txt);
        day_amount_edt = (EditText) findViewById(R.id.day_amount_edt);
        return_count_edt = (EditText) findViewById(R.id.return_count_edt);
        return_money_edt = (EditText) findViewById(R.id.return_money_edt);
        reason_edt = (EditText) findViewById(R.id.reason_edt);
        response_edt = (EditText) findViewById(R.id.response_edt);

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

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (date_btn.getText().toString().equals("") || type_spinner.getSelectedItemId() == 0 || store_spinner.getSelectedItemId() == 0) {
                    Toast.makeText(StationReportSearchActivity.this, "請輸入完整查詢條件", Toast.LENGTH_SHORT).show();
                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            type_txt.setText("");
                            store_txt.setText("");
                            date_txt.setText("");
                            day_amount_edt.setText("");
                            return_count_edt.setText("");
                            return_money_edt.setText("");
                            reason_edt.setText("");
                            response_edt.setText("");
                        }
                    });

                    search_llt.setVisibility(View.GONE);
                    search_up_txt.setVisibility(View.GONE);
                    search_down_txt.setVisibility(View.VISIBLE);
                    result_llt.setVisibility(View.VISIBLE);
                    detail_tlt.setVisibility(View.VISIBLE);
                    detail_tlt.removeAllViews();
                    //與OkHttp建立連線 取得日報資訊跟照片路徑ID
                    sendRequestWithOkHttpForStationReportSearchMaster();
                    //與OkHttp建立連線 取得日報資訊明細
                    sendRequestWithOkHttpForStationReportSearchDetail();
                }
            }
        });

        correct_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        correct_btn.setVisibility(View.GONE);
                        correct_llt.setVisibility(View.VISIBLE);
                        count_spinner.setEnabled(true);
                        quantity_spinner.setEnabled(true);
                        /*day_amount_edt.setFocusable(true);
                        return_count_edt.setFocusable(true);
                        return_money_edt.setFocusable(true);
                        reason_edt.setFocusable(true);
                        response_edt.setFocusable(true);*/
                    }
                });
            }
        });

        check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //與OkHttp建立連線 修改日報單頭
                sendRequestWithOkHttpForStationReportCorrectMaster();
                correct_btn.setVisibility(View.VISIBLE);
                correct_llt.setVisibility(View.GONE);
                Toast.makeText(StationReportSearchActivity.this, "修改完成", Toast.LENGTH_SHORT).show();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correct_btn.setVisibility(View.VISIBLE);
                correct_llt.setVisibility(View.GONE);
                //與OkHttp建立連線 取得日報資訊跟照片路徑ID
                sendRequestWithOkHttpForStationReportSearchMaster();
                //與OkHttp建立連線 取得日報資訊明細
                sendRequestWithOkHttpForStationReportSearchDetail();
                Toast.makeText(StationReportSearchActivity.this, "修改取消", Toast.LENGTH_SHORT).show();
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                String store = String.valueOf(store_spinner.getSelectedItem());
                String date = date_btn.getText().toString();

                Intent intent_add = new Intent(StationReportSearchActivity.this, StationReportAddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("USER_ID", user_id_data);
                bundle.putString("SHOP_ID", store);
                bundle.putString("BUSINESS_DATE", date);

                intent_add.putExtras(bundle);//可放所有基本類別
                startActivity(intent_add);

            }
        });

        preview_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewImage(0);
            }
        });

        choice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView.getChildCount() != 0) {
                    uploadImage();
                    //與OkHttp建立連線(更新照片路徑)
                    sendRequestWithOkHttpForOrderPhotoUpdate();
                    Toast.makeText(StationReportSearchActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
     * 載入下拉選單
     */
    private void SpinnerAdapter() {
        context = this;
        //程式剛啟始時載入第一個下拉選單
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(adapter);
        type_spinner.setOnItemSelectedListener(type_selectListener);
        //因為下拉選單第一個為請選擇，所以不載入
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, empty);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        store_spinner.setAdapter(adapter2);

    }

    /**
     * 通路別 第一個下拉類別的監看式
     */
    private AdapterView.OnItemSelectedListener type_selectListener = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            //讀取第一個下拉選單是選擇第幾個
            //int pos = type_spinner.getSelectedItemPosition();
            //與DIYStore.PHP取得連線
            sendRequestWithOkHttpForStore();
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    /**
     * 與OkHttp建立連線(DIYStore)
     */
    private void sendRequestWithOkHttpForStore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String spinner_select = String.valueOf(type_spinner.getSelectedItem());
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("STORE", spinner_select)
                            .build();
                    Log.e("PictureActivity", spinner_select);
                    Request request = new Request.Builder()
                            //.url("http://192.168.0.172/WQP/DIYStore.php")
                            .url("http://a.wqp-water.com.tw/WQP/DIYStore.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e(LOG, responseData);
                    parseJSONWithJSONObjectForStore(responseData);
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
    private void parseJSONWithJSONObjectForStore(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            ArrayList<String> JArrayList = new ArrayList<String>();
            diy = new String[]{};
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String DIY_store = jsonObject.getString("D_NAME");
                //JSONArray加入SearchData資料
                JArrayList.add(DIY_store);
                diy = JArrayList.toArray(new String[i]);
                //Log.e("PictureActivity3", diy[i]);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (type_spinner.getSelectedItemId() == 0) {
                        adapter2 = new ArrayAdapter<String>(StationReportSearchActivity.this, android.R.layout.simple_spinner_item, empty);
                        adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        store_spinner.setAdapter(adapter2);
                    } else {
                        //重新產生新的Adapter/*，用的是二維陣列type2[pos]*/
                        adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, diy);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //載入第二個下拉選單Spinner
                        store_spinner.setAdapter(adapter2);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 與OkHttp建立連線 取得日報資訊跟照片路徑ID
     */
    private void sendRequestWithOkHttpForStationReportSearchMaster() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                //獲取日期挑選器的數據
                String date = date_btn.getText().toString();
                String store = String.valueOf(store_spinner.getSelectedItem());

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("USER_ID", user_id_data)
                            .add("BUSINESS_DATE", date)
                            .add("SHOP_ID", store)

                            .build();
                    Log.e(LOG, user_id_data);
                    Log.e(LOG, date);
                    Log.e(LOG, store);

                    Request request = new Request.Builder()
                            //.url("http://192.168.0.172/WQP/StationShopBusinessSearchMaster.php")
                            .url("http://a.wqp-water.com.tw/WQP/StationShopBusinessSearchMaster.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e(LOG, requestBody.toString());
                    Log.e(LOG, response.toString());
                    Log.e(LOG, responseData);
                    parseJSONWithJSONObjectForRequisitionSearchMaster(responseData);
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
    private void parseJSONWithJSONObjectForRequisitionSearchMaster(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String R_ID = jsonObject.getString("R_ID");
                String path_name = jsonObject.getString("PATH_ID");
                String shop_name = jsonObject.getString("SHOP_ID");
                String business_date = jsonObject.getString("BUSINESS_DATE");
                String day_amount = jsonObject.getString("DAY_AMOUNT");
                String visitors_count = jsonObject.getString("VISITORS_COUNT");
                String return_count = jsonObject.getString("RETURN_COUNT");
                String return_money = jsonObject.getString("RETURN_MONEY");
                String unsold_reason = jsonObject.getString("UNSOLD_REASON");
                String customer_count = jsonObject.getString("CUSTOMER_COUNT");
                String q_response = jsonObject.getString("Q_RESPONSE");
                String R_P_ID = jsonObject.getString("R_P_ID");

                Log.e(LOG, R_ID);
                Log.e(LOG, path_name);
                Log.e(LOG, shop_name);
                Log.e(LOG, business_date);
                Log.e(LOG, day_amount);
                Log.e(LOG, visitors_count);
                Log.e(LOG, return_count);
                Log.e(LOG, return_money);
                Log.e(LOG, unsold_reason);
                Log.e(LOG, customer_count);
                Log.e(LOG, q_response);
                Log.e(LOG, R_P_ID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter3 = new ArrayAdapter<String>(StationReportSearchActivity.this, android.R.layout.simple_spinner_item, count);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        count_spinner.setAdapter(adapter3);
                        //當迴圈與ResponseText內容一致時跳出迴圈 並顯示該ResponseText的Spinner位置
                        for (int a = 0; a < count.length; a++) {
                            if (count[a].equals(visitors_count)) {
                                count_spinner.setSelection(a, true);
                                break;
                            }
                        }
                        quantity_spinner.setAdapter(adapter3);
                        for (int b = 0; b < count.length; b++) {
                            if (count[b].equals(customer_count)) {
                                quantity_spinner.setSelection(b, true);
                                break;
                            }
                        }

                        count_spinner.setEnabled(false);
                        quantity_spinner.setEnabled(false);
                        /*day_amount_edt.setFocusable(false);
                        return_count_edt.setFocusable(false);
                        return_money_edt.setFocusable(false);
                        reason_edt.setFocusable(false);
                        response_edt.setFocusable(false);*/

                        id_txt.setText(R_ID);
                        p_id_txt.setText(R_P_ID);
                        type_txt.setText(path_name);
                        store_txt.setText(shop_name);
                        date_txt.setText(business_date);
                        day_amount_edt.setText(day_amount);
                        return_count_edt.setText(return_count);
                        return_money_edt.setText(return_money);
                        reason_edt.setText(unsold_reason);
                        response_edt.setText(q_response);

                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 與OkHttp建立連線 修改日報單頭
     */
    private void sendRequestWithOkHttpForStationReportCorrectMaster() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String R_ID = id_txt.getText().toString();
                String day_amount = day_amount_edt.getText().toString();
                String return_count = return_count_edt.getText().toString();
                String return_money = return_money_edt.getText().toString();
                String reason = reason_edt.getText().toString();
                String response_suggest = response_edt.getText().toString();
                String count = String.valueOf(count_spinner.getSelectedItem());
                String quantity = String.valueOf(quantity_spinner.getSelectedItem());

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("R_ID", R_ID)
                            .add("DAY_AMOUNT", day_amount)
                            .add("RETURN_COUNT", return_count)
                            .add("RETURN_MONEY", return_money)
                            .add("UNSOLD_REASON", reason)
                            .add("Q_RESPONSE", response_suggest)
                            .add("VISITORS_COUNT", count)
                            .add("CUSTOMER_COUNT", quantity)
                            .build();
                    Log.e(LOG, R_ID);
                    Request request = new Request.Builder()
                            //.url("http://192.168.0.172/WQP/StationShopBusinessCorrectMaster.php")
                            .url("http://a.wqp-water.com.tw/WQP/StationShopBusinessCorrectMaster.php")
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

    /**
     * 與OkHttp建立連線 取得日報資訊明細
     */
    private void sendRequestWithOkHttpForStationReportSearchDetail() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                //獲取日期挑選器的數據
                String date = date_btn.getText().toString();
                String store = String.valueOf(store_spinner.getSelectedItem());

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("USER_ID", user_id_data)
                            .add("BUSINESS_DATE", date)
                            .add("SHOP_ID", store)

                            .build();
                    Log.e(LOG, user_id_data);
                    Log.e(LOG, date);
                    Log.e(LOG, store);

                    Request request = new Request.Builder()
                            //.url("http://192.168.0.172/WQP/StationShopBusinessSearchDetail.php")
                            .url("http://a.wqp-water.com.tw/WQP/StationShopBusinessSearchDetail.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e(LOG, requestBody.toString());
                    Log.e(LOG, response.toString());
                    Log.e(LOG, responseData);
                    parseJSONWithJSONObjectForRequisitionSearchDetail(responseData);
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
    private void parseJSONWithJSONObjectForRequisitionSearchDetail(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            dynamically_imv = new ImageView[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String D_R_ID = jsonObject.getString("D_R_ID");
                String d_item_name = jsonObject.getString("ITEM_NAME");
                String d_item_count = jsonObject.getString("ITEM_COUNT");
                String d_item_amount = jsonObject.getString("ITEM_AMOUNT");

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(D_R_ID);
                JArrayList.add(d_item_name);
                JArrayList.add(d_item_count);
                JArrayList.add(d_item_amount);

                Log.e(LOG, D_R_ID);
                Log.e(LOG, d_item_name);
                Log.e(LOG, d_item_count);
                Log.e(LOG, d_item_amount);

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
                    detail_tlt.setStretchAllColumns(true);
                    //設置大TableLayout的TableRow
                    TableRow big_tbr = new TableRow(StationReportSearchActivity.this);
                    //設置每筆資料的小TableLayout
                    TableLayout small_tb = new TableLayout(StationReportSearchActivity.this);
                    //全部列自動填充空白處
                    small_tb.setStretchAllColumns(true);
                    small_tb.setBackgroundResource(R.drawable.warningline);
                    //設定TableRow的寬高
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    JArrayList = jb.getStringArrayList("JSON_data");

                    Log.e(LOG, JArrayList.get(0));
                    //設置每筆TableLayout的Button監聽器、與動態新增Button的ID
                    int loc = 0;
                    for (int j = 0; j < dynamically_imv.length; j++) {
                        if (dynamically_imv[j] == null) {
                            loc = j;
                            break;
                        }
                    }
                    dynamically_imv[loc] = new ImageView(StationReportSearchActivity.this);
                    dynamically_imv[loc].setImageResource(R.drawable.quotation);
                    dynamically_imv[loc].setScaleType(ImageView.ScaleType.CENTER);
                    dynamically_imv[loc].setId(loc);
                    dynamically_imv[loc].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int a = 0; a < dynamically_imv.length; a++) {
                                if (v.getId() == dynamically_imv[a].getId()) {
                                    Intent intent_quotation = new Intent(StationReportSearchActivity.this, StationReportCorrectActivity.class);
                                    //取得大TableLayout中的大TableRow位置
                                    TableRow tb_tbr = (TableRow) detail_tlt.getChildAt(a);
                                    //取得大TableRow中的小TableLayout位置
                                    TableLayout tb_tbr_tb = (TableLayout) tb_tbr.getChildAt(1);
                                    //取得小TableLayout中的小TableRow位置
                                    TableRow tb_tbr_tb_tbr = (TableRow) tb_tbr_tb.getChildAt(0);
                                    //派工資料的迴圈
                                    for (int x = 0; x < 4; x++) {
                                        //小TableRow中的layout_column位置
                                        TextView order_txt = (TextView) tb_tbr_tb_tbr.getChildAt(x);
                                        String ResponseText = order_txt.getText().toString();
                                        //將SQL裡的資料傳到MaintainActivity
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ResponseText" + x, ResponseText);

                                        intent_quotation.putExtras(bundle);//可放所有基本類別
                                        Log.e(LOG, ResponseText);

                                    }
                                    detail_tlt.removeAllViews();
                                    startActivity(intent_quotation);
                                }
                            }
                        }
                    });

                    //顯示每筆TableLayout的SQL資料
                    //TextView dynamically_txt1;
                    dynamically_txt1 = new TextView(StationReportSearchActivity.this);
                    dynamically_txt1.setText(JArrayList.get(0));
                    dynamically_txt1.setPadding(0, 2, 0, 0);
                    dynamically_txt1.setGravity(Gravity.LEFT);
                    dynamically_txt1.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_txt1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    dynamically_txt1.setVisibility(View.GONE);
                    //顯示每筆TableLayout的SQL資料
                    //TextView dynamically_txt2;
                    dynamically_txt2 = new TextView(StationReportSearchActivity.this);
                    dynamically_txt2.setText(JArrayList.get(1).trim());
                    dynamically_txt2.setPadding(0, 2, 0, 0);
                    dynamically_txt2.setGravity(Gravity.LEFT);
                    dynamically_txt2.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_txt2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    //顯示每筆TableLayout的SQL資料
                    //TextView dynamically_txt3;
                    dynamically_txt3 = new TextView(StationReportSearchActivity.this);
                    dynamically_txt3.setText(JArrayList.get(2));
                    dynamically_txt3.setPadding(0, 2, 0, 0);
                    dynamically_txt3.setGravity(Gravity.LEFT);
                    dynamically_txt3.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_txt3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    dynamically_txt3.setVisibility(View.GONE);
                    //顯示每筆TableLayout的SQL資料
                    //TextView dynamically_txt3;
                    dynamically_txt4 = new TextView(StationReportSearchActivity.this);
                    dynamically_txt4.setText(JArrayList.get(3));
                    dynamically_txt4.setPadding(0, 2, 0, 0);
                    dynamically_txt4.setGravity(Gravity.LEFT);
                    dynamically_txt4.setTextColor(Color.rgb(6, 102, 219));
                    dynamically_txt4.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    dynamically_txt4.setVisibility(View.GONE);
                    TableRow tr1 = new TableRow(StationReportSearchActivity.this);
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

                    detail_tlt.addView(big_tbr);

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 與OkHttp建立連線(更新照片路徑)
     */
    private void sendRequestWithOkHttpForOrderPhotoUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                String p_id = p_id_txt.getText().toString();
                String report_date = date_txt.getText().toString();

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("R_P_ID", p_id)
                            .add("FILE_NAME", savePath)
                            .add("USER_ID", user_id_data)
                            .build();
                    Log.e(LOG, p_id);
                    Log.e(LOG, report_date);
                    Log.e(LOG, user_id_data);
                    Request request = new Request.Builder()
                            //.url("http://192.168.0.172/WQP/StationShopBusinessFileUpdate.php")
                            .url("http://a.wqp-water.com.tw/WQP/StationShopBusinessFileUpdate.php")
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
    }

    /**
     * 照片選擇器
     */
    private void AlbumGlide() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));//預覽欄位數量
        Divider divider = new Api21ItemDivider(Color.TRANSPARENT, 10, 10);
        recyclerView.addItemDecoration(divider);

        mAdapter = new Adapter(this, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                previewImage(position);
            }
        });
        recyclerView.setAdapter(mAdapter);

        Log.e(LOG + "111", mAdapter.toString());
    }

    /**
     * Select picture, from album.
     */
    private void selectImage() {
        Album.image(this)
                .multipleChoice()
                .camera(true)
                //選擇欄位數量
                .columnCount(2)
                //可選欄位數量
                .selectCount(30)
                .checkedList(mAlbumFiles)

                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        mAlbumFiles = result;
                        mAdapter.notifyDataSetChanged(mAlbumFiles);
                        //mTvMessage.setVisibility(result.size() > 0 ? View.VISIBLE : View.GONE);
                        Log.e(LOG, result.toString());
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toast.makeText(StationReportSearchActivity.this, R.string.canceled, Toast.LENGTH_LONG).show();
                    }
                })
                .start();
    }

    /**
     * Preview image, to album.
     */
    private void previewImage(int position) {
        if (mAlbumFiles == null || mAlbumFiles.size() == 0) {
            Toast.makeText(this, R.string.no_selected, Toast.LENGTH_LONG).show();
        } else {
            Album.galleryAlbum(this)
                    .checkable(true)
                    .checkedList(mAlbumFiles)
                    .currentPosition(position)

                    .onResult(new Action<ArrayList<AlbumFile>>() {
                        @Override
                        public void onAction(@NonNull ArrayList<AlbumFile> result) {
                            mAlbumFiles = result;
                            mAdapter.notifyDataSetChanged(mAlbumFiles);
                            //mTvMessage.setVisibility(result.size() > 0 ? View.VISIBLE : View.GONE);

                            Log.e(LOG + "123", mAlbumFiles.toString());
                        }
                    })
                    .start();
        }
    }

    /**
     * 點擊空白區域，隐藏虛擬鍵盤
     */
    public boolean onTouchEvent(MotionEvent event) {
        if(null != this.getCurrentFocus()){
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super .onTouchEvent(event);
    }

    private void uploadImage() {
        photo_client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        //取得檔案路徑及檔案名稱並上傳至SERVER
        for(int x = 0; x < mAlbumFiles.size(); x++) {
            //GET the file path
            AlbumFile file = mAlbumFiles.get(x);
            path = file.getPath();
            Log.e(LOG + "888", path);

            image_file = new File(path);
            builder.addFormDataPart("img", image_file.getName(), RequestBody.create(MEDIA_TYPE_PNG, image_file));
            Log.e(LOG + "8888", image_file.getName());

            String fileName = "C:\\xampp\\htdocs\\WQP\\station_order_pic\\";
            savePath = savePath + fileName + image_file.getName() + ",";
            Log.e(LOG + "BBB", savePath);

            // TODO upload...
            MultipartBody requestBody = builder.build();
            Request request = new Request.Builder()
                    //.url("http://192.168.0.172/WQP/RequisitionPicture.php")
                    .url("http://a.wqp-water.com.tw/WQP/StationOrderPicture.php")
                    .post(requestBody)
                    .build();
            photo_client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(LOG + "FAIL", "onFailure: 失敗" + e.getLocalizedMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(LOG + "SUCCESS", "onResponse: " + response.body().string());
                    //提交成功處理结果....
                }
            });
        }

        Log.e(LOG + "BBBB", savePath);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG, "onRestart");
        detail_tlt.removeAllViews();
        //與OkHttp建立連線 取得日報資訊跟照片路徑ID
        sendRequestWithOkHttpForStationReportSearchMaster();
        //與OkHttp建立連線 取得日報資訊明細
        sendRequestWithOkHttpForStationReportSearchDetail();
    }
}
