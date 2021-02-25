package com.example.a10609516.app.FCM;

/**
 * Created by 10609516 on 2018/1/18.
 */

public class MyNotice {
    private static final MyNotice ourInstance = new MyNotice();

    public static MyNotice getInstance() {
        return ourInstance;
    }

    private MyNotice() {
    }

    private OnMessageReceivedListener mOnMessageReceivedListener;
    public interface OnMessageReceivedListener{
        void onMessageReceived(String s);
    }
    public void setOnMessageReceivedListener(OnMessageReceivedListener listener){
        mOnMessageReceivedListener = listener;
    }
    public void notifyOnMessageReceived(String s){
        if(mOnMessageReceivedListener != null){
            mOnMessageReceivedListener.onMessageReceived(s);
        }
    }
}