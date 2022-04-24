package kr.co.alphanewcare.listview;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.co.alphanewcare.R;

public class CareListAdapter extends BaseAdapter {

  private Context mContext;
  private LayoutInflater mLayoutInflater;
  private JSONArray mJsonArray;
  private Bitmap[] mBitmaps;
  public CareListAdapter(Context context, JSONArray jsonArray, Bitmap[] bitmaps){
    this.mContext = context;
    this.mLayoutInflater = LayoutInflater.from(context);
    this.mJsonArray = jsonArray;
    this.mBitmaps = bitmaps;
  }

  @Override
  public int getCount() {
    return mJsonArray.length();
  }

  @Override
  public Object getItem(int position) {
    return null;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final ViewHolder holder;
    if (convertView == null)
    {
      convertView = mLayoutInflater.inflate(R.layout.item_care_list, null);
      holder = new ViewHolder();
      holder.mTvTestNum = convertView.findViewById(R.id.tv_test_number);
      holder.mTvDate = convertView.findViewById(R.id.tv_date);
      holder.mTvTime = convertView.findViewById(R.id.tv_time);
      holder.mIvPicture = convertView.findViewById(R.id.iv_picture);
      convertView.setTag(holder);
    }
    else
    {
      holder = (ViewHolder) convertView.getTag();
    }
    holder.mTvTestNum.setText( (position + 1) + "");
    holder.mIvPicture.setImageBitmap(mBitmaps[position]);
    JSONObject jsonObject;
    String[] dataAndTime = new String[2];
    String memo = "";
    try {
      jsonObject = mJsonArray.getJSONObject(position);
      dataAndTime[0] = (jsonObject.getString("CreateTime")).split(" ")[0];
      dataAndTime[1] = (jsonObject.getString("CreateTime")).split(" ")[1];
      memo = jsonObject.getString("memo");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    holder.mTvDate.setText(dataAndTime[0] + "   " + dataAndTime[1]);
    holder.mTvTime.setText(memo);

    return convertView;
  }

  class ViewHolder
  {
    TextView mTvTestNum;
    TextView mTvDate;
    TextView mTvTime;
    CircleImageView mIvPicture;
  }
}
