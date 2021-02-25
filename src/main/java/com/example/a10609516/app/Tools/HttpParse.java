package com.example.a10609516.app.Tools;

/**
 * Created by 10609516 on 2017/10/26.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpParse {

    String FinalHttpData = "";
    String Result;
    BufferedWriter bufferedWriter;
    OutputStream outputStream;
    InputStream inputStream;
    BufferedReader bufferedReader;
    HttpURLConnection httpURLConnection = null;
    StringBuilder stringBuilder = new StringBuilder();
    URL url;

    public String postRequest(HashMap<String, String> Data, String HttpUrlHolder) {

        try {
            url = new URL(HttpUrlHolder);

            httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(10000);

            httpURLConnection.setConnectTimeout(15000);

            httpURLConnection.setRequestMethod("POST");

            httpURLConnection.setDoInput(true);

            httpURLConnection.setDoOutput(true);

            httpURLConnection.connect();

            outputStream = httpURLConnection.getOutputStream();

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            bufferedWriter.write(FinalDataParse(Data));

            bufferedWriter.flush();

            bufferedWriter.close();

            outputStream.close();

            //httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                inputStream = httpURLConnection.getInputStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                FinalHttpData = bufferedReader.readLine();

            } else {
                FinalHttpData = "伺服端無法連線";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return FinalHttpData;
    }

    public String FinalDataParse(HashMap<String, String> hashMap2) throws UnsupportedEncodingException {

        for (Map.Entry<String, String> map_entry : hashMap2.entrySet()) {


            stringBuilder.append(URLEncoder.encode(map_entry.getKey(), "UTF-8"));

            stringBuilder.append("=");

            stringBuilder.append(URLEncoder.encode(map_entry.getValue(), "UTF-8"));

            stringBuilder.append("&");

        }

        Result = stringBuilder.toString();

        Result = Result.substring(0, Result.length() - 1);

        Log.e("LoginActivity",Result);

        return Result;
    }
}
