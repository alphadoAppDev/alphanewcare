package kr.co.alphacare.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import kr.co.alphacare.R;

public class CustomAdapter extends BaseAdapter{

    private Context context;

    public CustomAdapter(Context context) {
        this.context = context;
    }


    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<CustomRowItem> mItems = new ArrayList<>();

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public CustomRowItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row_item, parent, false);
        }

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        CustomRowItem myItem = getItem(position);

        // 특정 item 터치 이벤트 막기
        if(myItem.getColor() == 0) {
            // item 뷰의 터치 이벤트를 설정합니다.
            convertView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    // 여기서 이벤트를 막습니다.
                    return true;
                }
            });
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img) ;
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name) ;
        TextView tv_value = (TextView) convertView.findViewById(R.id.tv_value) ;
        ImageView iv_click = (ImageView) convertView.findViewById(R.id.iv_click) ;




        //if (position == 0) tv_name.setTextColor(myItem.getColor());
        //if (myItem.getColor() != 0) tv_value.setTextColor(myItem.getColor());
        if (myItem.getColor() != 0) tv_name.setTextColor(myItem.getColor());

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        if (myItem.getIcon() == null) iv_img.setVisibility(View.GONE);
        else iv_img.setImageDrawable(myItem.getIcon());
        tv_name.setText(myItem.getName());
        tv_value.setText(myItem.getContents());

        if (myItem.getLastIcon() != null) iv_click.setImageDrawable(myItem.getLastIcon());



        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */


        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(int img, String name, String contents, int color, int img1) {
        //public void addItem(Drawable img, String name, String contents, int color, Drawable img1) {

        CustomRowItem mItem = new CustomRowItem();

        /* MyItem에 아이템을 setting한다. */
        if (img != 0) mItem.setIcon(ContextCompat.getDrawable(context, img));
        else mItem.setIcon(null);

        mItem.setName(name);
        mItem.setContents(contents);
        mItem.setColor(color);
        if (img1 != 0) mItem.setLastIcon(ContextCompat.getDrawable(context, img1));
        else mItem.setLastIcon(null);

        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);

    }
}