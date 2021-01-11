package kr.co.alphacare.tutorial;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.alphacare.R;
import kr.co.alphacare.URSDefine;
import kr.co.alphacare.capture.CaptureActivity;

public class TutorialActivity extends Activity
{
  public final static String REQ_MODE = "mode";

  private TutorialUISetup uiSetup;
  private TutorialAction action;

  private int mode;
  private ViewPager mViewPager;
  private TutorialPagerAdapter mAdapter;
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tutorial);

    init();
  }

  public void btnBack(View v) {
    finish();
  }

  private void init()
  {
    Intent intent = getIntent();
    mode = intent.getIntExtra(REQ_MODE, URSDefine.PaperMode.litmus10.getValue());

    mViewPager = findViewById(R.id.view_pager);
    mViewPager.setClipToPadding(false);
    int dpValue = 30;
    float d = getResources().getDisplayMetrics().density;
    int margin = (int) (dpValue * d);
    mViewPager.setPadding(margin,0, margin,0);
    mViewPager.setPageMargin(margin/2);
    mAdapter = new TutorialPagerAdapter();
    mViewPager.setAdapter(mAdapter);
//        uiSetup = new TutorialUISetup(TutorialActivity.this, TutorialActivity.this);
//        action = new TutorialAction(TutorialActivity.this, TutorialActivity.this);
//
//        uiSetup.setAction(action);
//        action.setUiSetup(uiSetup);
//
//        uiSetup.setupViews();
//        uiSetup.setupButtons();

    (findViewById(R.id.btn_skip)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hidden();
      }
    });

  }

  public void showCaptureActivity()
  {
    Intent intent = new Intent(this, CaptureActivity.class);
    intent.putExtra(CaptureActivity.REQ_MODE, mode);
    startActivity(intent);
  }

  public class TutorialPagerAdapter extends PagerAdapter{

    @Override
    public int getCount() {
      return 3;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
      View view = null ;
      LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.item_tutorail_pager, container, false);

      String step = "STEP";
      TextView tvStep = view.findViewById(R.id.tv_step);
      TextView tvTutorial = view.findViewById(R.id.tv_tutorial);
      ImageView iv = view.findViewById(R.id.iv_tutorial);
      ImageView ivStep1 = view.findViewById(R.id.iv_step1);
      ImageView ivStep2 = view.findViewById(R.id.iv_step2);
      ImageView ivStep3 = view.findViewById(R.id.iv_step3);
      if(position == 0){
        tvStep.setText(step + " 1");
        tvTutorial.setText(R.string.caption_control_tutorial1);
        iv.setImageResource(R.drawable.shooting_tip_one);
        ivStep1.setVisibility(View.VISIBLE);
      }else if(position == 1){
        tvStep.setText(step + " 2");
        tvTutorial.setText(R.string.caption_control_tutorial2);
        iv.setImageResource(R.drawable.shooting_tip_two);
        ivStep2.setVisibility(View.VISIBLE);
      }else if(position == 2){
        tvStep.setText(step + " 3");
        tvTutorial.setText(R.string.caption_control_tutorial3);
        iv.setImageResource(R.drawable.shooting_tip_three);
        ivStep3.setVisibility(View.VISIBLE);
      }
      container.addView(view) ;
      return view ;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      container.removeView((View) object);
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
      return (view == object);
    }
  }

  private AlertDialog dialog;

  private Timer timer;
  private Handler timerHandler;
  private int timerCounter = 0;
  public void hidden()
  {
    if (dialog != null)
    {
      if (dialog.isShowing())
      {
        dialog.dismiss();
      }

      dialog = null;
    }

    dialog = new AlertDialog.Builder(TutorialActivity.this)
            .setCancelable(false)
            .setTitle(R.string.warning)
            .setMessage(R.string.timer_dialog)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                stopTimer();
                dialog.dismiss();
                showCaptureActivity();
                finish();
              }
            })
            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                startTimer();
                dialog.dismiss();
              }
            })
            .show();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    stopTimer();
  }

  public void startTimer()
  {
    if (timerHandler == null)
    {
      timerHandler = new Handler()
      {
        @Override
        public void handleMessage(Message msg)
        {
          super.handleMessage(msg);

          TextView timerTextView = findViewById(R.id.timerText);
          timerTextView.setText(String.format("%d", (60 - timerCounter)));
        }
      };

      if (timer == null)
      {
        timer = new Timer();
        timer.schedule(new TimerTask()
        {
          @Override
          public void run()
          {
            if (timerCounter == 60)
            {
              stopTimer();

              if (dialog != null)
              {
                if (dialog.isShowing())
                {
                  dialog.dismiss();
                }

                dialog = null;
              }

              showCaptureActivity();
              finish();
              return;
            }

            timerCounter++;
            timerHandler.obtainMessage().sendToTarget();
          }
        }, 0, 1000);
      }
    }
  }

  public void stopTimer()
  {
    if (timer != null)
    {
      timer.cancel();
    }
  }
}
