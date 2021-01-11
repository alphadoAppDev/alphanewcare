package kr.co.alphacare.utils;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import org.opencv.core.Size;

public class URSUtils
{
    /**
     * outputSize 에 맞추어 inputSize를 scaleAspectFit 으로 위치 / 크기를 맞추어 주고 ( = resultRect),
     * 그 위치와 크기를 사용하여 inputSize 내의 inputArea를 "outputArea"로 변경해준다.
     * @param inputSize
     * @param inputArea
     * @param outputSize
     * @return
     */
    public static RectF scaleAspectFitRectInputSizeToOutputSize(Size inputSize, RectF inputArea, Size outputSize)
    {
        float tempX = 0.0f;
        float tempY = 0.0f;
        float tempWidth = (float) outputSize.width;
        float tempHeight = (float)(inputSize.height * outputSize.width) / (float)inputSize.width;

        if(tempHeight > outputSize.height)
        {
            tempHeight = (float)outputSize.height;
            tempWidth = (float)(inputSize.width * outputSize.height) / (float)inputSize.height;
        }

        tempX = ((float)outputSize.width - tempWidth) / 2.0f;
        tempY = ((float)outputSize.height - tempHeight) / 2.0f;

        RectF resultRect = new RectF(
                tempX,
                tempY,
                tempX + tempWidth,
                tempY + tempHeight);

        RectF outputArea = new RectF();
        outputArea.left = inputArea.left / (float)inputSize.width * resultRect.width();
        outputArea.top = inputArea.top / (float) inputSize.height * resultRect.height();
        outputArea.right = inputArea.right / (float)inputSize.width * resultRect.width();
        outputArea.bottom = inputArea.bottom / (float) inputSize.height * resultRect.height();

        outputArea.left += resultRect.left;
        outputArea.top += resultRect.top;
        outputArea.right += resultRect.left;
        outputArea.bottom += resultRect.top;

        return outputArea;
    }

    public static Drawable changeDrawableColor(Drawable drawable, int color)
    {
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }


    public static float dpToPx(Resources res, float dp)
    {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    public static int pxToDp(Resources res, float px)
    {
        return (int) (px / res.getDisplayMetrics().density);
    }

    public static float spToPx(Resources res, int sp)
    {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, res.getDisplayMetrics());
    }

    public static int pxToSp(Resources res, float px)
    {
        return (int) (px / res.getDisplayMetrics().scaledDensity);
    }

    public static double getDeltaTimeSeconds(long startTime, long endTime)
    {
        return (double)(endTime - startTime) / 1000.0f;
    }

    public static double getDeltaTimeSeconds(long startTime)
    {
        return getDeltaTimeSeconds(startTime, System.currentTimeMillis());
    }
}
