package kr.co.alphacare.graph.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import kr.co.alphacare.utils.URSUtils;

@SuppressLint("AppCompatCustomView")
public class GraphBarView extends View
{
    private Context context;

    private int barBackgroundColor;
    private int barColor;
    private int pointColor;
    private int rulerColor;

    private float maxValue;
    private float barValue;
    private float pointValue;

    private float pointSizeDp;
    private float rulerWidthDp;

    public GraphBarView(Context context)
    {
        super(context);

        this.context = context;
        init();
    }

    public GraphBarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        this.context = context;
        init();
    }

    public GraphBarView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        this.context = context;
        init();
    }

    private void init()
    {
        barBackgroundColor = Color.LTGRAY;
        barColor = Color.DKGRAY;
        pointColor = Color.RED;
        rulerColor = Color.WHITE;

        maxValue = 5;
        barValue = 3;
        pointValue = 1;

        pointSizeDp = 10;
        rulerWidthDp = 5;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        // 막대 그래프 배경
        Rect backgroundRect = new Rect(0,
                                        0,
                                        canvas.getWidth(),
                                        canvas.getHeight());
        paint.setColor(barBackgroundColor);
        canvas.drawRect(backgroundRect, paint);

        // 막대
        Rect barRect = new Rect(0,
                                0,
                                (int)(canvas.getWidth() / maxValue * barValue),
                                canvas.getHeight());
        paint.setColor(barColor);
        canvas.drawRect(barRect, paint);

        // 점
        paint.setColor(pointColor);
        canvas.drawCircle((int)(canvas.getWidth() / maxValue * pointValue),
                canvas.getHeight() / 2.0f,
                URSUtils.dpToPx(context.getResources(), pointSizeDp),
                paint);

        // 눈금
        paint.setColor(rulerColor);
        float halfWidth = URSUtils.dpToPx(context.getResources(), rulerWidthDp) / 2.0f;

        for (int i = 1; i < maxValue; i++)
        {
            Rect rulerRect = new Rect(
                    (int)((canvas.getWidth() / maxValue * i) - halfWidth),
                    (int)(canvas.getHeight() * 0.75),
                    (int)((canvas.getWidth() / maxValue * i) + halfWidth),
                    canvas.getHeight()
            );

            canvas.drawRect(rulerRect, paint);
        }
    }

    public void setBarBackgroundColor(int barBackgroundColor)
    {
        if (this.barBackgroundColor != barBackgroundColor)
        {
            this.barBackgroundColor = barBackgroundColor;
            this.invalidate();
        }
    }

    public void setBarColor(int barColor)
    {
        if (this.barColor != barColor)
        {
            this.barColor = barColor;
            this.invalidate();
        }
    }

    public void setPointColor(int pointColor)
    {
        if (this.pointColor != pointColor)
        {
            this.pointColor = pointColor;
            this.invalidate();
        }
    }

    public void setRulerColor(int rulerColor)
    {
        if (this.rulerColor != rulerColor)
        {
            this.rulerColor = rulerColor;
            this.invalidate();
        }
    }

    public void setMaxValue(int maxValue)
    {
        if (this.maxValue != maxValue)
        {
            this.maxValue = maxValue;
            this.invalidate();
        }
    }

    public void setBarValue(int barValue)
    {
        if (this.barValue != barValue)
        {
            this.barValue = barValue;
            this.invalidate();
        }
    }

    public void setPointValue(int pointValue)
    {
        if (this.pointValue != pointValue)
        {
            this.pointValue = pointValue;
            this.invalidate();
        }
    }

    public void setPointSizeDp(float pointSizeDp)
    {
        if (this.pointSizeDp != pointSizeDp)
        {
            this.pointSizeDp = pointSizeDp;
            this.invalidate();
        }
    }

    public void setRulerWidthDp(float rulerWidthDp)
    {
        if (this.rulerWidthDp != rulerWidthDp)
        {
            this.rulerWidthDp = rulerWidthDp;
            this.invalidate();
        }
    }
}
