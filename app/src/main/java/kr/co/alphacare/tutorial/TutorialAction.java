package kr.co.alphacare.tutorial;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.alphacare.R;

public class TutorialAction
{
  private Context context;
  private TutorialActivity activity;
  private TutorialUISetup uiSetup;

  private AlertDialog dialog;

  private Timer timer;
  private Handler timerHandler;
  private int timerCounter = 0;

  public TutorialAction(Context context, TutorialActivity activity)
  {
    this.context = context;
    this.activity = activity;
  }

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

    dialog = new AlertDialog.Builder(context)
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
                activity.showCaptureActivity();
                activity.finish();
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

          TextView timerTextView = activity.findViewById(R.id.timerText);
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

              activity.showCaptureActivity();
              activity.finish();
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

  public void setUiSetup(TutorialUISetup uiSetup)
  {
    this.uiSetup = uiSetup;
  }
}
