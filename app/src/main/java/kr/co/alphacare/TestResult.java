package kr.co.alphacare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.alphacare.utils.PetInfo;
import kr.co.alphacare.utils.TestHistory;


public class TestResult extends AppCompatActivity {
    private final String tag = "TestResult";
    Context mContext;
    LinearLayout lyManageState;
    LinearLayout llSelectScope;

    Timer mScanTimer;
    TimerTask mScanTask;

    int showPeriod; // 1:1개월, 2: 3개월, 3: 6개월, 4: 1년, 5: 전체

    int selectedItem = 1;

    PetInfo mPetInfo;
    TestHistory mTestHistory;
    LineChart lineChart;
    boolean chartScrolled;

    final String[] mDays = {"07/01", "08/11","08/22","09/31","10/02"};
    final String[] mValue = {"", "음성", "미량", "양성(1+)", "양성(2+)", "양성(3+)", "양성(4+)"}; // Your List / array with String Values For X-axis Labels


    // 신규
    private ImageView mIvArrow_1;
    private ImageView mIvArrow_2;
    private ImageView mIvArrow_3;
    private ImageView mIvArrow_4;
    private ImageView mIvArrow_5;
    private ImageView mIvArrow_6;
    private ImageView mIvArrow_7;
    private ImageView mIvArrow_8;
    private ImageView mIvArrow_9;
    private ImageView mIvArrow_10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_result);
/*
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setElevation(0f);
        }*/

        mContext = getApplicationContext();

        ((LinearLayout) findViewById(R.id.linear_1)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.linear_2)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.linear_3)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.linear_4)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.linear_5)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.linear_6)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.linear_7)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.linear_8)).setVisibility(View.VISIBLE);


        TextView textView = findViewById(R.id.txtTitle);
        textView.setText(mContext.getResources().getString(R.string.test_result));

