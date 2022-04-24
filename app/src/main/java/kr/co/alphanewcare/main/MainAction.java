package kr.co.alphanewcare.main;

import android.content.Context;

import kr.co.alphanewcare.URSDefine;

import kr.co.alphanewcare.MainActivity;

public class MainAction
{
    private Context context;
    private MainActivity activity;
    private MainUISetup uiSetup;

    public MainAction(Context context, kr.co.alphanewcare.MainActivity activity)
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
