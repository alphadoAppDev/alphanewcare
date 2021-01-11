package kr.co.alphacare.capture;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import kr.co.alphacare.AppGlobals;
import kr.co.alphacare.R;
import kr.co.alphacare.URSDefine;
import kr.co.alphacare.result.ResultActivity;
import kr.co.alphacare.result.model.ResultData;
import kr.co.alphacare.utils.Constants;

public class CaptureActivity extends AppCompatActivity
{
    public final static String REQ_MODE = "mode";
    public static final String URS_RESULT = "urs_result";
    public static final String PAPER_MODE = "paper_mode";
    public static final int REQ_KEY_RESULT_ACTIVITY = 1001;

    private Context mContext;
    private CaptureUISetup uiSetup;
    private CaptureAction action;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mContext = this;

        init();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    private void init()
    {
        // 모드 설정
        Intent intent = getIntent();
        int paperMode = intent.getIntExtra(REQ_MODE, URSDefine.PaperMode.litmus10.getValue());

        uiSetup = new CaptureUISetup(this, this);
        action = new CaptureAction(this, this, paperMode);

        uiSetup.setAction(action);
        action.setUiSetup(uiSetup);

        uiSetup.setupViews();
        uiSetup.setupButtons();
    }

    public void startResultActivity(ResultData resultData, int paperMode)
    {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(URS_RESULT, resultData);
        intent.putExtra(PAPER_MODE, paperMode);
        startActivityForResult(intent, REQ_KEY_RESULT_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQ_KEY_RESULT_ACTIVITY:
            {
                if (resultCode == RESULT_OK)
                {
                    if (data != null && data.hasExtra("MATCHING_INDEX")) {
                        Log.e("NNNNNNNNN", "index: " + data.getStringExtra("MATCHING_INDEX"));
                        Intent intent = new Intent(Constants.MATCHING_INDEX);
                        intent.putExtra("MATCHING_INDEX", data.getStringExtra("MATCHING_INDEX"));
                        AppGlobals.INSTANCE.getContext().sendBroadcast(intent);
                        /*
                        Intent intent = new Intent(Constants.MATCHING_INDEX);
                        intent.putExtra("MATCHING_INDEX", data.getStringExtra("MATCHING_INDEX"));
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                        */

                    }
                    action.finish();
                }
                else
                {
                    /*
                    Log.e("NNNNNNNNN", "index: " + data.getStringExtra("MATCHING_INDEX"));
                    Intent intent = new Intent(Constants.MATCHING_INDEX);
                    intent.putExtra("MATCHING_INDEX", data.getStringExtra("MATCHING_INDEX"));
                    AppGlobals.INSTANCE.getContext().sendBroadcast(intent);

                     */

                    action.reset();
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        action.back();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            action.back();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
