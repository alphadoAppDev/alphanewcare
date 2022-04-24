package kr.co.alphanewcare.graph;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import kr.co.alphanewcare.R;
import kr.co.alphanewcare.graph.view.GraphBarView;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;

public class GraphActivity extends Activity
{
    private ArrayList<String> labels;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initBarChart();
        initLineChart();
    }

    private void initBarChart()
    {
        GraphBarView graphBar = findViewById(R.id.barChart);

        graphBar.setBarBackgroundColor(Color.LTGRAY);
        graphBar.setBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        graphBar.setPointColor(Color.RED);
        graphBar.setRulerColor(Color.WHITE);

        graphBar.setMaxValue(5);
        graphBar.setBarValue(3);
        graphBar.setPointValue(1);

        graphBar.setPointSizeDp(5);
        graphBar.setRulerWidthDp(2);
    }

    private void initLineChart()
    {
        LineChart lineChart = findViewById(R.id.lineChart);
    }
}
