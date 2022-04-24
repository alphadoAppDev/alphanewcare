package kr.co.alphanewcare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.alphanewcare.listview.CareListAdapter;
import kr.co.alphanewcare.utils.HttpAsyncTask;
import kr.co.alphanewcare.utils.HttpString;

public class CareListActivity extends BaseActivity {

    private CareListAdapter mAdapter;
    private ListView mListView;
    private TextView mTvCareInfo;
    private LinearLayout mLinearBtnCareInfo;

    private String title = "";
    private String btnCareInfoName = "";
    private String nextCatrgory = "";
    private String subCategory_1 = "";
    private String subCategory_2 = "";
    private String mCategoryCode = "";

    private ProgressDialog mDialog;
    private String mService = "";

    // 챠트
    private LineChart mLineChart;
    private ArrayList<Entry> mValues; //차트 데이터 셋에 담겨질 데이터
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_list);
        init();
    }

    private void init()
    {
        mDialog = new ProgressDialog(CareListActivity.this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("Loading...");

        ((ImageView) findViewById(R.id.imgBack)).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {finish();}});
        Intent intent = getIntent();
        String category = intent.getStringExtra("category");
        mCategoryCode = category;
        if(category.equals(MainActivity.TOOTH_LIST))
        {
            title = "치아관리";
            btnCareInfoName = "치아관리정보보기";
            nextCatrgory = "치아관리정보";
            subCategory_1 = "치아 세정방법";
            subCategory_2 = "치아질병 및 치료방법";
            ((TextView) findViewById(R.id.tv_sub_title)).setText("치아관리내역");
            mService = HttpString.PET_TOOTH_SERVICE;
        }else if(category.equals(MainActivity.SKIN_LIST)){
            title = "피부관리";
            btnCareInfoName = "피부관리정보보기";
            nextCatrgory = "피부관리정보";
            subCategory_1 = "피부 세정방법";
            subCategory_2 = "피부질병 및 치료방법";
            ((TextView) findViewById(R.id.tv_sub_title)).setText("피부관리내역");
            mService = HttpString.PET_SKIN_SERVICE;
        }else if(category.equals(MainActivity.EAR_LIST)){
            title = "귀 관리";
            btnCareInfoName = "귀관리정보보기";
            nextCatrgory = "귀관리정보";
            subCategory_1 = "귀 세정방법";
            subCategory_2 = "귀 질병 및 치료방법";
            ((TextView) findViewById(R.id.tv_sub_title)).setText("귀관리내역");
            mService = HttpString.PET_EAR_SERVICE;
        }
        ((TextView) findViewById(R.id.txtTitle)).setText(title);
        mTvCareInfo = findViewById(R.id.tv_go_care);
        mTvCareInfo.setText(btnCareInfoName);
        mLinearBtnCareInfo = findViewById(R.id.linear_btn_go_care);
        mLinearBtnCareInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 정보 Web페이지 보여주는 Activity로 이동.
                Intent intent = new Intent(CareListActivity.this, CareActivity.class);
                intent.putExtra("category", nextCatrgory);
                intent.putExtra("title_1", subCategory_1);
                intent.putExtra("title_2", subCategory_2);
                intent.putExtra("category_code", mCategoryCode);
                startActivity(intent);
            }
        });


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(HttpString.SERVICE, mService);
            jsonObject.put(HttpString.MODE, HttpString.LIST);
            jsonObject.put("memidx", BaseActivity.mMemidx);
            new HttpAsyncTask(CareListActivity.this, HttpString.LIST, jsonObject, mHandler).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(CareListActivity.this, CareAddActivity.class);
            intent.putExtra("category", mCategoryCode);
            intent.putExtra("position", position+"");
            intent.putExtra("json_arr_data", mJsonArrDataCareList.toString());
            CareAddActivity.ACTIVITY_MODE = 2;  // 수정모드
            startActivity(intent);
        }
    };
    public static Bitmap[] bitmaps;
    private void initListView(final JSONArray jsonArrData)
    {
        String[] realPathStr = new String[jsonArrData.length()];
        for(int i = 0 ; i < jsonArrData.length() ; i++)
        {
            try {
                JSONObject jsonObject = jsonArrData.getJSONObject(i);
                realPathStr[i] = jsonObject.getString("filepath");
                Log.e("realPathStr[ + " + i + "]", realPathStr[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        bitmaps = new Bitmap[jsonArrData.length()];
        final boolean[] isLoadingFinish = {false};
        for(int i = 0 ; i < jsonArrData.length() ; i++)
        {
            final int finalI = i;
            try {
                Glide.with(CareListActivity.this).load(realPathStr[i]).asBitmap().listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e("bitmap", bitmaps[finalI] + "onException" + finalI);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        final Bitmap bitmap = resource;
                        bitmaps[finalI] = bitmap;
                        Log.e("bitmap", bitmaps[finalI] + "===" + finalI);
                        return false;
                    }
                }).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        final Bitmap bitmap = resource;
                        bitmaps[finalI] = bitmap;
                        Log.e("bitmap", bitmaps[finalI] + "===" + finalI);
                        if(jsonArrData.length() == finalI +1)
                        {
                            mDialog.show();
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    mDialog.dismiss();
                                    isLoadingFinish[0] = true;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mAdapter = new CareListAdapter(CareListActivity.this, jsonArrData, bitmaps);
                                            mListView = findViewById(R.id.listview);
                                            mListView.setAdapter(mAdapter);
                                            mListView.setOnItemClickListener(mOnItemClickListener);
                                            showChart();
                                        }
                                    });
                                }
                            },3000);

                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showChart()
    {
        mLineChart = findViewById(R.id.chart);
        mLineChart.setVisibility(View.VISIBLE);
        mLineChart.fitScreen();
        mLineChart.getXAxis().setValueFormatter(null);
        mLineChart.notifyDataSetChanged();
        mLineChart.clear();
        mLineChart.invalidate();
        mValues = new ArrayList<>();
        ArrayList<String> xValue = new ArrayList<String>();
        ArrayList<String> status = new ArrayList<String>();
        for(int i = 0 ; i < mJsonArrDataCareList.length() ; i++)
        {
            JSONObject jsonObject = null;
            try {
                jsonObject = mJsonArrDataCareList.getJSONObject(i);
                status.add(jsonObject.getString("Status"));

                String date = jsonObject.getString("CreateTime");
                String[] dateStr = date.split(" ");
                xValue.add(dateStr[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Collections.reverse(status);
        Collections.reverse(xValue);
        for(int i = 0 ; i < mJsonArrDataCareList.length() ; i++)
        {
            mValues.add(new Entry(i, Integer.valueOf(status.get(i))));
        }

        LineDataSet lineDataSet = new LineDataSet(mValues, "냠냠");
        lineDataSet.setColor(Color.parseColor("#A1B4DC"));
        lineDataSet.setCircleColor(Color.parseColor("#FFd1d1"));
        lineDataSet.setCircleHoleColor(getResources().getColor(R.color.colorRed));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(true);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);

        LineData lineData = new LineData();
        lineData.addDataSet(lineDataSet);

        lineData.setValueTextColor(R.color.colorBlack);
        lineData.setValueTextSize(9);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(3, false);
        xAxis.setTextColor(R.color.colorBlack); // X축 텍스트컬러설정
        xAxis.setGridColor(R.color.colorBlack); // X축 줄의 컬러 설정
        xAxis.setAxisMaximum(xValue.size());
        xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValue));

        YAxis yLeft = mLineChart.getAxisLeft();
        yLeft.setLabelCount(2, false);
        yLeft.setAxisMinimum(1);
        yLeft.setAxisMaximum(3);
        yLeft.setTextColor(R.color.colorBlack);
        yLeft.setGridColor(R.color.colorBlack);
        String[] statusWord = {"",  "정상", "의심", "위험"};

        yLeft.setValueFormatter(new IndexAxisValueFormatter(statusWord));

        YAxis yAxisRight = mLineChart.getAxisRight(); //Y축의 오른쪽면 설정
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);

        Legend l = mLineChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        //l.setTypeface(tfLight);
        l.setTextColor(Color.BLACK);
        l.setEnabled(true); // 속성명 제거

        mLineChart.setData(lineData);
    }
    public static JSONArray mJsonArrDataCareList;
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case ERROR:
                    try{
                        mJsonResponseData = new JSONObject((String) msg.obj);
                    }catch (Exception e){}
                    break;
                case OK:
                    mJsonResponseData = (JSONObject)msg.obj;
                    if(mNowConnectionAPI.equals(HttpString.LIST))
                    {
                        mDialog.dismiss();
                        JSONObject jsonObj = mJsonResponseData;

                        String result = "";
                        try {
                            result = jsonObj.getString("result");
                            mJsonArrDataCareList = jsonObj.getJSONArray("data");
                            Log.e("mJsonResponseData", "mJsonResponseData = " + mJsonResponseData.toString());
                            if(result.equals("ok"))
                            {
                                initListView(mJsonArrDataCareList);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };

    private Timer mScanTimer;
    boolean chartScrolled;
    TimerTask mScanTask;
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
}
