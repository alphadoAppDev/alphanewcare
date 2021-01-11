package kr.co.alphacare.main;

import android.content.Context;

import kr.co.alphacare.URSDefine;

import kr.co.alphacare.MainActivity;

public class MainAction
{
    private Context context;
    private MainActivity activity;
    private MainUISetup uiSetup;

    public MainAction(Context context, kr.co.alphacare.MainActivity activity)
    {
        this.context = context;
        this.activity = activity;
    }

    public void startCamera(URSDefine.PaperMode mode)
    {
//        activity.showTutorialActivity(mode);
    }

    public void startGraph()
    {
        activity.showGraphActivity();
    }

    public void setUiSetup(MainUISetup uiSetup)
    {
        this.uiSetup = uiSetup;
    }
}
