package kr.co.alphanewcare.main;

import android.content.Context;
import android.view.View;

import kr.co.alphanewcare.R;
import kr.co.alphanewcare.URSDefine;

import kr.co.alphanewcare.MainActivity;

public class MainUISetup
{
    private Context context;
    private MainActivity activity;
    private MainAction action;

    public MainUISetup(Context context, MainActivity activity)
    {
        this.context = context;
        this.activity = activity;
    }

    public void setupViews()
    {
        //TextView textView = activity.findViewById(R.id.tv_text);
        //textView.setText(ImageProcessing.getVersion());
    }

    public void setupButtons()
    {
        activity.findViewById(R.id.rlGoTest).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                action.startCamera(URSDefine.PaperMode.litmus10);
                //action.startCamera(URSDefine.PaperMode.nitric_oxide);
            }
        });
        /*
        activity.findViewById(R.id.btn_litmus10).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                action.startCamera(URSDefine.PaperMode.litmus10);
            }
        });

        activity.findViewById(R.id.btn_litmus1_slim_diet_stix).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                action.startCamera(URSDefine.PaperMode.slim_diet_stix);
            }
        });

        activity.findViewById(R.id.btn_litmus1_self_stik_fr).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                action.startCamera(URSDefine.PaperMode.self_stik_fr);
            }
        });

        activity.findViewById(R.id.btn_litmus1_nitric_oxide).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                action.startCamera(URSDefine.PaperMode.nitric_oxide);
            }
        });


        Button graphButton = activity.findViewById(R.id.btn_graph);
        graphButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                action.startGraph();
            }
        });
        */

    }

    public void setAction(MainAction action)
    {
        this.action = action;
    }
}
