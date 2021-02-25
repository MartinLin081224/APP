package com.example.a10609516.app.DepartmentAndDIY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.a10609516.app.Basic.MenuActivity;
import com.example.a10609516.app.Clerk.QuotationActivity;
import com.example.a10609516.app.Manager.InventoryActivity;
import com.example.a10609516.app.R;
import com.example.a10609516.app.Basic.VersionActivity;
import com.example.a10609516.app.Workers.CalendarActivity;
import com.example.a10609516.app.Basic.QRCodeActivity;
import com.example.a10609516.app.Workers.EngPointsActivity;
import com.example.a10609516.app.Workers.GPSActivity;
import com.example.a10609516.app.Workers.MissCountActivity;
import com.example.a10609516.app.Workers.PointsActivity;
import com.example.a10609516.app.Workers.ScheduleActivity;
import com.example.a10609516.app.Workers.SearchActivity;

public class UploadActivity extends AppCompatActivity {

    private String[] typespinner = new String[]{"請選擇", "百貨通路", "特力屋", "普萊利"};
    private String[] empty = new String[]{""};
    private String[][] storespinner = new String[][]{{""}, {"台北新光信義A9 (02)2723-9721", "新北環球中和 (02)7731-6890", "台北美麗華 櫃位無電話",
            "忠孝SOGO (02)7711-5151", "台北新光站前 (02)2371-9306", "中壢SOGO",
            "新竹SOGO (03)526-2828", "新竹遠百 (03)523-8453", "新竹新光 (03)515-1056",
            "台中中友百貨 (04)2229-3568", "台中大遠百 (04)2254-8583", "台中廣三 (04)2328-2630",
            "台中新光", "嘉義遠百 (05) 283-5705", "台南新天地 (06)303-0045",
            "南紡夢時代 (07)973-3888", "台南遠東", "高雄新光三多店 (07)336-6100",
            "高雄夢時代 (07)973-3888", "高雄漢神巨蛋 (07)555-9696", "大江購物中心",
            "高雄 新光左營店 (07)346-9930", "高雄大遠百(07)537-3364", "高雄SOGO (07)335-8076",
            "集雅社", "嘉義耐斯百貨 (05)276-7888", "台北天母SOGO (02)2834-5000",
            "高雄成功漢神百貨 (07)226-3855", "三多旗艦門市 (07)725-6606"},
            {"特力屋桃園南崁店 (03)321-1000", "特力屋台北新莊店 (02)2906-1212", "特力屋高雄大順店 (07)225-9111",
                    "特力屋台中復興店 (04)2262-0300", "特力屋嘉義店 (05)271-6568", "特力屋台南文賢店 (06)358-5656",
                    "特力屋台北內湖店 (02)8791-8896", "特力屋桃園平鎮店 (03)428-7111", "特力屋台北士林店 (02)2889-1000",
                    "特力屋彰化彰化店 (04)736-9999", "特力屋台中北屯店 (04)2247-1000", "特力屋高雄鳳山店 (07)766-9000",
                    "特力屋台南仁德店 (06)249-2888", "特力屋台北土城店 (02)8262-6000", "特力屋新竹店 (03)575-1234",
                    "特力屋台北中和店 (02)2240-1888", "特力屋屏東店 (08)721-6000", "特力屋台北新店店 (02)2910-9988",
                    "特力屋高雄左營店 (07)310-4000", "特力屋宜蘭羅東店 (03)9552000", "特力屋花蓮店 (03)833-0098",
                    "特力屋台中豐原店 (04)2513-2200", "特力屋台北三峽店 (02)8970-5089", "特力屋台中西屯店 (04)2462-9333",
                    "特力屋網路店", "特力屋桃園八德店 (03)375-3666"}, {"普來利桃園店 (03)392-1100", "普來利新竹店 (03)526-9966",
            "普來利竹北店 (03)555-8086", "普來利竹南店 (037)550095", "普來利頭份店 (037)669900",
            "普來利太平店 (04)2391-9555", "普萊利竹科店 (03)666-9598"}};
    private Spinner sp1;//第一個下拉選單
    private Spinner sp2;//第二個下拉選單
    private Context context;

    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;

    private ScrollView scrollView;

