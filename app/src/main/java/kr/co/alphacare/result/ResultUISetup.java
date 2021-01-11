package kr.co.alphacare.result;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import kr.co.alphacare.R;
import kr.co.alphacare.result.model.ResultData;
import kr.co.alphacare.utils.BitmapUtils;
import kr.co.alphacare.result.view.DrawImageView;

public class ResultUISetup
{
    private Context context;
    private ResultActivity activity;
    private ResultAction action;

    private ResultData resultData;

    public ResultUISetup(Context context, ResultActivity activity, ResultData resultData)
    {
        this.context = context;
        this.activity = activity;
        this.resultData = resultData;
    }

    public void setupViews()
    {
        Bitmap bitmap = BitmapUtils.create(resultData.getImagePath());
        DrawImageView imageView = activity.findViewById(R.id.resultImage);
        imageView.setImageBitmap(bitmap);

        action.setDrawImageViewListener(imageView);
    }

    public void setupButtons()
    {
        Button yesButton = activity.findViewById(R.id.btn_yes);
        yesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO 결과값 전달
                action.finish(Activity.RESULT_OK);

            }
        });

        Button noButton = activity.findViewById(R.id.btn_no);
        noButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                action.finish(Activity.RESULT_CANCELED);
            }
        });
    }

    public void setAction(ResultAction action)
    {
        this.action = action;
    }
}
