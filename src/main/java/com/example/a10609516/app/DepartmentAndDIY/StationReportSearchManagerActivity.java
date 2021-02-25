package com.example.a10609516.app.DepartmentAndDIY;

import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.example.a10609516.app.R;
import com.example.a10609516.app.Tools.WQPServiceActivity;

public class StationReportSearchManagerActivity extends WQPServiceActivity {

    private ScrollView search_scv;
    private LinearLayout separate_llt, result_llt;
    private EditText name_edt;
    private Button start_btn, end_btn, search_btn;
    private Spinner station_spinner;

    private ArrayAdapter<String> category_listAdapter;
    private String[] category_type = new String[]{"全部", "資訊需求單", "行銷需求單"};

    private String LOG = "StationReportSearchManagerActivity";

    private String s_department, department, mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_report_search_manager);
        //動態取得 View 物件
        InItFunction();
        //店別類別Spinner
        Station_Spinner();
    }

    /**
     *  動態取得 View 物件
     */
    private void InItFunction() {
        search_scv = (ScrollView) findViewById(R.id.search_scv);
        separate_llt = (LinearLayout) findViewById(R.id.separate_llt);
        result_llt = (LinearLayout) findViewById(R.id.result_llt);
        name_edt = (EditText) findViewById(R.id.name_edt);
        start_btn = (Button) findViewById(R.id.start_btn);
        end_btn = (Button) findViewById(R.id.end_btn);
        search_btn = (Button) findViewById(R.id.search_btn);
        station_spinner = (Spinner) findViewById(R.id.station_spinner);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result_llt.removeAllViews();
                separate_llt.setVisibility(View.VISIBLE);
                result_llt.setVisibility(View.VISIBLE);
                //與OkHttp建立連線 取得需求單資訊
                //sendRequestWithOkHttpForRequisitionSearch();
            }
        });
    }

    /**
     * 需求類別Spinner
     */
    private void Station_Spinner() {
        category_listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, category_type);
        category_listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        station_spinner.setAdapter(category_listAdapter);
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


}
