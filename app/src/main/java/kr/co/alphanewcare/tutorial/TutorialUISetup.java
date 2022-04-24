package kr.co.alphanewcare.tutorial;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.alphanewcare.R;
import kr.co.alphanewcare.tutorial.adapter.TutorialPageAdapter;
import kr.co.alphanewcare.tutorial.view.CircleAnimIndicator;
import kr.co.alphanewcare.utils.URSUtils;

import java.util.ArrayList;

public class TutorialUISetup
{
  private Context context;
  private TutorialActivity activity;
  private TutorialAction action;

  private ArrayList<TutorialGuide> guideList;
  private CircleAnimIndicator circleAnimIndicator;
  private LinearLayout llStep1, llStep2, llStep3;
  private TextView txtTutorialTitle;

  public class TutorialGuide
  {
    private Drawable image;
    private int textId;

    public TutorialGuide(Drawable image, int textId)
    {
      this.image = image;
      this.textId  = textId;
    }

    public Drawable getImage()
    {
      return image;
    }

    public int getTextId()
    {
      return textId;
    }
  }

  public TutorialUISetup(Context context, TutorialActivity activity)
  {
    this.context = context;
    this.activity = activity;
  }

  public void setupViews()
  {
    // 커스텀 툴바 타이틀
    TextView textView = activity.findViewById(R.id.txtTitle);
    textView.setText(context.getResources().getString(R.string.title_shooting));

    ImageView imgBack = activity.findViewById(R.id.imgBack);
    imgBack.setImageResource(R.drawable.chevrons_left);

    guideList = new ArrayList<>();
        /*
        guideList.add(new TutorialGuide(context.getResources().getDrawable(R.drawable.ic_tutorial1), R.string.caption_control_tutorial1));
        guideList.add(new TutorialGuide(context.getResources().getDrawable(R.drawable.ic_tutorial2), R.string.caption_control_tutorial2));
        guideList.add(new TutorialGuide(context.getResources().getDrawable(R.drawable.ic_tutorial3), R.string.caption_control_tutorial3));
        */

    guideList.add(new TutorialGuide(context.getResources().getDrawable(R.drawable.shooting_tip_one), R.string.caption_control_tutorial1));
    guideList.add(new TutorialGuide(context.getResources().getDrawable(R.drawable.shooting_tip_two), R.string.caption_control_tutorial2));
    guideList.add(new TutorialGuide(context.getResources().getDrawable(R.drawable.shooting_tip_three), R.string.caption_control_tutorial3));
//
//
//        txtTutorialTitle = activity.findViewById(R.id.txtTutorialTitle);
//        llStep1 = activity.findViewById(R.id.llStep1);
//        llStep2 = activity.findViewById(R.id.llStep2);
//        llStep3 = activity.findViewById(R.id.llStep3);
//
//        ViewPager viewPager = activity.findViewById(R.id.viewPager);
//
//        circleAnimIndicator = activity.findViewById(R.id.circleAnimIndicator);
//        circleAnimIndicator.setItemMargin(15);
//        circleAnimIndicator.setAnimDuration(300);

    circleAnimIndicator.createDotPanel(
            guideList.size(),
            R.drawable.dot_default, Color.LTGRAY,
            R.drawable.dot_selected, context.getResources().getColor(R.color.colorPrimaryDark),
            (int) URSUtils.dpToPx(context.getResources(), 20)
    );

    TutorialPageAdapter adapter = new TutorialPageAdapter(guideList, context);
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
//        {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
//            {
//
//            }
//
//            @Override
//            public void onPageSelected(int position)
//            {
//                txtTutorialTitle.setText("STEP " + (position + 1));
//                switch (position) {
//                    case 0:
//                        llStep1.setVisibility(View.VISIBLE);
//                        llStep2.setVisibility(View.GONE);
//                        llStep3.setVisibility(View.GONE);
//                        break;
//                    case 1:
//                        llStep1.setVisibility(View.GONE);
//                        llStep2.setVisibility(View.VISIBLE);
//                        llStep3.setVisibility(View.GONE);
//                        break;
//                    case 2:
//                        llStep1.setVisibility(View.GONE);
//                        llStep2.setVisibility(View.GONE);
//                        llStep3.setVisibility(View.VISIBLE);
//                        break;
//                }
//                circleAnimIndicator.selectDot(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state)
//            {
//
//            }
//        });
  }

  public void setupButtons()
  {
    activity.findViewById(R.id.btn_skip).setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        action.hidden();
      }
    });
  }

  public void setAction(TutorialAction action)
  {
    this.action = action;
  }
}
