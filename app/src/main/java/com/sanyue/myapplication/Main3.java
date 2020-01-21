package com.sanyue.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.sanyue.reticularview.ReticularView;

import java.util.ArrayList;

/***
 * Create by hyw
 * Create Time 2020-01-20 15:44
 */
public class Main3 extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_3);
        ReticularView myView = findViewById(R.id.view);
        ArrayList<ReticularView.Values> data = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            ReticularView.Values values = new ReticularView.Values();
            values.setName("图"+i);
            if(i==0){
                values.setValue(13);
            }else {
                values.setValue(i+5);
            }
            data.add(values);
        }
        myView.drawView()
                .setData(data)
                .setShapeNumber(data.size())
                .setCycleNumber(4)
                .setDrawRegionCircle(Color.BLACK)

                .setPolygonBgColors(false,new int[]{Color.RED})
                .setRegionColor(Color.YELLOW)
//                .setRegionColor(Color.RED,false,130)
//                .setRegionColor(Color.YELLOW)
                .setLinColors(new int[]{Color.RED})
//                .setLinColors(new int[]{Color.RED,Color.BLUE,Color.GREEN,Color.MAGENTA})
                .setTextColor(Color.BLACK)
                .setLinWidth(2)
                .setTextSize(16)
                .setMaxValue(16)
                .build();

    }

}
