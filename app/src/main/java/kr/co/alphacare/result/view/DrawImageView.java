package kr.co.alphacare.result.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import kr.co.alphacare.utils.URSUtils;

import org.opencv.core.Size;

@SuppressLint("AppCompatCustomView")
public class DrawImageView extends ImageView
{
    private Bitmap bitmap;
    private RectF viewRect;

    private OnDrawListener onDrawListener;

    public interface OnDrawListener
    {
        void onDraw(Canvas canvas);
    }

    public DrawImageView(Context context)
    {
        super(context);
        init();
    }

    public DrawImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DrawImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        setScaleType(ScaleType.FIT_CENTER);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        computeLayout();
    }

    private void computeLayout()
    {
        if (bitmap == null)
        {
            viewRect = new RectF(0, 0, getWidth(), getHeight());
            return;
        }

        Rect imageBoundRect = new Rect(
                0 + getPaddingLeft(),
                0 + getPaddingTop(),
                getWidth() - getPaddingRight(),
                getHeight() - getPaddingBottom()
        );

        viewRect = URSUtils.scaleAspectFitRectInputSizeToOutputSize(
                new Size(bitmap.getWidth(), bitmap.getHeight()),
                new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new Size(imageBoundRect.width(), imageBoundRect.height()));

        viewRect.left += getPaddingLeft();
        viewRect.right += getPaddingLeft();
        viewRect.top += getPaddingTop();
        viewRect.bottom += getPaddingTop();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (onDrawListener != null)
        {
            onDrawListener.onDraw(canvas);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bitmap)
    {
        super.setImageBitmap(bitmap);
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public RectF getViewRect()
    {
        return viewRect;
    }

    public void setOnDrawListener(OnDrawListener onDrawListener)
    {
        this.onDrawListener = onDrawListener;
    }
}
