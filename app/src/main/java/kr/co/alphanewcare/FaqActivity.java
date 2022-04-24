package kr.co.alphanewcare;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


public class FaqActivity extends AppCompatActivity {

    private final String tag = "FaqActivity";
    private Context mContext;
    private TextView textAnswer;

    private int showAnswerView;

    private LinearLayout mLinearAnswer1;
    private LinearLayout mLinearAnswer2;
    private LinearLayout mLinearAnswer3;
    private LinearLayout mLinearAnswer4;
    private LinearLayout mLinearAnswer6;
    private LinearLayout mLinearAnswer7;
    private LinearLayout mLinearAnswer8;
    private LinearLayout mLinearAnswer9;

    private ImageView mIvArrow1;
    private ImageView mIvArrow2;
    private ImageView mIvArrow3;
    private ImageView mIvArrow4;
    private ImageView mIvArrow6;
    private ImageView mIvArrow7;
    private ImageView mIvArrow8;
    private ImageView mIvArrow9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_faq);

        // 커스텀 툴바 타이틀
        TextView textView = findViewById(R.id.txtTitle);
        textView.setText(this.getResources().getString(R.string.menu_faq));
        mContext = this;

        InitView();
    }
    private void InitView() {
        mLinearAnswer1 = findViewById(R.id.linear_Answer1);
        mLinearAnswer2 = findViewById(R.id.linear_Answer2);
        mLinearAnswer3 = findViewById(R.id.linear_Answer3);
        mLinearAnswer4 = findViewById(R.id.linear_Answer4);
        mLinearAnswer6 = findViewById(R.id.linear_Answer6);
        mLinearAnswer7 = findViewById(R.id.linear_Answer7);
        mLinearAnswer8 = findViewById(R.id.linear_Answer8);
        mLinearAnswer9 = findViewById(R.id.linear_Answer9);
        mIvArrow1 = findViewById(R.id.iv_arrow_1);
        mIvArrow2 = findViewById(R.id.iv_arrow_2);
        mIvArrow3 = findViewById(R.id.iv_arrow_3);
        mIvArrow4 = findViewById(R.id.iv_arrow_4);
        mIvArrow6 = findViewById(R.id.iv_arrow_6);
        mIvArrow7 = findViewById(R.id.iv_arrow_7);
        mIvArrow8= findViewById(R.id.iv_arrow_8);
        mIvArrow9 = findViewById(R.id.iv_arrow_9);
        LinearLayout linearLayout;

        View.OnClickListener clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case  R.id.llFaq1:
                        if(mLinearAnswer1.getVisibility() == View.GONE) {
                            mLinearAnswer1.setVisibility(View.VISIBLE);
                            mIvArrow1.setImageResource((R.drawable.ic_arrow_top));
                        }else{
                            mLinearAnswer1.setVisibility(View.GONE);
                            mIvArrow1.setImageResource((R.drawable.ic_arrow_bottom));
                        }
                        break;
                    case  R.id.llFaq2:
                        if(mLinearAnswer2.getVisibility() == View.GONE) {
                            mLinearAnswer2.setVisibility(View.VISIBLE);
                            mIvArrow2.setImageResource((R.drawable.ic_arrow_top));
                        }else{
                            mLinearAnswer2.setVisibility(View.GONE);
                            mIvArrow2.setImageResource((R.drawable.ic_arrow_bottom));
                        }
                        break;
                    case  R.id.llFaq3:
                        if(mLinearAnswer3.getVisibility() == View.GONE) {
                            mLinearAnswer3.setVisibility(View.VISIBLE);
                            mIvArrow3.setImageResource((R.drawable.ic_arrow_top));
                        }else{
                            mLinearAnswer3.setVisibility(View.GONE);
                            mIvArrow3.setImageResource((R.drawable.ic_arrow_bottom));
                        }
                        break;
                    case  R.id.llFaq4:
                        if(mLinearAnswer4.getVisibility() == View.GONE) {
                            mLinearAnswer4.setVisibility(View.VISIBLE);
                            mIvArrow4.setImageResource((R.drawable.ic_arrow_top));
                        }else{
                            mLinearAnswer4.setVisibility(View.GONE);
                            mIvArrow4.setImageResource((R.drawable.ic_arrow_bottom));
                        }
                        break;
                    case  R.id.llFaq6:
                        if(mLinearAnswer6.getVisibility() == View.GONE) {
                            mLinearAnswer6.setVisibility(View.VISIBLE);
                            mIvArrow6.setImageResource((R.drawable.ic_arrow_top));
                        }else{
                            mLinearAnswer6.setVisibility(View.GONE);
                            mIvArrow6.setImageResource((R.drawable.ic_arrow_bottom));
                        }
                        break;
                    case  R.id.llFaq7:
                        if(mLinearAnswer7.getVisibility() == View.GONE) {
                            mLinearAnswer7.setVisibility(View.VISIBLE);
                            mIvArrow7.setImageResource((R.drawable.ic_arrow_top));
                        }else{
                            mLinearAnswer7.setVisibility(View.GONE);
                            mIvArrow7.setImageResource((R.drawable.ic_arrow_bottom));
                        }
                        break;
                    case  R.id.llFaq8:
                        if(mLinearAnswer8.getVisibility() == View.GONE) {
                            mLinearAnswer8.setVisibility(View.VISIBLE);
                            mIvArrow8.setImageResource((R.drawable.ic_arrow_top));
                        }else{
                            mLinearAnswer8.setVisibility(View.GONE);
                            mIvArrow8.setImageResource((R.drawable.ic_arrow_bottom));
                        }
                        break;
                    case  R.id.llFaq9:
                        if(mLinearAnswer9.getVisibility() == View.GONE) {
                            mLinearAnswer9.setVisibility(View.VISIBLE);
                            mIvArrow9.setImageResource((R.drawable.ic_arrow_top));
                        }else{
                            mLinearAnswer9.setVisibility(View.GONE);
                            mIvArrow9.setImageResource((R.drawable.ic_arrow_bottom));
                        }
                        break;
                }
            }
        };

        linearLayout = findViewById(R.id.llFaq1);
        linearLayout.setOnClickListener(clickListener);
        linearLayout = findViewById(R.id.llFaq2);
        linearLayout.setOnClickListener(clickListener);
        linearLayout = findViewById(R.id.llFaq3);
        linearLayout.setOnClickListener(clickListener);
        linearLayout = findViewById(R.id.llFaq4);
        linearLayout.setOnClickListener(clickListener);
        linearLayout = findViewById(R.id.llFaq6);
        linearLayout.setOnClickListener(clickListener);
        linearLayout = findViewById(R.id.llFaq7);
        linearLayout.setOnClickListener(clickListener);

        /**
         * 210113 언어셋이 fi 일 경우 일곱번째 답변 gone 처리
         * */
        linearLayout = findViewById(R.id.llFaq8);
        linearLayout.setOnClickListener(clickListener);
        if (Locale.getDefault().getLanguage().equals("fi")) {
            linearLayout.setVisibility(View.GONE);
        }

        linearLayout = findViewById(R.id.llFaq9);
        linearLayout.setOnClickListener(clickListener);
    }

    public void btnBack(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onStart(){
        super.onStart();
        //각종 리스너 등록
    }

    @Override
    public void onResume() {
        super.onResume();
        //사용자에게 보여질 데이터 등 가져오기
    }

    @Override
    public void onPause(){
        super.onPause();
        //사용자에게 보여지지 않을 때 임시로 뭔가 저장하거나 자원 해제 등 작성

    }

    /*
     * When the activity is destroyed, make a call to super class
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
