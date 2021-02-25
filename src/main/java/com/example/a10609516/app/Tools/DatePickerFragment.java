package com.example.a10609516.app.Tools;

import android.app.DatePickerDialog;
import android.app.Dialog;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import androidx.fragment.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.ParsePosition;
import java.util.Date;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    int vid;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        super.onCreateDialog(savedInstanceState);

        //欲轉換的日期字串
        Bundle bData = getArguments();
        //記錄下傳進來的是哪個Button的ID
        vid = bData.getInt("view");
        String str = bData.getString("date");
        final Calendar c = Calendar.getInstance();
        Date date;
        if(!str.equals("")){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            //進行轉換
            ParsePosition pos = new ParsePosition(0);
            date = sdf.parse(str, pos);
            c.setTime(date);
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //建立 DatePickerDialog instance 並回傳
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day){
        Button button = (Button) getActivity().findViewById(vid);
        if (month > 8) {
            if (day < 10) {
                //月的起始值是0,所以要加1
                //日小於10,前面補0
                button.setText("" + year + "-" + (month + 1) + "-0" + day);
            } else {
                //月的起始值是0,所以要加1
                button.setText("" + year + "-" + (month + 1) + "-" + day);
            }
        } else {
            if (day < 10) {
                //月的起始值是0,所以要加1
                //日小於10,前面補0
                button.setText("" + year + "-0" + (month + 1) + "-0" + day);
            } else {
                //月的起始值是0,所以要加1
                button.setText("" + year + "-0" + (month + 1) + "-" + day);
            }
        }
    }
}
