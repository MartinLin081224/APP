package com.example.a10609516.app.Tools;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by 10609516 on 2017/9/30.
 */

public class ListViewForScrollView extends ListView {

    public ListViewForScrollView (Context context){
        super (context);
    }

    public ListViewForScrollView (Context context, AttributeSet attrs){
        super (context, attrs);
    }

    public ListViewForScrollView (Context context,AttributeSet attrs, int defStyle){
        super (context,attrs,defStyle);
    }

    @Override

    protected void onMeasure (int WidthMeasureSpec, int heightMeasureSpec){

        int expendSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(WidthMeasureSpec,expendSpec);
    }

}
