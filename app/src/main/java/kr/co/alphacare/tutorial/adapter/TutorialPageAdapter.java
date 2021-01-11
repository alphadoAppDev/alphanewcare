package kr.co.alphacare.tutorial.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import kr.co.alphacare.R;
import kr.co.alphacare.tutorial.TutorialUISetup;

import java.util.ArrayList;

public class TutorialPageAdapter extends PagerAdapter
{
  private Context context;
  private ArrayList<TutorialUISetup.TutorialGuide> list;

  public TutorialPageAdapter(ArrayList<TutorialUISetup.TutorialGuide> list, Context context)
  {
    this.context = context;
    this.list = list;
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position)
  {
    View view = null;

    if (context != null)
    {
      LayoutInflater inflater = LayoutInflater.from(context);
      view = inflater.inflate(R.layout.page_tutorial, container,false);

      ImageView imageView = view.findViewById(R.id.iv_guide_image);
      imageView.setImageDrawable(list.get(position).getImage());

      TextView textView = view.findViewById(R.id.tv_guide_text);
      textView.setText(list.get(position).getTextId());
    }

    container.addView(view);
    return view;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object)
  {
    container.removeView((View)object);
  }

  @Override
  public int getCount()
  {
    return list.size();
  }

  @Override
  public boolean isViewFromObject(View view, Object object)
  {
    return (view == (View)object);
  }
}
