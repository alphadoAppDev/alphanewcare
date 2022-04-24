package kr.co.alphanewcare.result;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import kr.co.alphanewcare.R;
import kr.co.alphanewcare.URSDefine;
import kr.co.alphanewcare.result.model.DistanceRankData;
import kr.co.alphanewcare.result.model.ResultData;
import kr.co.alphanewcare.utils.BitmapUtils;
import kr.co.alphanewcare.utils.URSUtils;
import kr.co.alphanewcare.result.view.DrawImageView;

import java.util.ArrayList;

public class ResultAction
{
    private Context context;
    private ResultActivity activity;
    private ResultUISetup uiSetup;

    private ResultData resultData;

    private int paperMode;
    private int resultIndex;
    private String matchingResult;
    private  int matchingCount;

    public ResultAction(Context context, ResultActivity activity, ResultData resultData, int paperMode)
    {
        this.context = context;
        this.activity = activity;
        this.resultData = resultData;
        this.paperMode = paperMode;
        this.resultIndex = -1;
        matchingCount = 0;
        matchingResult = "";

    }

    public void finish(int result)
    {

        Intent intent = new Intent();
        intent.putExtra("MATCHING_INDEX", matchingResult);
        activity.setResult(result, intent);
        //activity.setResult(result);
        activity.finish();
    }

    public void setDrawImageViewListener(DrawImageView imageView)
    {
        imageView.setOnDrawListener(new DrawImageView.OnDrawListener()
        {
            @Override
            public void onDraw(Canvas canvas)
            {
                if(resultData == null)
                {
                    return;
                }

                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(URSUtils.dpToPx(context.getResources(), 2));
                paint.setTextSize(URSUtils.spToPx(context.getResources(), 14));

                DrawImageView resultImageView = activity.findViewById(R.id.resultImage);
                RectF viewRect = resultImageView.getViewRect();

                float ratio = viewRect.width() / (float)BitmapUtils.getOriginImageSize(resultData.getImagePath()).width;

                // 실제 검출 영역을 이미지 뷰에 맞게 리사이즈 - 리트머스 막대
                ArrayList<Rect> litmusRects = resultData.getLitmusRects();

                for (int i = 0; i < litmusRects.size(); i++)
                {
                    Rect resizedLitmusRect = new Rect(litmusRects.get(i));
                    resizedLitmusRect.left = (int)((resizedLitmusRect.left * ratio) + viewRect.left);
                    resizedLitmusRect.top = (int)((resizedLitmusRect.top * ratio) + viewRect.top);
                    resizedLitmusRect.right = (int)((resizedLitmusRect.right * ratio) + viewRect.left);
                    resizedLitmusRect.bottom = (int)((resizedLitmusRect.bottom * ratio) + viewRect.top);

                    paint.setColor(Color.WHITE);

                    canvas.drawCircle(
                            resizedLitmusRect.centerX(),
                            resizedLitmusRect.centerY(),
                            URSUtils.dpToPx(context.getResources(), 10),
                            paint
                    );

                    // & 인덱스번째에 해당하는 후보군들 중 가장 가까운 색상 표시
                    int[] comparisionRange;
                    if (paperMode == URSDefine.PaperMode.litmus10.getValue())
                    {
                        comparisionRange = URSDefine.litmus10_comparisionRange;
                    }
                    else if(paperMode == URSDefine.PaperMode.slim_diet_stix.getValue())
                    {
                        comparisionRange = URSDefine.slim_diet_stix_comparisionRange;
                    }
                    else if(paperMode == URSDefine.PaperMode.self_stik_fr.getValue())
                    {
                        comparisionRange = URSDefine.self_stik_fr_comparisionRange;
                    }
                    else // nitric_oxide
                    {
                        comparisionRange = URSDefine.nitric_oxide_comparisionRange;
                    }

                    int max = comparisionRange[i];

                    int min;
                    if (i - 1 >= 0)
                    {
                        min = comparisionRange[i-1] + 1;
                    }
                    else
                    {
                        min = 0;
                    }

                    Log.e("dodo", "min : " + min + ", max : " + max);

                    int minRank = -1;
                    Point minRankCenterPoint = null;

                    for (int j = min; j <= max; j++)
                    {
                        Rect resizedComparisionRect = new Rect(resultData.getComparisionRects().get(j));
                        resizedComparisionRect.left = (int)((resizedComparisionRect.left * ratio) + viewRect.left);
                        resizedComparisionRect.top = (int)((resizedComparisionRect.top * ratio) + viewRect.top);
                        resizedComparisionRect.right = (int)((resizedComparisionRect.right * ratio) + viewRect.left);
                        resizedComparisionRect.bottom = (int)((resizedComparisionRect.bottom * ratio) + viewRect.top);

                        if (minRankCenterPoint == null)
                        {
                            minRankCenterPoint = new Point(resizedComparisionRect.centerX(), resizedComparisionRect.centerY());
                        }

                        ArrayList<DistanceRankData> distanceList = resultData.getDistanceList().get(i);

                        for(int k = 0; k < distanceList.size(); k++)
                        {
                            DistanceRankData distanceResult = distanceList.get(k);

                            if (distanceResult.getIndex() == j)
                            {
                                if (minRank == -1)
                                {
                                    minRank = distanceResult.getRank();
                                }

                                if (minRank >= distanceResult.getRank())
                                {
                                    resultIndex = j;

                                    minRank = distanceResult.getRank();
                                    minRankCenterPoint.x = resizedComparisionRect.centerX();
                                    minRankCenterPoint.y = resizedComparisionRect.centerY();
                                }

                                break;
                            }
                        }
                    }

                    matchingCount++;
                    if (paperMode == URSDefine.PaperMode.litmus10.getValue()) {
                        if (matchingCount < 10) matchingResult += resultIndex + ",";
                        else matchingResult += resultIndex;

                    }else {
                        matchingResult = "" + resultIndex;
                    }


                    paint.setColor(context.getResources().getColor(R.color.resultColor));
                    canvas.drawCircle(
                            minRankCenterPoint.x,
                            minRankCenterPoint.y,
                            URSUtils.dpToPx(context.getResources(), 10),
                            paint
                    );
                }
            }
        });

        imageView.invalidate();
    }

    public void setUiSetup(ResultUISetup uiSetup)
    {
        this.uiSetup = uiSetup;
    }
}
