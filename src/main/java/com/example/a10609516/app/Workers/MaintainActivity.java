package com.example.a10609516.app.Workers;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10609516.app.Tools.DatePickerFragment;
import com.example.a10609516.app.Tools.ScannerActivity;
import com.example.a10609516.app.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MaintainActivity extends AppCompatActivity {

    private ImageView add_qrcode;
    private Button arrive_button, check_button, cancel_button;
    private TextView work_type_name_txt, svd_service_no_txt, esv_note_txt, time_period_name_txt, esv_address_txt, cp_name_txt,
            esvd_eng_points_txt, esvd_is_money_txt, esvd_booking_remark_txt, have_get_money_txt,
            reason_txt, esvd_is_eng_money_txt, maintain_textView, my_on_type, esvd_eng_money_txt, esvd_a_points_txt, esvd_b_points_txt;
    private TableLayout maintain_tablelayot, qrcode_tablelayot;
    private CheckBox is_get_money_checkBox, have_get_money_checkBox, not_get_money_checkBox, not_get_all_checkBox;
    private EditText have_get_money_edt, not_get_money_edt, not_get_all_edt, not_get_all_reason_edt, esvd_remark_txt, esvd_customer_remark_txt;
    private Spinner arrive_spinner, leave_spinner, reason_spinner, useless_spinner, cp_name_spinner;
    private ArrayAdapter<String> time_listAdapter, work_listAdapter, reason_listAdapter, pay_listAdapter;
    private String[] time = new String[]{"選擇", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30"
            , "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"
            , "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30"
            , "23:00", "23:30", "00:00"};
    /*private String[] time2 = new String[]{"", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30"
            , "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"
            , "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30"
            , "23:00", "23:30", "00:00"};*/
    private String[] reason_type = new String[]{"請選擇", "產品暇庛", "安裝不當", "使用不良", "安裝收尾", "零件老化", "現場堪察", "其他"};
    private String[] useless_work = new String[]{"請選擇", "客人不在場", "現場無法施工", "材料有異", "業務取消", "其他"};
    private String[] pay_mode = new String[]{"無", "現金", "匯款", "支票", "信用卡"};

    //轉畫面的Activity參數
    private Class<?> mClss;
    //ZXING_CAMERA權限
    private static final int ZXING_CAMERA_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain);
        //取消ActionBar
        getSupportActionBar().hide();
        //動態取得 View 物件
        InItFunction();
        //與OKHttp取得連線(取得工務點數分配金額)
        sendRequestWithOkHttpForWorkMoney();
        //與OKHttp取得連線(取得工務點數)
        sendRequestWithOkHttpForWorkPoints();
        //取得SearchActivity傳遞過來的值
        GetResponseData();
        //判斷SearchActivity的是否要收款傳遞過來的值為(是/否)
        CheckYesOrNo();
        //設置收款金額的代入與取消
        HaveGetMoney();
        //抵達時間與離開時間的Spinner
        TimeSpinner();
        //付款方式的Spinner
        PayModeSpinner();
        //檢修(一)(二)的主因Spinner
        ReasonSpinner();
        //無效派工的Spinner
        UselessWorkSpinner();
    }

    /**
     * 動態取得 View 物件
     */
    private void InItFunction() {
        maintain_tablelayot = (TableLayout) findViewById(R.id.maintain_tablelayot);
        qrcode_tablelayot = (TableLayout) findViewById(R.id.qrcode_tablelayot);
        check_button = (Button) findViewById(R.id.check_button);
        cancel_button = (Button) findViewById(R.id.cancel_button);
        work_type_name_txt = (TextView) findViewById(R.id.work_type_name_txt);
        svd_service_no_txt = (TextView) findViewById(R.id.svd_service_no_txt);
        esv_note_txt = (TextView) findViewById(R.id.esv_note_txt);
        time_period_name_txt = (TextView) findViewById(R.id.time_period_name_txt);
        esv_address_txt = (TextView) findViewById(R.id.esv_address_txt);
        esvd_is_money_txt = (TextView) findViewById(R.id.esvd_is_money_txt);
        esvd_booking_remark_txt = (TextView) findViewById(R.id.esvd_booking_remark_txt);
        have_get_money_txt = (TextView) findViewById(R.id.have_get_money_txt);
        reason_txt = (TextView) findViewById(R.id.reason_txt);
        esvd_is_eng_money_txt = (TextView) findViewById(R.id.esvd_is_eng_money_txt);
        esvd_eng_points_txt = (TextView) findViewById(R.id.esvd_eng_points_txt);
        maintain_textView = (TextView) findViewById(R.id.maintain_textView);
        my_on_type = (TextView) findViewById(R.id.my_on_type);
        esvd_eng_money_txt = (TextView) findViewById(R.id.esvd_eng_money_txt);
        esvd_a_points_txt = (TextView) findViewById(R.id.esvd_a_points_txt);
        esvd_b_points_txt = (TextView) findViewById(R.id.esvd_b_points_txt);
        arrive_button = (Button) findViewById(R.id.arrive_button);
        is_get_money_checkBox = (CheckBox) findViewById(R.id.is_get_money_checkBox);
        have_get_money_checkBox = (CheckBox) findViewById(R.id.have_get_money_checkBox);
        not_get_money_checkBox = (CheckBox) findViewById(R.id.not_get_money_checkBox);
        not_get_all_checkBox = (CheckBox) findViewById(R.id.not_get_all_checkBox);
        have_get_money_edt = (EditText) findViewById(R.id.have_get_money_edt);
        //not_get_money_edt = (EditText) findViewById(R.id.not_get_money_edt);
        not_get_all_edt = (EditText) findViewById(R.id.not_get_all_edt);
        esvd_remark_txt = (EditText) findViewById(R.id.esvd_remark_txt);
        //not_get_all_reason_edt = (EditText) findViewById(R.id.not_get_all_reason_edt);
        arrive_spinner = (Spinner) findViewById(R.id.arrive_spinner);
        leave_spinner = (Spinner) findViewById(R.id.leave_spinner);
        reason_spinner = (Spinner) findViewById(R.id.reason_spinner);
        useless_spinner = (Spinner) findViewById(R.id.useless_spinner);
        cp_name_spinner = (Spinner) findViewById(R.id.cp_name_spinner);
        add_qrcode = (ImageView) findViewById(R.id.add_qrcode);

        //add_qrcode.setOnClickListener監聽器
        /*add_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                if(getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size()==0)
                {
                    //未安裝
                    Toast.makeText(MaintainActivity.this, "請至 Play商店 安裝 ZXing 條碼掃描器", Toast.LENGTH_LONG).show();
                }else{
                    //SCAN_MODE, 可判別所有支援條碼
                    //QR_CODE_MODE, 只判別QR_CODE
                    //PRODUCT_MODE, UPC and EAN條碼
                    //ONE_D_MODE, 1維條碼
                    intent.putExtra("SCAN_MODE","SCAN_MODE");
                    //呼叫ZXing Scanner,完成動作後回傳1給onActivityResult的requestCode參數
                    startActivityForResult(intent,1);
                }
            }
        });*/

        //check_button.setOnClickListener監聽器
        check_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (my_on_type.getText().toString().equals("2")) {
                    Toast.makeText(MaintainActivity.this, "【此派工已結案】", Toast.LENGTH_SHORT).show();
                } else {
                    if (String.valueOf(useless_spinner.getSelectedItem()).equals("請選擇")) {
                        if (arrive_button.getText().toString().equals("") || arrive_spinner.getSelectedItem().equals("選擇") || leave_spinner.getSelectedItem().equals("選擇")) {
                            if (arrive_button.getText().toString().equals("")) {
                                Toast.makeText(MaintainActivity.this, "【請選擇抵達日期!!!】", Toast.LENGTH_SHORT).show();
                            }
                            if (arrive_spinner.getSelectedItem().equals("選擇")) {
                                Toast.makeText(MaintainActivity.this, "【請選擇抵達時間!!!】", Toast.LENGTH_SHORT).show();
                            }
                            if (leave_spinner.getSelectedItem().equals("選擇")) {
                                Toast.makeText(MaintainActivity.this, "【請選擇離開時間!!!】", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String arrive_txt = String.valueOf(arrive_spinner.getSelectedItem());
                            String leave_txt = String.valueOf(leave_spinner.getSelectedItem());
                            String start_txt = arrive_txt.replace(":", "");
                            String end_txt = leave_txt.replace(":", "");
                            int time1 = Integer.parseInt(start_txt);
                            int time2 = Integer.parseInt(end_txt);
                            int result = time1 - time2;
                            if (result > 0) {
                                Toast.makeText(MaintainActivity.this, "【離開時間不能小於抵達時間!!!】", Toast.LENGTH_SHORT).show();
                            } else {
                                if (is_get_money_checkBox.isChecked()) {
                                    if (have_get_money_checkBox.isChecked()) {
                                        if ((work_type_name_txt.getText().equals("檢修(一)") && reason_spinner.getSelectedItem().equals("請選擇")) || (work_type_name_txt.getText().equals("檢修(二)") && reason_spinner.getSelectedItem().equals("請選擇"))) {
                                            Toast.makeText(MaintainActivity.this, "【請選擇檢修主因!!!】", Toast.LENGTH_SHORT).show();
                                        } else {
                                            //金額已收齊的OKHttp(工務收錢)
                                            sendRequestWithOkHttpForAll();
                                            finish();
                                        /*//是否進入客戶電子簽名頁面
                                        if ((useless_spinner.getSelectedItem().equals("客人不在場")) || (useless_spinner.getSelectedItem().equals("業務取消"))) {
                                            finish();
                                        } else {
                                            String SN_txt = svd_service_no_txt.getText().toString();
                                            Bundle bundle1 = getIntent().getExtras();
                                            String SEQ_txt = bundle1.getString("ResponseText" + 19);
                                            Intent intent = new Intent(MaintainActivity.this, SignatureActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("ResponseText1", SN_txt);
                                            bundle.putString("ResponseText2", SEQ_txt);
                                            intent.putExtras(bundle);//可放所有基本類別
                                            startActivity(intent);
                                            finish();
                                        }*/
                                        }
                                    }
                                    if (not_get_all_checkBox.isChecked()) {
                                        if ((work_type_name_txt.getText().equals("檢修(一)") && reason_spinner.getSelectedItem().equals("請選擇")) || (work_type_name_txt.getText().equals("檢修(二)") && reason_spinner.getSelectedItem().equals("請選擇"))) {
                                            Toast.makeText(MaintainActivity.this, "【請選擇檢修主因!!!】", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (not_get_all_edt.getText().toString().equals("")) {
                                                Toast.makeText(MaintainActivity.this, "【請輸入收款金額!!!】", Toast.LENGTH_SHORT).show();
                                            } else {
                                                //金額未收齊的OKHttp(工務收錢)
                                                sendRequestWithOkHttpForNotAll();
                                                finish();
                                            /*//是否進入客戶電子簽名頁面
                                            if ((useless_spinner.getSelectedItem().equals("客人不在場")) || (useless_spinner.getSelectedItem().equals("業務取消"))) {
                                                finish();
                                            } else {
                                                String SN_txt = svd_service_no_txt.getText().toString();
                                                Bundle bundle1 = getIntent().getExtras();
                                                String SEQ_txt = bundle1.getString("ResponseText" + 19);
                                                Intent intent = new Intent(MaintainActivity.this, SignatureActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("ResponseText1", SN_txt);
                                                bundle.putString("ResponseText2", SEQ_txt);
                                                intent.putExtras(bundle);//可放所有基本類別
                                                startActivity(intent);
                                                finish();
                                            }*/
                                            }
                                        }
                                    }
                                    if (not_get_money_checkBox.isChecked()) {
                                        if ((work_type_name_txt.getText().equals("檢修(一)") && reason_spinner.getSelectedItem().equals("請選擇")) || (work_type_name_txt.getText().equals("檢修(二)") && reason_spinner.getSelectedItem().equals("請選擇"))) {
                                            Toast.makeText(MaintainActivity.this, "【請選擇檢修主因!!!】", Toast.LENGTH_SHORT).show();
                                        } else {
                                            //金額未收的OKHttp(工務收錢)
                                            sendRequestWithOkHttpForNotGet();
                                            finish();
                                        /*//是否進入客戶電子簽名頁面
                                        if ((useless_spinner.getSelectedItem().equals("客人不在場")) || (useless_spinner.getSelectedItem().equals("業務取消"))) {
                                            finish();
                                        } else {
                                            String SN_txt = svd_service_no_txt.getText().toString();
                                            Bundle bundle1 = getIntent().getExtras();
                                            String SEQ_txt = bundle1.getString("ResponseText" + 19);
                                            Intent intent = new Intent(MaintainActivity.this, SignatureActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("ResponseText1", SN_txt);
                                            bundle.putString("ResponseText2", SEQ_txt);
                                            intent.putExtras(bundle);//可放所有基本類別
                                            startActivity(intent);
                                            finish();
                                        }*/
                                        }
                                    }
                                    if (have_get_money_checkBox.isChecked() || not_get_all_checkBox.isChecked() || not_get_money_checkBox.isChecked()) {
                                    } else {
                                        Toast.makeText(MaintainActivity.this, "【請勾選是否已收款!!!】", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    if ((work_type_name_txt.getText().equals("檢修(一)") && reason_spinner.getSelectedItem().equals("請選擇")) || (work_type_name_txt.getText().equals("檢修(二)") && reason_spinner.getSelectedItem().equals("請選擇"))) {
                                        Toast.makeText(MaintainActivity.this, "【請選擇檢修主因!!!】", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //不須收款的OKHttp
                                        sendRequestWithOkHttpForIsGet();
                                        finish();
                                    /*//是否進入客戶電子簽名頁面
                                    if ((useless_spinner.getSelectedItem().equals("客人不在場")) || (useless_spinner.getSelectedItem().equals("業務取消"))) {
                                        finish();
                                    } else {
                                        String SN_txt = svd_service_no_txt.getText().toString();
                                        Bundle bundle1 = getIntent().getExtras();
                                        String SEQ_txt = bundle1.getString("ResponseText" + 19);
                                        Intent intent = new Intent(MaintainActivity.this, SignatureActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ResponseText1", SN_txt);
                                        bundle.putString("ResponseText2", SEQ_txt);
                                        intent.putExtras(bundle);//可放所有基本類別
                                        startActivity(intent);
                                        finish();
                                    }*/
                                    }
                                }
                            }
                        }
                    } else {
                        //不須收款的OKHttp
                        sendRequestWithOkHttpForIsGet();
                        finish();
                    }
                }
            }
        });//end setOnItemClickListener
        //cancel_button.setOnClickListener監聽器
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });//end setOnItemClickListener
    }

    /**
     * 取得SearchActivity傳遞過來的值
     */
    private void GetResponseData() {
        Bundle bundle = getIntent().getExtras();
        maintain_tablelayot.setStretchAllColumns(true);
        String ResponseText0 = bundle.getString("ResponseText" + 0);
        work_type_name_txt.setText(ResponseText0);
        String ResponseText1 = bundle.getString("ResponseText" + 1);
        svd_service_no_txt.setText(ResponseText1);
        String ResponseText2 = bundle.getString("ResponseText" + 2);
        esv_note_txt.setText(ResponseText2);
        String ResponseText3 = bundle.getString("ResponseText" + 3);
        time_period_name_txt.setText(ResponseText3);
        String ResponseText7 = bundle.getString("ResponseText" + 7);
        esv_address_txt.setText(ResponseText7);
        String ResponseText10 = bundle.getString("ResponseText" + 10);
        esvd_is_money_txt.setText(ResponseText10);
        String ResponseText16 = bundle.getString("ResponseText" + 16);
        esvd_booking_remark_txt.setText(ResponseText16);
        String ResponseText13 = bundle.getString("ResponseText" + 13);
        arrive_button.setText(ResponseText13);
        String ResponseText18 = bundle.getString("ResponseText" + 18);
        esvd_remark_txt.setText(ResponseText18);
        String ResponseText20 = bundle.getString("ResponseText" + 20);
        esvd_eng_points_txt.setText(ResponseText20);
        String ResponseText23 = bundle.getString("ResponseText" + 23);
        my_on_type.setText(ResponseText23);
        Log.e("MaintainActivity", ResponseText23);

        //如果日期為0000-00-00,則把該TextView改為空值
        if (arrive_button.getText().toString().equals("1900-01-01")) {
            arrive_button.setText("");
        }
        //如果派工類別為"臨時取消",則把OK按鈕隱藏
        if(ResponseText0.equals("臨時取消")) {
            check_button.setVisibility(View.GONE);
        }
    }

    /**
     * 判斷SearchActivity的是否要收款傳遞過來的值為(是/否)
     */
    private void CheckYesOrNo() {
        Bundle bundle = getIntent().getExtras();
        String ResponseText9 = bundle.getString("ResponseText" + 9);
        //如果傳遞過來的值為"是" 則預設is_get_money_checkBox為被勾選
        if (ResponseText9.equals("是")) {
            is_get_money_checkBox.setChecked(true);
            //如果is_get_money_checkBox被勾選 則是否已收款相關欄位能被修改
            have_get_money_txt.setEnabled(true);
            have_get_money_checkBox.setEnabled(true);
            have_get_money_edt.setEnabled(true);
            not_get_money_checkBox.setEnabled(true);
            //not_get_money_edt.setEnabled(true);
            not_get_all_checkBox.setEnabled(true);
            not_get_all_edt.setEnabled(true);
            //not_get_all_reason_edt.setEnabled(true);
        }
        //如果傳遞過來的值為"否" 則預設is_get_money_checkBox為不被勾選
        else {
            is_get_money_checkBox.setChecked(false);
            //如果is_get_money_checkBox不被勾選 則是否已收款相關欄位不能被修改
            have_get_money_txt.setEnabled(false);
            have_get_money_checkBox.setEnabled(false);
            have_get_money_edt.setEnabled(false);
            not_get_money_checkBox.setEnabled(false);
            //not_get_money_edt.setEnabled(false);
            not_get_all_checkBox.setEnabled(false);
            not_get_all_edt.setEnabled(false);
            //not_get_all_reason_edt.setEnabled(false);
            //have_get_money_edt.setText("0");//如果is_get_money_checkBox不被勾選 have_get_money_edt設置顯示0
        }
    }

    /**
     * 設置是否已收款的三個CheckBox只能一個被勾選
     *
     * @param view
     */
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.have_get_money_checkBox:
                if (checked) {
                    have_get_money_edt.setEnabled(true);
                    not_get_money_checkBox.setChecked(false);
                    //not_get_money_edt.setEnabled(false);
                    not_get_all_checkBox.setChecked(false);
                    not_get_all_edt.setEnabled(false);
                    //not_get_all_reason_edt.setEnabled(false);
                    esvd_is_eng_money_txt.setText("1");
                } /*else {
                    not_get_money_checkBox.setEnabled(true);
                    //not_get_money_edt.setEnabled(true);
                    not_get_all_checkBox.setEnabled(true);
                    not_get_all_edt.setEnabled(true);
                    //not_get_all_reason_edt.setEnabled(true);
                }*/
                break;
            case R.id.not_get_all_checkBox:
                if (checked) {
                    not_get_all_edt.setEnabled(true);
                    have_get_money_checkBox.setChecked(false);
                    have_get_money_edt.setEnabled(false);
                    not_get_money_checkBox.setChecked(false);
                    //not_get_money_edt.setEnabled(false);
                    esvd_is_eng_money_txt.setText("1");
                }/* else {
                    have_get_money_checkBox.setEnabled(true);
                    have_get_money_edt.setEnabled(true);
                    not_get_money_checkBox.setEnabled(true);
                    //not_get_money_edt.setEnabled(true);
                }*/
                break;
            case R.id.not_get_money_checkBox:
                if (checked) {
                    have_get_money_checkBox.setChecked(false);
                    have_get_money_edt.setEnabled(false);
                    not_get_all_checkBox.setChecked(false);
                    not_get_all_edt.setEnabled(false);
                    //not_get_all_reason_edt.setEnabled(false);
                    esvd_is_eng_money_txt.setText("");
                }/* else {
                    have_get_money_checkBox.setEnabled(true);
                    //have_get_money_edt.setEnabled(true);
                    not_get_all_checkBox.setEnabled(true);
                    not_get_all_edt.setEnabled(true);
                    //not_get_all_reason_edt.setEnabled(true);
                }*/
                break;
        }
    }

    /**
     * 設置收款金額的代入與取消
     */
    private void HaveGetMoney() {
        have_get_money_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果have_get_money_checkBox被勾選 則收款金額直接帶入應收金額
                    have_get_money_edt.setText(esvd_is_money_txt.getText().toString());
                }
                if (!isChecked) {
                    //如果have_get_money_checkBox被取消勾選 則收款金額清空
                    have_get_money_edt.setText("");
                }
            }
        });
        not_get_all_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    //如果not_get_all_checkBox被取消勾選 則收款金額清空
                    not_get_all_edt.setText("");
                }
            }
        });
    }

    /**
     * 日期挑選器
     *
     * @param v
     */
    public void showDatePickerDialog(View v) {
        //日期挑選器
        DialogFragment newFragment = new DatePickerFragment();
        Bundle bData = new Bundle();
        bData.putInt("view", v.getId());
        Button button = (Button) v;
        bData.putString("date", button.getText().toString());
        newFragment.setArguments(bData);
        newFragment.show(getSupportFragmentManager(), "日期挑選器");
    }

    /**
     * 付款方式的Spinner
     */
    private void PayModeSpinner() {
        Bundle bundle = getIntent().getExtras();
        String ResponseText8 = bundle.getString("ResponseText" + 8);
        pay_listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pay_mode);
        pay_listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cp_name_spinner.setAdapter(pay_listAdapter);
        //當迴圈與ResponseText內容一致時跳出迴圈 並顯示該ResponseText的Spinner位置
        for (int i = 0; i < pay_mode.length; i++) {
            if (pay_mode[i].equals(ResponseText8)) {
                cp_name_spinner.setSelection(i, true);
                break;
            }
        }
    }

    /**
     * 抵達時間與離開時間的Spinner
     */
    private void TimeSpinner() {
        Bundle bundle = getIntent().getExtras();
        String ResponseText14 = bundle.getString("ResponseText" + 14);
        String ResponseText15 = bundle.getString("ResponseText" + 15);
        String ResponseText21 = bundle.getString("ResponseText" + 21);
        String ResponseText22 = bundle.getString("ResponseText" + 22);
        String ResponseText23 = bundle.getString("ResponseText" + 23);
        char on_type = ResponseText23.charAt(0);
        Log.e("MAINTAIN1", String.valueOf(on_type));
        Log.e("MAINTAIN2", ResponseText14);
        Log.e("MAINTAIN3", ResponseText15);
        Log.e("MAINTAIN4", ResponseText21);
        Log.e("MAINTAIN5", ResponseText22);
        Log.e("MAINTAIN6", ResponseText23);
        switch (on_type) {
            case '0':
                time_listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, time);
                time_listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                arrive_spinner.setAdapter(time_listAdapter);
                //當迴圈與ResponseText內容一致時跳出迴圈 並顯示該ResponseText的Spinner位置
                for (int i = 0; i < time.length; i++) {
                    if (time[i].equals(ResponseText21)) {
                        arrive_spinner.setSelection(i, true);
                        break;
                    }
                }
                leave_spinner.setAdapter(time_listAdapter);
                for (int i = 0; i < time.length; i++) {
                    if (time[i].equals(ResponseText22)) {
                        leave_spinner.setSelection(i, true);
                        break;
                    }
                }
                break;
            default:
                time_listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, time);
                time_listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                arrive_spinner.setAdapter(time_listAdapter);
                //當迴圈與ResponseText內容一致時跳出迴圈 並顯示該ResponseText的Spinner位置
                for (int i = 0; i < time.length; i++) {
                    if (time[i].equals(ResponseText14)) {
                        arrive_spinner.setSelection(i, true);
                        break;
                    }
                }
                leave_spinner.setAdapter(time_listAdapter);
                for (int i = 0; i < time.length; i++) {
                    if (time[i].equals(ResponseText15)) {
                        leave_spinner.setSelection(i, true);
                        break;
                    }
                }
                break;
        }

        //抵達日期Button自動帶入當天日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(new java.util.Date());
        arrive_button.setText(date);

        Log.e("MaintainActivity", time_period_name_txt.getText().toString());
        String booking_date = time_period_name_txt.getText().toString().substring(0, 11);
        arrive_button.setText(booking_date);
    }

    /**
     * 檢修(一)(二)的主因Spinner
     */
    private void ReasonSpinner() {
        reason_listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reason_type);
        reason_listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reason_spinner.setAdapter(reason_listAdapter);

        if (work_type_name_txt.getText().equals("檢修(一)")) {
            reason_txt.setVisibility(View.VISIBLE);
            reason_spinner.setVisibility(View.VISIBLE);
        }
        if (work_type_name_txt.getText().equals("檢修(二)")) {
            reason_txt.setVisibility(View.VISIBLE);
            reason_spinner.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 無效派工的Spinner
     */
    private void UselessWorkSpinner() {
        work_listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, useless_work);
        work_listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        useless_spinner.setAdapter(work_listAdapter);
    }

    /**
     * 與OkHttp建立連線(收齊)
     */
    private void sendRequestWithOkHttpForAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("MaintainActivity", user_id_data);

                Bundle bundle = getIntent().getExtras();
                String seq_id = bundle.getString("ResponseText" + 19);
                Log.e("MaintainActivity", seq_id);
                //獲取出勤維護的數據
                String arrive_date = arrive_button.getText().toString();
                String get_money = have_get_money_edt.getText().toString();
                String arrive_time = String.valueOf(arrive_spinner.getSelectedItem());
                String leave_time = String.valueOf(leave_spinner.getSelectedItem());
                String check_reason = String.valueOf(reason_spinner.getSelectedItem());
                String useless_work = String.valueOf(useless_spinner.getSelectedItem());
                String work_remark = esvd_remark_txt.getText().toString();
                String get_money_type = esvd_is_eng_money_txt.getText().toString();
                String work_points = esvd_eng_points_txt.getText().toString();
                String pay_mode = String.valueOf(cp_name_spinner.getSelectedItem());

                if (check_reason.toString().equals("請選擇")) {
                    check_reason = "";
                }
                if (useless_work.toString() != "請選擇") {
                    work_points = "0.5";
                }
                if (useless_work.toString().equals("請選擇")) {
                    useless_work = "無";
                }
                if (pay_mode.toString() == "無") {
                    pay_mode = "";
                }
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .add("ESVD_SEQ_ID", seq_id)
                            .add("ESVD_GET_ENG_MONEY", get_money)
                            .add("ESVD_BEGIN_DATE", arrive_date)
                            .add("ESVD_BEGIN_TIME", arrive_time)
                            .add("ESVD_END_TIME", leave_time)
                            .add("ESVD_CENSON_TYPE", check_reason)
                            .add("ESVD_INVALID_FLAG", useless_work)
                            .add("ESVD_REMARK", work_remark)
                            .add("ESVD_IS_ENG_MONEY", get_money_type)
                            .add("ESVD_ENG_POINTS", work_points)
                            .add("CP_NAME", pay_mode)
                            .build();
                    Log.e("MaintainActivity", user_id_data);
                    Log.e("MaintainActivity", seq_id);
                    Log.e("MaintainActivity", get_money);
                    Log.e("MaintainActivity", arrive_date);
                    Log.e("MaintainActivity", arrive_time);
                    Log.e("MaintainActivity", leave_time);
                    Log.e("MaintainActivity", check_reason);
                    Log.e("MaintainActivity", useless_work);
                    Log.e("MaintainActivity", work_remark);
                    Log.e("MaintainActivity", get_money_type);
                    Log.e("MaintainActivity", work_points);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/wqp/UpdateMaintainData.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.i("MaintainActivity", responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 與OkHttp建立連線(未收齊)
     */
    private void sendRequestWithOkHttpForNotAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("MaintainActivity", user_id_data);

                Bundle bundle = getIntent().getExtras();
                String seq_id = bundle.getString("ResponseText" + 19);
                Log.e("MaintainActivity", seq_id);
                //獲取出勤維護的數據
                String arrive_date = arrive_button.getText().toString();
                String get_money = not_get_all_edt.getText().toString();
                String arrive_time = String.valueOf(arrive_spinner.getSelectedItem());
                String leave_time = String.valueOf(leave_spinner.getSelectedItem());
                String check_reason = String.valueOf(reason_spinner.getSelectedItem());
                String useless_work = String.valueOf(useless_spinner.getSelectedItem());
                String work_remark = esvd_remark_txt.getText().toString();
                String get_money_type = esvd_is_eng_money_txt.getText().toString();
                String work_points = esvd_eng_points_txt.getText().toString();
                String pay_mode = String.valueOf(cp_name_spinner.getSelectedItem());

                if (check_reason.toString().equals("請選擇")) {
                    check_reason = "";
                }
                if (useless_work.toString() != "請選擇") {
                    work_points = "0.5";
                }
                if (useless_work.toString().equals("請選擇")) {
                    useless_work = "無";
                }
                if (pay_mode.toString() == "無") {
                    pay_mode = "";
                }
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .add("ESVD_SEQ_ID", seq_id)
                            .add("ESVD_GET_ENG_MONEY", get_money)
                            .add("ESVD_BEGIN_DATE", arrive_date)
                            .add("ESVD_BEGIN_TIME", arrive_time)
                            .add("ESVD_END_TIME", leave_time)
                            .add("ESVD_CENSON_TYPE", check_reason)
                            .add("ESVD_INVALID_FLAG", useless_work)
                            .add("ESVD_REMARK", work_remark)
                            .add("ESVD_IS_ENG_MONEY", get_money_type)
                            .add("ESVD_ENG_POINTS", work_points)
                            .add("CP_NAME", pay_mode)
                            .build();
                    Log.e("MaintainActivity", user_id_data);
                    Log.e("MaintainActivity", seq_id);
                    Log.e("MaintainActivity", get_money);
                    Log.e("MaintainActivity", arrive_date);
                    Log.e("MaintainActivity", arrive_time);
                    Log.e("MaintainActivity", leave_time);
                    Log.e("MaintainActivity", check_reason);
                    Log.e("MaintainActivity", useless_work);
                    Log.e("MaintainActivity", work_remark);
                    Log.e("MaintainActivity", get_money_type);
                    Log.e("MaintainActivity", work_points);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/wqp/UpdateMaintainData.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.i("MaintainActivity", responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 與OkHttp建立連線(未收)
     */
    private void sendRequestWithOkHttpForNotGet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("MaintainActivity", user_id_data);

                Bundle bundle = getIntent().getExtras();
                String seq_id = bundle.getString("ResponseText" + 19);
                Log.e("MaintainActivity", seq_id);
                //獲取出勤維護的數據
                String arrive_date = arrive_button.getText().toString();
                String get_money = not_get_all_edt.getText().toString();
                String arrive_time = String.valueOf(arrive_spinner.getSelectedItem());
                String leave_time = String.valueOf(leave_spinner.getSelectedItem());
                String check_reason = String.valueOf(reason_spinner.getSelectedItem());
                String useless_work = String.valueOf(useless_spinner.getSelectedItem());
                String work_remark = esvd_remark_txt.getText().toString();
                String get_money_type = esvd_is_eng_money_txt.getText().toString();
                String work_points = esvd_eng_points_txt.getText().toString();
                String pay_mode = String.valueOf(cp_name_spinner.getSelectedItem());

                if (check_reason.toString().equals("請選擇")) {
                    check_reason = "";
                }
                if (useless_work.toString() != "請選擇") {
                    work_points = "0.5";
                }
                if (useless_work.toString().equals("請選擇")) {
                    useless_work = "無";
                }
                if (pay_mode.toString() == "無") {
                    pay_mode = "";
                }
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .add("ESVD_SEQ_ID", seq_id)
                            .add("ESVD_GET_ENG_MONEY", get_money)
                            .add("ESVD_BEGIN_DATE", arrive_date)
                            .add("ESVD_BEGIN_TIME", arrive_time)
                            .add("ESVD_END_TIME", leave_time)
                            .add("ESVD_CENSON_TYPE", check_reason)
                            .add("ESVD_INVALID_FLAG", useless_work)
                            .add("ESVD_REMARK", work_remark)
                            .add("ESVD_IS_ENG_MONEY", get_money_type)
                            .add("ESVD_ENG_POINTS", work_points)
                            .add("CP_NAME", pay_mode)
                            .build();
                    Log.e("MaintainActivity", user_id_data);
                    Log.e("MaintainActivity", seq_id);
                    Log.e("MaintainActivity", get_money);
                    Log.e("MaintainActivity", arrive_date);
                    Log.e("MaintainActivity", arrive_time);
                    Log.e("MaintainActivity", leave_time);
                    Log.e("MaintainActivity", check_reason);
                    Log.e("MaintainActivity", useless_work);
                    Log.e("MaintainActivity", work_remark);
                    Log.e("MaintainActivity", get_money_type);
                    Log.e("MaintainActivity", work_points);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/wqp/UpdateMaintainData.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.i("MaintainActivity", responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 與OkHttp建立連線(不需收)
     */
    private void sendRequestWithOkHttpForIsGet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("MaintainActivity", user_id_data);

                Bundle bundle = getIntent().getExtras();
                String seq_id = bundle.getString("ResponseText" + 19);
                Log.e("MaintainActivity", seq_id);
                //獲取出勤維護的數據
                String arrive_date = arrive_button.getText().toString();
                String get_money = have_get_money_edt.getText().toString();
                String arrive_time = String.valueOf(arrive_spinner.getSelectedItem());
                String leave_time = String.valueOf(leave_spinner.getSelectedItem());
                String check_reason = String.valueOf(reason_spinner.getSelectedItem());
                String useless_work = String.valueOf(useless_spinner.getSelectedItem());
                String work_remark = esvd_remark_txt.getText().toString();
                String get_money_type = esvd_is_eng_money_txt.getText().toString();
                String work_points = esvd_eng_points_txt.getText().toString();
                String pay_mode = String.valueOf(cp_name_spinner.getSelectedItem());

                if (check_reason.toString().equals("請選擇")) {
                    check_reason = "";
                }
                if (useless_work.toString() != "請選擇") {
                    work_points = "0.5";
                }
                if (useless_work.toString().equals("請選擇")) {
                    useless_work = "無";
                }
                if (pay_mode.toString() == "無") {
                    pay_mode = "";
                }
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .add("ESVD_SEQ_ID", seq_id)
                            .add("ESVD_GET_ENG_MONEY", get_money)
                            .add("ESVD_BEGIN_DATE", arrive_date)
                            .add("ESVD_BEGIN_TIME", arrive_time)
                            .add("ESVD_END_TIME", leave_time)
                            .add("ESVD_CENSON_TYPE", check_reason)
                            .add("ESVD_INVALID_FLAG", useless_work)
                            .add("ESVD_REMARK", work_remark)
                            .add("ESVD_IS_ENG_MONEY", get_money_type)
                            .add("ESVD_ENG_POINTS", work_points)
                            .add("CP_NAME", pay_mode)
                            .build();
                    Log.e("MaintainActivity", user_id_data);
                    Log.e("MaintainActivity", seq_id);
                    Log.e("MaintainActivity", get_money);
                    Log.e("MaintainActivity", arrive_date);
                    Log.e("MaintainActivity", arrive_time);
                    Log.e("MaintainActivity", leave_time);
                    Log.e("MaintainActivity", check_reason);
                    Log.e("MaintainActivity", useless_work);
                    Log.e("MaintainActivity", work_remark);
                    Log.e("MaintainActivity", get_money_type);
                    Log.e("MaintainActivity", work_points);
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/wqp/UpdateMaintainData.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("MaintainActivity", requestBody.toString());
                    Log.e("MaintainActivity", request.toString());
                    Log.e("MaintainActivity", response.toString());
                    Log.e("MaintainActivity", responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 與OKHttp連線(工務獎金)_new
     */
    private void sendRequestWithOkHttpForWorkMoney() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("MaintainActivity", user_id_data);
                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WorkMoney.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("MaintainActivity", responseData);
                    parseJSONWithJSONObjectOfWorkMoney(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 獲得JSON字串並解析成String字串
     *取得工務點數獎金
     * @param jsonData
     */
    private void parseJSONWithJSONObjectOfWorkMoney(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String distribution_money = jsonObject.getString("ASSIGN_MONEY");
                Log.e("MaintainActivity", "MONEY : " + distribution_money);
                float points = Float.parseFloat(esvd_eng_points_txt.getText().toString());
                float money = Float.parseFloat(distribution_money);
                float total = points * money;
                esvd_eng_money_txt.setText(String.valueOf(total));
                Log.e("MaintainActivity", "MONEY2 : " + esvd_eng_money_txt.getText().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 與OKHttp連線(工務點數)_new
     */
    private void sendRequestWithOkHttpForWorkPoints() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //接收LoginActivity傳過來的值
                SharedPreferences user_id = getSharedPreferences("user_id_data", MODE_PRIVATE);
                String user_id_data = user_id.getString("ID", "");
                Log.e("MaintainActivity", user_id_data);

                Bundle bundle = getIntent().getExtras();
                String seq_id = bundle.getString("ResponseText" + 19);
                String service_no = bundle.getString("ResponseText" + 1);
                Log.e("MaintainActivity", seq_id);

                try {
                    OkHttpClient client = new OkHttpClient();
                    //POST
                    RequestBody requestBody = new FormBody.Builder()
                            .add("User_id", user_id_data)
                            .add("ESVD_SEQ_ID", seq_id)
                            .add("ESVD_SERVICE_NO", service_no)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://a.wqp-water.com.tw/WQP/WorkPoints.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.e("MaintainActivity", responseData);
                    parseJSONWithJSONObjectForWorkPoints(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 取得工務點數(A、B)
     *
     * @param jsonData
     */
    private void parseJSONWithJSONObjectForWorkPoints(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                //JSON格式改為字串
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                final String esvd_a_point = jsonObject.getString("A_POINTS");
                final String esvd_b_point = jsonObject.getString("B_POINTS");

                Log.e("MaintainActivity", esvd_a_point);
                Log.e("MaintainActivity", esvd_b_point);

                MaintainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        esvd_a_points_txt.setText(esvd_a_point);
                        esvd_b_points_txt.setText(esvd_b_point);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Button的設置
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

                qrcode_tablelayot.setWeightSum(6);
                TableRow tableRow1 = new TableRow(MaintainActivity.this);
                LinearLayout linearLayout = new LinearLayout(MaintainActivity.this);
                Button delete_btn = new Button(MaintainActivity.this);
                TextView result_txt = new TextView(MaintainActivity.this);
                linearLayout.setGravity(Gravity.CENTER_VERTICAL);

                result_txt.setText(intent.getStringExtra("result_text"));
                result_txt.setPadding(10, 3, 5, 3);
                result_txt.setTextColor(Color.rgb(6, 102, 219));
                result_txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                result_txt.setGravity(Gravity.CENTER_VERTICAL);

                delete_btn.setText("刪除");
                delete_btn.setBackgroundResource(R.drawable.button);
                delete_btn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                delete_btn.setTextColor(Color.rgb(6, 102, 219));
                delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TableRow tableRow = (TableRow) view.getParent();
                        qrcode_tablelayot.removeView(tableRow);
                    }
                });

                linearLayout.addView(result_txt);
                tableRow1.addView(linearLayout, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 5));
                tableRow1.addView(delete_btn, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                qrcode_tablelayot.addView(tableRow1);
            } else {
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(MaintainActivity.this, "取消掃描", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}