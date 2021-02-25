package com.example.a10609516.app.Tools;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a10609516.app.BOSS.ApplyExchangeActivity;
import com.example.a10609516.app.BOSS.ExchangeActivity;
import com.example.a10609516.app.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UsualActivity extends AppCompatActivity {

    private LinearLayout usual_llt;
    private TextView[] dynamically_item_txt;

    private String LOG = "UsualActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usual);
        //動態取得 View 物件
        InItFunction();
        //與OkHttp建立連線(1駁回常用詞語)
        sendRequestWithOkHttpForUsualReject();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        usual_llt = (LinearLayout) findViewById(R.id.usual_llt);
    }

    /**
     * 與OkHttp建立連線(1駁回常用詞語)
     */
    private void sendRequestWithOkHttpForUsualReject() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e(LOG, user_id_data);
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .build();
                    Request request = new Request.Builder()
                            //.url("http://a.wqp-water.com.tw/WQP/Reason_Reject.php")
                            .url("http://192.168.0.172/WQP/Reason_Reject.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e(LOG, requestBody.toString());
                    Log.e(LOG, response.toString());
                    Log.e(LOG, responseData);
                    parseJSONWithJSONObjectForDetail(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 獲得JSON字串並解析成String字串(換貨申請單單身)
     *
     * @param jsonData
     */
    private void parseJSONWithJSONObjectForDetail(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            dynamically_item_txt = new TextView[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String U_WORDS = jsonObject.getString("U_WORDS");

                //JSONArray加入SearchData資料
                ArrayList<String> JArrayList = new ArrayList<String>();
                JArrayList.add(U_WORDS);

                Log.e(LOG, U_WORDS);

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

                    LinearLayout big_llt = new LinearLayout(UsualActivity.this);
                    big_llt.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout item_llt = new LinearLayout(UsualActivity.this);
                    item_llt.setOrientation(LinearLayout.HORIZONTAL);
                    item_llt.setBackgroundResource(R.drawable.underline);

                    Bundle jb = msg.getData();
                    ArrayList<String> JArrayList = new ArrayList<String>();
                    //int i = b.getStringArrayList("JSON_data").size();
                    JArrayList = jb.getStringArrayList("JSON_data");

                    //設置每筆TableLayout的Button監聽器、與動態新增Button的ID
                    int loc = 0;
                    for (int i = 0; i < dynamically_item_txt.length; i++) {
                        if (dynamically_item_txt[i] == null) {
                            loc = i;
                            break;
                        }
                    }
                    dynamically_item_txt[loc] = new TextView(UsualActivity.this);
                    dynamically_item_txt[loc].setText(JArrayList.get(0));
                    dynamically_item_txt[loc].setTextColor(Color.rgb(0, 0, 0));
                    dynamically_item_txt[loc].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                    dynamically_item_txt[loc].setGravity(Gravity.LEFT);
                    dynamically_item_txt[loc].setPadding(10, 3, 0, 3);
                    dynamically_item_txt[loc].setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    dynamically_item_txt[loc].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int a = 0; a < dynamically_item_txt.length; a++) {
                                if (v.getId() == dynamically_item_txt[a].getId()) {
                                    Intent intent_reason = new Intent(UsualActivity.this, ExchangeActivity.class);
                                    LinearLayout id_llt = (LinearLayout) usual_llt.getChildAt(a);

                                    LinearLayout big_llt = (LinearLayout) id_llt.getChildAt(1);
                                    LinearLayout word_llt = (LinearLayout) big_llt.getChildAt(0);
                                    TextView reason_txt = (TextView) word_llt.getChildAt(0);
                                    String id = reason_txt.getText().toString();
                                    //將SQL裡的資料傳到ExchangeActivity
                                    Bundle bundle = new Bundle();
                                    bundle.putString("reason_txt", id);
                                    //intent_gps.putExtra("TitleText", TitleText);//可放所有基本類別
                                    intent_reason.putExtras(bundle);//可放所有基本類別

                                    startActivity(intent_reason);
                                    //進入MapsActivity後 清空usual_llt的資料
                                    usual_llt.removeAllViews();
                                }
                            }
                        }
                    });


                    //設置每筆LinearLayout的間隔分隔線
                    TextView dynamically_txt0 = new TextView(UsualActivity.this);
                    dynamically_txt0.setBackgroundColor(Color.rgb(220, 220, 220));
                    dynamically_txt0.setAlpha(0);

                    item_llt.addView(dynamically_item_txt[loc], LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    big_llt.addView(dynamically_txt0, LinearLayout.LayoutParams.MATCH_PARENT, 10);
                    big_llt.addView(item_llt);

                    usual_llt.addView(big_llt);
                    LinearLayout first_llt = (LinearLayout) big_llt.getChildAt(0);
                    first_llt.getChildAt(0).setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}