package com.drn.airqualitymonitoring.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.drn.airqualitymonitoring.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {
    private LineChart mChart;
    private ArrayList lineEntries = new ArrayList();
    private LineDataSet lineDataSet;
    private LineData lineData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        mChart = (LineChart) findViewById(R.id.chart);
        ArrayList<Entry> xValues = new ArrayList<>();
        for (int i = 0,j=0; i < 1000; i=i+50,j++) {
            xValues.add(new Entry(j,i));
        }
        lineDataSet = new LineDataSet(xValues, "");
        lineData = new LineData(lineDataSet);
        mChart.setData(lineData);
        mChart.invalidate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Double point){
        getEntries(point,lineEntries.size());
    }

    private void getEntries(Double point, int size) {
        lineEntries.add(new Entry(size,point.floatValue()));
        lineDataSet = new LineDataSet(lineEntries, "");
        lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(18f);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setCubicIntensity(.6f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.rgb(0,255,255));
        lineData = new LineData(lineDataSet);
        mChart.setData(lineData);
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }
}
