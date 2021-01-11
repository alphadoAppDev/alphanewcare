package kr.co.alphacare.tutorial.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import kr.co.alphacare.utils.URSUtils;

public class CircleAnimIndicator extends LinearLayout
{
  private Context context;

  //원 사이의 간격
  private int itemMargin = 10;

  //애니메이션 시간
  private int animDuration = 250;

  private Drawable defaultCircle;
  private Drawable selectedCircle;

  private ImageView[] imageDot;

  public CircleAnimIndicator(Context context)
  {
    super(context);
    this.context = context;

    init();
  }

  public CircleAnimIndicator(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    this.context = context;

    init();
  }

  public CircleAnimIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    this.context = context;

    init();
  }

  private void init()
  {
    setGravity(Gravity.CENTER);
    setOrientation(LinearLayout.HORIZONTAL);
  }

  public void setAnimDuration(int animDuration)
  {
    this.animDuration = animDuration;
  }

  public void setItemMargin(int itemMargin)
  {
    this.itemMargin = itemMargin;
  }

  /**
   * 기본 점 생성
   * @param count 점의 갯수
   * @param defaultCircle 점의 이미지
   */
  public void createDotPanel(int count, int defaultCircle, int defaultCircleColor, int selectedCircle, int selectedCircleColor, int dotSize)
  {
    this.defaultCircle = URSUtils.changeDrawableColor(context.getResources().getDrawable(defaultCircle), defaultCircleColor);
    this.selectedCircle = URSUtils.changeDrawableColor(context.getResources().getDrawable(selectedCircle), selectedCircleColor);

    imageDot = new ImageView[count];

    for (int i = 0; i < count; i++)
    {
      imageDot[i] = new ImageView(context);
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
              dotSize,
              dotSize
      );

      params.topMargin = itemMargin;
      params.bottomMargin = itemMargin;
      params.leftMargin = itemMargin;
      params.rightMargin = itemMargin;

      imageDot[i].setLayoutParams(params);
      imageDot[i].setImageDrawable(this.defaultCircle);
      imageDot[i].setTag(imageDot[i].getId(), false);
      this.addView(imageDot[i]);
    }

    //첫인덱스 선택
    selectDot(0);
  }


  /**
   * 선택된 점 표시
   * @param position
   */
  public void selectDot(int position)
  {
    for (int i = 0; i < imageDot.length; i++)
    {
      if (i == position)
      {
        imageDot[i].setImageDrawable(selectedCircle);
        selectScaleAnim(imageDot[i],1f,1.5f);
      }
      else
      {
        if((boolean)imageDot[i].getTag(imageDot[i].getId()) == true)
        {
          imageDot[i].setImageDrawable(defaultCircle);
          defaultScaleAnim(imageDot[i], 1.5f, 1f);
        }
      }
    }
  }

  /**
   * 선택된 점의 애니메이션
   * @param view
   * @param startScale
   * @param endScale
   */
  public void selectScaleAnim(View view, float startScale, float endScale)
  {
    Animation anim = new ScaleAnimation(
            startScale, endScale,
            startScale, endScale,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
    );

    anim.setFillAfter(true);
    anim.setDuration(animDuration);
    view.startAnimation(anim);
    view.setTag(view.getId(),true);
  }

  /**
   * 선택되지 않은 점의 애니메이션
   * @param view
   * @param startScale
   * @param endScale
   */
  public void defaultScaleAnim(View view, float startScale, float endScale)
  {
    Animation anim = new ScaleAnimation(
            startScale, endScale,
            startScale, endScale,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
    );

    anim.setFillAfter(true);
    anim.setDuration(animDuration);
    view.startAnimation(anim);
    view.setTag(view.getId(),false);
  }
}