//        llDescriptionItem = findViewById(R.id.llDescriptionItem);
        llSelectScope = findViewById(R.id.llSelectScope);

        int currentPet = AppGlobals.INSTANCE.getCurrentPage();
        mPetInfo = AppGlobals.INSTANCE.getPetInfo(currentPet);

       /*
        mPetInfo.getDataList(showPeriod);
        mPetInfo.getItemValue(1, showPeriod);
        for (int i = 0; i < 10; i++) {
            mPetInfo.getItemRange(i + 1);
        }*/

        mScanTimer = null;

        lineChart = findViewById(R.id.chart);
        lineChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!(lineChart.getLowestVisibleX() == lineChart.getXAxis().getAxisMinimum() || lineChart.getHighestVisibleX() == lineChart.getXAxis().getAxisMaximum())){
                    //Log.e("AAAAAAA", "111111111111111");
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP: {
                            SetScanTimer(3000);
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            //Log.e("AAAAAAA", "222222222222222222");
                            break;
                        }
                        case MotionEvent.ACTION_DOWN: {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            //Log.e("AAAAAAA", "33333333333333333333");
                            break;
                        }
                        case  MotionEvent.ACTION_MOVE:{
                            //llSelectScope.setVisibility(View.INVISIBLE);
                            chartScrolled = true;
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            //Log.e("AAAAAAA", "4444444444444444444444");
                            break;
                        }
                    }
                }
                return false;
            }
        });
        /*
        lineChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!(lineChart.getLowestVisibleX() == lineChart.getXAxis().getAxisMinimum() || lineChart.getHighestVisibleX() == lineChart.getXAxis().getAxisMaximum())) {
                    // Do your work here
                    Log.e("AAAAAAAAAAAA", "BBBBBBBBBBBBBBBBB");
                    return false;
                }
                return  true;
            }
            });*/

        showPeriod = 1;
        findViewById(R.id.llDay).setBackgroundResource(R.drawable.background_button_test_result);

        // =========신규========
        mArrowIndexUpDown[0] = true;
        mArrowIndexUpDown[1] = false;
        mArrowIndexUpDown[2] = false;
        mArrowIndexUpDown[3] = false;
        mArrowIndexUpDown[4] = false;
        mArrowIndexUpDown[5] = false;
        mArrowIndexUpDown[6] = false;
        mArrowIndexUpDown[7] = false;
        mArrowIndexUpDown[8] = false;
        mArrowIndexUpDown[9] = false;
        mIvArrow_1 = findViewById(R.id.iv_arrow_1);
        mIvArrow_2 = findViewById(R.id.iv_arrow_2);
        mIvArrow_3 = findViewById(R.id.iv_arrow_3);
        mIvArrow_4 = findViewById(R.id.iv_arrow_4);
        mIvArrow_5 = findViewById(R.id.iv_arrow_5);
        mIvArrow_6 = findViewById(R.id.iv_arrow_6);
        mIvArrow_7 = findViewById(R.id.iv_arrow_7);
        mIvArrow_8 = findViewById(R.id.iv_arrow_8);
        mIvArrow_9 = findViewById(R.id.iv_arrow_9);
        mIvArrow_10 = findViewById(R.id.iv_arrow_10);
        // =========신규========

        LoadPetHistory();
        UpdateItem(mTestHistory);


        //showChart(1);

    }


    private void setTestSelected(TestHistory history,int normal, int doubt, int alert)
    {
        ((TextView) findViewById(R.id.tv_test_number)).setText(history.getNumber()+"");
        ((TextView) findViewById(R.id.tv_date)).setText(history.getDate());
        ((TextView) findViewById(R.id.tv_time)).setText(history.getTime());
        ((TextView) findViewById(R.id.tv_test_noraml)).setText(mContext.getResources().getString(R.string.test_result_normal) + " " + normal);
        ((TextView) findViewById(R.id.tv_test_doubt)).setText(mContext.getResources().getString(R.string.test_result_doubt) + " " + doubt);
        ((TextView) findViewById(R.id.tv_test_alert)).setText(mContext.getResources().getString(R.string.test_result_alert) + " " + alert);
    }



    void UpdateItem(TestHistory history) {
        UpdateCircle(1);
        // 1. 잠혈
        UpdateItemOne(history.getBlood());
        // 2. 빌리루빈
        UpdateItemTwo(history.getBilirubin());
        // 3. 우르빌리노겐
        UpdateItemThree(history.getUrobilinogen());
        // 4. 케톤
        UpdateItemFour(history.getKetones());
        // 5. 단백질
        UpdateItemFive(history.getProtein());
        // 6. 아질산염
        UpdateItemSix(history.getNitrite());
        // 7. 포도당
        UpdateItemSeven(history.getGlucose());
        // 8. 산성도
        UpdateItemEight(history.getPh());
        // 9. 비중
        UpdateItemNine(history.getSg());
        // 10. 백혈구
        UpdateItemTen(history.getLeukocytes());
        setStatusNum(history);
        setTestSelected(history, normal, doubt, alert);
    }

    int normal = 0;
    int doubt = 0;
    int alert = 0;
    private void setStatusNum(TestHistory history)
    {
        normal = 0;
        doubt = 0;
        alert = 0;
        if(history.getBlood() == 1){normal += 1;}
        else if(history.getBlood() == 2 || history.getBlood() == 3){doubt += 1;}
        else if(history.getBlood() == 4 || history.getBlood() == 5){alert += 1;}

        if(history.getBilirubin() == 1){normal += 1;}
        else if(history.getBilirubin() == 2 || history.getBilirubin() == 3){doubt += 1;}
        else if(history.getBilirubin() == 4){alert += 1;}

        if(history.getUrobilinogen() == 1 || history.getUrobilinogen() == 2){normal += 1;}
        else if(history.getUrobilinogen() == 3 || history.getUrobilinogen() == 4){doubt += 1;}
        else if(history.getUrobilinogen() == 5){alert += 1;}

        if(history.getKetones() == 1 || history.getKetones() == 2){normal += 1;}
        else if(history.getKetones() == 3 || history.getKetones() == 4){doubt += 1;}
        else if(history.getKetones() == 5 || history.getKetones() == 6){alert += 1;}

        if(history.getProtein() == 1 || history.getProtein() == 2){normal += 1;}
        else if(history.getProtein() == 3 || history.getProtein() == 4){doubt += 1;}
        else if(history.getProtein() == 5 || history.getProtein() == 6){alert += 1;}

        if(history.getNitrite() == 1){normal += 1;}
        else if(history.getNitrite() == 2){alert += 1;}

        if(history.getGlucose() == 1 || history.getGlucose() == 2){normal += 1;}
        else if(history.getGlucose() == 3 || history.getGlucose() == 4){doubt += 1;}
        else if(history.getGlucose() == 5 || history.getGlucose() == 6){alert += 1;}

        if(history.getPh() == 2 || history.getPh() == 3 || history.getPh() == 4){normal += 1;}
        else if(history.getPh() == 5 || history.getPh() == 6){doubt += 1;}
        else if(history.getPh() == 1 || history.getPh() == 7){alert += 1;}

        if(history.getSg() == 2 || history.getSg() == 3){normal += 1;}
        else if(history.getSg() == 4 || history.getSg() == 5){doubt += 1;}
        else if(history.getSg() == 1 || history.getSg() == 6 || history.getSg() == 7){alert += 1;}

        if(history.getLeukocytes() == 1 || history.getLeukocytes() == 2){normal += 1;}
        else if(history.getLeukocytes() == 3 || history.getLeukocytes() == 4){doubt += 1;}
        else if(history.getLeukocytes() == 5){alert += 1;}
    }

    void UpdateCircle(int index) {
        ArrowImageChange(index);
        selectedItem = index;
        showChart();
    }

    private boolean[] mArrowIndexUpDown = new boolean[10];
    private void ArrowImageChange(int index)
    {
        switch (index){
            case 1:
                if(!mArrowIndexUpDown[0]) {
                    mIvArrow_1.setImageResource(R.drawable.ic_arrow_top);
                    mArrowIndexUpDown[0] = true;
                    (findViewById(R.id.linear_content_1)).setVisibility(View.VISIBLE);
                }else{
                    mIvArrow_1.setImageResource(R.drawable.ic_arrow_bottom);
                    mArrowIndexUpDown[0] = false;
                    (findViewById(R.id.linear_content_1)).setVisibility(View.GONE);
                }
                break;
            case 2:
                if(!mArrowIndexUpDown[1]) {
                    mIvArrow_2.setImageResource(R.drawable.ic_arrow_top);
                    mArrowIndexUpDown[1] = true;
                    (findViewById(R.id.linear_content_2)).setVisibility(View.VISIBLE);
                }else{
                    mIvArrow_2.setImageResource(R.drawable.ic_arrow_bottom);
                    mArrowIndexUpDown[1] = false;
                    (findViewById(R.id.linear_content_2)).setVisibility(View.GONE);
                }
                break;
            case 3:
                if(!mArrowIndexUpDown[2]) {
                    mIvArrow_3.setImageResource(R.drawable.ic_arrow_top);
                    mArrowIndexUpDown[2] = true;
                    (findViewById(R.id.linear_content_3)).setVisibility(View.VISIBLE);
                }else{
                    mIvArrow_3.setImageResource(R.drawable.ic_arrow_bottom);
                    mArrowIndexUpDown[2] = false;
                    (findViewById(R.id.linear_content_3)).setVisibility(View.GONE);
                }
                break;
            case 4:
                if(!mArrowIndexUpDown[3]) {
                    mIvArrow_4.setImageResource(R.drawable.ic_arrow_top);
                    mArrowIndexUpDown[3] = true;
                    (findViewById(R.id.linear_content_4)).setVisibility(View.VISIBLE);
                }else{
                    mIvArrow_4.setImageResource(R.drawable.ic_arrow_bottom);
                    mArrowIndexUpDown[3] = false;
                    (findViewById(R.id.linear_content_4)).setVisibility(View.GONE);
                }
                break;
            case 5:
                if(!mArrowIndexUpDown[4]) {
                    mIvArrow_5.setImageResource(R.drawable.ic_arrow_top);
                    mArrowIndexUpDown[4] = true;
                    (findViewById(R.id.linear_content_5)).setVisibility(View.VISIBLE);
                }else{
                    mIvArrow_5.setImageResource(R.drawable.ic_arrow_bottom);
                    mArrowIndexUpDown[4] = false;
                    (findViewById(R.id.linear_content_5)).setVisibility(View.GONE);
                }
                break;
            case 6:
                if(!mArrowIndexUpDown[5]) {
                    mIvArrow_6.setImageResource(R.drawable.ic_arrow_top);
                    mArrowIndexUpDown[5] = true;
                    (findViewById(R.id.linear_content_6)).setVisibility(View.VISIBLE);
                }else{
                    mIvArrow_6.setImageResource(R.drawable.ic_arrow_bottom);
                    mArrowIndexUpDown[5] = false;
                    (findViewById(R.id.linear_content_6)).setVisibility(View.GONE);
                }
                break;
            case 7:
                if(!mArrowIndexUpDown[6]) {
                    mIvArrow_7.setImageResource(R.drawable.ic_arrow_top);
                    mArrowIndexUpDown[6] = true;
                    (findViewById(R.id.linear_content_7)).setVisibility(View.VISIBLE);
                }else{
                    mIvArrow_7.setImageResource(R.drawable.ic_arrow_bottom);
                    mArrowIndexUpDown[6] = false;
                    (findViewById(R.id.linear_content_7)).setVisibility(View.GONE);
                }
                break;
            case 8:
                if(!mArrowIndexUpDown[7]) {
                    mIvArrow_8.setImageResource(R.drawable.ic_arrow_top);
                    mArrowIndexUpDown[7] = true;
                    (findViewById(R.id.linear_content_8)).setVisibility(View.VISIBLE);
                }else{
                    mIvArrow_8.setImageResource(R.drawable.ic_arrow_bottom);
                    mArrowIndexUpDown[7] = false;
                    (findViewById(R.id.linear_content_8)).setVisibility(View.GONE);
                }
                break;
            case 9:
                if(!mArrowIndexUpDown[8]) {
                    mIvArrow_9.setImageResource(R.drawable.ic_arrow_top);
                    mArrowIndexUpDown[8] = true;
                    (findViewById(R.id.linear_content_9)).setVisibility(View.VISIBLE);
                }else{
                    mIvArrow_9.setImageResource(R.drawable.ic_arrow_bottom);
                    mArrowIndexUpDown[8] = false;
                    (findViewById(R.id.linear_content_9)).setVisibility(View.GONE);
                }
                break;
            case 10:
                if(!mArrowIndexUpDown[9]) {
                    mIvArrow_10.setImageResource(R.drawable.ic_arrow_top);
                    mArrowIndexUpDown[9] = true;
                    (findViewById(R.id.linear_content_10)).setVisibility(View.VISIBLE);
                }else{
                    mIvArrow_10.setImageResource(R.drawable.ic_arrow_bottom);
                    mArrowIndexUpDown[9] = false;
                    (findViewById(R.id.linear_content_10)).setVisibility(View.GONE);
                }
                break;
        }
    }

    // 1. 잠혈
    void UpdateItemOne(int val) {
        ImageView txtItemOne1, txtItemOne2, txtItemOne3, txtItemOne4, txtItemOne5;
        txtItemOne1 = findViewById(R.id.txtItemOne1);
        txtItemOne2 = findViewById(R.id.txtItemOne2);
        txtItemOne3 = findViewById(R.id.txtItemOne3);
        txtItemOne4 = findViewById(R.id.txtItemOne4);
        txtItemOne5 = findViewById(R.id.txtItemOne5);
        txtItemOne1.setVisibility(View.GONE);
        txtItemOne2.setVisibility(View.GONE);
        txtItemOne3.setVisibility(View.GONE);
        txtItemOne4.setVisibility(View.GONE);
        txtItemOne5.setVisibility(View.GONE);
        View viewDot = findViewById(R.id.view_dot_1);
        switch (val) {
            case 1:
                txtItemOne1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 2:
                txtItemOne2.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 3:
                txtItemOne3.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 4:
                txtItemOne4.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            case 5:
                txtItemOne5.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            default:
                txtItemOne1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
        }
    }

    // 2. 빌리루빈
    void UpdateItemTwo(int val) {
        ImageView txtItemTwo1, txtItemTwo2, txtItemTwo3, txtItemTwo4;
        txtItemTwo1 = findViewById(R.id.txtItemTwo1);
        txtItemTwo2 = findViewById(R.id.txtItemTwo2);
        txtItemTwo3 = findViewById(R.id.txtItemTwo3);
        txtItemTwo4 = findViewById(R.id.txtItemTwo4);
        txtItemTwo1.setVisibility(View.GONE);
        txtItemTwo2.setVisibility(View.GONE);
        txtItemTwo3.setVisibility(View.GONE);
        txtItemTwo4.setVisibility(View.GONE);

        View viewDot = findViewById(R.id.view_dot_2);
        switch (val) {
            case 1:
                txtItemTwo1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 2:
                txtItemTwo2.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 3:
                txtItemTwo3.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 4:
                txtItemTwo4.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            default:
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                txtItemTwo1.setVisibility(View.VISIBLE);
                break;
        }
    }

    // 3. 우르빌리노겐
    void UpdateItemThree(int val) {
        ImageView txtItemThree1, txtItemThree2, txtItemThree3, txtItemThree4, txtItemThree5;

        txtItemThree1 = findViewById(R.id.txtItemThree1);
        txtItemThree2 = findViewById(R.id.txtItemThree2);
        txtItemThree3 = findViewById(R.id.txtItemThree3);
        txtItemThree4 = findViewById(R.id.txtItemThree4);
        txtItemThree5 = findViewById(R.id.txtItemThree5);

        txtItemThree1.setVisibility(View.GONE);
        txtItemThree2.setVisibility(View.GONE);
        txtItemThree3.setVisibility(View.GONE);
        txtItemThree4.setVisibility(View.GONE);
        txtItemThree5.setVisibility(View.GONE);
        View viewDot = findViewById(R.id.view_dot_3);
        switch (val) {
            case 1:
                txtItemThree1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 2:
                txtItemThree2.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 3:
                txtItemThree3.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 4:
                txtItemThree4.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 5:
                txtItemThree5.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            default:
                txtItemThree1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
        }
    }

    // 4. 케톤
    void UpdateItemFour(int val) {
        ImageView txtItemFour1, txtItemFour2, txtItemFour3, txtItemFour4, txtItemFour5, txtItemFour6;

        txtItemFour1 = findViewById(R.id.txtItemFour1);
        txtItemFour2 = findViewById(R.id.txtItemFour2);
        txtItemFour3 = findViewById(R.id.txtItemFour3);
        txtItemFour4 = findViewById(R.id.txtItemFour4);
        txtItemFour5 = findViewById(R.id.txtItemFour5);
        txtItemFour6 = findViewById(R.id.txtItemFour6);

        txtItemFour1.setVisibility(View.GONE);
        txtItemFour2.setVisibility(View.GONE);
        txtItemFour3.setVisibility(View.GONE);
        txtItemFour4.setVisibility(View.GONE);
        txtItemFour5.setVisibility(View.GONE);
        txtItemFour6.setVisibility(View.GONE);

        View viewDot = findViewById(R.id.view_dot_4);
        switch (val) {
            case 1:
                txtItemFour1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 2:
                txtItemFour2.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 3:
                txtItemFour3.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 4:
                txtItemFour4.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 5:
                txtItemFour5.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            case 6:
                txtItemFour6.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            default:
                txtItemFour1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
        }
    }

    // 5. 단백질
    void UpdateItemFive(int val) {
        ImageView txtItemFive1, txtItemFive2, txtItemFive3, txtItemFive4, txtItemFive5, txtItemFive6;
        txtItemFive1 = findViewById(R.id.txtItemFive1);
        txtItemFive2 = findViewById(R.id.txtItemFive2);
        txtItemFive3 = findViewById(R.id.txtItemFive3);
        txtItemFive4 = findViewById(R.id.txtItemFive4);
        txtItemFive5 = findViewById(R.id.txtItemFive5);
        txtItemFive6 = findViewById(R.id.txtItemFive6);
        txtItemFive1.setVisibility(View.GONE);
        txtItemFive2.setVisibility(View.GONE);
        txtItemFive3.setVisibility(View.GONE);
        txtItemFive4.setVisibility(View.GONE);
        txtItemFive5.setVisibility(View.GONE);
        txtItemFive6.setVisibility(View.GONE);

        View viewDot = findViewById(R.id.view_dot_5);
        switch (val) {
            case 1:
                txtItemFive1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 2:
                txtItemFive2.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 3:
                txtItemFive3.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 4:
                txtItemFive4.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 5:
                txtItemFive5.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            case 6:
                txtItemFive6.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            default:
                txtItemFive1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
        }
    }

    // 6. 아질산염
    void UpdateItemSix(int val) {
        ImageView txtItemSix1, txtItemSix2;
        txtItemSix1 = findViewById(R.id.txtItemSix1);
        txtItemSix2 = findViewById(R.id.txtItemSix2);
        txtItemSix1.setVisibility(View.GONE);
        txtItemSix2.setVisibility(View.GONE);
        View viewDot = findViewById(R.id.view_dot_6);
        switch (val) {
            case 1:
                txtItemSix1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 2:
                txtItemSix2.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            default:
                txtItemSix1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
        }
    }

    // 7. 포도당
    void UpdateItemSeven(int val) {
        ImageView txtItemSeven1, txtItemSeven2, txtItemSeven3, txtItemSeven4, txtItemSeven5, txtItemSeven6;
        txtItemSeven1 = findViewById(R.id.txtItemSeven1);
        txtItemSeven2 = findViewById(R.id.txtItemSeven2);
        txtItemSeven3 = findViewById(R.id.txtItemSeven3);
        txtItemSeven4 = findViewById(R.id.txtItemSeven4);
        txtItemSeven5 = findViewById(R.id.txtItemSeven5);
        txtItemSeven6 = findViewById(R.id.txtItemSeven6);
        txtItemSeven1.setVisibility(View.GONE);
        txtItemSeven2.setVisibility(View.GONE);
        txtItemSeven3.setVisibility(View.GONE);
        txtItemSeven4.setVisibility(View.GONE);
        txtItemSeven5.setVisibility(View.GONE);
        txtItemSeven6.setVisibility(View.GONE);
        View viewDot = findViewById(R.id.view_dot_7);
        switch (val) {
            case 1:
                txtItemSeven1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 2:
                txtItemSeven2.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 3:
                txtItemSeven3.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 4:
                txtItemSeven4.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 5:
                txtItemSeven5.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            case 6:
                txtItemSeven5.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            default:
                txtItemSeven1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
        }
    }

    // 8. 산성도
    void UpdateItemEight(int val) {
        ImageView txtItemEight1, txtItemEight2, txtItemEight3, txtItemEight4, txtItemEight5, txtItemEight6, txtItemEight7;
        txtItemEight1 = findViewById(R.id.txtItemEight1);
        txtItemEight2 = findViewById(R.id.txtItemEight2);
        txtItemEight3 = findViewById(R.id.txtItemEight3);
        txtItemEight4 = findViewById(R.id.txtItemEight4);
        txtItemEight5 = findViewById(R.id.txtItemEight5);
        txtItemEight6 = findViewById(R.id.txtItemEight6);
        txtItemEight7 = findViewById(R.id.txtItemEight7);
        txtItemEight1.setVisibility(View.GONE);
        txtItemEight2.setVisibility(View.GONE);
        txtItemEight3.setVisibility(View.GONE);
        txtItemEight4.setVisibility(View.GONE);
        txtItemEight5.setVisibility(View.GONE);
        txtItemEight6.setVisibility(View.GONE);
        txtItemEight7.setVisibility(View.GONE);
        View viewDot = findViewById(R.id.view_dot_8);
        switch (val) {
            case 1:
                txtItemEight1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            case 2:
                txtItemEight2.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 3:
                txtItemEight3.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 4:
                txtItemEight4.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 5:
                txtItemEight5.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 6:
                txtItemEight6.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 7:
                txtItemEight6.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            default:
                txtItemEight1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
        }
    }

    // 9. 비중
    void UpdateItemNine(int val) {
        ImageView txtItemNine1, txtItemNine2, txtItemNine3, txtItemNine4, txtItemNine5, txtItemNine6, txtItemNine7;
        txtItemNine1 = findViewById(R.id.txtItemNine1);
        txtItemNine2 = findViewById(R.id.txtItemNine2);
        txtItemNine3 = findViewById(R.id.txtItemNine3);
        txtItemNine4 = findViewById(R.id.txtItemNine4);
        txtItemNine5 = findViewById(R.id.txtItemNine5);
        txtItemNine6 = findViewById(R.id.txtItemNine6);
        txtItemNine7 = findViewById(R.id.txtItemNine7);
        txtItemNine1.setVisibility(View.GONE);
        txtItemNine2.setVisibility(View.GONE);
        txtItemNine3.setVisibility(View.GONE);
        txtItemNine4.setVisibility(View.GONE);
        txtItemNine5.setVisibility(View.GONE);
        txtItemNine6.setVisibility(View.GONE);
        txtItemNine7.setVisibility(View.GONE);
        View viewDot = findViewById(R.id.view_dot_9);
        switch (val) {
            case 1:
                txtItemNine1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            case 2:
                txtItemNine2.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 3:
                txtItemNine3.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 4:
                txtItemNine4.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 5:
                txtItemNine5.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 6:
                txtItemNine6.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            case 7:
                txtItemNine7.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            default:
                txtItemNine1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
        }
    }

    // 10. 백혈구
    void UpdateItemTen(int val) {
        ImageView txtItemTen1, txtItemTen2, txtItemTen3, txtItemTen4, txtItemTen5;
        txtItemTen1 = findViewById(R.id.txtItemTen1);
        txtItemTen2 = findViewById(R.id.txtItemTen2);
        txtItemTen3 = findViewById(R.id.txtItemTen3);
        txtItemTen4 = findViewById(R.id.txtItemTen4);
        txtItemTen5 = findViewById(R.id.txtItemTen5);
        txtItemTen1.setVisibility(View.GONE);
        txtItemTen2.setVisibility(View.GONE);
        txtItemTen3.setVisibility(View.GONE);
        txtItemTen4.setVisibility(View.GONE);
        txtItemTen5.setVisibility(View.GONE);
        View viewDot = findViewById(R.id.view_dot_10);
        switch (val) {
            case 1:
                txtItemTen1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 2:
                txtItemTen2.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
            case 3:
                txtItemTen3.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 4:
                txtItemTen4.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_yellow);
                break;
            case 5:
                txtItemTen5.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_red);
                break;
            default:
                txtItemTen1.setVisibility(View.VISIBLE);
                viewDot.setBackgroundResource(R.drawable.test_result_dot_green);
                break;
        }
    }

    void LoadPetHistory() {
        Intent intent = getIntent();
        int listIndex = 0;
        listIndex = intent.getExtras().getInt("list_index");
        mTestHistory = mPetInfo.getTestHistory(listIndex);
        Log.e("TTTTTTTTT", mTestHistory.getDisplayFormat());
    }

    public void btnShowManageState(View v) {
        Log.e("TTTTT", "TTTTTTTT");
        if (lyManageState.getVisibility() == View.GONE) lyManageState.setVisibility(View.VISIBLE);
        else lyManageState.setVisibility(View.GONE);
    }
    private void ChangeButtonState(int index) {
        LinearLayout llDay, llWeek, llMonth, llYear, llSetting;

        if (chartScrolled) return;

        llDay = findViewById(R.id.llDay);
        llWeek = findViewById(R.id.llWeek);
        llMonth = findViewById(R.id.llMonth);
        llYear = findViewById(R.id.llYear);
        llSetting = findViewById(R.id.llSetting);

        llDay.setBackgroundResource(android.R.color.transparent);
        llWeek.setBackgroundResource(android.R.color.transparent);
        llMonth.setBackgroundResource(android.R.color.transparent);
        llYear.setBackgroundResource(android.R.color.transparent);
        llSetting.setBackgroundResource(android.R.color.transparent);

        switch (index) {
            case 1:
                llDay.setBackgroundResource(R.drawable.background_button_test_result);
                break;
            case 2:
                llWeek.setBackgroundResource(R.drawable.background_button_test_result);
                break;
            case 3:
                llMonth.setBackgroundResource(R.drawable.background_button_test_result);
                break;
            case 4:
                llYear.setBackgroundResource(R.drawable.background_button_test_result);
                break;
            case 5:
                llSetting.setBackgroundResource(R.drawable.background_button_test_result);
                break;
            default:
                llDay.setBackgroundResource(R.drawable.background_button_test_result);
        }

        showPeriod = index;
        showChart();
    }

    public void btnBack(View v) {
        finish();
        //overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
    }

    public void btnShowDay(View v) {
        ChangeButtonState(1);
    }

    public void btnShowWeek(View v) {
        ChangeButtonState(2);
    }

    public void btnShowMonth(View v) {
        ChangeButtonState(3);
    }

    public void btnShowYear(View v) {
        ChangeButtonState(4);
    }

    public void btnShowSetting(View v) {
        ChangeButtonState(5);
    }

    public void btnSelectedItemOne(View v) {
        UpdateCircle(1);
    }
    public void btnSelectedItemTwo(View v) {
        UpdateCircle(2);
    }
    public void btnSelectedItemThree(View v) {
        UpdateCircle(3);
    }
    public void btnSelectedItemFour(View v) {
        UpdateCircle(4);
    }
    public void btnSelectedItemFive(View v) {
        UpdateCircle(5);
    }
    public void btnSelectedItemSix(View v) {
        UpdateCircle(6);
    }
    public void btnSelectedItemSeven(View v) {
        UpdateCircle(7);
    }
    public void btnSelectedItemEight(View v) {
        UpdateCircle(8);
    }
    public void btnSelectedItemNine(View v) {
        UpdateCircle(9);
    }
    public void btnSelectedItemTen(View v) {
        UpdateCircle(10);
    }

    public void btnShowTestList(View v) {
        List<Map<String, Object>> dialogItemList;
        int testHistorySize;
        TestHistory history;

        testHistorySize = mPetInfo.getTestHistorySize();

        String title;
        String content;

        dialogItemList = new ArrayList<>();

        for(int i = 0; i < testHistorySize; i++)
        {
            history = mPetInfo.getTestHistory(i);
            //title = history.getTestDateTime();
            //content = history.getTestResult();
            title = getTestNumber(history);
            setStatusNum(history);
            content = getTestResult();

            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("textTitle", title);
            itemMap.put("textContent", content);

            dialogItemList.add(itemMap);
        }



        final AlertDialog.Builder builder = new AlertDialog.Builder(TestResult.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.show_test_history, null);
        builder.setView(view);

        final ListView listview = view.findViewById(R.id.lvChooseTestHistory);
        final AlertDialog dialog = builder.create();

        ((TextView) view.findViewById(R.id.tv_btn_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        SimpleAdapter simpleAdapter = new SimpleAdapter(TestResult.this, dialogItemList,
                R.layout.text_text_item,
                new String[]{"textTitle", "textContent"},
                new int[]{R.id.txtTitle, R.id.txtContent});

        listview.setAdapter(simpleAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TestHistory history = mPetInfo.getTestHistory(position);
                UpdateItem(history);

                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    String getTestNumber(TestHistory history) {
        String result;

        result = history.getNumber() + mContext.getResources().getString(R.string.test_count) + " " + history.getDateTime();
        return result;
    }

    String getTestResult() {
        String result;

        result = getResources().getString(R.string.test_result_normal) + normal + " " +
                getResources().getString(R.string.test_result_doubt) + doubt + " " +
                getResources().getString(R.string.test_result_alert) + alert;
        return result;
    }

    private void SetScanTimer(long period)
    {
        if (mScanTimer != null) return;

        mScanTimer = new Timer();

        mScanTask = new TimerTask() {

            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mScanTimer.cancel();
                                mScanTimer = null;
                                chartScrolled = false;


                            }
                        });
                    }
                }).start();
            }
        };
        mScanTimer.schedule(mScanTask, period);
    }

    void showChart() {
        int type;
        String showType;
        String[] xValue;
        String[] yValue;
        int[] value;
        int count;

        switch (showPeriod) {
            case 1:
                showType = mContext.getResources().getString(R.string.one_month);
                break;
            case 2:
                showType = mContext.getResources().getString(R.string.three_month);
                break;
            case 3:
                showType = mContext.getResources().getString(R.string.six_month);
                break;
            case 4:
                showType = mContext.getResources().getString(R.string.one_year);
                break;
            case 5:
                showType = mContext.getResources().getString(R.string.all);
                break;
            default:
                showType = mContext.getResources().getString(R.string.one_month);
        }

        type = selectedItem;

        value = mPetInfo.getItemValue(type, showPeriod);
        xValue = mPetInfo.getDataList(showPeriod);
        yValue = getItemRange(type);
        //yValue = mPetInfo.getItemRange(type);
        count = value.length;
        for(int i = 0 ; i < count ; i++)
        {
            Log.e("value = ", value[i] + "");
        }
        for(int i = 0 ; i < xValue.length ; i++)
        {
            Log.e("xValue = ", xValue[i] + "");
        }
        for(int i = 0 ; i < yValue.length ; i++)
        {
            Log.e("yValue = ", yValue[i] + "");
        }

        List<Entry> entries = new ArrayList<>();
        //ArrayList<Integer> colors = new ArrayList<>();
        ArrayList<Integer> colors;


        for (int i = 0; i < count; i++) {
            entries.add(new Entry(i, (value[i] * 2) - 1));
        }

       /*
        entries.add(new Entry(0, 1));
        entries.add(new Entry(1, 2));
        entries.add(new Entry(2, 1));
        entries.add(new Entry(3, 1));
        entries.add(new Entry(4, 3));
        entries.add(new Entry(5, 1));
        //entries.add(new Entry(6, 1));
        */

        if (lineChart.getLineData() != null) {
            lineChart.fitScreen();
            lineChart.getLineData().clearValues();
            lineChart.getXAxis().setValueFormatter(null);
            lineChart.notifyDataSetChanged();
            lineChart.clear();
            lineChart.invalidate();
        }

        LineDataSet lineDataSet = new LineDataSet(entries, showType);
        //LineDataSet lineDataSet = new LineDataSet(entries, "속성명");

        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        //lineDataSet.setCircleColor(Color.parseColor("#ffd2d2"));
        colors = mPetInfo.getColorList(type, showPeriod);
        lineDataSet.setCircleColors(colors);

        //lineDataSet.setCircleHoleColor(Color.parseColor("#ffd2d2")); // LineChart에서 Line Hole Circle Color 설정
        lineDataSet.setColor(Color.parseColor("#A1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(true);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setDrawValues(false);

        lineDataSet.setLineWidth(1); // 라인 두께

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        //l.setTypeface(tfLight);
        l.setTextColor(Color.BLACK);
        l.setEnabled(true); // 속성명 제거

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setLabelCount(6, false); //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면 반드시 보여줌
        xAxis.setLabelCount(3, false); //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면 반드시 보여줌
        xAxis.setTextColor(Color.BLACK);
        //xAxis.setAvoidFirstLastClipping(true);
        //xAxis.setGridColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)); // X축 줄의 컬러 설정
        //xAxis.setValueFormatter(new GraphAxisValueFormatter(mDays));
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValue));

        //xAxis.enableGridDashedLine(8, 24, 0);
        xAxis.setDrawGridLines(false);
        //xAxis.setEnabled(false);
        //xAxis.setAxisMaximum(xValue.length + 1);
        xAxis.setAxisMaximum(xValue.length);


        //Axis.setAvoidFirstLastClipping(true);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setLabelCount(yValue.length, false);
        yLAxis.setAxisMinimum(0);
        //yLAxis.setAxisMaximum(6);
        yLAxis.setAxisMaximum(yValue.length);
        //yLAxis.setTextColor(Color.RED);
        //yLAxis.setTextSize(10);
        yLAxis.setValueFormatter(new IndexAxisValueFormatter(yValue));



        // y축 라인 색상 변경
/*
        LimitLine upperLimitLine = new LimitLine(2);
        upperLimitLine.setLineColor(Color.parseColor("#8ac1f2"));
        upperLimitLine.setLineWidth(2);
        yLAxis.addLimitLine(upperLimitLine);

        LimitLine lowerLimitLine = new LimitLine(0);
        lowerLimitLine.setLineWidth(2);
        lowerLimitLine.setLineColor(Color.parseColor("#8ac1f2"));
        yLAxis.addLimitLine(lowerLimitLine);
        */


/*
        int yIndex = yValue.length;
        LimitLine lowerLimitLine;
        for (int i = 0; i < yIndex; i++) {
            if ((i % 2) == 1) {
                lowerLimitLine = new LimitLine(i);
                //lowerLimitLine.setLineColor(Color.WHITE);
                lowerLimitLine.setLineColor(Color.TRANSPARENT);
                yLAxis.addLimitLine(lowerLimitLine);
            }
        }
*/


        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");
        //lineChart.setDescription(description);
        lineChart.getDescription().setEnabled(false);

        lineChart.setVisibleXRangeMaximum(3);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);

        //lineChart.animateY(2000, Easing.EaseInCubic);
        //lineChart.animateY(2000, Easing.EaseInCirc);
        //lineChart.invalidate();

        lineChart.moveViewToX(xValue.length); // X 축 끝으로 이동
    }

    private String[] getItemRange(int type) {
        String[] result;

        result = null;

        switch (type) {
            case 1:
                String[] one = {"",GetString(R.string.nagative),"",GetString(R.string.positive) + "(1+)","",GetString(R.string.positive) + "(2+)","",GetString(R.string.positive) + "(3+)"};
                result = one;
                break;
            case 2:
                String[] two = {"",GetString(R.string.nagative),"",GetString(R.string.positive) + "(1+)","",GetString(R.string.positive) + "(2+)","",GetString(R.string.positive) + "(3+)"};
                result = two;
                break;
            case 3:
                String[] three = {"","0.1","", "1","", "2","", "4","", "8","", "12"};
                result = three;
                break;
            case 4:
                String[] four = {"",GetString(R.string.nagative),"",GetString(R.string.very_small_amount),"", GetString(R.string.positive) + "(1+)","",GetString(R.string.positive) + "(2+)","",GetString(R.string.positive) + "(3+)"};
                result = four;
                break;
            case 5: // 단백질
                String[] five = {"",GetString(R.string.nagative),"",GetString(R.string.very_small_amount),"", GetString(R.string.positive) + "(1+)","",GetString(R.string.positive) + "(2+)","",GetString(R.string.positive) + "(3+)","", GetString(R.string.positive) + "(4+)"};
                result = five;
                break;
            case 6: // 아질산염
                String[] six = {"",GetString(R.string.nagative),"",GetString(R.string.positive)};
                result = six;
                break;
            case 7: // 포도당
                String[] seven = {"",GetString(R.string.nagative),"",GetString(R.string.very_small_amount),"", GetString(R.string.positive) + "(1+)","",GetString(R.string.positive) + "(2+)","",GetString(R.string.positive) + "(3+)"};
                result = seven;
                break;
            case 8: // PH
                String[] eight = {"","5.0","", "6.0","", "6.5","", "7.0","", "8.0","", "9.0"};
                result = eight;
                break;
            case 9: // 비중
                String[] nine = {"","1.000","", "1.010","", "1.020","", "1.030","", "1.040","", "1.050","", "1.060","", "1.070"};
                result = nine;
                break;
            case 10:
                String[] ten = {"",GetString(R.string.nagative),"",GetString(R.string.very_small_amount),"", GetString(R.string.positive) + "(1+)","",GetString(R.string.positive) + "(2+)","",GetString(R.string.positive) + "(3+)"};
                result = ten;
                break;
            default:
        }

        if (result != null) {
            String report;
            int count = result.length;
            report = "";
            for (int i = 0; i < count; i++) {
                report += result[i] + ", ";
            }
            //Log.e("getItemRange " + type, report);
        }

        return result;
    }

    private String GetString(int id) {
        return mContext.getResources().getString(id);
    }

}