    /**
     * 創建Menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //接收LoginActivity傳過來的值
        SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
        String user_id_data = user_id.getString("ID", "");
        SharedPreferences department_id = getSharedPreferences("department_id", MODE_PRIVATE);
        String department_id_data = department_id.getString("D_ID", "");
        if ((user_id_data.toString().equals("09706013")) || user_id_data.toString().equals("09908023") || user_id_data.toString().equals("10010039")
                || user_id_data.toString().equals("10012043") || user_id_data.toString().equals("10101046") || user_id_data.toString().equals("10405235")) {
            getMenuInflater().inflate(R.menu.workers_manager_menu, menu);
            return true;
        }else if (department_id_data.toString().equals("2100")) {
            getMenuInflater().inflate(R.menu.clerk_menu, menu);
            return true;
        } else if (department_id_data.toString().equals("2200")) {
            getMenuInflater().inflate(R.menu.diy_menu, menu);
            return true;
        } else if (department_id_data.toString().equals("5200")) {
            getMenuInflater().inflate(R.menu.workers_menu, menu);
            return true;
        } else {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
    }

    /**
     * 進入Menu各個頁面
     *
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_item:
                Intent intent = new Intent(UploadActivity.this, MenuActivity.class);
                startActivity(intent);
                Toast.makeText(this, "HOME", Toast.LENGTH_SHORT).show();
                finish();
                break; //返回首頁
            case R.id.schedule_item:
                Intent intent7 = new Intent(UploadActivity.this, ScheduleActivity.class);
                startActivity(intent7);
                Toast.makeText(this, "行程資訊",Toast.LENGTH_SHORT).show();
                break; //進入行程資訊頁面
            case R.id.calendar_item:
                Intent intent1 = new Intent(UploadActivity.this, CalendarActivity.class);
                startActivity(intent1);
                Toast.makeText(this, "派工行事曆", Toast.LENGTH_SHORT).show();
                break; //進入派工行事曆頁面
            case R.id.work_item:
                Intent intent2 = new Intent(UploadActivity.this, SearchActivity.class);
                startActivity(intent2);
                Toast.makeText(this, "查詢派工資料", Toast.LENGTH_SHORT).show();
                break; //進入查詢派工資料頁面
            /*case R.id.signature_item:
                Intent intent3 = new Intent(UploadActivity.this, SignatureActivity.class);
                startActivity(intent3);
                Toast.makeText(this, "客戶電子簽名", Toast.LENGTH_SHORT).show();
                break; //進入客戶電子簽名頁面*/
            /*case R.id.record_item:
                Intent intent8 = new Intent(UploadActivity.this, RecordActivity.class);
                startActivity(intent8);
                Toast.makeText(this, "上傳日報紀錄",Toast.LENGTH_SHORT).show();
                break; //進入上傳日報紀錄頁面*/
            case R.id.picture_item:
                Intent intent4 = new Intent(UploadActivity.this, PictureActivity.class);
                startActivity(intent4);
                Toast.makeText(this, "客戶訂單照片上傳", Toast.LENGTH_SHORT).show();
                break; //進入客戶訂單照片上傳頁面
            case R.id.customer_item:
                Intent intent5 = new Intent(UploadActivity.this, CustomerActivity.class);
                startActivity(intent5);
                Toast.makeText(this, "客戶訂單查詢", Toast.LENGTH_SHORT).show();
                break; //進入客戶訂單查詢頁面
            /*case R.id.upload_item:
                Toast.makeText(this, "上傳日報", Toast.LENGTH_SHORT).show();
                break; //顯示上傳日報
            case R.id.correct_item:
                Intent intent6 = new Intent(UploadActivity.this, CorrectActivity.class);
                startActivity(intent6);
                Toast.makeText(this, "日報修正", Toast.LENGTH_SHORT).show();
                break; //進入日報修正頁面*/
            case R.id.about_item:
                Intent intent9 = new Intent(UploadActivity.this, VersionActivity.class);
                startActivity(intent9);
                Toast.makeText(this, "版本資訊", Toast.LENGTH_SHORT).show();
                break; //進入版本資訊頁面
            case R.id.QRCode_item:
                Intent intent10 = new Intent(UploadActivity.this, QRCodeActivity.class);
                startActivity(intent10);
                Toast.makeText(this, "QRCode", Toast.LENGTH_SHORT).show();
                break; //進入QRCode頁面
            case R.id.quotation_item:
                Intent intent11 = new Intent(UploadActivity.this, QuotationActivity.class);
                startActivity(intent11);
                Toast.makeText(this, "報價單審核", Toast.LENGTH_SHORT).show();
                break; //進入報價單審核頁面
            case R.id.points_item:
                Intent intent12 = new Intent(UploadActivity.this, PointsActivity.class);
                startActivity(intent12);
                Toast.makeText(this, "我的點數", Toast.LENGTH_SHORT).show();
                break; //進入查詢工務點數頁面
            case R.id.miss_item:
                Intent intent14 = new Intent(UploadActivity.this, MissCountActivity.class);
                startActivity(intent14);
                Toast.makeText(this, "未回單數量", Toast.LENGTH_SHORT).show();
                break; //進入工務未回單數量頁面
            case R.id.inventory_item:
                Intent intent15 = new Intent(UploadActivity.this, InventoryActivity.class);
                startActivity(intent15);
                Toast.makeText(this, "倉庫盤點", Toast.LENGTH_SHORT).show();
                break; //進入倉庫盤點管理頁面
            case R.id.map_item:
                Intent intent17 = new Intent(UploadActivity.this, GPSActivity.class);
                startActivity(intent17);
                Toast.makeText(this, "工務打卡GPS", Toast.LENGTH_SHORT).show();
                break; //進入GPS地圖頁面
            case R.id.eng_points_item:
                Intent intent18 = new Intent(UploadActivity.this, EngPointsActivity.class);
                startActivity(intent18);
                Toast.makeText(this, "工務點數明細", Toast.LENGTH_SHORT).show();
                break; //進入工務點數明細頁面
            default:
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        scrollView = (ScrollView)findViewById(R.id.scrollview);

        context = this;

        //程式剛啟始時載入第一個下拉選單
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typespinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1 = (Spinner) findViewById(R.id.typespinner);
        sp1.setAdapter(adapter);
        sp1.setOnItemSelectedListener(selectListener);

        //因為下拉選單第一個為請選擇，所以不載入
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, empty);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2 = (Spinner) findViewById(R.id.storespinner);
        sp2.setAdapter(adapter2);
    }

    //第一個下拉類別的監看式
    private AdapterView.OnItemSelectedListener selectListener = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            //讀取第一個下拉選單是選擇第幾個
            int pos = sp1.getSelectedItemPosition();
            //重新產生新的Adapter，用的是二維陣列type2[pos]
            adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, storespinner[pos]);
            //載入第二個下拉選單Spinner
            sp2.setAdapter(adapter2);
        }
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    public void GoTopBtn(View view) {

        Handler mHandler = new Handler();

        mHandler.post(new Runnable() {
            public void run() {
                //實現畫面置頂按鈕
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("UploadActivity", "onDestroy");
    }
}
