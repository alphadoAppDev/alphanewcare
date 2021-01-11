package kr.co.alphacare.listview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.co.alphacare.AppGlobals;
import kr.co.alphacare.MainActivity;
import kr.co.alphacare.R;
import kr.co.alphacare.pets.PetKind;
import kr.co.alphacare.utils.CommonUtils;
import kr.co.alphacare.utils.PetInfo;
import kr.co.alphacare.utils.TestHistory;

import static kr.co.alphacare.utils.CommonUtils.rotateImage;

public class ProfileListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ProfileListAdapter(Context context)
    {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return AppGlobals.INSTANCE.getPetCount();
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
            convertView = mLayoutInflater.inflate(R.layout.item_profile, null);
            holder = new ViewHolder();

            holder.mCirclePetPicture = convertView.findViewById(R.id.circlle_pet_picture);
            holder.mTvPetName = convertView.findViewById(R.id.tv_pet_name);
            holder.mTvPetInfo = convertView.findViewById(R.id.tv_pet_info);
            holder.mTvPetBirthday = convertView.findViewById(R.id.tv_brith);
            holder.mTvPetWeigth = convertView.findViewById(R.id.tv_weight);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        PetInfo info = AppGlobals.INSTANCE.getPetInfo(position);
        if(MainActivity.mArrPetPicture.get(position) != null)
        {
            holder.mCirclePetPicture.setImageBitmap((Bitmap) (MainActivity.mArrPetPicture.get(position)));
        }else{
            holder.mCirclePetPicture.setImageResource(R.drawable.ic_add_camera);
        }

        holder.mTvPetName.setText(info.getPetName());
//        holder.mTvPetName.setSelected(true);
//        holder.mTvPetInfo.setSelected(true);
        int petType, petKind;
        petType = info.getPetType();
        petKind = info.getBreed();
        holder.mTvPetInfo.setText(PetKind.GetPetKind(mContext, petType, petKind));
        holder.mTvPetBirthday.setText(info.getBirthDay());
        holder.mTvPetWeigth.setText(info.getWeight() + "kg");
        return convertView;
    }

    class ViewHolder
    {
        CircleImageView mCirclePetPicture;
        TextView mTvPetName;
        TextView mTvPetInfo;
        TextView mTvPetBirthday;
        TextView mTvPetWeigth;
    }
}
